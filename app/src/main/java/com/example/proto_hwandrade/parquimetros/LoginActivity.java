package com.example.proto_hwandrade.parquimetros;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText email,password;
    private CheckBox checkBoxSP;
    private Session session;
    //private static String URL_LOG = "http://192.168.0.24/Parquimetros/login.php";
    private static String URL_LOG = "http://192.168.43.22/Parquimetros/login.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.editTextEmailLog);
        password = (EditText) findViewById(R.id.editTextPassLog);

        checkBoxSP = (CheckBox) findViewById(R.id.checkBoxShowP);

        session = new Session(this);
        if (session.loggedin()){
            startActivity(new Intent(this, SplashActivity.class));
            finish();
        }
    }

    public void login(View view){
        StringRequest request = new StringRequest(Request.Method.POST, URL_LOG,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("1")){
                            startActivity(new Intent(getApplicationContext(),SplashActivity.class));
                            session.setLoggedin(true);
                            finish();
                            email.getText().clear();
                            password.getText().clear();
                        }else {
                           Dialog.show(LoginActivity.this,"Inicio de Sesión","Usuario o Contraseña Incorrectos");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email.getText().toString());
                params.put("password", password.getText().toString());
                return  params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    public void register(View view){
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }
    public void showPass(View view){
        password = (EditText) findViewById(R.id.editTextPassLog);
        int cursor = password.getSelectionEnd();
        if (checkBoxSP.isChecked()){
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }else{
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        password.setSelection(cursor);
    }
}
