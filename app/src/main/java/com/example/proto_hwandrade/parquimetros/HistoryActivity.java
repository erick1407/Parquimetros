package com.example.proto_hwandrade.parquimetros;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        llenadoPagos();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, AntesPaypal.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete_pagos, menu);
        return true;
    }

    public void deletePagos(MenuItem menuItem){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar Historial de Pagos");
        builder.setMessage("¿Esta seguro de elminar el historial de Pagos?");
        builder.setIcon(R.drawable.warning);
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                limpiarDB();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void llenadoPagos(){
        SQLHelper sqlHelper = new SQLHelper(this);
        SQLiteDatabase sqLiteDatabase = sqlHelper.getReadableDatabase();
        if (sqLiteDatabase!=null){
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM Pago",null);
            final List<String> list = new ArrayList<String>();
            try{
                while (cursor.moveToNext()){
                    String fec = cursor.getString(0);
                    String hor = cursor.getString(1);
                    String veh = cursor.getString(2);
                    String par = cursor.getString(3);
                    String cla = cursor.getString(4);
                    String mon = cursor.getString(5);
                    String tie = cursor.getString(6);
                    String tar = cursor.getString(7);
                    list.add("Fecha Del Pago: "+fec+ "\nHora del Pago: "+hor+"\nVehiculo: "+veh+"\nParquimetro: "+par+"\nConfirmacion: " + cla + "\nMonto Pagado: " + mon + "\nTiempo Rentado: " + tie+"\nTarjeta: " +tar);
                }
            }finally {
                cursor.close();
            }
            ListView listView = (ListView) findViewById(R.id.listViewPagos);
            ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
            listView.setAdapter(arrayAdapter);
        }else{
            Toast.makeText(this, "No hay datos", Toast.LENGTH_SHORT).show();
        }
    }

    public void limpiarDB(){
        SQLHelper sqlHelper = new SQLHelper(this);
        SQLiteDatabase sqLiteDatabase = sqlHelper.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM Pago");
        llenadoPagos();
        Dialog.show(this, "Historial de Pagos", "Historial de Pagos Eliminado");
    }
}
