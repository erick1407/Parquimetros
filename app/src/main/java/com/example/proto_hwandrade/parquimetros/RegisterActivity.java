package com.example.proto_hwandrade.parquimetros;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    String email;
    String emailPattern;
    private  EditText userName, userMail, password, passwordC, aPaterno, aMaterno;
    private CheckBox checkBoxPass;
    //private static String URL_REG = "http://192.168.0.24/Parquimetros/register.php";
    private static String URL_REG = "http://192.168.43.22/Parquimetros/register.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userMail = (EditText) findViewById(R.id.editTextMailReg);
        password = (EditText) findViewById(R.id.editTextPassReg);
        passwordC = (EditText) findViewById(R.id.editTextPassRegC);
        userName = (EditText) findViewById(R.id.editTextNameReg);
        aPaterno = (EditText) findViewById(R.id.editTextApater);
        aMaterno = (EditText) findViewById(R.id.editTextAmater);

        checkBoxPass = (CheckBox) findViewById(R.id.checkBoxShowPR);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void saveRegister(View view){
        email = userMail.getText().toString();
        emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (email.matches(emailPattern) && email.length() > 0){
            if (password.getText().toString().isEmpty() || passwordC.getText().toString().isEmpty()){
                Dialog.show(this, "Error", "Las contraseñas no deben estar vacias");
            }else {
                String namePattern = "[a-z A-Zá-ñÑ]+";
                if (userName.getText().toString().matches(namePattern) && userName.getText().toString().length() >0){
                    String apPattern = "[a-z A-Zá-ñÑ]+";
                    if (aPaterno.getText().toString().matches(apPattern) || aPaterno.getText().toString().equals("")){
                        String amPattern = "[a-z A-Zá-ñÑ]+";
                        if (aMaterno.getText().toString().matches(amPattern) || aMaterno.getText().toString().equals("")){
                            String passC = passwordC.getText().toString();
                            if (password.getText().toString().equals(passC)){
                                registrar();
                                addUserSQLite();
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
        }else{
            Dialog.show(this, "Error", "Dirección de correo electrónico no válida");
        }
    }

    public void registrar(){
        final String name = userName.getText().toString().trim();
        final String mail = userMail.getText().toString().trim();
        final String pass = password.getText().toString().trim();
        final String apat = aPaterno.getText().toString().trim();
        final String amat = aMaterno.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REG, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        builder.setTitle("Datos Guardados");
                        builder.setMessage("Los Datos del Usuario se Guardarón Correctamente");
                        builder.setIcon(R.drawable.sucess);
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onBackPressed();
                                Toast.makeText(RegisterActivity.this, "Datos Guardados", Toast.LENGTH_SHORT).show();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        userMail.getText().clear();
                        password.getText().clear();
                        passwordC.getText().clear();
                        userName.getText().clear();
                        aPaterno.getText().clear();
                        aMaterno.getText().clear();
                    }else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        builder.setTitle("Datos No Guardados");
                        builder.setMessage("Los Datos del Usuario No se Guardarón");
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
                    Toast.makeText(RegisterActivity.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", mail);
                params.put("password", pass);
                params.put("name", name);
                params.put("apellidop", apat);
                params.put("apellidom", amat);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void addUserSQLite(){
        SQLHelper sqlHelper = new SQLHelper(this);
        SQLiteDatabase sqLiteDatabase = sqlHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Email", userMail.getText().toString());
        contentValues.put("Password", password.getText().toString());
        contentValues.put("UserName", userName.getText().toString());
        contentValues.put("APaterno", aPaterno.getText().toString());
        contentValues.put("AMaterno", aMaterno.getText().toString());
        sqLiteDatabase.insert("Usuario", null, contentValues);
    }

    public void showP(View view){
        password = (EditText) findViewById(R.id.editTextPassReg);
        passwordC = (EditText) findViewById(R.id.editTextPassRegC);
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

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

}
