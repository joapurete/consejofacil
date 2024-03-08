package com.java.consejofacil.controller;

import com.java.consejofacil.config.StageManager;
import com.java.consejofacil.view.FXMLView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class LoginController implements Initializable {
    // Stage Manager
    @Autowired
    @Lazy
    private StageManager stageManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    void login() { stageManager.switchScene(FXMLView.MainLayout); }

    @FXML
    void registrar() { }
}
