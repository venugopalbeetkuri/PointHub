package com.pointhub.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * Created by Provigil on 06-06-2016.
 */
public class BluetoothUtil extends Activity {

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;


    /**
     * Return Intent extra
     */
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // private BluetoothAdapter bluetoothAdapter;
    // private Set<BluetoothDevice> pairedDevices;


    /**
     * Member object for the chat services
     */
    private BluetoothChatService mChatService = null;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    BluetoothDevice mConnectedDevice = null;

    String mConnectedDeviceName = "";

    private SingBroadcastReceiver mReceiver;

    String storeName;

    private static BluetoothUtil util = null;

    private BluetoothUtil() {

    }

    public static synchronized BluetoothUtil getInstance() {
        if (null == util) {
            util = new BluetoothUtil();
        }
        return util;
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        this.unregisterReceiver(mReceiver);
    }

    public void initBluetoothAdapter() {

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {

            Toast.makeText(BluetoothUtil.this, "Bluetooth is not available", Toast.LENGTH_LONG).show();

        }

        // Enable the bluetooth adapter.
        if (!mBluetoothAdapter.isEnabled()) {

            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        }

        int state = mBluetoothAdapter.getState();

        if(BluetoothAdapter.STATE_OFF == state) {

            mBluetoothAdapter.enable();
        }

        //cancel any prior BT device discovery
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        // re-start discovery
        mBluetoothAdapter.startDiscovery();

        // let's make a broadcast receiver to register our things
        mReceiver = new SingBroadcastReceiver();
        IntentFilter ifilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, ifilter);

        // Register for broadcasts when discovery has finished
        ifilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
         this.registerReceiver(mReceiver, ifilter);
    }

    public void write(String msg) {
        try {
            byte[] send = msg.getBytes();
            mChatService.write(send);
        } catch (Exception ex) {

        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    Toast.makeText(BluetoothUtil.this, "Bluetooth enabled.", Toast.LENGTH_SHORT).show();
                    // setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    // Log.d(TAG, "BT not enabled");
                    Toast.makeText(BluetoothUtil.this, "Bluetooth not enabled.", Toast.LENGTH_SHORT).show();
                    // getActivity().finish();
                }
        }
    }


    /**
     * Establish connection with other divice
     *

     */
    private void connectDevice(Intent data, boolean secure) {

        // Get the device MAC address
        String address = data.getExtras().getString(EXTRA_DEVICE_ADDRESS);

        // Get the BluetoothDevice object
        mConnectedDevice = mBluetoothAdapter.getRemoteDevice(address);

        mConnectedDeviceName = mConnectedDevice.getName();

        if(null == mChatService) {
            // Initialize the BluetoothChatService to perform bluetooth connections
            mChatService = new BluetoothChatService(this.getApplicationContext(), mHandler);
            mChatService.start();
        }

        // Attempt to connect to the device
        mChatService.connect(mConnectedDevice, secure);
    }

    private void connectDevice(String macAddress, boolean secure) {

        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(macAddress);

        if(null == mChatService) {
            // Initialize the BluetoothChatService to perform bluetooth connections
            mChatService = new BluetoothChatService(this.getApplicationContext(), mHandler);
            mChatService.start();

        }

        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            // setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            //mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            // setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            // setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:

                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);

                   // Toast.makeText(EarnBillAmountActivity.this, "Message: " + writeMessage, Toast.LENGTH_SHORT).show();
                    // mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                   Toast.makeText(BluetoothUtil.this, "Message: " + readMessage, Toast.LENGTH_SHORT).show();

                   /* try{
                        // PointHubMessage msg = new PointHubMessage("Earn",billAmount, );

                        Gson gson = new Gson();
                        PointHubMessage pointHubMessage = gson.fromJson(readMessage, PointHubMessage.class);
                        saveInLocal(pointHubMessage);
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }*/


                    // mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    String mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    // if (null != activity) {
                     Toast.makeText(BluetoothUtil.this, "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    // }
                    break;
                case Constants.MESSAGE_TOAST:
                    /*if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST), Toast.LENGTH_SHORT).show();
                    }*/
                    break;
            }
        }
    };

    private class SingBroadcastReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {

            // May need to chain this to a recognizing function.
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                // Get the Bluetooth Device object from the Intent.
                BluetoothDevice bdDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if(null == storeName){
                    storeName = "Redmi";
                }

                // bdDevice.getName()
                String bdName = bdDevice.getName();
                if (null == bdName) {

                    return;
                }

                // String deviceName = "Redmi";


                String remoteAddress = bdDevice.getAddress();

                if(null != bdName && storeName.contains(bdName)) {

                    if (bdDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                        connectDevice(remoteAddress, false);
                    }

                    // sendFile(deviceName);

                    // Add the name and address to an array adapter to show in a Toast
                    /*String derp = bdName + " - " + bdDevice.getAddress();
                    System.out.println(derp);
                    Toast.makeText(context, derp, Toast.LENGTH_LONG);*/
                }

            }
        }
    }

}
