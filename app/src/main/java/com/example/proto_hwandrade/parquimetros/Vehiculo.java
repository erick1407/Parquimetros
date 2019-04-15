package com.example.proto_hwandrade.parquimetros;

public class Vehiculo {
    private int idvehicle;
    private String placavehicular;
    private String marca;
    private String modelo;
    private String color;

    public Vehiculo(int idvehicle, String placavehicular, String marca, String modelo, String color){
        this.idvehicle = idvehicle;
        this.placavehicular = placavehicular;
        this.marca = marca;
        this.modelo = modelo;
        this.color = color;
    }

    public int getIdvehicle() {
        return idvehicle;
    }

    public String getPlacavehicular() {
        return placavehicular;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public String getColor() {
        return color;
    }
}
