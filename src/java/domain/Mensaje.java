/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

/**
 *
 * @author Alfon
 */
public class Mensaje {
   private String username;
   private String id;
   private String mensaje;
   private String tipoMensaje;
   private String destinatario;

    public Mensaje() {
    }

    public Mensaje(String username, String id, String mensaje, String tipoMensaje, String destinatario) {
        this.username = username;
        this.id = id;
        this.mensaje = mensaje;
        this.tipoMensaje = tipoMensaje;
        this.destinatario = destinatario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTipoMensaje() {
        return tipoMensaje;
    }

    public void setTipoMensaje(String tipoMensaje) {
        this.tipoMensaje = tipoMensaje;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    @Override
    public String toString() {
        return "Mensaje{" + "username=" + username + ", id=" + id + ", mensaje=" + mensaje + ", tipoMensaje=" + tipoMensaje + ", destinatario=" + destinatario + '}';
    }
    
    
}
