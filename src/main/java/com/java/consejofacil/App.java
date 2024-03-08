package com.java.consejofacil;

import com.java.consejofacil.config.SpringApplicationContext;
import com.java.consejofacil.config.StageManager;
import com.java.consejofacil.config.StageReadyEvent;
import com.java.consejofacil.view.FXMLView;
import javafx.application.Application;
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class App implements ApplicationListener<StageReadyEvent> {

    // Contenedor de beans gestionados por Spring
    @Autowired
    private ConfigurableApplicationContext springContext;

    // Stage Manager
    private StageManager stageManager;

    // Logger para gestionar informacion
    private final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(final String[] args) {
        Application.launch(SpringApplicationContext.class, args);
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        // Cargamos la tipografía
        Font.loadFont(getClass().getResourceAsStream("/font/Poppins-Medium.ttf"), 0);

        log.info("Cargando la ventana principal...");

        // Obtenemos el bean Stage Manager del contenedor de Spring
        // Le pasamos el stage obtenido del evento Stage Ready Event
        stageManager = springContext.getBean(StageManager.class, event.getStage());
        displayInitialScene();
    }

    private void displayInitialScene() {
        // Cambiamos la escena del stage principal
        stageManager.switchScene(FXMLView.Login);
    }
}
