package com.lnt.p2plibrary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.IBinder;
import android.util.Log;

public class CheckP2PService extends Service {

    //start and alarm every 10 seconds to keep checking the peers
    // An alarm for rising in special times to fire the
    // pendingIntentPositioning
    private AlarmManager alarmManagerP2P;
    // A PendingIntent for calling a receiver in special times
    public PendingIntent pendingIntentP2P;
    public WifiP2pManager manager;
    WifiP2pManager.Channel mChannel;


    public CheckP2PService() {
    }

    private void SetP2PCheckAlarm() {

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), CheckP2PService.class);
        intent.putExtra("locationSendingAlarm", true);
        Log.d("service", "SetP2PCheckAlarm created");

        pendingIntentP2P = PendingIntent.getService(this, 987654321, intent,0);
        try {
            alarmManager.cancel(pendingIntentP2P);
        } catch (Exception e) {

        }
        int timeForAlarm=1000;


        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+timeForAlarm, timeForAlarm,pendingIntentP2P);
    }


    @Override
    public void onCreate() {
        super.onCreate();
       // SetP2PCheckAlarm();
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = manager.initialize(this, getMainLooper(), null);
        Log.d("service", "service created");


    }

    private void discoverPeers()
    {

    }



    private void handleCommand(Intent intent)
    {
        discoverPeers();
    }




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleCommand(intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onDestroy() {
      //  this.alarmManagerP2P.cancel(pendingIntentP2P);

    }
}
