package com.example.proto_hwandrade.parquimetros;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.santalu.maskedittext.MaskEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CardsActivity extends AppCompatActivity {
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    EditText editTextCardId, editTextTitular, editTextCVV;
    MaskEditText editTextTarjeta, editTextFecha;
    ListView listViewT;
    ProgressBar progressBarC;
    Button buttonAddUp;
    List<Tarjeta> tarjetaList;
    CheckBox checkboxCode;
    boolean isUpdating;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        editTextCardId = (EditText) findViewById(R.id.editTextCardId);
        editTextTarjeta = (MaskEditText) findViewById(R.id.editTextTarjeta);
        editTextTitular = (EditText) findViewById(R.id.editTextTitular);
        editTextFecha = (MaskEditText) findViewById(R.id.editTextFecha);
        editTextCVV = (EditText) findViewById(R.id.editTextCVV);

        listViewT = (ListView) findViewById(R.id.listViewTarjetas);
        progressBarC = (ProgressBar) findViewById(R.id.progressBarC);
        buttonAddUp = (Button) findViewById(R.id.buttonAddUpdateC);

        checkboxCode = (CheckBox) findViewById(R.id.checkboxCode);
        tarjetaList = new ArrayList<>();
        readCards();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MenuActivity.class));
        finish();
    }

    public void functionCard(View view){
        if (editTextTarjeta.getText().toString().isEmpty()){
            Dialog.show(this, "Campos Vacios", "Ingrese el numero de Tarjeta");
        }else{
            String tituPatterns = "[a-z A-Zá-ñÑ]+";
            if (editTextTitular.getText().toString().matches(tituPatterns)){
                if (editTextFecha.getText().toString().isEmpty()){
                    Dialog.show(this, "Campos Vacios", "Ingrese la Fecha de Vencimiento de la Tarjeta");
                }else {
                    if (editTextCVV.getText().toString().isEmpty()){
                        Dialog.show(this, "Campos Vacios", "Ingrese los tres Digitos de la Parte Trasera de la Tarjeta");
                    }else{
                        String separador = editTextFecha.getText().toString();
                        int countSeparador = 0;
                        for(int x=0;x<separador.length();x++){
                            if (separador.charAt(x)=='/'){
                                countSeparador++;
                            }
                        }
                        if (countSeparador >0){
                            if (isUpdating){
                                updateCard();
                            }else {
                                createCard();
                            }
                        }else {
                            Snackbar snackbar = Snackbar.make(view,"Formato de Fecha Incorrecto (MES/AÑO)", Snackbar.LENGTH_LONG).setAction("Action", null);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundColor(Color.RED);
                            snackbar.show();
                        }
                    }
                }
            }else {
                Dialog.show(this, "Campos Vacios", "Ingrese el Nombre Completo y Correcto del Titular de la Tarjeta");
            }
        }
    }

    public void clearMatches(){
        editTextTarjeta.getText().clear();
        editTextTitular.getText().clear();
        editTextFecha.getText().clear();
        editTextCVV.getText().clear();
    }

    private void createCard(){
        String numerotarjeta = editTextTarjeta.getText().toString().trim();
        String titular = editTextTitular.getText().toString().trim();
        String fechavencimiento = editTextFecha.getText().toString().trim();
        String cvv = editTextCVV.getText().toString().trim();

        HashMap<String, String> params = new HashMap<>();
        params.put("numerotarjeta", numerotarjeta);
        params.put("titular", titular);
        params.put("fechavencimiento", fechavencimiento);
        params.put("cvv", cvv);

        PerformNetworkRequest request = new PerformNetworkRequest(ApiC.URL_CREATE_CARD, params, CODE_POST_REQUEST);
        request.execute();
        clearMatches();
    }

    private void readCards(){
        PerformNetworkRequest request = new PerformNetworkRequest(ApiC.URL_READ_CARDS, null, CODE_GET_REQUEST);
        request.execute();
    }

    private void updateCard(){
        String idcard = editTextCardId.getText().toString().trim();
        String numerotarjeta = editTextTarjeta.getText().toString().trim();
        String titular = editTextTitular.getText().toString().trim();
        String fechavencimiento = editTextFecha.getText().toString().trim();
        String cvv = editTextCVV.getText().toString().trim();

        HashMap<String, String> params = new HashMap<>();
        params.put("idcard", idcard);
        params.put("numerotarjeta", numerotarjeta);
        params.put("titular", titular);
        params.put("fechavencimiento", fechavencimiento);
        params.put("cvv", cvv);

        PerformNetworkRequest request = new PerformNetworkRequest(ApiC.URL_UPDATE_CARD, params, CODE_POST_REQUEST);
        request.execute();

        buttonAddUp.setText("Agregar");
        clearMatches();
        isUpdating = false;
    }

    private void deleteCard(int idcard){
        PerformNetworkRequest request = new PerformNetworkRequest(ApiC.URL_DELETE_CARD + idcard, null, CODE_GET_REQUEST);
        request.execute();
        clearMatches();
    }

    private void  refreshCardList(JSONArray cards) throws JSONException{
        tarjetaList.clear();
        for (int i= 0; i < cards.length(); i++){
            JSONObject obj = cards.getJSONObject(i);
            tarjetaList.add(new Tarjeta(
                    obj.getInt("idcard"),
                    obj.getString("numerotarjeta"),
                    obj.getString("titular"),
                    obj.getString("fechavencimiento"),
                    obj.getInt("cvv")
            ));
        }
        CardAdapter adapter = new CardAdapter(tarjetaList);
        listViewT.setAdapter(adapter);
    }

    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;

        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode){
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarC.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBarC.setVisibility(View.GONE);
            try{
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")){
                    Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    refreshCardList(object.getJSONArray("cards"));
                }
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandlerC requestHandler = new RequestHandlerC();
            if (requestCode == CODE_POST_REQUEST){
                return requestHandler.sendPostRequest(url, params);
            }

            if (requestCode == CODE_GET_REQUEST){
                return requestHandler.sendGetRequest(url);
            }

            return null;
        }
    }

    class CardAdapter extends ArrayAdapter<Tarjeta> {
        List<Tarjeta> tarjetaList;

        public CardAdapter(List<Tarjeta> tarjetaList){
            super(CardsActivity.this, R.layout.layout_card_list, tarjetaList);
            this.tarjetaList = tarjetaList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.layout_card_list, null, true);
            TextView textViewCard = listViewItem.findViewById(R.id.textViewCard);
            ImageView imageEdit = listViewItem.findViewById(R.id.imageEdit);
            ImageView imageDele = listViewItem.findViewById(R.id.imageDelete);

            final Tarjeta tarjeta = tarjetaList.get(position);
            textViewCard.setText(tarjeta.getNumerotarjeta());

            imageEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isUpdating = true;
                    editTextCardId.setText(String.valueOf(tarjeta.getIdcard()));
                    editTextTarjeta.setText(tarjeta.getNumerotarjeta());
                    editTextTitular.setText(tarjeta.getTitular());
                    editTextFecha.setText(tarjeta.getFechavencimiento());
                    editTextCVV.setText(String.valueOf(tarjeta.getCvv()));
                    buttonAddUp.setText("Modificar");
                }
            });

            imageDele.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CardsActivity.this);
                    builder.setTitle("Eliminar " + tarjeta.getNumerotarjeta())
                            .setMessage("¿Estas Seguro de Eliminar esta Tarjeta?")
                            .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteCard(tarjeta.getIdcard());
                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            })
                            .setIcon(R.drawable.warning)
                            .show();
                }
            });

            return listViewItem;
        }
    }

    public void showCode(View view){
        editTextCVV = (EditText) findViewById(R.id.editTextCVV);
        int cursor = editTextCVV.getSelectionEnd();
        if (checkboxCode.isChecked()){
            editTextCVV.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }else{
            editTextCVV.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        editTextCVV.setSelection(cursor);
    }
}
