package com.example.roman.fileshare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

/**
 * Created by Roman on 2/28/18.
 */

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    static String TAG = WiFiDirectBroadcastReceiver.class.getName();

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity mActivity;

    private class MyActionListener implements WifiP2pManager.ActionListener {

        @Override
        public void onSuccess() {
            Log.i(TAG, "Discover peers succeeded.");
        }

        @Override
        public void onFailure(int reason) {
            Log.e(TAG, "Failed to discover peers.");
        }
    }

    private class MyPeerListListener implements WifiP2pManager.PeerListListener {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peers) {
            mActivity.updateDeviceList(peers);
        }
    }

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;

        mManager.discoverPeers(mChannel, new MyActionListener());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_DISABLED) {
                // p2p is not supported. :(
                mActivity.setErrorState();
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            if (mManager != null) {
                mManager.requestPeers(mChannel, new MyPeerListListener());
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                // Respond to this device's wifi state changing
        }
    }
}

