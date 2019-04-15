package com.example.proto_hwandrade.parquimetros;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentDetails extends AppCompatActivity {
    TextView textViewID, textViewMonto, textViewStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);
        textViewID = (TextView) findViewById(R.id.textViewID);
        textViewMonto = (TextView) findViewById(R.id.textViewMonto);
        textViewStatus = (TextView) findViewById(R.id.textViewStatus);

        Intent intent = getIntent();

        try{
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("PaymentDetails"));
            showDetails(jsonObject.getJSONObject("response"), intent.getStringExtra("PaymentAmount"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showDetails(JSONObject response, String paymentAmount) {
        try {
            textViewID.setText(response.getString("id"));
            textViewStatus.setText(response.getString("state"));
            textViewMonto.setText(response.getString(String.format("$" + paymentAmount)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

