package com.java.consejofacil.helper.Componentes;

import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.control.PasswordField;

public class PasswordSkinHelper extends TextFieldSkin {

    private static final String BULLET = "•";

    public PasswordSkinHelper(PasswordField passwordField) {
        super(passwordField);
    }

    protected String maskText(String txt) {
        if (getSkinnable() instanceof PasswordField) {
            return BULLET.repeat(txt.length());
        } else {
            return txt;
        }
    }
}