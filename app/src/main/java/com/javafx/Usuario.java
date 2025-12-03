package com.javafx;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Usuario implements Modelo{
    private String DNI;
    private String nombre;
    private String apellidos;
    private ImageView foto;
    private String sexo;
    private String telefono;
    private String fecha_nacimiento;
    public Usuario(String dNI, String nombre, String apellidos, String foto, String sexo,
            String telefono, String fecha_nacimiento) {
        DNI = dNI;
        this.nombre = nombre;
        this.apellidos = apellidos;
        //transformamos a base 64 la imagen
        byte[] imageBytes = Base64.getDecoder().decode(foto);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        Image image = new Image(bis);
        this.foto = new ImageView(image);
        this.foto.setFitHeight(64);
        this.foto.setFitWidth(32);
        this.foto.setPreserveRatio(true);
        this.sexo = sexo;
        this.telefono = telefono;
        this.fecha_nacimiento = fecha_nacimiento;
    }
    public String getDNI() {
        return DNI;
    }
    public void setDNI(String dNI) {
        DNI = dNI;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getApellidos() {
        return apellidos;
    }
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }
    public ImageView getFoto() {
        return foto;
    }
    public String getFotoB64(){
        try{
        Image image = this.foto.getImage();
        
        // Convertir Image a BufferedImage
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        
        // Convertir BufferedImage a bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        byte[] imageBytes = baos.toByteArray();
        
        // Convertir bytes a Base64
        String base64 = Base64.getEncoder().encodeToString(imageBytes);
        
        return base64;
        }catch(IOException e){
            System.out.println(e.getMessage());
            return null;
        }
        
    }
    public void setFoto(ImageView foto) {
        this.foto = foto;
    }
    public String getSexo() {
        return sexo;
    }
    public void setSexo(String sexo) {
        this.sexo = sexo;
    }
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public String getFecha_nacimiento() {
        return fecha_nacimiento;
    }
    public void setFecha_nacimiento(String fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    
}
