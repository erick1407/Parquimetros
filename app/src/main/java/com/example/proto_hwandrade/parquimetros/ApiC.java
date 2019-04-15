package com.example.proto_hwandrade.parquimetros;

public class ApiC {
    //private static final String ROOT_URL = "http://192.168.0.24/Parquimetros/API/v1/ApiC.php?apicall=";
    private static final String ROOT_URL = "http://192.168.43.22/Parquimetros/API/v1/ApiC.php?apicall=";

    public static final String URL_CREATE_CARD = ROOT_URL + "createcard";
    public static final String URL_READ_CARDS = ROOT_URL + "getcards";
    public static final String URL_UPDATE_CARD = ROOT_URL + "updatecard";
    public static final String URL_DELETE_CARD = ROOT_URL + "deletecard&idcard=";
}
