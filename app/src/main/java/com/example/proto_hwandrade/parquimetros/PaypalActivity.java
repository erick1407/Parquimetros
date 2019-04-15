package com.example.proto_hwandrade.parquimetros;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

public class PaypalActivity extends AppCompatActivity {
    private static final int PAYPAL_REQUEST_CODE = 7171;
    private static PayPalConfiguration config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX).clientId(Config.PAYPAL_CLIENT_ID);
    TextView textViewMont;
    Button buttonPay;
    String monto, montoA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal);

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        Bundle bundle = this.getIntent().getExtras();
        montoA = bundle.getString("montopay");

        textViewMont = (TextView) findViewById(R.id.textViewMont);
        buttonPay = (Button) findViewById(R.id.buttonPay);

        textViewMont.setText(montoA);

        buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processPayment();
            }
        });
    }



    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAYPAL_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null){
                    try{
                        String paymentDetails = confirmation.toJSONObject().toString(4);
                        startActivity(new Intent(this, PaymentDetails.class).putExtra("PaymentDetails", paymentDetails).putExtra("PaymentAmount", monto));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }else if (resultCode == Activity.RESULT_CANCELED){
                Dialog.show(this, "Pago Cancelado", "Si no Efectua el pago no podra Rentar el Parquimetro");
            }
        }else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID){
            Dialog.show(this,"Datos de Pago Invalidos", "Pago Invalido o Datos Incorrectos");
        }
    }

    private void processPayment() {
        monto = textViewMont.getText().toString();
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(monto)),"MXN","Pagar a Digipark", PayPalPayment.PAYMENT_INTENT_ORDER);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }
}

