package servicio;

import modelo.Paciente;
import modelo.ResumenTriage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ControlUrgencias {
    private ArrayList<Paciente> pacientes;

    public ControlUrgencias() {
        pacientes = new ArrayList<>();
    }

    public boolean agregar(Paciente p) {
        if (buscarPorId(p.getId()) != null) return false;
        pacientes.add(p);
        return true;
    }

    public Paciente buscarPorId(String id) {
        for (Paciente p : pacientes) {
            if (p.getId().equalsIgnoreCase(id)) return p;
        }
        return null;
    }

    public ArrayList<Paciente> buscarPorNombre(String texto) {
        ArrayList<Paciente> resultado = new ArrayList<>();
        for (Paciente p : pacientes) {
            if (p.getNombre().toLowerCase().contains(texto.toLowerCase())) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public ArrayList<Paciente> buscarPorTriage(String categoria) {
        ArrayList<Paciente> resultado = new ArrayList<>();
        for (Paciente p : pacientes) {
            if (p.getCategoriaTriage().equalsIgnoreCase(categoria)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public boolean eliminar(String id) {
        Paciente p = buscarPorId(id);
        if (p == null) return false;
        pacientes.remove(p);
        return true;
    }

    public boolean actualizarRiesgo(String id, double nuevoRiesgo) {
        Paciente p = buscarPorId(id);
        if (p == null) return false;
        p.setRiesgo(nuevoRiesgo);
        return true;
    }

    public boolean actualizarTriage(String id, String nuevoTriage) {
        Paciente p = buscarPorId(id);
        if (p == null) return false;
        p.setCategoriaTriage(nuevoTriage);
        return true;
    }

    public double riesgoPromedio() {
        if (pacientes.isEmpty()) return 0;
        double suma = 0;
        for (Paciente p : pacientes) suma += p.getRiesgo();
        return suma / pacientes.size();
    }

    public ArrayList<Paciente> pacientesEnAlerta() {
        ArrayList<Paciente> resultado = new ArrayList<>();
        for (Paciente p : pacientes) {
            if (p.enAlerta()) resultado.add(p);
        }
        return resultado;
    }

    public List<ResumenTriage> resumenPorTriage() {
        Map<String, int[]> mapa = new LinkedHashMap<>();
        String[] categorias = {"ROJO", "NARANJA", "AMARILLO", "VERDE", "AZUL"};
        for (String cat : categorias) mapa.put(cat, new int[]{0, 0}); // [cantidad, sumaRiesgo*100]

        for (Paciente p : pacientes) {
            String cat = p.getCategoriaTriage().toUpperCase();
            if (mapa.containsKey(cat)) {
                mapa.get(cat)[0]++;
                // store riesgo sum as int * 100 to avoid double array complexity
            }
        }

        // Recalculate properly
        Map<String, double[]> mapaD = new LinkedHashMap<>();
        for (String cat : categorias) mapaD.put(cat, new double[]{0, 0});
        for (Paciente p : pacientes) {
            String cat = p.getCategoriaTriage().toUpperCase();
            if (mapaD.containsKey(cat)) {
                mapaD.get(cat)[0]++;
                mapaD.get(cat)[1] += p.getRiesgo();
            }
        }

        List<ResumenTriage> resultado = new ArrayList<>();
        for (String cat : categorias) {
            double[] vals = mapaD.get(cat);
            if (vals[0] > 0) {
                resultado.add(new ResumenTriage(cat, (int) vals[0], vals[1] / vals[0]));
            }
        }
        return resultado;
    }

    public ArrayList<Paciente> getTodos() {
        return new ArrayList<>(pacientes);
    }
}
