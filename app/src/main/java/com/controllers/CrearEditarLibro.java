package com.controllers;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;
import java.util.ResourceBundle;

import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import com.javafx.Libro;
import com.javafx.Modelo;
import com.javafx.Prestamo;
import com.javafx.Rellenable;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class CrearEditarLibro implements Rellenable,Initializable{
    private Connection conexionBBDD;
    private boolean creando;
    private String imagenPortada;
    private ValidationSupport validationSupport = new ValidationSupport();
    private Libro libro;

    @FXML
    private Button btnlibrosportada;

    @FXML
    private Button btnprestamosaceptar;

    @FXML
    private Button btnprestamoscancelar;

    @FXML
    private ComboBox<String> cmblibrosestado;

    @FXML
    private Spinner<Integer> spinnerlibrospaginas;

    @FXML
    private TextField txtlibrosautor;

    @FXML
    private TextField txtlibrosgenero;

    @FXML
    private TextField txtlibrosisbn;

    @FXML
    private TextField txtlibrostitulo;

    @FXML
    void aceptarLibro(ActionEvent event) {

        
        validationSupport.initInitialDecoration();
         if (validationSupport.isInvalid() || this.imagenPortada==null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validación");
            alert.setHeaderText("Formulario incompleto o con errores");
            alert.setContentText("Por favor, corrige los campos marcados en rojo");
            alert.showAndWait();
            return;
        }
        
        
    

    System.out.println("Datos válidos. Guardando...");

    String estado = cmblibrosestado.getSelectionModel().getSelectedItem();
    int paginas  =spinnerlibrospaginas.getValue().intValue();
    String autor = txtlibrosautor.getText();
    String titulo = txtlibrostitulo.getText();
    String genero = txtlibrosgenero.getText();
    String isbn = txtlibrosisbn.getText();
    boolean yaExiste = Prestamo.getListaLibros().stream().anyMatch(libro -> libro.getISBN().equals(isbn));
    
    if (creando) {
        if (!yaExiste) {
            String sql = "INSERT INTO LIBRO (ISBN, Titulo, Autor, Estado, Nro_paginas, Imagen_portada, Generos) "
           + "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try {
                PreparedStatement ps = this.conexionBBDD.prepareStatement(sql);
                ps.setString(1, isbn);
                ps.setString(2, titulo);
                ps.setString(3, autor);
                ps.setString(4, estado);
                ps.setInt(5, paginas);
                ps.setString(6, imagenPortada);
                ps.setString(7, genero);

                int filasInsertadas = ps.executeUpdate();
                if (filasInsertadas > 0) {
                    System.out.println("Libro insertado correctamente.");
                }
                ps.close();
            
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            Alert a1 =new Alert(AlertType.ERROR,"ERROR, EL ISBN YA EXISTE");
            a1.showAndWait();
        }
        
    }else{
        String sql = "UPDATE LIBRO SET Titulo=?, Autor=?, Estado=?, Nro_paginas=?, Imagen_portada=?, Generos=? WHERE ISBN=?";

    try {
        PreparedStatement ps = this.conexionBBDD.prepareStatement(sql);
        ps.setString(1, titulo);
        ps.setString(2, autor);
        ps.setString(3, estado);
        ps.setInt(4, paginas);
        ps.setString(5, imagenPortada);
        ps.setString(6, genero);
        ps.setString(7, isbn); // WHERE ISBN = ? (no se modifica, por eso va al final)

        int filasActualizadas = ps.executeUpdate();
        if (filasActualizadas > 0) {
            System.out.println("Libro actualizado correctamente.");
        }
        ps.close();
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
    try{
        Stage ventana = (Stage) this.btnlibrosportada.getScene().getWindow();
        ventana.close();
        this.conexionBBDD.close();
    }catch(SQLException e){
        System.out.println(e.getMessage());
    }

    }
        

        Stage estaVentana = (Stage) this.btnlibrosportada.getScene().getWindow();
        estaVentana.fireEvent(
                new WindowEvent(estaVentana, WindowEvent.WINDOW_CLOSE_REQUEST)
            );
            
    }   

    @FXML
    void cancelarLibro(ActionEvent event) {
        Stage ventana = (Stage) this.btnlibrosportada.getScene().getWindow();
        ventana.close();
    }

    @FXML
    void seleccionarimagenportada(ActionEvent event) {
         FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar Imagen");

            // Filtros de archivos
            FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
                    "Archivos de Imagen", "*.jpg", "*.jpeg", "*.png");
            fileChooser.getExtensionFilters().add(imageFilter);

            // Abrir el FileChooser
            File archivoSeleccionado = fileChooser.showOpenDialog(this.btnlibrosportada.getScene().getWindow());

            if (archivoSeleccionado != null) {
                try {
                    // imagen a Base64
                    String base64Imagen = convertirArchivoA64(archivoSeleccionado);
                    this.imagenPortada = base64Imagen;
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }
    public void setValores(Connection c1, boolean creando){
        this.conexionBBDD = c1;
        this.creando = creando;
        
    }
    public void inicializarValidaciones() {
        Validator<String> valTitulo = (control, value) -> {
            if (value == null || value.trim().isEmpty()) {
                return ValidationResult.fromError(control, "El título es obligatorio");
            }
            return null;
        };
        validationSupport.registerValidator(txtlibrostitulo, valTitulo);

        
        Validator<String> valAutor = (control, value) -> {
            if (value == null || value.trim().isEmpty()) {
                return ValidationResult.fromError(control, "El autor es obligatorio");
            }
            return null;
        };
        validationSupport.registerValidator(txtlibrosautor, valAutor);

     
        Validator<String> valISBN = (control, value) -> {
            if (value == null || value.isEmpty()) {
                return ValidationResult.fromError(control, "El ISBN no puede estar vacío");
            }
            boolean formato10 = value.matches("\\d{10}");
            boolean formato13 = value.matches("\\d{13}");
            if (!formato10 && !formato13) {
                return ValidationResult.fromError(control, "El ISBN debe tener 10 o 13 dígitos");
            }
            return null;
        };
        validationSupport.registerValidator(txtlibrosisbn, valISBN);

     
        Validator<Integer> valPaginas = (control, value) -> {
            if (value == null || value <= 0) {
                return ValidationResult.fromError(control, "Debe haber al menos 1 página");
            }
            return null;
        };
        validationSupport.registerValidator(spinnerlibrospaginas, valPaginas);

   
        Validator<String> valEstado = (control, value) -> {
            if (value == null) {
                return ValidationResult.fromError(control, "Debe seleccionar un estado");
            }
            return null;
        };
        validationSupport.registerValidator(cmblibrosestado, valEstado);
        
    }

private String convertirArchivoA64(File archivo) throws IOException {
        try (FileInputStream fis = new FileInputStream(archivo)) {
            byte[] bytes = fis.readAllBytes();
            return Base64.getEncoder().encodeToString(bytes);
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {


        Platform.runLater( () -> {
            Stage stage = (Stage)this.cmblibrosestado.getScene().getWindow();
            Image icon = new Image(getClass().getResourceAsStream("/iconoPrincipal.png"));
            stage.getIcons().add(icon);
        });
        
        this.spinnerlibrospaginas.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 5000, 0, 1));
                spinnerlibrospaginas.setEditable(true);
        this.cmblibrosestado.getItems().addAll("BUENO","REGULAR","MALO");
        inicializarValidaciones();
        
        

    }

    @Override
    public void rellenarValores(Modelo r) {
        if (r instanceof Libro l) {
            this.libro = l;
        }else{
            System.out.println("ERROR NO SE HA PASADO UN LIBRO PARA MODIFICAR");
        }
        rellenarEdicion();
    }
    private void rellenarEdicion(){
        //PASAMOS A MODO DE EDICION, LO QUE COGERÁ EL MODELO Y HARÁ QUE LOS CAMPOS DEL FORMULARIO VALGAN LO MISMO QUE EL MODELO
        if (this.libro != null) {
         
            txtlibrosisbn.setText(this.libro.getISBN());
            txtlibrosisbn.setDisable(true);
            txtlibrosisbn.setStyle("-fx-opacity: 0.6; -fx-background-color: #f0f0f0;");
            
           
            txtlibrostitulo.setText(this.libro.getTitulo());
            
          
            txtlibrosautor.setText(this.libro.getAutor());
            
           
            if (this.libro.getGeneros() != null) {
                txtlibrosgenero.setText(this.libro.getGeneros());
            }
          
            spinnerlibrospaginas.getValueFactory().setValue(this.libro.getNro_paginas());
            
          
            cmblibrosestado.setValue(this.libro.getEstado());
            
         
            this.imagenPortada = this.libro.getImagen_portadaB64();
            if (imagenPortada != null && !imagenPortada.isEmpty()) {
                btnlibrosportada.setText("Imagen cargada");
                btnlibrosportada.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            }
        }
        
        
    }


}
