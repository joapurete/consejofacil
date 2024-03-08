package com.java.consejofacil.config;

import javafx.stage.Stage;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

// Declaramos la clase como un evento de aplicacion
@Getter
public class StageReadyEvent extends ApplicationEvent {

    // Ventana que está lista para ser mostrada
    private final Stage stage;

    public StageReadyEvent(Stage stage) {
        // LLamamos al constructor de la clase base Application Event y le pasamos como argumento el stage
        // Esto inicializa el evento con el stage asociado
        super(stage);

        this.stage = stage;
    }
}
