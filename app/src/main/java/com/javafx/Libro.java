package com.javafx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Libro implements Modelo{
    private String ISBN;
    private String titulo;
    private String autor;
    private String estado;
    private int Nro_paginas;
    private ImageView imagen_portada;
    private String generos;

    
    public Libro(String iSBN, String titulo, String autor, String estado, int nro_paginas, String imagen_portada,
            String generos) {
        ISBN = iSBN;
        this.titulo = titulo;
        this.autor = autor;
        this.estado = estado;
        Nro_paginas = nro_paginas;
        //transformamos a base 64 la imagen
        byte[] imageBytes = Base64.getDecoder().decode(imagen_portada);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        Image image = new Image(bis);
        this.imagen_portada = new ImageView(image);
        this.imagen_portada.setFitHeight(64);
        this.imagen_portada.setFitWidth(48);
        this.generos = generos;
    }
    public String getISBN() {
        return ISBN;
    }
    public void setISBN(String iSBN) {
        ISBN = iSBN;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getAutor() {
        return autor;
    }
    public void setAutor(String autor) {
        this.autor = autor;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public int getNro_paginas() {
        return Nro_paginas;
    }
    public void setNro_paginas(int nro_paginas) {
        Nro_paginas = nro_paginas;
    }
    public ImageView getImagen_portada() {
        return imagen_portada;
    }
    public String getImagen_portadaB64(){
        try {
        Image image = this.imagen_portada.getImage();
        
        // Convertir Image a BufferedImage
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        
        // Convertir BufferedImage a bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        byte[] imageBytes = baos.toByteArray();
        
        // Convertir bytes a Base64
        String base64 = Base64.getEncoder().encodeToString(imageBytes);
        
        return base64;
        
    } catch (IOException e) {
        e.printStackTrace();
        return null;
    }
    }
    public void setImagen_portada(ImageView imagen_portada) {
        this.imagen_portada = imagen_portada;
    }
    public String getGeneros() {
        return generos;
    }
    public void setGeneros(String generos) {
        this.generos = generos;
    }
}
