package com.javafx;

import java.sql.Connection;




public interface Rellenable {
    public void setValores(Connection c1, boolean creando);
    void rellenarValores(Modelo r);
}
