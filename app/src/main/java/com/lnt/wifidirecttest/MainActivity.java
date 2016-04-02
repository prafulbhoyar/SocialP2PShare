package com.lnt.wifidirecttest;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.DataSetObserver;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lnt.p2plibrary.CheckP2PService;
import com.lnt.p2plibrary.P2PEventListener;
import com.lnt.p2plibrary.P2PManager;

import java.util.List;


public class MainActivity extends AppCompatActivity implements P2PEventListener,
        SwipeRefreshLayout.OnRefreshListener{




    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView listView;
    private ListAdapter adapter;
    private WifiP2pDeviceList wifiP2pDeviceList = null;
    private Boolean groupOwnerServiceStarted = false;
    private WifiP2pDevice selfDeviceInfo =null;
    private boolean FirstConnectMessage = true;
    private GroupOwnerService.GroupOwnerServiceBinder groupOwnerServiceBinder = null;
    private boolean IsGOServiceBound = false;
    private String TAG = "MainActivity";
    private WifiP2pInfo groupOwnerInfo = null;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.group:
                Log.d(TAG,"Initiating group creation");
                initiateGroupCreation();
                break;

            default:
                break;
        }

        return true;
    }

    private void initiateGroupCreation()
    {
        //check if group is already formed
        if(groupOwnerInfo == null)
        {
            if(wifiP2pDeviceList != null)
            {
                if(wifiP2pDeviceList.getDeviceList().size()>0)
                {
                    //this means there is atleast one peer
                    WifiP2pDevice wifiP2pDevice = (WifiP2pDevice) wifiP2pDeviceList.getDeviceList().toArray()[0];
                    P2PManager.getInstance().ConnectToPeer(wifiP2pDevice);
                }

            }
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addRefreshLayout();
        //  startService();
    }

    private void startService()
    {
        //start the service
        if (isMyServiceRunning(WebService.class))
            return;
        Log.d("MainActivity", "startService");
        Intent intent = new Intent(this, WebService.class);
        this.startService(intent);
    }

    private void stopService()
    {
        //start the service
        Log.d("MainActivity", "startService");
        Intent intent = new Intent(this, WebService.class);
        this.stopService(intent);
    }

    private void bindToGOService()
    {
        Intent intent = new Intent(this, GroupOwnerService.class);
        this.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                groupOwnerServiceBinder = (GroupOwnerService.GroupOwnerServiceBinder) iBinder;
                IsGOServiceBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                IsGOServiceBound = false;

            }
        }, Context.BIND_IMPORTANT);
    }

    private void startGroupOwnerService()
    {
        //start the service
        //start the service
        if (isMyServiceRunning(GroupOwnerService.class)) {
            //just bind to the service that is already running
            bindToGOService();
            return;
        }
        Log.d("MainActivity", "startGroupOwnerService");
        Intent intent = new Intent(this, GroupOwnerService.class);
        this.startService(intent);
        bindToGOService();
        groupOwnerServiceStarted = true;
    }

    private void stopGroupOwnerService()
    {

        Log.d("MainActivity", "startService");
        Intent intent = new Intent(this, GroupOwnerService.class);
        this.stopService(intent);
        groupOwnerServiceStarted = false;
    }
    public void deviceSelected(View view){

        Log.d(TAG,"inside deviceSelected");

        WifiP2pDevice wifiP2pDevice =(WifiP2pDevice)view.getTag();
        if(groupOwnerInfo == null)
        {
            //
            Log.d(TAG,"inside deviceSelected group not formed");
            Toast.makeText(this,"Group not formed",Toast.LENGTH_SHORT);
            return;
        }
        if(groupOwnerInfo.isGroupOwner)
        {
            //get the ip address of the device from the group owner service
            Log.d(TAG,"I am the group owner");
            if(groupOwnerServiceBinder != null)
            {
                GroupOwnerService groupOwnerService = groupOwnerServiceBinder.getService();
                String ipAddress = groupOwnerService.getIpForMacAddress(wifiP2pDevice.deviceAddress);
                Log.d(TAG,"ip address of the client:"+ipAddress);
                //add the 8080 as port
                ipAddress =ipAddress +":8080";
                launchPeerView(ipAddress);
            }
        }
        else{
            Log.d(TAG,"i am not the group owner");
            if(wifiP2pDevice.isGroupOwner())
            {
                Log.d(TAG,"The selected device is group owner");
                if(groupOwnerInfo != null)
                {
                    String URL = groupOwnerInfo.groupOwnerAddress.getHostAddress()+":8080";
                    launchPeerView(URL);
                }
            }
        }
        //if you are the group owner
        //get the ip address from the GroupOwner services and show the webview with this address

        //else if you are not the group owner

        //if the device is the group owner then you know the ip address so show the webview with the go address

        //else ask the GO to give the ip address for this and in async mode start the web view with that
        //data

        //if the selected device is the group owner, just display the webview with the ip address of the
        //group owner

        //

        /*
        //get the device id selected
        //get the devic config
        WifiP2pDevice wifiP2pDevice =(WifiP2pDevice)view.getTag();
        Log.d("main activity", "connecting to :" + wifiP2pDevice.deviceName + "ipAddress:" + wifiP2pDevice.deviceAddress);
        P2PManager.getInstance().ConnectToPeer(wifiP2pDevice);
        */

    }

    private void launchPeerView(String URL)
    {
        Intent intent = new Intent(this,PeerViewActivity.class);
        Log.d(TAG, "Launching the webview with URL:" + URL);
        intent.putExtra("URL","http://"+URL);
        this.startActivity(intent);

    }

    private void addRefreshLayout()
    {
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        listView = (ListView)findViewById(R.id.peerList);
        adapter = new ListAdapter() {

            @Override
            public void registerDataSetObserver(DataSetObserver dataSetObserver) {

            }

            @Override
            public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

            }

            @Override
            public int getCount() {
                int count = 0;
                if(wifiP2pDeviceList != null)
                    count =wifiP2pDeviceList.getDeviceList().size();

                return count;
            }

            @Override
            public Object getItem(int i) {

                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                RelativeLayout relativeLayout =(RelativeLayout) getLayoutInflater().inflate(R.layout.listitem, viewGroup,false);
                TextView textView =(TextView) relativeLayout.findViewById(R.id.deviceName);
                WifiP2pDevice wifiP2pDevice =(WifiP2pDevice) wifiP2pDeviceList.getDeviceList().toArray()[i];
                textView.setText(wifiP2pDevice.deviceName);
                relativeLayout.setTag(wifiP2pDevice);
                return relativeLayout;
            }

            @Override
            public int getItemViewType(int i) {
                return 0;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean areAllItemsEnabled() {
                return false;
            }

            @Override
            public boolean isEnabled(int i) {
                return false;
            }
        };
        listView.setAdapter(adapter);

    }

    /* register the broadcast receiver with the inent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onStart() {
        super.onStart();
        //registerReceiver(mReceiver, mIntentFilter);
        P2PManager.getInstance().Initialise(this, this);
        P2PManager.getInstance().getPeers();
        startService();

    }

    protected void onDestroy() {
        super.onDestroy();
        // unregisterReceiver(mReceiver);
        P2PManager.getInstance().release();
        stopService();
        if(groupOwnerServiceStarted) {
            stopGroupOwnerService();
            groupOwnerServiceStarted = false;
        }

    }


    @Override
    public void PeersUpdated(WifiP2pDeviceList listOfPeers) {
        wifiP2pDeviceList = listOfPeers;
        Log.d("Main Activity", "peers discovered count:" + listOfPeers.getDeviceList().size());
    }

    @Override
    public void OnError(int errorCode) {

        Log.d("MainActivity","Error in P2P Code:"+errorCode);

    }

    @Override
    public void OnGroupFormed(WifiP2pInfo groupOwnerInfo) {
        Log.d("MainActivity","OnGroupFormed");
        //if you are the group owner start the server else connect to the server as client
        this.groupOwnerInfo = groupOwnerInfo;
        if(this.groupOwnerInfo.isGroupOwner)
        {
            //start the
            startGroupOwnerService();
        }
        else{
            //start the client socket
            Log.d("MainActivity","starting the client socket to sned self info");
            if(groupOwnerInfo.groupFormed == false)
                return;
            if(selfDeviceInfo == null)
                return;
            ClientSocket clientSocket = new ClientSocket(groupOwnerInfo.groupOwnerAddress.getHostAddress(),9080,selfDeviceInfo.deviceAddress);
            clientSocket.start();
        }
    }

    @Override
    public void OnOwnWifiStatusChanged(WifiP2pDevice wifiP2pDevice) {
        this.selfDeviceInfo = wifiP2pDevice;
    }

    @Override
    public void onRefresh() {
        //call discover peers of the p2p managers
        P2PManager.getInstance().getPeers();
        mSwipeRefreshLayout.setRefreshing(false);
    }

}
