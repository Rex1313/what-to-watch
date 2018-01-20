package uk.co.sszymanski.cinema.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;

import java.net.UnknownHostException;

import uk.co.sszymanski.cinema.R;

/**
 * Created by rex on 20/01/2018.
 */

public class DialogUtils {
    public static AlertDialog getSimpleAlertDialog(String message,  String title, Context context) {
        return new AlertDialog.Builder(context, R.style.DialogTheme)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }

    public static void displayNetworkConnectionDialog(Exception exception, final Context context){
        if (exception instanceof UnknownHostException) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    DialogUtils.getSimpleAlertDialog(context.getString(R.string.connection_error_message), context.getString(R.string.connection_error_title), context).show();
                }
            });
        }
    }
}
