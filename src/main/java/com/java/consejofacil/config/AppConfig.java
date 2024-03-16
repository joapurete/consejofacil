package com.java.consejofacil.config;

import com.java.consejofacil.security.SessionManager;
import com.java.consejofacil.security.SecurityConfig;
import com.java.consejofacil.security.SessionInfo;
import javafx.stage.Stage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
import java.util.ResourceBundle;

@Configuration
public class AppConfig {
    // Creamos un bean de tipo Resource Bundle
    // Este bean gestiona las propiedades de las vistas FXML
    @Bean
    public ResourceBundle resourceBundle() {
        return ResourceBundle.getBundle("views");
    }

    // Creamos un bean de tipo Stage Manager
    // Este bean administra las escenas y ventanas de la aplicacion
    @Bean
    @Lazy
    public StageManager stageManager(Stage stage) throws IOException {
        return new StageManager(stage);
    }

    @Bean
    @Lazy
    public SecurityConfig securityConfig() {
        return new SecurityConfig();
    }

    @Bean
    @Lazy
    public SessionInfo sessionInfo() {
        return new SessionInfo();
    }

    @Bean
    @Lazy
    public SessionManager sessionController() { return new SessionManager(); }
}
