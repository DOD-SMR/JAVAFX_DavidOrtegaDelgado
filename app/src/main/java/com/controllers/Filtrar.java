package com.controllers;



import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.javafx.Libro;
import com.javafx.Prestamo;
import com.javafx.Usuario;

import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Filtrar implements Initializable{
    private Tab tabTrabajar;
    private FilteredList listafiltrada;
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
        String campo = this.cmbusuariofiltro.getValue();
        String valor = this.txtusuariofiltro.getText().toLowerCase();
        if (!listafiltrada.isEmpty()) {
            this.listafiltrada.setPredicate(item -> {
                if (valor == null || valor.isBlank()) return true;

                String campoValor = switch (item.getClass().getSimpleName()) {
                    case "Libro" -> {
                        Libro l = (Libro) item;
                        yield switch (campo) {
                            case "ISBN" -> l.getISBN();
                            case "Título" -> l.getTitulo();
                            case "Autor" -> l.getAutor();
                            case "Género" -> l.getGeneros();
                            default -> "";
                        };
                    }
                    case "Usuario" -> {
                        Usuario u = (Usuario) item;
                        yield switch (campo) {
                            case "DNI" -> u.getDNI();
                            case "Nombre" -> u.getNombre();
                            case "Apellidos" -> u.getApellidos();
                            case "Sexo" -> u.getSexo();
                            default -> "";
                        };
                    }
                    case "Prestamo" -> {
                        Prestamo p = (Prestamo) item;
                        yield switch (campo) {
                            case "DNI" -> p.getDNI();
                            case "ISBN" -> p.getISBN();
                            case "Fecha inicio" -> p.getFecha_inicio();
                            case "Fecha devolucion" -> p.getFecha_devolucion();
                            case "Estado" -> p.getBuenestado() ? "BUENO" : "MALO";
                            default -> "";
                        };
                    }
                    default -> "";
                };
                    return campoValor != null && campoValor.toLowerCase().contains(valor);
                });
            Stage stage = (Stage) btnusuariofiltroaceptar.getScene().getWindow();
            EventHandler<WindowEvent> handler = stage.getOnCloseRequest();
            if (handler != null) {
                handler.handle(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
            }
            stage.close();
        }else{
            System.out.println("ERROR, LA LISTA ESTÁ VACÍA");
        }
        
        
    }

    @FXML
    void cancelarFiltro(ActionEvent event) {
        Stage ventanaActual = (Stage) btnusuariofiltroaceptar.getScene().getWindow();
        ventanaActual.close();
    }
    public void setValores(Tab tab, FilteredList<?> lista){
        this.tabTrabajar=tab;
        this.listafiltrada = lista;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
                if (!this.listafiltrada.isEmpty()) {
                    Class<?> tipoLista = listafiltrada.get(0).getClass();
                    List<String> campos = new ArrayList<>();

                    if (tipoLista == Libro.class) {
                    campos.addAll(List.of("ISBN", "Título", "Autor", "Género"));
                    } else if (tipoLista == Usuario.class) {
                        campos.addAll(List.of("DNI", "Nombre", "Apellidos", "Sexo"));
                    } else if (tipoLista == Prestamo.class) {
                        campos.addAll(List.of("DNI", "ISBN", "Estado","Fecha inicio","Fecha devolucion"));
                    }

                    this.cmbusuariofiltro.getItems().addAll(campos);
                    this.cmbusuariofiltro.getSelectionModel().selectFirst();
                }else{
                    System.out.println("ERROR, LA LISTA PASADA AL FILTRO ESTÁ  VACÍA");
                }
        });
    }

    
}
