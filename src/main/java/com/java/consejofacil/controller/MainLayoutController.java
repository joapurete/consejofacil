package com.java.consejofacil.controller;

import com.java.consejofacil.config.StageManager;
import com.java.consejofacil.view.FXMLView;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class MainLayoutController implements Initializable {
    // BorderPane del contenedor principal
    @FXML
    private BorderPane bpMainLayout;

    // Stage Manager
    @Autowired
    @Lazy
    private StageManager stageManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void cambiarCentro(FXMLView view)  {
        // Obtenemos el nodo raiz del centro actual del contenedor
        Node centroActual = bpMainLayout.getCenter();
        // Obtenemos el nodo raiz del nuevo centro a colocar en el contenedor
        Node nuevoCentro = stageManager.loadView(view.getFxmlFile());

        // Cambiamos el título de la ventana principal
        stageManager.setTitle(view.getTitle());
        // Establecemos la opacidad en 0 del nuevo centro para que aparezca inicialmente invisible
        nuevoCentro.setOpacity(0);

        // Creamos una transición de desvanecimiento para cambiar el centro del contenedor
        FadeTransition fadeOut = crearTransition(centroActual, nuevoCentro);
        // Iniciamos la transicion de salida
        fadeOut.play();
    }

    private FadeTransition crearTransition(Node centroActual, Node nuevoCentro) {
        // Creamos una transición de desvanecimiento de salida para el centro actual
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.3), centroActual);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(event -> {
            // Al terminar el desvanecimiento de salida, establecemos el nuevo centro (invisible)
            bpMainLayout.setCenter(nuevoCentro);

            // Creamos una transición de desvanecimiento de entrada para el nuevo centro
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.3), nuevoCentro);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            // Iniciamos la transicion de entrada
            fadeIn.play();
        });

        // Devolvemos la transicion de salida
        return fadeOut;
    }
}
