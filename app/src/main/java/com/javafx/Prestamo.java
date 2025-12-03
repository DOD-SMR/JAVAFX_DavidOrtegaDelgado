package com.javafx;

import javafx.collections.ObservableList;

public class Prestamo implements Modelo{
    private int idPrestamo;
    private String DNI;
    private String ISBN;
    private String fecha_inicio;
    private String fecha_devolucion;
    private int dias_restantes;
    private boolean buenestado;
    private static ObservableList<Usuario> usuariosOL;
    private static ObservableList<Libro> librosOL;
    private Prestamo(int idPrestamo,String dNI, String iSBN, String fecha_inicio, String fecha_devolucion, int dias_restantes,
            boolean buenestado) {
        this.idPrestamo = idPrestamo;
        this.DNI = dNI;
        this.ISBN = iSBN;
        this.fecha_inicio = fecha_inicio;
        this.fecha_devolucion = fecha_devolucion;
        this.dias_restantes = dias_restantes;
        this.buenestado = buenestado;
    }
    public static Prestamo crear(int idPrestamo,String DNI, String ISBN, String fecha_inicio, String fecha_devolucion,int dias_restantes,boolean buenestado) {
            boolean usuarioExiste = usuariosOL.stream()
                .anyMatch(u -> u.getDNI().equals(DNI));
            boolean libroExiste = librosOL.stream()
                .anyMatch(l -> l.getISBN().equals(ISBN));
            if (!usuarioExiste || !libroExiste ) {
                return null;
            }
            return new Prestamo(idPrestamo,DNI, ISBN, fecha_inicio,
                                fecha_devolucion, dias_restantes, buenestado);
    }
    
    
    
    public String getDNI() {
        return DNI;
    }
    public void setDNI(String dNI) {
        DNI = dNI;
    }
    public String getISBN() {
        return ISBN;
    }
    public void setISBN(String iSBN) {
        ISBN = iSBN;
    }
    public String getFecha_inicio() {
        return fecha_inicio;
    }
    public void setFecha_inicio(String fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }
    public String getFecha_devolucion() {
        return fecha_devolucion;
    }
    public void setFecha_devolucion(String fecha_devolucion) {
        this.fecha_devolucion = fecha_devolucion;
    }
    public int getDias_restantes() {
        return dias_restantes;
    }
    public void setDias_restantes(int dias_restantes) {
        this.dias_restantes = dias_restantes;
    }
    public boolean getBuenestado() {
        return buenestado;
    }
    public void buenestado(boolean buenestado) {
        this.buenestado = buenestado;
    }
    public int getIdPrestamo(){
        return this.idPrestamo;
    }
    public static void setListas(ObservableList<Usuario> listaUsuarios, ObservableList<Libro> listaLibros){
        Prestamo.librosOL = listaLibros;
        Prestamo.usuariosOL = listaUsuarios;
    }
    public static ObservableList<Usuario> getListaUsuarios(){
        return Prestamo.usuariosOL;
    }
    public static ObservableList<Libro> getListaLibros(){
        return Prestamo.librosOL;
    }
    
}
