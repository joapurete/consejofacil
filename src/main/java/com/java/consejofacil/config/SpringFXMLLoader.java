package com.java.consejofacil.config;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.ResourceBundle;

public class SpringFXMLLoader {

    // Archivo de propiedades de las vistas FXML
    private final ResourceBundle resourceBundle;

    // Contenedor de beans gestionados por Spring
    private final ApplicationContext context;

    @Autowired
    public SpringFXMLLoader(ApplicationContext context, ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        this.context = context;
    }

    public Parent loadView(String fxmlPath) throws IOException {
        // Preparamos el loader y le pasamos la ruta del archivo FXML
        FXMLLoader loader = prepareLoader(fxmlPath);
        // Cargamos el archivo FXML segun su ruta
        // Devolvemos el nodo raiz del arbol de todos
        return loader.load();
    }

    private FXMLLoader prepareLoader(String fxmlPath){
        // Creamos un nuevo loader
        FXMLLoader loader = new FXMLLoader();

        // Configuramos el loader utilizando el contexto de la aplicacion de Spring y el Resource Bundle
        loader.setControllerFactory(context::getBean);
        loader.setResources(resourceBundle);
        loader.setLocation(getClass().getResource(fxmlPath));

        // Devolvemos el loader configurado
        return loader;
    }
}
