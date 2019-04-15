package com.example.proto_hwandrade.parquimetros;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class ParkcarActivity extends AppCompatActivity {
    TextView timeRenta;
    String tiempo;
    String contadorTiempo;
    //Notificacion
    private PendingIntent pendingIntent;
    private PendingIntent pendingIntentSI;
    private PendingIntent pendingIntentNO;
    private final static String CHANNEL_ID = "NOTIFICACION";
    public final static int NOTIFICACION_ID = 0;

    //Contador
    private EditText mEditTextInput;
    private TextView mTextViewCountDown, textViewTiempoAConsumir;
    private Button mButtonSet;
    private Button mButtonStartPause;
    private Button mButtonReset;

    private CountDownTimer mCountDownTimer;

    private boolean mTimerRunning;

    private long mStartTimeInMillis;
    private long mTimeLeftInMillis;
    private long mEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parkcar);

        timeRenta = (TextView) findViewById(R.id.textViewTi);
        Bundle bundle = this.getIntent().getExtras();
        tiempo = bundle.getString("tiempo");
        timeRenta.setText(tiempo);

        mEditTextInput = findViewById(R.id.editTextInput);
        mTextViewCountDown = findViewById(R.id.text_view_countdown);
        textViewTiempoAConsumir = findViewById(R.id.textViewTi);

        mButtonSet = findViewById(R.id.buttonSet);
        mButtonStartPause = findViewById(R.id.buttonStartPause);
        mButtonReset = findViewById(R.id.buttonReset);

        addTime();

        iniciarreanudar();

        mButtonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = mEditTextInput.getText().toString();
                if (input.length() == 0) {
                    Toast.makeText(ParkcarActivity.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                long millisInput = Long.parseLong(input) * 60000;
                if (millisInput == 0) {
                    Toast.makeText(ParkcarActivity.this, "Please enter a positive number", Toast.LENGTH_SHORT).show();
                    return;
                }

                setTime(millisInput);
                mEditTextInput.setText("");
            }
        });



        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });

        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Regresar a Pagar");
        builder.setMessage("¿Esta Seguro de Salir ya no se hara reembolso del pago que realizo anteriormente?");
        builder.setIcon(R.drawable.warning);
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(ParkcarActivity.this, AntesPaypal.class));
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

    public void addTime(){
        String input = textViewTiempoAConsumir.getText().toString();
        long millisInput = Long.parseLong(input) * 60000;
        setTime(millisInput);
    }

    public void iniciarreanudar(){
        startTimer();
        ejecutar1();
    }

    public void detener(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.warning);
        builder.setTitle("Detener Contador");
        builder.setMessage("¿Desea Detener el Contador y Finalizar la renta del parquimetro?");
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                pauseTimer();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setTime(long milliseconds) {
        mStartTimeInMillis = milliseconds;
        resetTimer();
        closeKeyboard();
    }

    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                updateWatchInterface();
            }
        }.start();

        mTimerRunning = true;
        updateWatchInterface();
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        updateWatchInterface();
    }

    private void resetTimer() {
        mTimeLeftInMillis = mStartTimeInMillis;
        updateCountDownText();
        updateWatchInterface();
    }

    private void updateCountDownText() {
        int hours = (int) (mTimeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((mTimeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted;
        if (hours > 0) {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%02d:%02d", minutes, seconds);
        }

        mTextViewCountDown.setText(timeLeftFormatted);
    }

    private void updateWatchInterface() {
        if (mTimerRunning) {
            mEditTextInput.setVisibility(View.INVISIBLE);
            mButtonSet.setVisibility(View.INVISIBLE);
            mButtonReset.setVisibility(View.INVISIBLE);
            mButtonStartPause.setText("Pausar");
        } else {
            mEditTextInput.setVisibility(View.INVISIBLE);
            mButtonSet.setVisibility(View.INVISIBLE);
            mButtonStartPause.setText("Reanudar");

            if (mTimeLeftInMillis < 1000) {
                mButtonStartPause.setVisibility(View.INVISIBLE);
            } else {
                mButtonStartPause.setVisibility(View.INVISIBLE);
            }

            if (mTimeLeftInMillis < mStartTimeInMillis) {
                mButtonReset.setVisibility(View.INVISIBLE);
            } else {
                mButtonReset.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("startTimeInMillis", mStartTimeInMillis);
        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);

        editor.apply();

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        mStartTimeInMillis = prefs.getLong("startTimeInMillis", 600000);
        mTimeLeftInMillis = prefs.getLong("millisLeft", mStartTimeInMillis);
        mTimerRunning = prefs.getBoolean("timerRunning", false);

        updateCountDownText();
        updateWatchInterface();

        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                updateCountDownText();
                updateWatchInterface();
            } else {
                startTimer();
            }
        }
    }

    public void actionButtonN(View view){
        //setPendingIntent();
        setPendingIntentSI();
        setPendingIntentNO();
        createNotificationChannel();
        createNotification();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setPendingIntentNO() {
        Intent intent = new Intent(this, MenuActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MenuActivity.class);
        stackBuilder.addNextIntent(intent);
        pendingIntentNO =  stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setPendingIntentSI() {
        Intent intent = new Intent(this, AntesPaypal.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(AntesPaypal.class);
        stackBuilder.addNextIntent(intent);
        pendingIntentSI =  stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setPendingIntent() {
        Intent intent = new Intent(this, MenuActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MenuActivity.class);
        stackBuilder.addNextIntent(intent);
        pendingIntent =  stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Notificacion";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_notificacion_icon);
        builder.setContentTitle("Tu tiempo de Renta esta por Agotarse ");
        builder.setContentText("¿Quiere añadir mas tiempo?");
        builder.setColor(Color.GREEN);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setLights(Color.WHITE, 1000, 1000);
        builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        builder.setDefaults(Notification.DEFAULT_SOUND);

        builder.setContentIntent(pendingIntent);
        builder.addAction(R.drawable.ic_notificacion_icon, "Si", pendingIntentSI);
        builder.addAction(R.drawable.ic_notificacion_icon, "No", pendingIntentNO);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(NOTIFICACION_ID, builder.build());
    }

    private void ejecutar1(){
        final Handler handler = new Handler();
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        metodoAEjecutar();
                    }
                });
            }
        };

        timer.schedule(task, 0, 1000);
    }

    private void metodoAEjecutar(){
        contadorTiempo = mTextViewCountDown.getText().toString();
        if (contadorTiempo.equals("05:00")){
            setPendingIntentSI();
            setPendingIntentNO();
            createNotificationChannel();
            createNotification();
        }
        if (contadorTiempo.equals("04:00")){
            setPendingIntentSI();
            setPendingIntentNO();
            createNotificationChannel();
            createNotification();
        }
        if (contadorTiempo.equals("03:00")){
            setPendingIntentSI();
            setPendingIntentNO();
            createNotificationChannel();
            createNotification();
        }
        if (contadorTiempo.equals("02:00")){
            setPendingIntentSI();
            setPendingIntentNO();
            createNotificationChannel();
            createNotification();
        }
        if (contadorTiempo.equals("01:00")){
            setPendingIntentSI();
            setPendingIntentNO();
            createNotificationChannel();
            createNotification();
        }
        if (contadorTiempo.equals("00:00") || contadorTiempo.equals("00:01")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.infraction);
            builder.setTitle("Tiempo Vencido");
            builder.setMessage("Se a terminado su tiempo de renta, \nDebe quitar su carro, la infraccion se mandara a su correo desde nuestro Servidor\n \nAl Dar Clic en Aceptar usted esta liberando el parquimetro");
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                    //finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
