/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package infatlan.hn.generador.prenotas.models;

/**
 *
 * @author enajera
 */
public class Trama {
    
    private String canal;
    private String agencia;
    private String moneda;
    private String cuenta;
    private String monto;
    private String referencia;
    private String descripcion;
    private String codigoTrn;
    private String llaveAdicional;
    private String debCreFlag;
    private String user;
    private String dia;
    private String mes;
    private String anio;
    private String respuesta;
    private String msg;

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCodigoTrn() {
        return codigoTrn;
    }

    public void setCodigoTrn(String codigoTrn) {
        this.codigoTrn = codigoTrn;
    }

    public String getLlaveAdicional() {
        return llaveAdicional;
    }

    public void setLlaveAdicional(String llaveAdicional) {
        this.llaveAdicional = llaveAdicional;
    }

    public String getDebCreFlag() {
        return debCreFlag;
    }

    public void setDebCreFlag(String debCreFlag) {
        this.debCreFlag = debCreFlag;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    

    @Override
    public String toString() {
        return canal + "|" + agencia + "|" + moneda + "|" + cuenta + "|" + monto + "|" + referencia + "|" + descripcion + "|" + codigoTrn + "|" + llaveAdicional + "|" + debCreFlag + "|" + user + "|" + dia + "|" + mes + "|" + anio + "|" + respuesta + "|";
    }
    
    
    
    
}
