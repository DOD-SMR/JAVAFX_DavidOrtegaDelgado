package com.controllers;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;

import com.javafx.Libro;
import com.javafx.Modelo;
import com.javafx.Prestamo;
import com.javafx.Rellenable;
import com.javafx.TipoVentana;
import com.javafx.Usuario;
import com.pixelduke.transit.TransitTheme;

import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class ControladorPrincipal implements Initializable{

    //VARIABLES DE INSTANCIA NECESARIAS 
    private int fila=0;
    private int columna=0;
    private Connection conexion;
    private Stage ventanaModal;
    private Tab tabActual;
    private Modelo modeloAPasar;
    private AnchorPane anchorPaneSeleccionado;
    protected ObservableList<Usuario> usuariosOL;
    protected ObservableList<Prestamo> prestamosOL;
    protected ObservableList<Libro> librosOL;
    private FilteredList<Usuario> listafiltradaUsuarios;
    private FilteredList<Prestamo> listafiltradaPrestamos;
    private FilteredList<Libro> listafiltradaLibros;
    private ContextMenu menuContextualTablasCards;
    private ContextMenu menuContextualItems;

// Crear los items del menú


    
    private  List<String> tablasValidas = Arrays.asList("USUARIO", "PRESTAMO", "LIBRO");
    
    @FXML
    private Button botonBorrraFiltro;

    @FXML
    private Button botonCrear;

    @FXML
    private Button botonEditar;

    @FXML
    private Button botonEliminar;

    @FXML
    private Button botonFiltro;
    
    @FXML
    private Tab tabLibros;

    @FXML
    private Tab tabPrestamos;

    @FXML
    private Tab tabUsuarios;

    @FXML
    private TabPane tabPane;

    @FXML
    private GridPane librosGridPane;

    @FXML
    private Tab pestañaSalir;

    
    @FXML
    private TableColumn<Prestamo, Integer> prestamosID;

    @FXML
    private TableColumn<Prestamo, String> prestamosDNI;

    @FXML
    private TableColumn<Prestamo, ImageView> prestamosEntregado;

    @FXML
    private TableColumn<Prestamo, String> prestamosFechaDevolucion;

    @FXML
    private TableColumn<Prestamo, String> prestamosISBN;

    @FXML
    private TableColumn<Prestamo, Integer> prestamosDiasRestantes;

    @FXML
    private TableColumn<Prestamo, String> prestamosFechaInicio;

    @FXML
    private TableView<Prestamo> tablaPrestamos;

    @FXML
    private TableView<Usuario> tablaUsuarios;

    @FXML
    private Text textoRegistrosNoEncontrados = new Text("NO SE HA ENCONTRADO NINGÚN REGISTRO");

    @FXML
    private TableColumn<Usuario, String> usuariosApellido;

    @FXML
    private TableColumn<Usuario, String> usuariosDNI;

    @FXML
    private TableColumn<Usuario, String> usuariosFechaNacimiento;

    @FXML
    private TableColumn<Usuario, ImageView> usuariosFoto;

    @FXML
    private TableColumn<Usuario, String> usuariosNombre;

    @FXML
    private TableColumn<Usuario, String> usuariosSexo;

    @FXML
    private TableColumn<Usuario, String> usuariosTelefono;

    @FXML
    void abrirFiltrar(ActionEvent event) throws IOException{
        
        Stage ventana = crearVentana("/filtrar.fxml","Aplicar filtro");
        ventana.show();
        
        
    }

    @FXML
    void borrarFiltro(ActionEvent event) {
        if (this.tabActual.equals(tabUsuarios)) {
            this.listafiltradaUsuarios.setPredicate(p -> true);
        }
        if (this.tabActual.equals(tabPrestamos)) {
            this.listafiltradaPrestamos.setPredicate(p -> true);
                
        }
        if (this.tabActual.equals(tabLibros)) {
            this.listafiltradaLibros.setPredicate(p -> true);
        }
    }
    

    @FXML
    void crearRegistro(ActionEvent event) throws IOException{
        
        if (this.tabActual.equals(tabUsuarios)) {
            
            Stage formUsuario = crearVentana("/crearoeditarusuario.fxml","Crear usuario");
           
            formUsuario.show();
            formUsuario.setOnCloseRequest(e -> {
                System.out.println("ACTUALIZANDO USUARIOS...");
               this.rellenarUsuarios(this.consultarTabla("USUARIO"));

            });
        }
        if (this.tabActual.equals(tabPrestamos)) {
            Stage formPrestamo = crearVentana("/crearoeditarprestamo.fxml", "Crear prestamo");
            formPrestamo.show();
            formPrestamo.setOnCloseRequest(e -> {
                System.out.println("ACTUALIZANDO PRESTAMOS...");
               this.rellenarPrestamos(this.consultarTabla("PRESTAMO"));

            });
            
        }
        if (this.tabActual.equals(tabLibros)) {
            
            Stage formLibro = crearVentana("/crearoeditarlibro.fxml", "Crear libro");
            formLibro.show();
            formLibro.setOnCloseRequest(e -> {
                System.out.println("ACTUALIZANDO LIBROS...");
               this.rellenarLibros(this.consultarTabla("LIBRO"));
               
            });
        }
    }

    @FXML
    void editarRegistro(ActionEvent event) throws IOException{
        
        if (this.tabActual.equals(tabUsuarios) && this.tablaUsuarios.getSelectionModel().getSelectedItem() != null) {
            
            this.modeloAPasar = this.tablaUsuarios.getSelectionModel().getSelectedItem();
            Stage formUsuario = crearVentana("/crearoeditarusuario.fxml","Editar usuario");
            formUsuario.show();
            formUsuario.setOnCloseRequest(e -> {
                System.out.println("ACTUALIZANDO USUARIOS...");
                realizarConexion();
               this.rellenarUsuarios(this.consultarTabla("USUARIO"));
               cerrarConexion();

            });            
        }
        else if (this.tabActual.equals(tabPrestamos) && this.tablaPrestamos.getSelectionModel().getSelectedItem() != null)  {
            this.modeloAPasar = this.tablaPrestamos.getSelectionModel().getSelectedItem();
            Stage formPrestamo = crearVentana("/crearoeditarprestamo.fxml", "Editar prestamo");
            formPrestamo.show();
            formPrestamo.setOnCloseRequest(e -> {
                System.out.println("ACTUALIZANDO PRESTAMOS...");
                realizarConexion();
               this.rellenarPrestamos(this.consultarTabla("PRESTAMO"));
                cerrarConexion();
            });
        }else if
         (this.tabActual.equals(tabLibros) && this.anchorPaneSeleccionado != null) {
            Stage formLibro = crearVentana("/crearoeditarlibro.fxml", "Editar libro");
            formLibro.show();
            formLibro.setOnCloseRequest(e -> {
                System.out.println("ACTUALIZANDO LIBROS...");
                realizarConexion();
               this.rellenarLibros(this.consultarTabla("LIBRO"));
               this.anchorPaneSeleccionado = null;
               cerrarConexion();
            });
        }else{
            Alert a1 = new Alert(Alert.AlertType.INFORMATION,"NO SE HA SELECCIONADO NINGUN REGISTRO");
            a1.showAndWait();
        }
        
    }

    @FXML
    void eliminarRegistro(ActionEvent event) {
        String mensaje= "";
        try{

        
        if (this.tabActual.equals(tabUsuarios) && this.tablaUsuarios.getSelectionModel().getSelectedItem()!=null) {
            mensaje="¿Está seguro de borrar el usuario seleccionado?";
            mostrarConfirmacion(mensaje, "Eliminar Registro",TipoVentana.ELIMINAR);
        }
        else if (this.tabActual.equals(tabPrestamos) && this.tablaPrestamos.getSelectionModel().getSelectedItem()!=null) {
            mensaje="¿Está seguro de borrar el préstamo seleccionado?";
            mostrarConfirmacion(mensaje, "Eliminar Registro",TipoVentana.ELIMINAR);

        }else if (this.tabActual.equals(tabLibros) && this.anchorPaneSeleccionado!=null) {
            mensaje="¿Está seguro de borrar el libro seleccionado?";
            mostrarConfirmacion(mensaje, "Eliminar Registro",TipoVentana.ELIMINAR);
        }else{
            Alert a1 = new Alert(Alert.AlertType.INFORMATION,"NO HAY NINGUN REGISTRO SELECCIONADO");
            a1.showAndWait();   
        }
        
        
              
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
        
    }

    
    
    
    private Stage crearVentana(String rutaFXML,String titulo) throws IOException{
        FXMLLoader cargadorFxml = new FXMLLoader(getClass().getResource(rutaFXML));
        Parent root = cargadorFxml.load();
        Scene scene = new Scene(root);
        Stage ventana = new Stage();
        ventana.setTitle(titulo);
        if(cargadorFxml.getController() instanceof Filtrar f){
            if (this.tabActual.equals(tabUsuarios)) {
                f.setValores(tabActual, listafiltradaUsuarios);
            }
            if (this.tabActual.equals(tabPrestamos)) {
                f.setValores(tabActual, listafiltradaPrestamos);
            }
            if (this.tabActual.equals(tabLibros)) {
                f.setValores(tabActual, listafiltradaLibros);
            }
            
        }
        if (cargadorFxml.getController() instanceof Card c) {
            TransitTheme t1 = new TransitTheme();
            t1.setScene(scene);
            c.setLibroMostrado(modeloAPasar);
        }
        if (cargadorFxml.getController() instanceof Rellenable r) {
            realizarConexion();
            
            if (ventana.getTitle().indexOf("Crear") != -1) {

                r.setValores(this.conexion, true);
            }else{
                r.setValores(this.conexion, false);
                r.rellenarValores(modeloAPasar);
            }
        }
            ventanaModal = ventana;
            ventana.setScene(scene);
            ventana.setResizable(false);
            ventana.initModality(Modality.APPLICATION_MODAL);
            return ventana;
    }
    private void mostrarConfirmacion(String textoVentana,String tituloVentana,TipoVentana tipoVentana) throws IOException{
        FXMLLoader cargadorFxml = new FXMLLoader(getClass().getResource("/ventana.fxml"));
        Parent root = cargadorFxml.load();
        Scene scene = new Scene(root);
        Stage ventana = new Stage();
        ventana.setTitle(tituloVentana);
        if (cargadorFxml.getController() instanceof Ventana v) {
            v.setTexto(textoVentana);
            if (tipoVentana.equals(TipoVentana.SALIR)) {
                Stage ventanaActual = (Stage) this.botonBorrraFiltro.getScene().getWindow();
                v.btncancelar.setOnAction( e->{
                    this.tabPane.getSelectionModel().select(this.tabUsuarios);
                    Stage ventanaEmergente =(Stage) v.btncancelar.getScene().getWindow();
                    ventanaEmergente.close();

                });
                v.btnaceptar.setOnAction(e->{
                    Stage ventanaEmergente =(Stage) v.btnaceptar.getScene().getWindow();
                    ventanaActual.close();
                    ventanaEmergente.close();
                    System.out.println("SALIENDO DEL PROGRAMA");
                });
            }
            
            if (tipoVentana.equals(TipoVentana.ELIMINAR)) {
                
                v.btncancelar.setOnAction((e)->{
                    System.out.println("CANCELANDO OPERACIÓN");
                    Stage ventanaEmergente =(Stage) v.btncancelar.getScene().getWindow();
                    ventanaEmergente.close();
                });
                v.btnaceptar.setOnAction(e ->{
                    if (this.tabActual.equals(tabUsuarios)) {
                        if(this.tablaUsuarios.getSelectionModel().getSelectedItem()!=null){
                            Usuario usuarioBorrado = this.tablaUsuarios.getSelectionModel().getSelectedItem();
                            if (this.prestamosOL.stream().noneMatch( p -> p.getDNI().equals(usuarioBorrado.getDNI()))) {
                                    realizarConexion();

                                    String sql = "DELETE FROM USUARIO WHERE dni = ?";

                                try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                                    ps.setString(1, usuarioBorrado.getDNI());

                                    int filas = ps.executeUpdate();

                                    if (filas > 0) {
                                        System.out.println("Registro eliminado correctamente");
                                        this.usuariosOL.remove(usuarioBorrado);
                                    } else {
                                        System.out.println("No se encontró ningún registro con ese DNI");
                                    }

                                } catch (SQLException error) {
                                    error.printStackTrace();
                                }finally{
                                    Stage ventanaEmergente =(Stage) v.btncancelar.getScene().getWindow();
                                    ventanaEmergente.close();
                                    cerrarConexion();
                                }
                            }else{
                                Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                                alerta.setTitle("Usuario ya registrado");
                                alerta.setHeaderText("Operación no permitida");
                                alerta.setContentText("Este usuario aparece en la tabla de préstamos.");
                                alerta.showAndWait();
                            }
                            
                        }
                    }
                    if (this.tabActual.equals(tabPrestamos)) {
                        if (this.tablaPrestamos.getSelectionModel().getSelectedItem() != null) {
                            realizarConexion();
                            Prestamo prestamoBorrado = this.tablaPrestamos.getSelectionModel().getSelectedItem();
                             String sql = "DELETE FROM PRESTAMO WHERE id_prestamo=?";

                            try (PreparedStatement ps = conexion.prepareStatement(sql)) {

                                ps.setInt(1,prestamoBorrado.getIdPrestamo());

                                if (ps.executeUpdate()>0) {
                                    this.prestamosOL.remove(prestamoBorrado);
                                }
                                
                                
 

                            } catch (SQLException error) {
                                error.printStackTrace();

                            }finally{
                                Stage ventanaEmergente =(Stage) v.btncancelar.getScene().getWindow();
                                ventanaEmergente.close();
                                cerrarConexion();
                            }
                        }
                    }
                    if (this.tabActual.equals(tabLibros)) {

                        if(this.anchorPaneSeleccionado!=null){
                            
                            Libro libroBorrado = this.modeloAPasar instanceof Libro l ? l : null;
                            if (prestamosOL.stream().noneMatch(p -> p.getISBN().equals(libroBorrado.getISBN()))) {
                                realizarConexion();
                                String sql = "DELETE FROM LIBRO WHERE ISBN = ?";

                                try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                                    ps.setString(1, libroBorrado.getISBN());

                                    int filas = ps.executeUpdate();

                                    if (filas > 0) {
                                        System.out.println("Registro eliminado correctamente");
                                        this.librosOL.remove(libroBorrado);
                                        this.rellenarLibros(this.consultarTabla("LIBRO"));
                                        this.anchorPaneSeleccionado = null;
                                    } else {
                                        System.out.println("No se encontró ningún registro con ese ISBN");
                                    }

                                } catch (SQLException error) {
                                    error.printStackTrace();
                                }finally{
                                    Stage ventanaEmergente =(Stage) v.btncancelar.getScene().getWindow();
                                    ventanaEmergente.close();
                                    cerrarConexion();
                                }
                            }else{
                                Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                                alerta.setTitle("Libro ya registrado");
                                alerta.setHeaderText("Operación no permitida");
                                alerta.setContentText("Este Libro aparece en la tabla de préstamos.");
                                alerta.showAndWait();
                            }
                            
                        }
                    }
                });
                
            }
        }
        ventanaModal = ventana;
        ventana.setScene(scene);
        ventana.setResizable(false);
        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.show();
    }
    private ResultSet consultarTabla(String nombreTabla){
        ResultSet resultado = null;
        if (tablasValidas.contains(nombreTabla) && this.conexion != null) {
            try{
            String sql = "SELECT * FROM "+nombreTabla;
            
            
            Statement Statement = conexion.createStatement();
            resultado = Statement.executeQuery(sql);
            
            }catch(SQLException e){
                System.out.println(e.getMessage());
            }
        }else{
            System.out.println("ERROR, LA TABLA NO EXISTE");
        }
        
        return resultado;
    }
    private void rellenarUsuarios(ResultSet usuarios){
        
        this.usuariosOL.clear();
        try{
            if (usuarios != null && this.conexion != null) {
            while (usuarios.next()) {
            String nombre = usuarios.getString("nombre");
            String apellidos = usuarios.getString("apellido1")+" "+usuarios.getString("apellido2");
            String DNI = usuarios.getString("DNI");
            String Foto = usuarios.getString("FOTO");
            String telefono = usuarios.getString("Telefono");
            String fecha_nac = usuarios.getString("Fecha_nacimiento");
            String sexo = usuarios.getString("sexo");
            Usuario u =new Usuario(DNI, nombre, apellidos, Foto, sexo, telefono, fecha_nac);
            usuariosOL.add(u);

                
        }
        }else{
            System.out.println("Error, LA CONSULTA DEVOLVIO NULL");
        }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
       
    }
    private void rellenarPrestamos(ResultSet prestamos){
        this.prestamosOL.clear();
        try{
            if (prestamos != null && this.conexion != null) {
            while (prestamos.next()) {
            int idPrestamo = prestamos.getInt("id_prestamo");
            String DNI = prestamos.getString("DNI");
            boolean Entregado =  prestamos.getBoolean("buenestado");
            String ISBN = prestamos.getString("ISBN");
            String fecha_inicio = prestamos.getString("fecha_inicio");
            String fecha_devolucion = prestamos.getString("fecha_devolucion");
            int dias_restantes = prestamos.getInt("dias_restantes");
            Prestamo p1 = Prestamo.crear(idPrestamo,DNI, ISBN, fecha_inicio, fecha_devolucion, dias_restantes, Entregado);
                if (p1!=null) {
                    prestamosOL.add(p1);
                }else{
                    System.out.println("ERROR, EL PRESTAMO CONTIENE UNA CLAVE PRIMARIA NO EXISTENTE");
                }
        }
        }else{
            System.out.println("ERROR LA CONSULTA DEVOLVIO NULL O LA CONEXION ES NULA");
        }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    private void rellenarLibros(ResultSet libros){
        librosOL.clear();
        try{
            if (libros != null) {
            while (libros.next()) {
                String ISBN = libros.getString("ISBN");
                String titulo = libros.getString("Titulo");
                String autor = libros.getString("Autor");
                String estado = libros.getString("Estado");
                int Nro_paginas = libros.getInt("Nro_paginas");
                String imagen_portada = libros.getString("imagen_portada");
                String generos = libros.getString("Generos");
                    
                Libro l =  new Libro(ISBN, titulo, autor, estado, Nro_paginas, imagen_portada, generos);
                librosOL.add(l);
            }
            this.rellenarGrid();
        }else{
            System.out.println("ERROR, LA CONSULTA DEVOLVIO NULL O LA CONEXION ES NULA");
            this.tabLibros.getContent().setCursor(Cursor.CLOSED_HAND);
        }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        
    }
    
    public void cerrarConexion(){
        if (conexion!=null){
            try {
                conexion.close();
                System.out.println("Conexión cerrada");

            }catch (SQLException e){
                System.out.println(e.getMessage());
            }
        }else{
            System.out.println("Error, no se ha seleccionado una conexión aún");
        }

    }
    private void rellenarGrid(){
        this.fila = 0;
        this.columna= 0;
        this.librosGridPane.getChildren().clear();
        this.listafiltradaLibros.forEach( (libro) ->{
            
                AnchorPane root = new AnchorPane();
                
                root.getStyleClass().remove("card-selected");
                root.getStyleClass().add("card"); // clase seleccionado
                root.setOnMouseClicked(e -> {
                    if (e.getClickCount()==2) {
                        try{
                            ActionEvent evento = new ActionEvent();
                            editarRegistro(evento);
                        }catch(IOException error){
                            System.out.println(error.getMessage());
                        }
                    }
                    this.modeloAPasar=libro;
                    this.anchorPaneSeleccionado = root;
                    
                    System.out.println("AnchorPane seleccionado");
                    this.librosGridPane.getChildren().forEach(
                        pane -> {
                            pane.getStyleClass().remove("card-selected");
                            if (!pane.getStyleClass().contains("card")) {
                                pane.getStyleClass().add("card");
                            }
                        }
                    );
                    root.getStyleClass().remove("card");
                    root.getStyleClass().add("card-selected"); // clase seleccionado

                });
                root.setPrefSize(150, 180); // ancho x alto fijo
                root.setMinSize(150, 180);
                root.setMaxSize(150, 180);
                
                VBox card = new VBox(10); 
                
                card.setPadding(new Insets(20));
                card.setAlignment(Pos.CENTER);
                Label labelPrincipal = new Label(libro.getTitulo());
                labelPrincipal.setFont(Font.font("Arial", 14));
                Label labelSecundario = new Label(libro.getAutor());
                labelSecundario.setFont(Font.font("Arial", 10));
                labelSecundario.setTextFill(Color.GRAY);
                ImageView imageView = libro.getImagen_portada();
                Button boton = new Button("Mostrar datos");
                
                
                boton.setOnAction((e) -> {
                    try{
                        this.modeloAPasar = libro;
                        Stage ventana = crearVentana("/card.fxml", "Crear Card");
                        ventana.show();
                       
                    }catch(IOException a){
                        System.out.println(a.getMessage());
                    }
                });
                card.getChildren().addAll(labelPrincipal, labelSecundario, imageView, boton);
                
                AnchorPane.setTopAnchor(card, 0.0);
                AnchorPane.setBottomAnchor(card, 0.0);
                AnchorPane.setLeftAnchor(card, 0.0);
                AnchorPane.setRightAnchor(card, 0.0);
                card.setAlignment(Pos.CENTER);
                root.getChildren().add(card);
                this.librosGridPane.add(root, columna, fila);
                this.columna++;
                if (this.columna>3) {
                    this.columna=0;
                    this.fila++;
                }
            
        });
    }
    private void realizarConexion(){
        Properties propertyDatabase = new Properties();
        try{
            propertyDatabase.load(getClass().getClassLoader().getResourceAsStream("database.properties"));
        }catch(IOException e ){
            System.out.println(e.getMessage());
        }
        String url = propertyDatabase.getProperty("db.url");
        String user = propertyDatabase.getProperty("db.user");
        String pass = propertyDatabase.getProperty("db.password");
        try{
            this.conexion = DriverManager.getConnection(url, user, pass);
            System.out.println("CONEXION CREADA CON EXITO");
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //CREACIÓN MENÚS CONTEXTUALES
        this.menuContextualTablasCards= new ContextMenu();
        MenuItem crearItem = new MenuItem("Crear");
        MenuItem filtrarItem = new MenuItem("Filtrar");
        MenuItem eliminarFiltroItem = new MenuItem("Eliminar filtro");
        MenuItem eliminarItem = new MenuItem("Eliminar Registro");
        MenuItem editarItem = new MenuItem("Editar Registro");

        crearItem.setOnAction(E -> {
            try{
                crearRegistro(E);
            }catch(IOException e){
                System.out.println(e.getMessage());
            }
        });
         filtrarItem.setOnAction(E -> {
            try{
                abrirFiltrar(E);
            }catch(IOException e){
                System.out.println(e.getMessage());
            }
        });
        eliminarFiltroItem.setOnAction(E ->borrarFiltro(E));
        eliminarItem.setOnAction(E -> eliminarRegistro(E));
        eliminarItem.setDisable(true);
        editarItem.setDisable(true);
        editarItem.setOnAction(E -> {
            try{
                editarRegistro(E);
            }catch(IOException e){
                System.out.println(e.getMessage());
            }
        });
        this.tablaUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            eliminarItem.setDisable(newSelection == null);
            editarItem.setDisable(newSelection == null);

        });
        this.tablaPrestamos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            editarItem.setDisable(newSelection == null);
            eliminarItem.setDisable(newSelection == null);
        });
        
        

        menuContextualTablasCards.getItems().addAll(crearItem, filtrarItem, eliminarFiltroItem,eliminarItem,editarItem);

        
        //CONFIGURACIÓN DE LAS TABLAS
        
        usuariosOL = FXCollections.observableArrayList();
        prestamosOL = FXCollections.observableArrayList();
        librosOL = FXCollections.observableArrayList();

        listafiltradaUsuarios = new FilteredList<>(usuariosOL, u -> true);
        listafiltradaPrestamos = new FilteredList<>(prestamosOL, p -> true);
        listafiltradaLibros = new FilteredList<>(librosOL, l -> true);
        listafiltradaLibros.predicateProperty().addListener((observable,newvalue,oldvalue) -> {
            this.rellenarGrid();
        });
        this.librosGridPane.setPadding(new Insets(20, 20, 20, 20)); 
        this.librosGridPane.setHgap(20); 
        this.librosGridPane.setVgap(20); 
        //  USUARIOS
        //INICIALIZAMOS COLUMNAS
        tablaUsuarios.setItems(listafiltradaUsuarios);
        tablaUsuarios.setContextMenu(menuContextualTablasCards);
        usuariosNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        usuariosApellido.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        usuariosDNI.setCellValueFactory(new PropertyValueFactory<>("DNI"));
        usuariosFoto.setCellValueFactory(new PropertyValueFactory<>("foto"));
        usuariosSexo.setCellValueFactory(new PropertyValueFactory<>("sexo"));
        usuariosTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        usuariosFechaNacimiento.setCellValueFactory(new PropertyValueFactory<>("fecha_nacimiento"));
        
        //PRESTAMOS
        //INICIALIZAMOS COLUMNAS
            tablaPrestamos.setItems(listafiltradaPrestamos);
            tablaPrestamos.setContextMenu(menuContextualTablasCards);
            prestamosID.setCellValueFactory(new PropertyValueFactory<>("idPrestamo"));
            prestamosDNI.setCellValueFactory(new PropertyValueFactory<>("DNI"));
            prestamosEntregado.setCellValueFactory(new PropertyValueFactory<>("imagenEntrega"));
            prestamosISBN.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
            prestamosFechaDevolucion.setCellValueFactory(new PropertyValueFactory<>("fecha_devolucion"));
            prestamosFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fecha_inicio"));
            prestamosDiasRestantes.setCellValueFactory(new PropertyValueFactory<>("dias_restantes"));
           
        //LIBROS
        this.tabLibros.setContextMenu(menuContextualTablasCards);
        
     //CONEXION A LA BBDD
    
        //RELLENAMOS TODAS LAS TABLAS Y CARDS
        realizarConexion();
        ResultSet usuarios = consultarTabla("USUARIO");
        rellenarUsuarios(usuarios);

        ResultSet libros = consultarTabla("LIBRO");
        rellenarLibros(libros);
        
        Prestamo.setListas(usuariosOL, librosOL);


        ResultSet prestamos = consultarTabla("PRESTAMO");
        rellenarPrestamos(prestamos);

        
        cerrarConexion();
        
        
    
        

        //DESPUES 
        
    
        
        //FUNCIONALIDAD EDITAR DOBLECLICK
        tablaUsuarios.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
                if (seleccionado != null) {
                    ActionEvent evento= new ActionEvent();
                    try{
                        editarRegistro(evento);                        
                    }catch(IOException e){
                        System.out.println(e.getMessage());
                    }
                }
            }
        });
        tablaPrestamos.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                Prestamo seleccionado = tablaPrestamos.getSelectionModel().getSelectedItem();
                if (seleccionado != null) {
                    ActionEvent evento= new ActionEvent();
                    try{
                        editarRegistro(evento);                        
                    }catch(IOException e){
                        System.out.println(e.getMessage());
                    }
                }
            }
        });

//CONFIGURACION DE NAVEGABILIDAD ENTRE PESTAÑAS
        this.tabActual = this.tabUsuarios;
       tabPane.getSelectionModel().selectedItemProperty().addListener((observable, viejo, nuevo) -> {
            if (nuevo != null) {
                this.tabActual = nuevo;
                if (this.anchorPaneSeleccionado!=null) {
                    this.anchorPaneSeleccionado.getStyleClass().remove("card-selected");
                    this.anchorPaneSeleccionado.getStyleClass().add("card");
                }
                this.anchorPaneSeleccionado=null;
                if (nuevo.equals(tabUsuarios) && this.tablaUsuarios.getSelectionModel().getSelectedItem()!=null) {
                    editarItem.setDisable(false);
                    eliminarItem.setDisable(false);

                }else{
                    editarItem.setDisable(true);
                    eliminarItem.setDisable(true);
                }
                if (nuevo.equals(tabPrestamos) && this.tablaPrestamos.getSelectionModel().getSelectedItem()!=null) {
                    editarItem.setDisable(false);
                    eliminarItem.setDisable(false);

                }else{
                    editarItem.setDisable(true);
                    eliminarItem.setDisable(true);
                }
                if (nuevo.equals(tabLibros) ) {
                    editarItem.setDisable(false);
                    eliminarItem.setDisable(false);

                }else{
                    editarItem.setDisable(true);
                    eliminarItem.setDisable(true);
                }


                if (this.tabActual.equals(pestañaSalir)) {
                    Stage ventana = (Stage)tabPane.getScene().getWindow();
                    try{
                        
                        mostrarConfirmacion("¿Está seguro de cerrar el programa?", "Salir del programa",TipoVentana.SALIR);
                        
                    }catch(IOException e){
                        System.out.println("Problema al cargar la imagen:");
                        System.out.println(e.getMessage());
                    }
                }
                System.out.println(this.tabActual.getId());
            }
        });
    
    }
}
