package com.example.proto_hwandrade.parquimetros;

public class Tarjeta {
    public int getIdcard() {
        return idcard;
    }

    public String getNumerotarjeta() {
        return numerotarjeta;
    }

    public String getTitular() {
        return titular;
    }

    public String getFechavencimiento() {
        return fechavencimiento;
    }

    public int getCvv() {
        return cvv;
    }

    private int idcard;
    private String numerotarjeta;
    private String titular;
    private String fechavencimiento;
    private int cvv;

    public Tarjeta(int idcard, String numerotarjeta, String titular, String fechavencimiento, int cvv){
        this.idcard = idcard;
        this.numerotarjeta = numerotarjeta;
        this.titular = titular;
        this.fechavencimiento = fechavencimiento;
        this.cvv = cvv;
    }
}
