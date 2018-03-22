package com.example.roman.fileshare;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    WiFiDirectBroadcastReceiver mBroadcastReceiver;
    IntentFilter mIntentFilter;
    ListView mDevicesListView;
    SwipeRefreshLayout mSwipeLayout;

    public void updateDeviceList(WifiP2pDeviceList devices) {
        List<String> deviceNames = new ArrayList<String>();
        for (WifiP2pDevice device : devices.getDeviceList())
        {
            deviceNames.add(device.deviceName.toString());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceNames);
        mDevicesListView.setAdapter(adapter);
    }

    public void onPeersDiscovered(boolean isSuccess){
        mSwipeLayout.setRefreshing(false);
        if (!isSuccess){
            setErrorState(R.string.app_error_discovery);
        }
    }

    public void setErrorState(int errorMessage){
        Context context = this;
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
    }

    public void setErrorState() {
        setErrorState(R.string.app_error);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDevicesListView = (ListView) findViewById(R.id.tv_peers_list);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mBroadcastReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mBroadcastReceiver.discoverPeers();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mBroadcastReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
    }
}
