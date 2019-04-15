package com.example.proto_hwandrade.parquimetros;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.example.proto_hwandrade.parquimetros.ParkcarActivity.NOTIFICACION_ID;

public class AntesPaypal extends AppCompatActivity {
    String fechp;
    String claveP;
    EditText minutos;
    TextView fechap, horap, rentap, montop;
    private Spinner spinnerTarjetas;
    //private static String URL_REG_PAY = "http://192.168.0.24/Parquimetros/pago.php";
    private static String URL_REG_PAY = "http://192.168.43.22/Parquimetros/pago.php";
    ArrayList<String> tarjetas;
    ArrayList<String> parquimetros;
    ArrayList<String> vehiculos;

    private Spinner spinnerAutos, spinnerParquimetro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antes_paypal);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.cancel(NOTIFICACION_ID);

        minutos = (EditText) findViewById(R.id.editTextMrenta);

        fechap = (TextView) findViewById(R.id.fechap);
        horap = (TextView) findViewById(R.id.horap);
        montop = (TextView) findViewById(R.id.montop);

        spinnerAutos = (Spinner) findViewById(R.id.spinnerAutos);
        spinnerParquimetro = (Spinner) findViewById(R.id.spinnerParquimetro);
        spinnerTarjetas = (Spinner) findViewById(R.id.spinnerTarjetas);

        parquimetros =new ArrayList<>();
        vehiculos = new ArrayList<>();
        tarjetas = new ArrayList<>();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        fechp = simpleDateFormat.format(date);
        fechap.setText(fechp);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String dataTime = sdf.format(new Date());
        horap.setText(dataTime);

        listarV();
        listar();
        listarT();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancelar el Pago y Renta del Parquimetro");
        builder.setMessage("¿Esta Seguro de Salir y NO Rentar Parquimetro?");
        builder.setIcon(R.drawable.warning);
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(AntesPaypal.this, MenuActivity.class));
                finish();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_open_maps, menu);
        getMenuInflater().inflate(R.menu.menu_list_pagos, menu);
        return true;
    }

    public void lugares(MenuItem menuItem){
        startActivity(new Intent(this, MapsActivity.class));
        finish();
    }

    public void historial(MenuItem menuItem){
        startActivity(new Intent(this, HistoryActivity.class));
        finish();
    }

    public void generarMonto(View view){
        String tiemp = minutos.getText().toString();
        int tiem = Integer.parseInt(tiemp);
        if (tiem > 90){
            Dialog.show(this, "Tiempo Excedido", "El tiempo que usted quiere rentar es demasiado. \nIngrese menos tiempo para dar fluides al trafico vehicular");
        }else {
            if (minutos.getText().toString().isEmpty()){
                Dialog.show(this,"Sin Tiempo de Renta", "Ingrese Tiempo que desea Rentar");
            }else {
                double inputD = Double.parseDouble(minutos.getText().toString());
                double monto = (inputD * 0.168);
                montop.setText(String.format("%.2f", monto));
                //minutos.getText().clear();
            }
        }
    }

    public static String generarClave(){
        SecureRandom random = new SecureRandom();
        String text = new BigInteger(130, random).toString(32);
        return text;
    }

    public void listarT(){
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.POST, Conexion.URL_WEB_SERVICES+"listarTarjeta.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("tarjetas");
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                String numero = jsonObject1.getString("numerotarjeta");
                                String titular = jsonObject1.getString("titular");
                                tarjetas.add(numero + " " + titular);
                            }
                            spinnerTarjetas.setAdapter(new ArrayAdapter<String>(AntesPaypal.this, android.R.layout.simple_spinner_dropdown_item, tarjetas));
                        }catch (JSONException e){e.printStackTrace();}
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    public void listar(){
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.POST, Conexion.URL_WEB_SERVICES+"listarParquimetros.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("parquimetros");
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                int numero = jsonObject1.getInt("idparquimetro");
                                String calle=jsonObject1.getString("calle");
                                parquimetros.add(String.valueOf(numero) + " " + calle);
                            }
                            spinnerParquimetro.setAdapter(new ArrayAdapter<String>(AntesPaypal.this, android.R.layout.simple_spinner_dropdown_item, parquimetros));
                        }catch (JSONException e){e.printStackTrace();}
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    public void listarV(){
        RequestQueue requestQueue=Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.POST, Conexion.URL_WEB_SERVICES+"listarVehiculo.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("vehiculos");
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                String placa= jsonObject1.getString("placavehicular");
                                vehiculos.add(placa);
                            }
                            spinnerAutos.setAdapter(new ArrayAdapter<String>(AntesPaypal.this, android.R.layout.simple_spinner_dropdown_item, vehiculos));
                        }catch (JSONException e){e.printStackTrace();}
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    public void pagarCargos(View view){
        if (montop.getText().toString().equals("0")){
            Dialog.show(this,"Sin Monto", "Debe generar el monto el cual se va cobrar");
        }else {
            claveP = generarClave();
            registrarPago();
        }
    }

    public void pagarPaypal(View view){
        String montoPay = montop.getText().toString();
        if (montoPay.equals("0")){
            Dialog.show(this,"Sin Monto", "Debe generar el monto el cual se va cobrar");
        }else {
            Intent intent = new Intent(this, PaypalActivity.class);
            intent.putExtra("montopay", montoPay);
            startActivity(intent);
        }

    }

    public void addSQLite(){
        SQLHelper sqlHelper = new SQLHelper(this);
        SQLiteDatabase sqLiteDatabase = sqlHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("FechaPago", fechap.getText().toString());
        contentValues.put("HoraPago", horap.getText().toString());
        contentValues.put("Carro", spinnerAutos.getItemAtPosition(spinnerAutos.getSelectedItemPosition()).toString());
        contentValues.put("Paquimetro", spinnerParquimetro.getItemAtPosition(spinnerParquimetro.getSelectedItemPosition()).toString());
        contentValues.put("Clave", claveP);
        contentValues.put("Monto", montop.getText().toString());
        contentValues.put("TiempoRentado", minutos.getText().toString());
        contentValues.put("Tarjeta", spinnerTarjetas.getItemAtPosition(spinnerTarjetas.getSelectedItemPosition()).toString());

        sqLiteDatabase.insert("Pago", null, contentValues);
    }

    public void registrarPago(){
        final String fech = fechap.getText().toString().trim();
        final String hora = horap.getText().toString().trim();
        final String parq = spinnerParquimetro.getItemAtPosition(spinnerParquimetro.getSelectedItemPosition()).toString();
        final String tiem = minutos.getText().toString().trim();
        final String mont = montop.getText().toString().trim();
        final String plac = spinnerAutos.getItemAtPosition(spinnerAutos.getSelectedItemPosition()).toString();
        final String clav = claveP;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REG_PAY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(AntesPaypal.this);
                        builder.setTitle("Datos Guardados");
                        builder.setMessage("Los Datos del Pago se Guardarón Correctamente");
                        builder.setIcon(R.drawable.sucess);
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                addSQLite();
                                Intent intent = new Intent(AntesPaypal.this, ParkcarActivity.class);
                                intent.putExtra("tiempo", tiem);
                                startActivity(intent);
                                finish();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }else if(success.equals("0")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(AntesPaypal.this);
                        builder.setTitle("Datos No Guardados");
                        builder.setMessage("Los Datos del Pago No se Guardarón");
                        builder.setIcon(R.drawable.fail);
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AntesPaypal.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AntesPaypal.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("fechapago",fech);
                params.put("horapago", hora);
                params.put("parquimetrou", parq);
                params.put("minutosrentados", tiem);
                params.put("monto", mont);
                params.put("pvehi", plac);
                params.put("clave", clav);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
