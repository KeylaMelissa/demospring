package com.example.demospring.model;

public class RespuestaDTO {
     private String mensaje;
    private String idTransaccion;
    private boolean exitoso;

    public RespuestaDTO(String mensaje, String idTransaccion, boolean exitoso) {
        this.mensaje = mensaje;
        this.idTransaccion = idTransaccion;
        this.exitoso = exitoso;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public boolean isExitoso() {
        return exitoso;
    }

    public void setExitoso(boolean exitoso) {
        this.exitoso = exitoso;
    }
    
}
