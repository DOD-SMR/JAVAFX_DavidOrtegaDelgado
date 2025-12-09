package com.controllers;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import com.javafx.Libro;
import com.javafx.Modelo;
import com.javafx.Prestamo;
import com.javafx.Rellenable;
import com.javafx.Usuario;
import com.pixelduke.transit.Style;
import com.pixelduke.transit.TransitTheme;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class CrearEditarPrestamo implements Rellenable,Initializable {

    private boolean creando;
    private Connection conexionBBDD;
    private Prestamo prestamo;
    private ValidationSupport validationSupport = new ValidationSupport();

    @FXML
    private Button btnprestamosaceptar;

    @FXML
    private Button btnprestamoscancelar;

    @FXML
    private ComboBox<String> cmbprestamosestado;

    @FXML
    private DatePicker dateprestamosfinal;

    @FXML
    private DatePicker dateprestamosinicio;

    @FXML
    private ComboBox<String> cmbprestamosdni;

    @FXML
    private ComboBox<String> cmbprestamosisbn;

    @FXML
    void aceptarprestamo(ActionEvent event) {

        validationSupport.initInitialDecoration();
        if (validationSupport.isInvalid()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validación");
            alert.setHeaderText("Formulario incompleto o incorrecto");
            alert.setContentText("Corrige los campos marcados en rojo.");
            alert.showAndWait();
            return;
        }

        System.out.println("Préstamo válido. Guardando...");

        
        String dni = cmbprestamosdni.getSelectionModel().getSelectedItem();
        String isbn = cmbprestamosisbn.getSelectionModel().getSelectedItem();
        LocalDate fechaIni = dateprestamosinicio.getValue();
        LocalDate fechaFin = dateprestamosfinal.getValue();
        String estado = cmbprestamosestado.getSelectionModel().getSelectedItem();

        boolean buenEstado = estado.equalsIgnoreCase("BUENO");

        
        int diasRestantes = fechaFin == null ? 0 : (int)
                java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), fechaFin);
        Prestamo p = null;
        if (this.prestamo!= null) {
            p = Prestamo.crear(
                this.prestamo.getIdPrestamo(),
                dni,
                isbn,
                fechaIni.toString(),
                fechaFin.toString(),
                diasRestantes,
                buenEstado
            );
        }else{
            p = Prestamo.crear(
                0,
                dni,
                isbn,
                fechaIni.toString(),
                fechaFin.toString(),
                diasRestantes,
                buenEstado
            );
        }
        

        if (p == null) {
            Alert a1 = new Alert(Alert.AlertType.ERROR);
            a1.setTitle("Error");
            a1.setHeaderText("Préstamo inválido");
            a1.setContentText("El DNI o el ISBN no existen en el sistema.");
            a1.showAndWait();
            return;
        }

        if (creando) {
            insertarPrestamo(p);
        } else {
            actualizarPrestamo(p);
            
        }

        Stage ventana = (Stage) btnprestamosaceptar.getScene().getWindow();
        ventana.fireEvent(new WindowEvent(ventana, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    @FXML
    void cancelarprestamo(ActionEvent event) {
        Stage ventana = (Stage) btnprestamoscancelar.getScene().getWindow();
        ventana.close();
    }

    public void setValores(Connection c1, boolean creando) {
        this.conexionBBDD = c1;
        this.creando = creando;
        inicializarValidaciones();
        inicializarCombos();
    }

    @Override
    public void rellenarValores(Modelo r) {
        if (r instanceof Prestamo p) {
            this.prestamo = p;
            rellenarEdicion();
        }
    }
    private void inicializarValidaciones() {

        
        validationSupport.registerValidator(cmbprestamosdni, 
            Validator.createEmptyValidator("DNI obligatorio"));

        
        validationSupport.registerValidator(cmbprestamosisbn, 
            Validator.createEmptyValidator("ISBN obligatorio"));

        
        validationSupport.registerValidator(dateprestamosinicio, 
            (control, value) -> value == null
                ? ValidationResult.fromError(control, "Fecha inicio requerida")
                : null);

        
        validationSupport.registerValidator(dateprestamosfinal, 
            (control, value) -> value == null
                ? ValidationResult.fromError(control, "Fecha devolución requerida")
                : null);

        
        validationSupport.registerValidator(cmbprestamosestado,
            (control, value) -> value == null
                ? ValidationResult.fromError(control, "Debe seleccionar el estado del libro")
                : null);
    }

    private void inicializarCombos() {

        cmbprestamosestado.getItems().addAll("BUENO", "REGULAR", "MALO","NO ENTREGADO");
        ObservableList<Usuario> listaUsuarios = Prestamo.getListaUsuarios();
        ObservableList<Libro> listaLibros = Prestamo.getListaLibros();
        ObservableList<String> listaUsuariosString = FXCollections.observableArrayList();
        ObservableList<String> listaLibrosString = FXCollections.observableArrayList();

        listaUsuarios.forEach( usuario -> {
            listaUsuariosString.add(usuario.getDNI());
        });
        listaLibros.forEach( libro -> {
            listaLibrosString.add(libro.getISBN());
        });
        FilteredList<String> listaFiltradaUsuarios = new FilteredList<>(listaUsuariosString, p -> true);
        FilteredList<String> listaFiltradaLibros = new FilteredList<>(listaLibrosString,p -> true);
        cmbprestamosdni.setEditable(true);
        cmbprestamosisbn.setEditable(true);

        TextField txtprestamosdni = cmbprestamosdni.getEditor();
        TextField txtprestamosisbn = cmbprestamosisbn.getEditor(); 

        cmbprestamosdni.setItems(listaFiltradaUsuarios);
        cmbprestamosisbn.setItems(listaFiltradaLibros);
        cmbprestamosdni.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (event.getCharacter().equals(" ")) {
                event.consume(); // bloquea que se escriba el espacio
            }
        });
        cmbprestamosisbn.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (event.getCharacter().equals(" ")) {
                event.consume(); // bloquea que se escriba el espacio
            }
        });
        txtprestamosdni.textProperty().addListener((obs, oldValue, newValue) -> {
            final String selected = cmbprestamosdni.getSelectionModel().getSelectedItem();

      
            if (newValue == null || newValue.trim().isEmpty()) {
                listaFiltradaUsuarios.setPredicate(p -> true);
                cmbprestamosdni.hide();// ocultamos combo
                return;
            }

    
            if (selected == null || !selected.equals(newValue)) {
                
                listaFiltradaUsuarios.setPredicate(p -> p.toLowerCase().startsWith(newValue.toLowerCase().trim()));
                cmbprestamosdni.setVisibleRowCount(5);
                cmbprestamosdni.show();
            } else {
                listaFiltradaUsuarios.setPredicate(p -> true);
            }
        });
         txtprestamosisbn.textProperty().addListener((obs, oldValue, newValue) -> {
            final String selected = cmbprestamosisbn.getSelectionModel().getSelectedItem();

       
            if (newValue == null || newValue.trim().isEmpty()) {
                listaFiltradaLibros.setPredicate(p -> true);
                cmbprestamosisbn.hide();// ocultamos combo
                return;
            }

            if (selected == null || !selected.equals(newValue)) {
               
                listaFiltradaLibros.setPredicate(p -> p.toLowerCase().startsWith(newValue.toLowerCase().trim()));
                cmbprestamosisbn.setVisibleRowCount(5);
                cmbprestamosisbn.show();
            } else {
                listaFiltradaLibros.setPredicate(p -> true);
            }
        });
        
    }

    //RELLENAMOS LA EDICION
    private void rellenarEdicion() {
        if (prestamo == null) return;
        
        cmbprestamosdni.getSelectionModel().select(prestamo.getDNI());
        cmbprestamosdni.setDisable(true);

        cmbprestamosisbn.getSelectionModel().select(prestamo.getISBN());
        cmbprestamosisbn.setDisable(true);

        dateprestamosinicio.setValue(LocalDate.parse(prestamo.getFecha_inicio()));
        dateprestamosfinal.setValue(LocalDate.parse(prestamo.getFecha_devolucion()));

        cmbprestamosestado.setValue(prestamo.getBuenestado() ? "BUENO" : "REGULAR");
    }
    private void insertarPrestamo(Prestamo p) {
        String sql = "INSERT INTO PRESTAMO(DNI, ISBN, Fecha_inicio, Fecha_devolucion, Dias_restantes, Buenestado) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexionBBDD.prepareStatement(sql)) {
            ps.setString(1, p.getDNI());
            ps.setString(2, p.getISBN());
            ps.setString(3, p.getFecha_inicio());
            ps.setString(4, p.getFecha_devolucion());
            ps.setInt(5, p.getDias_restantes());
            ps.setBoolean(6, p.getBuenestado());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void actualizarPrestamo(Prestamo p) {
        String sql = "UPDATE PRESTAMO SET Fecha_inicio=?, Fecha_devolucion=?, Dias_restantes=?, Buenestado=? WHERE id_prestamo=?";
        if (conexionBBDD!=null) {
            try (PreparedStatement ps = conexionBBDD.prepareStatement(sql)) {
            ps.setString(1, p.getFecha_inicio());
            ps.setString(2, p.getFecha_devolucion());
            ps.setInt(3, p.getDias_restantes());
            ps.setBoolean(4, p.getBuenestado());
            ps.setInt(5, p.getIdPrestamo());
            ps.executeUpdate();
            
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub
        Platform.runLater( () -> {
            Stage stage = (Stage)this.btnprestamoscancelar.getScene().getWindow();
           
            Image icon = new Image(getClass().getResourceAsStream("/iconoPrincipal.png"));
            stage.getIcons().add(icon);
        });
    }
}
