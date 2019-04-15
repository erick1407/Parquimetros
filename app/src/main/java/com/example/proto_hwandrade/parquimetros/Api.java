package com.example.proto_hwandrade.parquimetros;

public class Api {
    //private static final String ROOT_URL = "http://192.168.0.24/Parquimetros/API/v1/Api.php?apicall=";
    private static final String ROOT_URL = "http://192.168.43.22/Parquimetros/API/v1/Api.php?apicall=";

    public static final String URL_CREATE_VEHI = ROOT_URL + "createvehicle";
    public static final String URL_READ_VEHICL = ROOT_URL + "getvehicles";
    public static final String URL_UPDATE_VEHI = ROOT_URL + "updatevehicle";
    public static final String URL_DELETE_VEHI = ROOT_URL + "deletevehicle&idvehicle=";
}
