package com.lnt.p2plibrary;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;

/**
 * Created by 10609693 on 3/13/2016.
 */
public interface P2PEventListener {
    public final int WIFI_DIRECT_NOT_SUPPORTED = 0;
    public final int WIFI_DIRECT_DISOVER_PEERS_FAILED = WIFI_DIRECT_NOT_SUPPORTED +1;

    public void PeersUpdated(WifiP2pDeviceList listOfpeers);
    public void OnError(int errorCode);
    public void OnGroupFormed(WifiP2pInfo groupOwnerInfo);
    public void OnOwnWifiStatusChanged(WifiP2pDevice wifiP2pDevice);
}
