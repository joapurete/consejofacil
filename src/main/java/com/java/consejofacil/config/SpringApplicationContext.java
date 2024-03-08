package com.java.consejofacil.config;

import com.java.consejofacil.App;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

@SuppressWarnings("SpellCheckingInspection")
public class SpringApplicationContext extends Application {

    // Contenedor de beans gestionados por Spring
    private ConfigurableApplicationContext springContext;

    // Logger para gestionar informacion
    private final Logger log = LoggerFactory.getLogger(SpringApplicationContext.class);

    @Override
    public void init() {
        log.info("Inicializando ConsejoFacil...");
        // Definimos un inicializador del contexto de la aplicacion
        ApplicationContextInitializer<GenericApplicationContext> initializer =
                springContext ->
                {
                    // Registramos la clase como un bean
                    springContext.registerBean(Application.class, () -> SpringApplicationContext.this);
                };

        // Creamos un constructor de la aplicacion Spring
        // Utilizado para crear y configurar el contexto de la aplicacion
        springContext = new SpringApplicationBuilder()
                // Establecemos la clase principal de la aplicacion
                .sources(App.class)
                // Establecemos el inicializador de la aplicacion
                .initializers(initializer)
                // Ejecutamos la aplicacion Spring
                // Esto inicia el contexto de la aplicacion y carga todos los beans
                .run(getParameters().getRaw().toArray(new String[0])); // iniciamos la aplicacion Spring
    }

    @Override
    public void start(Stage primaryStage) {
        log.info("Iniciando ConsejoFacil...");
        // Publicamos un nuevo evento Stage Ready Event y es enviado a todos los listeners registrados
        // Le pasamos como parametro la ventana principal de la aplicacion
        springContext.publishEvent(new StageReadyEvent(primaryStage));
    }

    @Override
    public void stop() {
        log.info("Deteniendo ConsejoFacil...");
        // Cerramos el contenedor de Spring
        springContext.close();
        // Cerramos la aplicacion de forma ordenada
        Platform.exit();
    }
}
