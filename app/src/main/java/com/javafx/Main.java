package com.javafx;
import javax.swing.text.html.StyleSheet;

import com.pixelduke.transit.Style;
import com.pixelduke.transit.TransitTheme;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;


public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primeraEscena) throws Exception { //puede lanzar excep'
        Parent root = FXMLLoader.load(getClass().getResource("/principal.fxml"));
        Scene scene = new Scene(root);
        TransitTheme transit = new TransitTheme(Style.LIGHT);
        
        transit.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toString()); 
        primeraEscena.getIcons().add(new Image(getClass().getResource("/iconoPrincipal.png").toString()));
        primeraEscena.setResizable(false);
        primeraEscena.setScene(scene);
        primeraEscena.setTitle("Lybrasys");
        primeraEscena.show();
        
        
    }
}