package com.lnt.p2plibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {


    public static final int ACTION_REFRESH_SCHEDULE_ALARM = 1;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private P2PManager p2PManager;

    private final String TAG="BrdcastRecvr";

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       P2PManager p2PManager
                                       ) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.p2PManager = p2PManager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();



        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity

            Log.d(TAG,"WIFI_P2P_STATE_CHANGED_ACTION");
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Log.d(TAG,"Looks good wifi p2p is enabled on this device");
            } else {
                // Wi-Fi P2P is not enabled
                Log.e(TAG,"Wifi p2p is not enabled on this device");
                p2PManager.onFailure(P2PEventListener.WIFI_DIRECT_NOT_SUPPORTED);

            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
                mManager.requestPeers(mChannel, p2PManager);

            Log.d(TAG, "WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION");

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
            Log.d(TAG,"WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION");
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
            Log.d(TAG,"WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
            //you can get the device name from this call back
            WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            String thisDeviceName = device.deviceName;
            Log.d(TAG,"WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION"+thisDeviceName);
            p2PManager.SetSelfDeviceInfo(device);

        }
        Log.d(TAG,"finally handle all the generic extras");
        handleExtras(intent);
    }

    private void handleExtras(Intent intent)
    {
        Log.d(TAG,"Handling extras");

        WifiP2pInfo wifiP2pInfo = intent.getParcelableExtra("wifiP2pInfo");
        if(wifiP2pInfo != null)
        {
            Log.d(TAG, "wifip2pinfo available group part taken care");
        }

        WifiP2pGroup wifiP2pGroup = intent.getParcelableExtra("p2pGroupInfo");
        if(wifiP2pGroup != null && wifiP2pInfo != null)
        {
            Log.d(TAG,"wifiP2pGroup group seems to be formed");
            p2PManager.onConnectionInfoAvailable(wifiP2pInfo);

        }

        WifiP2pDeviceList wifiP2pDeviceList = intent.getParcelableExtra("wifiP2pDeviceList");
        if(wifiP2pDeviceList != null)
        {
            Log.d(TAG,"wifiP2pDeviceList available peer list part taken care");
            p2PManager.onPeersAvailable(wifiP2pDeviceList);
        }



    }

    private boolean IsPeerListAvailable(Intent intent)
    {
        boolean retVal = true;
        WifiP2pDeviceList wifiP2pDeviceList = (WifiP2pDeviceList)intent.getParcelableExtra("wifiP2pDeviceList");
        if(wifiP2pDeviceList == null) {
            retVal = false;
        }



        return retVal;

    }
}
