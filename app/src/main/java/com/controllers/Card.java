package com.controllers;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

import com.javafx.Libro;
import com.javafx.Modelo;
import com.javafx.Rellenable;
import com.pixelduke.transit.TransitTheme;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Card implements Initializable{
    private Libro libroMostrado;
    @FXML
    private Button btnSalir;

    @FXML
    private ImageView imgCardPortada;

    @FXML
    private Text txtCardAutor;

    @FXML
    private Text txtCardEstado;

    @FXML
    private Text txtCardISBN;

    @FXML
    private Text txtCardPaginas;

    @FXML
    private Text txtCardTitulo;
    
    @FXML
    void salir(ActionEvent event) {
        Stage stage = (Stage)this.btnSalir.getScene().getWindow();
        stage.close();
    }
    private void rellenarValores(){
        this.imgCardPortada.setImage(this.libroMostrado.getImagen_portada().getImage());
        this.txtCardAutor.setText("Autor: "+this.libroMostrado.getAutor());
        this.txtCardEstado.setText("Estado: "+this.libroMostrado.getEstado());
        this.txtCardISBN.setText("ISBN: "+this.libroMostrado.getISBN());
        this.txtCardPaginas.setText("Numero de páginas: "+this.libroMostrado.getNro_paginas());
        this.txtCardTitulo.setText("Título: "+this.libroMostrado.getTitulo());
        
    }
    public void setLibroMostrado(Modelo r) {
        if (r instanceof Libro l) {
            this.libroMostrado = l;
            rellenarValores();
        }else{
            System.out.println("ERROR, EL MODELO PASADO NO ES UN LIBRO");
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater( () -> {
            Stage stage = (Stage)this.btnSalir.getScene().getWindow();
            Image icon = new Image(getClass().getResourceAsStream("/iconoPrincipal.png"));
            stage.getIcons().add(icon);
        });
    }

}
