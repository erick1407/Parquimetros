package com.example.proto_hwandrade.parquimetros;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserActivity extends AppCompatActivity {
    EditText userName, password, passwordC, aPater, aMater;
    TextView textViewMessa , userMail;
    private CheckBox checkBoxPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        userName = (EditText) findViewById(R.id.editTextUserU);
        userMail = (TextView) findViewById(R.id.editTextMailUserU);
        password = (EditText) findViewById(R.id.editTextPassU);
        passwordC = (EditText) findViewById(R.id.editTextPassRCU);
        aPater = (EditText) findViewById(R.id.editTextUserUAP);
        aMater = (EditText) findViewById(R.id.editTextUserUAM);

        textViewMessa = (TextView) findViewById(R.id.textViewMessa);

        checkBoxPass = (CheckBox) findViewById(R.id.checkBoxShowPRU);

        cargarDatosUser();
    }

    public void cargarDatosUser(){
        SQLHelper sqlHelper = new SQLHelper(this);
        SQLiteDatabase sqLiteDatabase = sqlHelper.getReadableDatabase();
        //sqLiteDatabase.execSQL("CREATE TABLE 'Usuario'('Email' TEXT PRIMARY KEY, 'Password' TEXT, 'UserName' TEXT, 'APaterno' TEXT, 'AMaterno' TEXT)");
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM Usuario",null);
        if (cursor.moveToFirst()){
            String mailUser = cursor.getString(0);
            String pass = cursor.getString(1);
            String user = cursor.getString(2);
            String aP = cursor.getString(3);
            String aM = cursor.getString(4);
            userMail.setText(mailUser);
            password.setText(pass);
            passwordC.setText(pass);
            userName.setText(user);
            aPater.setText(aP);
            aMater.setText(aM);
        }
    }

    public void updateRegister(View view){
        if (password.getText().toString().isEmpty() || passwordC.getText().toString().isEmpty()){
            Dialog.show(this, "Error", "Las contraseñas no deben estar vacias");
        }else {
            String namePattern = "[a-z A-Zá-ñÑ]+";
            if (userName.getText().toString().matches(namePattern) && userName.getText().toString().length() >0){
                String apPattern = "[a-z A-Zá-ñÑ]+";
                if (aPater.getText().toString().matches(apPattern) || aPater.getText().toString().equals("")){
                    String amPattern = "[a-z A-Zá-ñÑ]+";
                    if (aMater.getText().toString().matches(amPattern) || aMater.getText().toString().equals("")){
                        String passC = passwordC.getText().toString();
                        if (password.getText().toString().equals(passC)){
                            actualizar();
                            SQLHelper sqlHelper = new SQLHelper(UserActivity.this);
                            SQLiteDatabase sqLiteDatabase = sqlHelper.getWritableDatabase();
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("Password", password.getText().toString());
                            contentValues.put("UserName", userName.getText().toString());
                            contentValues.put("APaterno", aPater.getText().toString());
                            contentValues.put("AMaterno", aMater.getText().toString());
                            sqLiteDatabase.update("Usuario", contentValues, "Email = '"+ userMail.getText().toString() +"'", null);

                        }else{
                            Snackbar snackbar = Snackbar.make(view,"Las Contraseñas no Coinciden", Snackbar.LENGTH_LONG).setAction("Action", null);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundColor(Color.RED);
                            snackbar.show();
                            //Snackbar.make(view,"Las Contraseñas no Coinciden", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }
                    }else{
                        Snackbar snackbar = Snackbar.make(view,"Este no parece Apellido Materno", Snackbar.LENGTH_LONG).setAction("Action", null);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }else {
                    Snackbar snackbar = Snackbar.make(view,"Este no parece Apellido Paterno", Snackbar.LENGTH_LONG).setAction("Action", null);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(Color.RED);
                    snackbar.show();
                }
            }else {
                Snackbar snackbar = Snackbar.make(view,"Ingrese un Nombre Valido", Snackbar.LENGTH_LONG).setAction("Action", null);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(Color.RED);
                snackbar.show();
            }
        }
    }

    public void actualizar(){
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            OkHttpClient client = new OkHttpClient();

            HttpUrl.Builder urlBuilder = HttpUrl.parse(Conexion.URL_WEB_SERVICES+"update.php").newBuilder();
            urlBuilder.addQueryParameter("email", userMail.getText().toString());
            urlBuilder.addQueryParameter("password", password.getText().toString());
            urlBuilder.addQueryParameter("name", userName.getText().toString());
            urlBuilder.addQueryParameter("apellidop", aPater.getText().toString());
            urlBuilder.addQueryParameter("apellidom", aMater.getText().toString());

            String url = urlBuilder.build().toString();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                textViewMessa.setText(response.body().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            });
            Toast.makeText(getApplicationContext(),"Se Actualizo el Registro en MySQL",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Se Produjo un Error: "+e,Toast.LENGTH_SHORT).show();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Datos Actualizados");
        builder.setMessage("Los Datos se Actualizaron Correctamente");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onBackPressed();
                dialogInterface.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MenuActivity.class));
        finish();
    }

    public void showPU(View view){
        password = (EditText) findViewById(R.id.editTextPassU);
        passwordC = (EditText) findViewById(R.id.editTextPassRCU);
        int cursor = password.getSelectionEnd();
        int cursorC = passwordC.getSelectionEnd();
        if (checkBoxPass.isChecked()){
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordC.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }else {
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordC.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        password.setSelection(cursor);
        passwordC.setSelection(cursorC);
    }
}
