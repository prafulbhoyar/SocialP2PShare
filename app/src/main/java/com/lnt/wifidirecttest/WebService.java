package com.lnt.wifidirecttest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class WebService extends Service {
    Webserver webServer = null;
    public WebService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "service starting", Toast.LENGTH_LONG).show();
        try
        {
            webServer = new Webserver();
            webServer.start();
        }
        catch( IOException ioe )
        {
            Log.d("service","Couldn't start server:\n" + ioe);
           // System.exit(-1);
        }
        return START_STICKY;
       // return super.onStartCommand(intent,flags,startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        webServer.stop();
    }


}
