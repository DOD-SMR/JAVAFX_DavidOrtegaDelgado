package com.javafx;
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
        primeraEscena.getIcons().add(new Image(getClass().getResource("/iconoPrincipal.png").toString()));
        primeraEscena.setResizable(false);
        primeraEscena.setScene(scene);
        primeraEscena.setTitle("Lybrasys");
        primeraEscena.show();
        
        
    }
}