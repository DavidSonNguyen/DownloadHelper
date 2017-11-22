package com.davidsonnguyen.freelancer3010.downloadhelper.Control;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.davidsonnguyen.freelancer3010.downloadhelper.Initial.Connection;

/**
 * Created by User on 1/14/2017.
 */

public class CheckInternetConnection extends Thread {
    Context context;
    Connection connection;

    public CheckInternetConnection(Context context, Connection connection){
        this.context = context;
        this.connection = connection;
    }

    @Override
    public void run() {
        super.run();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                connection.onChange(isNetworkAvailable(context));
            }
        };

        context.registerReceiver(receiver, intentFilter);
    }

    private boolean isNetworkAvailable(final Context context) {
        boolean result;
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null){
            if (networkInfo.isConnected()){
                result = true;
            }else {
                result = false;
            }
        }else {
            result = false;
        }
        return result;
    }
}