package com.lnt.wifidirecttest;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class GroupOwnerService extends Service {

    SocketServer socketServer;
    String TAG= "GroupOwnerService";

    private final IBinder groupOwnerBinder = new GroupOwnerServiceBinder();


    public GroupOwnerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return groupOwnerBinder;
    }


    public String getIpForMacAddress(String macAddress)
    {
       String ipAddress =  socketServer.getIpAddressForMac(macAddress);
        if(ipAddress != null)
        Log.d(TAG,"Mac Address:"+macAddress +"ipAddress:"+ipAddress);

        return ipAddress;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "service starting", Toast.LENGTH_LONG).show();
        Log.d("socket server","Socket server started");
        socketServer = new SocketServer();
        socketServer.startServer();

       // super.onStartCommand(intent,flags,startId);
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        socketServer.stopServer();

    }


    public class GroupOwnerServiceBinder extends Binder {
        public GroupOwnerService getService() {
            return GroupOwnerService.this;
        }
    }




}
