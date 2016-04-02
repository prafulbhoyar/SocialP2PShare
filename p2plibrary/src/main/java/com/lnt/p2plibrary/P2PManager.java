package com.lnt.p2plibrary;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

/**
 * Created by 10609693 on 3/13/2016.
 */
public class P2PManager implements WifiP2pManager.ChannelListener,WifiP2pManager.PeerListListener,
        WifiP2pManager.ActionListener,WifiP2pManager.ConnectionInfoListener
{

    private P2PEventListener mListener = null ;
    private static P2PManager ourInstance = new P2PManager();
    private Context mContext = null;
    private WiFiDirectBroadcastReceiver mReceiver;
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private String TAG ="P2PManager";
    boolean InitiateGroupFormation = true;


    public static P2PManager getInstance() {
        return ourInstance;
    }

    private P2PManager() {

    }

    public void Initialise(P2PEventListener listener,Context ctx)
    {
        Log.d(TAG,"Initialise");
        if(listener != null)
            mListener = listener;
        //start the broadcast receiver to get system messages
        //start the pinging service that will keep discovering peer - what happens to battery ??
        mContext = ctx;
        initWifiManager();
        setReceiver();
        startService();


    }


    public void ConnectToPeer(WifiP2pDevice wifiP2pDevice)
    {
        WifiP2pConfig wifiP2pConfig = new WifiP2pConfig();
        wifiP2pConfig.deviceAddress = wifiP2pDevice.deviceAddress;
        wifiP2pManager.requestConnectionInfo(channel, this);
        wifiP2pManager.connect(channel, wifiP2pConfig, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "connect to device started");

            }

            @Override
            public void onFailure(int i) {
                Log.d(TAG, "failed to connect to device error code:" + i);
                InitiateGroupFormation = true;
            }
        });
    }

    private void initWifiManager()
    {
        Log.d(TAG,"initWifiManager");
        wifiP2pManager = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(mContext, mContext.getMainLooper(), this);
    }

    public void SetSelfDeviceInfo(WifiP2pDevice wifiP2pDevice)
    {
        if(mListener != null)
            mListener.OnOwnWifiStatusChanged(wifiP2pDevice);
    }


    private void setReceiver()
    {
        Log.d(TAG,"setReceiver");
        mReceiver = new WiFiDirectBroadcastReceiver(wifiP2pManager,channel,this);
        IntentFilter intentFilter;
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mContext.registerReceiver(mReceiver, intentFilter);
    }

    private void startService()
    {
        //start the service
        Log.d(TAG,"startService");
         Intent intent = new Intent(mContext, CheckP2PService.class);
         mContext.startService(intent);
    }

    public void release()
    {
        Log.d(TAG,"release");
        Intent intent = new Intent(mContext, CheckP2PService.class);
        mContext.stopService(intent);
        mContext.unregisterReceiver(mReceiver);
    }


    @Override
    public void onChannelDisconnected() {
        Log.d(TAG,"onChannelDisconnected");
        initWifiManager();
    }



    public void getPeers()
    {
        Log.d(TAG,"getPeers");
        wifiP2pManager.discoverPeers(channel,this);

    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
        Log.d(TAG,"onPeersAvailable");
        if(wifiP2pDeviceList == null)
            return;
        if(wifiP2pDeviceList.getDeviceList().size() == 0)
            return;

        WifiP2pDevice wifiP2pDevice =(WifiP2pDevice) wifiP2pDeviceList.getDeviceList().toArray()[0];
        if(InitiateGroupFormation) {
       //     ConnectToPeer(wifiP2pDevice);
            InitiateGroupFormation = false;
        }
        mListener.PeersUpdated(wifiP2pDeviceList);
        //just connect to the first device in this list
        //this will get us the group formed
        //crazy solution ????


    }

    @Override
    public void onSuccess() {
        Log.d(TAG,"onSuccess");
    }

    @Override
    public void onFailure(int i) {
        Log.d(TAG,"onFailure");
        if(mListener != null)
        mListener.OnError(i);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        Log.d(TAG,"onConnectionInfoAvailable");

        Log.d(TAG,"onConnectionInfoAvailable group owner ip:"+wifiP2pInfo.groupOwnerAddress);
        if(mListener != null)
        {
            Log.d(TAG,"listener is not null calling ongroupformed");
            mListener.OnGroupFormed(wifiP2pInfo);
        }
        else
        {
            Log.d(TAG,"listener is null");
        }
    }
}
