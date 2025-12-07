package com.controllers;

import com.javafx.Filtro;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Filtrar implements Filtro {
    private Tab tabTrabajar;

    @FXML
    private Button btnusuariofiltroaceptar;

    @FXML
    private Button btnusuariofiltrocancelar;

    @FXML
    private ComboBox<String> cmbusuariofiltro;

    @FXML
    private TextField txtusuariofiltro;

    @FXML
    void aceptarFiltro(ActionEvent event) {
        System.out.println("Filtro aceptado");
        if (this.tabTrabajar.getId().equals("tabUsuarios")) {
            
        }
    }

    @FXML
    void cancelarFiltro(ActionEvent event) {
        Stage ventanaActual = (Stage) btnusuariofiltroaceptar.getScene().getWindow();
        ventanaActual.close();
    }
    @Override
    public void setTab(Tab tab){
        this.tabTrabajar=tab;
    }
}
