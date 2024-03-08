package com.java.consejofacil.config;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.ResourceBundle;

@Configuration
public class SpringFXMLConfig {

    // Creamos un bean de tipo Resource Bundle
    // Este bean carga las vistas FXML utilizando el contexto de la aplicacion
    @Bean
    public SpringFXMLLoader springFXMLLoader(ConfigurableApplicationContext springContext, ResourceBundle resourceBundle)
    {
        return new SpringFXMLLoader(springContext, resourceBundle);
    }

}
