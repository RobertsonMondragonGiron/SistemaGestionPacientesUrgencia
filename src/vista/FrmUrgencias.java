package vista;

import modelo.Paciente;
import modelo.ResumenTriage;
import servicio.ControlUrgencias;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class FrmUrgencias extends JFrame {

    // Bindings con el .form
    private JPanel panelPrincipal;

    private JTextField txtId;
    private JTextField txtNombre;
    private JComboBox<String> cmbTriage;
    private JSpinner spEdad;
    private JSpinner spPrioridad;
    private JSpinner spEspera;
    private JTextField txtRiesgo;
    private JTextField txtUmbral;

    private JButton btnAgregar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    private JTable tblPacientes;
    private JScrollPane scrollPacientes;

    private JTextField txtBuscar;
    private JButton btnBuscarId;
    private JButton btnBuscarNombre;
    private JButton btnBuscarTriage;
    private JButton btnMostrarTodo;

    private JButton btnRiesgoPromedio;
    private JButton btnAlertas;
    private JButton btnResumenTriage;
    private JTextArea txtReportes;

    private DefaultTableModel modeloTabla;
    private ControlUrgencias control = new ControlUrgencias();

    public FrmUrgencias() {
        setTitle("Sistema Triage - Urgencias");
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(950, 620);
        setLocationRelativeTo(null);

        inicializarComponentes();
        registrarEventos();
        cargarTabla(control.getTodos());
    }

    private void inicializarComponentes() {
        // Opciones del ComboBox
        cmbTriage.addItem("ROJO");
        cmbTriage.addItem("NARANJA");
        cmbTriage.addItem("AMARILLO");
        cmbTriage.addItem("VERDE");
        cmbTriage.addItem("AZUL");

        // Spinners
        spEdad.setModel(new SpinnerNumberModel(1, 1, 120, 1));
        spPrioridad.setModel(new SpinnerNumberModel(1, 1, 5, 1));
        spEspera.setModel(new SpinnerNumberModel(0, 0, 9999, 1));

        // Tabla
        String[] columnas = {"ID", "Nombre", "Triage", "Edad", "Prioridad", "Espera", "Riesgo", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblPacientes.setModel(modeloTabla);
        tblPacientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void registrarEventos() {
        btnAgregar.addActionListener(e -> accionAgregar());
        btnActualizar.addActionListener(e -> accionActualizar());
        btnEliminar.addActionListener(e -> accionEliminar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        btnBuscarId.addActionListener(e -> {
            String txt = txtBuscar.getText().trim();
            if (txt.isEmpty()) { JOptionPane.showMessageDialog(this, "Ingrese un ID."); return; }
            Paciente p = control.buscarPorId(txt);
            List<Paciente> res = new ArrayList<>();
            if (p != null) res.add(p);
            cargarTabla(res);
        });

        btnBuscarNombre.addActionListener(e -> {
            String txt = txtBuscar.getText().trim();
            if (txt.isEmpty()) { JOptionPane.showMessageDialog(this, "Ingrese un nombre."); return; }
            cargarTabla(control.buscarPorNombre(txt));
        });

        btnBuscarTriage.addActionListener(e -> {
            String txt = txtBuscar.getText().trim();
            if (txt.isEmpty()) { JOptionPane.showMessageDialog(this, "Ingrese una categoria (ej: ROJO)."); return; }
            cargarTabla(control.buscarPorTriage(txt.toUpperCase()));
        });

        btnMostrarTodo.addActionListener(e -> cargarTabla(control.getTodos()));

        btnRiesgoPromedio.addActionListener(e ->
            txtReportes.setText("Riesgo Promedio: " + String.format("%.2f", control.riesgoPromedio()))
        );

        btnAlertas.addActionListener(e -> {
            ArrayList<Paciente> alertas = control.pacientesEnAlerta();
            StringBuilder sb = new StringBuilder("Pacientes en Alerta (" + alertas.size() + "):\n");
            for (Paciente p : alertas)
                sb.append("  ").append(p.getId()).append(" - ").append(p.getNombre())
                  .append("  Riesgo: ").append(p.getRiesgo())
                  .append("  Umbral: ").append(p.getUmbralAlerta()).append("\n");
            if (alertas.isEmpty()) sb.append("  Ninguno en alerta.");
            txtReportes.setText(sb.toString());
        });

        btnResumenTriage.addActionListener(e -> {
            List<ResumenTriage> res = control.resumenPorTriage();
            StringBuilder sb = new StringBuilder("Resumen por Triage:\n");
            for (ResumenTriage r : res)
                sb.append("  ").append(r.toString()).append("\n");
            if (res.isEmpty()) sb.append("  Sin pacientes registrados.");
            txtReportes.setText(sb.toString());
        });

        // Click en fila -> llena formulario
        tblPacientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblPacientes.getSelectedRow();
                if (row >= 0) {
                    String id = modeloTabla.getValueAt(row, 0).toString();
                    Paciente p = control.buscarPorId(id);
                    if (p != null) cargarFormulario(p);
                }
            }
        });
    }

    private void accionAgregar() {
        Paciente p = leerFormulario();
        if (p == null) return;
        if (!control.agregar(p)) {
            JOptionPane.showMessageDialog(this, "El ID ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        cargarTabla(control.getTodos());
        limpiarFormulario();
        JOptionPane.showMessageDialog(this, "Paciente agregado correctamente.");
    }

    private void accionActualizar() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un paciente de la tabla.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (control.buscarPorId(id) == null) {
            JOptionPane.showMessageDialog(this, "Paciente no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            double riesgo = Double.parseDouble(txtRiesgo.getText().trim());
            if (riesgo < 0 || riesgo > 100) {
                JOptionPane.showMessageDialog(this, "Riesgo debe ser entre 0 y 100.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            control.actualizarRiesgo(id, riesgo);
            control.actualizarTriage(id, cmbTriage.getSelectedItem().toString());
            cargarTabla(control.getTodos());
            JOptionPane.showMessageDialog(this, "Paciente actualizado.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Riesgo invalido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionEliminar() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el ID del paciente.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int conf = JOptionPane.showConfirmDialog(this, "Eliminar paciente con ID: " + id + "?");
        if (conf == JOptionPane.YES_OPTION) {
            if (control.eliminar(id)) {
                cargarTabla(control.getTodos());
                limpiarFormulario();
                JOptionPane.showMessageDialog(this, "Paciente eliminado.");
            } else {
                JOptionPane.showMessageDialog(this, "Paciente no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Paciente leerFormulario() {
        String id = txtId.getText().trim();
        String nombre = txtNombre.getText().trim();

        if (id.isEmpty() || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID y Nombre son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        int edad = (int) spEdad.getValue();
        int prioridad = (int) spPrioridad.getValue();
        int espera = (int) spEspera.getValue();

        if (edad <= 0) {
            JOptionPane.showMessageDialog(this, "Edad debe ser mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        double riesgo, umbral;
        try {
            riesgo = Double.parseDouble(txtRiesgo.getText().trim());
            umbral = Double.parseDouble(txtUmbral.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Riesgo y Umbral deben ser numeros.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        if (riesgo < 0 || riesgo > 100) {
            JOptionPane.showMessageDialog(this, "Riesgo debe ser entre 0 y 100.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (umbral <= 0 || umbral > 100) {
            JOptionPane.showMessageDialog(this, "Umbral debe ser mayor a 0 y menor o igual a 100.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        return new Paciente(id, nombre, cmbTriage.getSelectedItem().toString(),
                edad, prioridad, espera, riesgo, umbral);
    }

    private void cargarFormulario(Paciente p) {
        txtId.setText(p.getId());
        txtNombre.setText(p.getNombre());
        cmbTriage.setSelectedItem(p.getCategoriaTriage());
        spEdad.setValue(p.getEdad());
        spPrioridad.setValue(p.getPrioridad());
        spEspera.setValue(p.getTiempoEspera());
        txtRiesgo.setText(String.valueOf(p.getRiesgo()));
        txtUmbral.setText(String.valueOf(p.getUmbralAlerta()));
    }

    private void limpiarFormulario() {
        txtId.setText("");
        txtNombre.setText("");
        cmbTriage.setSelectedIndex(0);
        spEdad.setValue(1);
        spPrioridad.setValue(1);
        spEspera.setValue(0);
        txtRiesgo.setText("");
        txtUmbral.setText("");
        tblPacientes.clearSelection();
    }

    public void cargarTabla(List<Paciente> lista) {
        modeloTabla.setRowCount(0);
        for (Paciente p : lista) {
            modeloTabla.addRow(new Object[]{
                p.getId(),
                p.getNombre(),
                p.getCategoriaTriage(),
                p.getEdad(),
                p.getPrioridad(),
                p.getTiempoEspera(),
                p.getRiesgo(),
                p.estadoAtencion()
            });
        }
    }
}
