package com.java.consejofacil.config;

import com.java.consejofacil.view.FXMLView;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.slf4j.LoggerFactory.getLogger;

@SuppressWarnings("SpellCheckingInspection")
public class StageManager {

    // Logger para gestionar informacion
    private static final Logger LOG = getLogger(StageManager.class);

    // Stage principal de la aplicacion
    private final Stage primaryStage;

    // Bean para cargar las vistas FXML
    @Autowired
    private SpringFXMLLoader springFXMLLoader;

    // Mapa de modales asociados a su titulo (clave)
    @Getter
    private final Map<String, Stage> modalStages = new HashMap<>();

    public StageManager(Stage stage) {
        this.primaryStage = stage;
    }

    // Recibimos un FXMLView que contiene información sobre la vista a cargar
    public void switchScene(final FXMLView view) {
        // Cargamos la vista FXML segun su ruta
        Parent viewRoot = loadView(view.getFxmlFile());

        // Mostramos la vista FXML
        show(viewRoot, view.getTitle());
    }

    private void show(final Parent rootnode, String title) {
        // Preparamos la escena y le pasamos el nodo raiz de la vista FXML cargada
        Scene scene = prepareScene(rootnode);
        //scene.getStylesheets().add("/styles/_styles.css");
        //primaryStage.initStyle(StageStyle.TRANSPARENT);

        // Ocultamos la ventana principal para realizar las configuraciones necesarias
        primaryStage.hide();

        // Configuramos la ventana principal
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image(("/images/logo2.png")));

        try {
            // Mostramos el Stage después de la animación
            primaryStage.show();

        } catch (Exception exception) {
            logAndExit("Unable to show scene for title" + title, exception);
        }
    }

    private Scene prepareScene(Parent rootnode) {
        // Obtenemos la escena actual de la ventana principal
        Scene scene = primaryStage.getScene();

        // Si la escena es nula, creamos una nueva utilizando el nodo raiz proporcioando
        if (scene == null) {
            scene = new Scene(rootnode);
        }

        // Cambiamos el nodo raiz de la escena
        scene.setRoot(rootnode);

        // Devolvemos la escena
        return scene;
    }

    /**
     *
     */

    public void openModal(Parent rootnode, FXMLView view) {
        // Creamos un nuevo modal y le pasamos el título deseado
        Stage modalStage = createModalStage(view.getKey());

        // Mostramos el modal creado respectivo con el nodo raiz y título deseado
        showInModal(modalStage, rootnode, view.getKey(), view.getTitle());
    }

    private Stage createModalStage(String key) {
        // Creamos una nueva ventana
        Stage modalStage = new Stage();

        // Establecemos la modalidad de la ventana
        modalStage.initModality(Modality.APPLICATION_MODAL);

        // Guardamos este modal en el mapa utilizando el título como clave
        modalStages.put(key, modalStage);

        // Devolvemos el modal creado
        return modalStage;
    }

    public void closeModal(String key) {
        // Buscamos el modal correspondiente en el mapa utilizando el título como clave
        Stage modalStage = modalStages.get(key);

        // Verificamos que el modal sea diferente de nulo
        if (modalStage != null) {
            // Cerramos el mdoal
            modalStage.close();

            // Eliminamos el modal del mapa
            modalStages.remove(key);
        }
    }

    private void showInModal(Stage modalStage, Parent rootnode, String key, String title) {
        // Creamos una nueva escena a partir del nodo raiz proporcionado
        Scene scene = new Scene(rootnode);

        // Configuramos el modal proporcionado
        modalStage.setTitle(title);
        modalStage.setScene(scene);
        modalStage.sizeToScene();
        modalStage.centerOnScreen();
        modalStage.setResizable(false);
        modalStage.getIcons().add(new Image(("/images/logo2.png")));

        // Cuando se cierre el modal, automaticamente lo elimine del mapa
        modalStage.setOnCloseRequest(event ->
                closeModal(key)
        );

        // Mostramos la ventana de manera modal
        // Esto significa que el programa esperara hasta que se cierre el modal antes de continuar
        modalStage.showAndWait();
    }

    /**
     *
     */

    public Parent loadView(String fxmlFilePath) {
        // Creamos un nodo raiz nulo
        Parent rootNode = null;

        try {
            // Cargamos la vista FXML segun su ruta y devuelve el nodo raiz
            rootNode = springFXMLLoader.loadView(fxmlFilePath);

            // Verificamos que el nodo raiz sea diferente de nulo
            Objects.requireNonNull(rootNode, "A Root FXML node must not be null");

        } catch (Exception exception) {
            logAndExit("Unable to load FXML view" + fxmlFilePath, exception);
        }
        return rootNode;
    }

    public void setTitle(String title) {
        // Establecemos un título a la ventana principal
        if (primaryStage != null) {
            primaryStage.setTitle(title);
        }
    }

    private void logAndExit(String errorMsg, Exception exception) {
        // Registramos el error utilizando el Logger
        LOG.error(errorMsg, exception, exception.getCause());
        // Cerramos de la aplicacion
        Platform.exit();
    }
}
