package com.example.proto_hwandrade.parquimetros;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLHelper extends SQLiteOpenHelper {
    public SQLHelper(Context context){
        super(context,"BaseDatosSQLite",null,14);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE 'Pago'('FechaPago' TEXT, 'HoraPago' TEXT, 'Carro' TEXT, 'Paquimetro' TEXT, 'Clave' TEXT, 'Monto' TEXT, 'TiempoRentado' TEXT, 'Tarjeta' TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE 'Usuario'('Email' TEXT PRIMARY KEY, 'Password' TEXT, 'UserName' TEXT, 'APaterno' TEXT, 'AMaterno' TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Pago");
        sqLiteDatabase.execSQL("CREATE TABLE 'Pago'('FechaPago' TEXT, 'HoraPago' TEXT, 'Carro' TEXT, 'Paquimetro' TEXT, 'Clave' TEXT, 'Monto' TEXT, 'TiempoRentado' TEXT, 'Tarjeta' TEXT)");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Usuario");
        sqLiteDatabase.execSQL("CREATE TABLE 'Usuario'('Email' TEXT PRIMARY KEY, 'Password' TEXT, 'UserName' TEXT, 'APaterno' TEXT, 'AMaterno' TEXT)");
    }
}
