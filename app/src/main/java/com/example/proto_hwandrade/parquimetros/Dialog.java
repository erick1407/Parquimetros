package com.example.proto_hwandrade.parquimetros;

import android.content.Context;
import android.support.v7.app.AlertDialog;

public class Dialog {
    public static void show(Context context, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
