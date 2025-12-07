package com.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.ResourceBundle;

import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import com.javafx.Modelo;
import com.javafx.Prestamo;
import com.javafx.Rellenable;
import com.javafx.Usuario;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class CrearEditarUsuario implements Rellenable, Initializable {
    private boolean creando;
    private Connection conexionBBDD;
    private String imagenFoto;
    private ValidationSupport validationSupport = new ValidationSupport();
    private Usuario usuario;

    @FXML
    private Button btnusuarioaceptar;

    @FXML
    private Button btnusuariocancelar;

    @FXML
    private Button btnusuariofoto;

    @FXML
    private ComboBox<String> cmbusuariosexo;

    @FXML
    private TextField txtusuarioapellidos;

    @FXML
    private TextField txtusuariodni;

    @FXML
    private TextField txtusuarionombre;

    @FXML
    private TextField txtusuariotelefono;

    @FXML
    private DatePicker dateusuariosfechanac;

    @FXML
    void aceptarusuario(ActionEvent event) {
        validationSupport.initInitialDecoration();
        if (validationSupport.isInvalid() || this.imagenFoto == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validación");
            alert.setHeaderText("Formulario incompleto o con errores");
            alert.setContentText("Por favor, corrige los campos marcados en rojo");
            alert.showAndWait();
            return;
        }

        System.out.println("Datos válidos. Guardando...");

        String dni = txtusuariodni.getText();
        String nombre = txtusuarionombre.getText();
        String apellidos = txtusuarioapellidos.getText();
        String[] partes = apellidos.split(" ");
        String apellido1 = partes.length > 0 ? partes[0] : "";
        String apellido2 = partes.length > 1 ? partes[1] : "";
        String telefono = txtusuariotelefono.getText();
        String sexo = cmbusuariosexo.getSelectionModel().getSelectedItem();
        String fecha_Nac = dateusuariosfechanac.getValue().toString();
        boolean yaExiste = Prestamo.getListaUsuarios().stream().anyMatch(usuario -> usuario.getDNI().equals(dni));

        if (creando) {
            // INSERT
            if (!yaExiste) {
                String sql = "INSERT INTO USUARIO (DNI, Nombre, Apellido1, Apellido2, FECHA_NACIMIENTO,Telefono, Sexo, Foto) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                try {
                    PreparedStatement ps = this.conexionBBDD.prepareStatement(sql);
                    ps.setString(1, dni);
                    ps.setString(2, nombre);
                    ps.setString(3, apellido1);
                    ps.setString(4, apellido2);
                    ps.setString(5, fecha_Nac);
                    ps.setString(6, telefono);
                    ps.setString(7, sexo);
                    ps.setString(8, imagenFoto);

                    int filasInsertadas = ps.executeUpdate();
                    if (filasInsertadas > 0) {
                        System.out.println("Usuario insertado correctamente.");
                    }
                    ps.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }else{
                Alert a1 =new Alert(AlertType.ERROR,"ERROR, EL DNI YA EXISTE");
                a1.showAndWait();
                System.out.println("ERROR AL GUARDAR, EL DNI DEL USUARIO YA EXISTE");
            }
            
        } else {
            // UPDATE
            String sql = "UPDATE USUARIO SET Nombre=?, Apellido1=?,Apellido2=?, FECHA_NACIMIENTO=?,Telefono=?, Sexo=?, Foto=? WHERE DNI=?";

            try {
                PreparedStatement ps = this.conexionBBDD.prepareStatement(sql);
                ps.setString(1, nombre);
                ps.setString(2, apellido1);
                ps.setString(3, apellido2);
                ps.setString(4, fecha_Nac);
                ps.setString(5, telefono);
                ps.setString(6, sexo);
                ps.setString(7, imagenFoto);
                ps.setString(8, dni);
                
                int filasActualizadas = ps.executeUpdate();
                if (filasActualizadas > 0) {
                    System.out.println("Usuario actualizado correctamente.");
                }
                ps.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                Stage ventana = (Stage) this.btnusuariofoto.getScene().getWindow();
                ventana.close();
                this.conexionBBDD.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        Stage estaVentana = (Stage) this.btnusuariofoto.getScene().getWindow();
        estaVentana.fireEvent(
                new WindowEvent(estaVentana, WindowEvent.WINDOW_CLOSE_REQUEST)
        );
    }

    @FXML
    void cancelarusuario(ActionEvent event) {
        Stage ventana = (Stage) this.btnusuariofoto.getScene().getWindow();
        ventana.close();
    }

    @FXML
    void seleccionarFoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Foto");

        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
                "Archivos de Imagen", "*.jpg", "*.jpeg", "*.png", "*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);

        File archivoSeleccionado = fileChooser.showOpenDialog(this.btnusuariofoto.getScene().getWindow());

        if (archivoSeleccionado != null) {
            try {
                String base64Imagen = convertirArchivoA64(archivoSeleccionado);
                this.imagenFoto = base64Imagen;
                btnusuariofoto.setText("✓ Foto seleccionada");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            
        }
    }

    public void setValores(Connection c1, boolean creando) {
        this.conexionBBDD = c1;
        this.creando = creando;
    }

    public void inicializarValidaciones() {
        // DNI - obligatorio y formato correcto
        Validator<String> valDNI = (control, value) -> {
            if (value == null || value.trim().isEmpty()) {
                return ValidationResult.fromError(control, "El DNI es obligatorio");
            }
            if (!value.matches("\\d{8}[A-Z]")) {
                return ValidationResult.fromError(control, "El DNI debe tener 8 números y una letra");
            }
            return null;
        };
        validationSupport.registerValidator(txtusuariodni, valDNI);

        // Nombre - obligatorio
        Validator<String> valNombre = (control, value) -> {
            if (value == null || value.trim().isEmpty()) {
                return ValidationResult.fromError(control, "El nombre es obligatorio");
            }
            return null;
        };
        validationSupport.registerValidator(txtusuarionombre, valNombre);

        // Apellidos - obligatorio
        Validator<String> valApellidos = (control, value) -> {
            if (value == null || value.trim().isEmpty()) {
                return ValidationResult.fromError(control, "Los apellidos son obligatorios");
            }
            return null;
        };
        validationSupport.registerValidator(txtusuarioapellidos, valApellidos);

        // Teléfono - obligatorio y formato
        Validator<String> valTelefono = (control, value) -> {
            if (value == null || value.trim().isEmpty()) {
                return ValidationResult.fromError(control, "El teléfono es obligatorio");
            }
            if (!value.matches("\\d{9}")) {
                return ValidationResult.fromError(control, "El teléfono debe tener 9 dígitos");
            }
            return null;
        };
        validationSupport.registerValidator(txtusuariotelefono, valTelefono);

        // Sexo - obligatorio
        Validator<String> valSexo = (control, value) -> {
            if (value == null) {
                return ValidationResult.fromError(control, "Debe seleccionar un sexo");
            }
            return null;
        };
        validationSupport.registerValidator(cmbusuariosexo, valSexo);
        //Fecha nacimiento - obligatorio
        Validator<LocalDate> valFecha = (control, value) -> {
    if (value == null) {
        return ValidationResult.fromError(control, "La fecha es obligatoria");
    }
    return null; // válido
};

    validationSupport.registerValidator(dateusuariosfechanac, valFecha);
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
            Stage stage = (Stage)this.btnusuariocancelar.getScene().getWindow();
            Image icon = new Image(getClass().getResourceAsStream("/iconoPrincipal.png"));
            stage.getIcons().add(icon);
        });
        this.cmbusuariosexo.getItems().addAll("MASCULINO", "FEMENINO", "OTRO");
        inicializarValidaciones();

    }

    @Override
    public void rellenarValores(Modelo r) {
        if (r instanceof Usuario u) {
            this.usuario = u;
        } else {
            System.out.println("ERROR: NO SE HA PASADO UN USUARIO PARA MODIFICAR");
        }
        rellenarEdicion();
    }

    private void rellenarEdicion() {
        if (this.usuario == null) {
            return;
        }

        // Rellenar DNI y deshabilitar
        txtusuariodni.setText(this.usuario.getDNI());
        txtusuariodni.setDisable(true);
        txtusuariodni.setStyle("-fx-opacity: 0.6; -fx-background-color: #f0f0f0;");

        // Rellenar nombre
        txtusuarionombre.setText(this.usuario.getNombre());

        // Rellenar apellidos
        txtusuarioapellidos.setText(this.usuario.getApellidos());

        // Rellenar teléfono
        if (this.usuario.getTelefono() != null) {
            txtusuariotelefono.setText(this.usuario.getTelefono());
        }

        // Rellenar sexo
        cmbusuariosexo.setValue(this.usuario.getSexo());

        // Cargar foto
        this.imagenFoto = this.usuario.getFotoB64();
        if (imagenFoto != null && !imagenFoto.isEmpty()) {
            btnusuariofoto.setText("✓ Foto cargada");
            btnusuariofoto.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        }
        //RELLENAR FECHA
        LocalDate ld1 = LocalDate.parse(this.usuario.getFecha_nacimiento());
        this.dateusuariosfechanac.setValue(ld1);
    }
}