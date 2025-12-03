package com.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Ventana implements Initializable{
    
    @FXML
    protected Button btnaceptar;

    @FXML
    protected Button btncancelar;

    @FXML
    private Text textoVentana;

    @FXML
    void aceptarFunction(ActionEvent event) {
        
    }

    @FXML
    void cancelarFunction(ActionEvent event) {
        Stage ventana = (Stage)textoVentana.getScene().getWindow();
        ventana.close();
    }
    public void setTexto(String texto){
        this.textoVentana.setText(texto);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater( () -> {
            Stage stage = (Stage)this.btnaceptar.getScene().getWindow();
            Image icon = new Image(getClass().getResourceAsStream("/iconoPrincipal.png"));
            stage.getIcons().add(icon);
        });
    }

}
