package com.java.consejofacil.security;

import com.java.consejofacil.model.Miembro;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SessionInfo {

    // Detalles del usuario
    private Miembro usuario;

    public SessionInfo () { }

    // Metodo para determinar si el usuario se encuentra en sesion
    public boolean inSession(){
        return usuario != null;
    }
}
