package com.example.proto_hwandrade.parquimetros;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ganfra.materialspinner.MaterialSpinner;

public class VehiclesActivity extends AppCompatActivity {
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    EditText editTextVehicleId, editTextPlaca, editTextModelo;
    Spinner spinnerMarca, spinnerColor;
    String marcaV, colorV;
    ListView listView;
    ProgressBar progressBar;
    Button buttonAddUpdate;

    List<Vehiculo> vehiculoList;

    boolean isUpdating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicles);

        editTextVehicleId = (EditText) findViewById(R.id.editTextVehicheId);
        editTextPlaca = (EditText) findViewById(R.id.editTextPlaca);
        editTextModelo = (EditText) findViewById(R.id.editTextModelo);

        buttonAddUpdate = (Button) findViewById(R.id.buttonAddUpdate);

        spinnerMarca = (Spinner) findViewById(R.id.spinnerMarca);
        spinnerColor = (Spinner) findViewById(R.id.spinnerColor);

        //spinnerMarca = (MaterialSpinner) findViewById(R.id.spinnerMarca);
        //List<String> marca = new ArrayList<String>();
        /*marca.add("Acura");
        marca.add("Audi");
        marca.add("BMW");
        marca.add("Cadillac");
        marca.add("Chevrolet");
        marca.add("Chrysler");
        marca.add("Dodge");
        marca.add("FIAT");
        marca.add("Ford");
        marca.add("Honda");
        marca.add("Hyundai");
        marca.add("Infiniti");
        marca.add("Jaguar");
        marca.add("Jeep");
        marca.add("KIA");
        marca.add("Land Rover");
        marca.add("Mazda");
        marca.add("Mercedes Benz");
        marca.add("Mitsubishi");
        marca.add("Nissan");
        marca.add("Peugeot");
        marca.add("Porsche");
        marca.add("Renault");
        marca.add("SEAT");
        marca.add("Tesla");
        marca.add("Toyota");
        marca.add("Volkswagen");
        marca.add("Volvo");
        marca.add("Otra");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, marca);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMarca.setBaseColor(R.color.colorWhite);
        spinnerMarca.setAdapter(adapter);

        spinnerColor = (MaterialSpinner) findViewById(R.id.spinnerColor);
        List<String> color = new ArrayList<String>();
        color.add("Amarillo");
        color.add("Azul");
        color.add("Beige");
        color.add("Blanco");
        color.add("Gris");
        color.add("Marrón");
        color.add("Naranja");
        color.add("Negro");
        color.add("Oro");
        color.add("Plata");
        color.add("Rojo");
        color.add("Turquesa");
        color.add("Verde");

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, color);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerColor.setBaseColor(R.color.colorWhite);
        spinnerColor.setAdapter(adapter1);*/

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        listView = (ListView) findViewById(R.id.listViewVehiculos);

        vehiculoList = new ArrayList<>();

        readVehicles();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MenuActivity.class));
        finish();
    }

    public void addVehicle(View view){
        String vehiPattern = "[A-Z0-9]+";
        if (editTextPlaca.getText().toString().matches(vehiPattern) && editTextPlaca.getText().toString().length() >0){
            marcaV = spinnerMarca.getSelectedItem().toString();
            if (marcaV.equals("Marca")){
                Dialog.show(this, "Campos Vacios", "Debe Seleccionar una Marca");
            }else {
                String modePattern = "[a-zA-Z 0-9]+";
                if (editTextModelo.getText().toString().matches(modePattern)&& editTextModelo.getText().toString().length() >0){
                    colorV = spinnerColor.getSelectedItem().toString();
                    if (colorV.equals("Color")){
                        Dialog.show(this, "Error", "Debe Seleccionar un Color");
                    }else{
                        if (isUpdating){
                            updateVehicle();
                        }else {
                            createVehicle();
                        }
                    }
                }else {
                    Dialog.show(this, "Error", "Ingrese el Modelo del Auto Correctamente");
                }
            }
        }else {
            Dialog.show(this, "Error", "Ingrese la Placa Vehiclar de su Auto \n Ejemplo (EJ54WE71)");
        }
    }
    public void clearMatches(){
        editTextPlaca.getText().clear();
        spinnerMarca.setSelection(0, true);
        editTextModelo.getText().clear();
        spinnerColor.setSelection(0, true);
    }

    private void createVehicle(){
        String placavehicular = editTextPlaca.getText().toString().trim();
        String marca = spinnerMarca.getSelectedItem().toString().trim();
        String modelo = editTextModelo.getText().toString().trim();
        String color = spinnerColor.getSelectedItem().toString().trim();

        HashMap<String, String> params = new HashMap<>();
        params.put("placavehicular", placavehicular);
        params.put("marca", marca);
        params.put("modelo", modelo);
        params.put("color", color);

        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CREATE_VEHI, params, CODE_POST_REQUEST);
        request.execute();
        clearMatches();

    }

    private void readVehicles(){
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_VEHICL, null, CODE_GET_REQUEST);
        request.execute();
    }

    private void updateVehicle(){
        String idvehicle = editTextVehicleId.getText().toString();
        String placavehicular = editTextPlaca.getText().toString().trim();
        String marca = spinnerMarca.getSelectedItem().toString().trim();
        String modelo = editTextModelo.getText().toString().trim();
        String color = spinnerColor.getSelectedItem().toString().trim();

        HashMap<String, String> params = new HashMap<>();
        params.put("idvehicle", idvehicle);
        params.put("placavehicular", placavehicular);
        params.put("marca", marca);
        params.put("modelo", modelo);
        params.put("color", color);

        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_UPDATE_VEHI, params, CODE_POST_REQUEST);
        request.execute();

        buttonAddUpdate.setText("Agregar");

        clearMatches();
        isUpdating = false;

    }

    private void deleteVehicle(int idvehicle){
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_DELETE_VEHI + idvehicle,null, CODE_GET_REQUEST);
        request.execute();
    }

    private void refreshVechicleList(JSONArray vehicles) throws JSONException{
        vehiculoList.clear();
        for (int i = 0; i < vehicles.length(); i++){
            JSONObject obj = vehicles.getJSONObject(i);
            vehiculoList.add(new Vehiculo(
                    obj.getInt("idvehicle"),
                    obj.getString("placavehicular"),
                    obj.getString("marca"),
                    obj.getString("modelo"),
                    obj.getString("color")
            ));
        }

        VehicleAdapter adapter = new VehicleAdapter(vehiculoList);
        listView.setAdapter(adapter);
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
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            try{
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")){
                    Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    refreshVechicleList(object.getJSONArray("vehicles"));
                }
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();
            if (requestCode == CODE_POST_REQUEST){
                return requestHandler.sendPostRequest(url, params);
            }

            if (requestCode == CODE_GET_REQUEST){
                return requestHandler.sendGetRequest(url);
            }

            return null;
        }
    }

    class VehicleAdapter extends ArrayAdapter<Vehiculo>{
        List<Vehiculo> vehiculoList;

        public VehicleAdapter(List<Vehiculo> vehiculoList){
            super(VehiclesActivity.this, R.layout.layout_vehicle_list, vehiculoList);
            this.vehiculoList = vehiculoList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.layout_vehicle_list, null, true);
            TextView textViewCar = listViewItem.findViewById(R.id.textViewCar);
            ImageView imageViewUpda = listViewItem.findViewById(R.id.imageEditV);
            ImageView imageViewDele = listViewItem.findViewById(R.id.imageDeleteV);

            final Vehiculo vehiculo = vehiculoList.get(position);
            textViewCar.setText(vehiculo.getModelo());
            imageViewUpda.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isUpdating = true;
                    editTextVehicleId.setText(String.valueOf(vehiculo.getIdvehicle()));
                    editTextPlaca.setText(vehiculo.getPlacavehicular());
                    spinnerMarca.setSelection(((ArrayAdapter<String>) spinnerMarca.getAdapter()).getPosition(vehiculo.getMarca()));
                    editTextModelo.setText(vehiculo.getModelo());
                    spinnerColor.setSelection(((ArrayAdapter<String>) spinnerColor.getAdapter()).getPosition(vehiculo.getColor()));
                    buttonAddUpdate.setText("Modificar");
                }
            });

            imageViewDele.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(VehiclesActivity.this);
                    builder.setTitle("Eliminar " + vehiculo.getModelo())
                            .setMessage("¿Estas Seguro de Eliminar este Vehiculo?")
                            .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteVehicle(vehiculo.getIdvehicle());
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
}
