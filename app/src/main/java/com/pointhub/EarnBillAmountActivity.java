package com.pointhub;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pointhub.PointHubMessage;
import com.pointhub.R;
import com.google.gson.Gson;
import com.pointhub.bluetooth.BluetoothChatService;
import com.pointhub.bluetooth.BluetoothSocketUtil;
import com.pointhub.bluetooth.BluetoothUtil;
import com.pointhub.bluetooth.Constants;
import com.pointhub.db.DatabaseHelper;
import com.pointhub.db.Points;
import com.pointhub.scanner.QRCode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Venu on 03-05-2016.
 */
public class EarnBillAmountActivity extends Activity {

    Button button;
    EditText billAmount;
    String storeName;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    // ConnectThread clientThread = null;


    /**
     * Return Intent extra
     */
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    BluetoothDevice mConnectedDevice = null;

    String mConnectedDeviceName = "";

    private SingBroadcastReceiver mReceiver;

    @Override
    protected void onDestroy () {
        super.onDestroy();
       this.unregisterReceiver(mReceiver);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.earn_bill_amount);

        initBluetoothAdapter();

       // registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy


        button = (Button) findViewById(R.id.okButton);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                boolean success = true;

                Bundle extras = getIntent().getExtras();
                if (extras != null) {

                    storeName = extras.getString("storeName");
                }

                /*if(null == clientThread) {

                    //cancel any prior BT device discovery
                    if (mBluetoothAdapter.isDiscovering()) {
                        mBluetoothAdapter.cancelDiscovery();
                    }

                    mBluetoothAdapter.startDiscovery();
                }*/

                success = updateInMongoDBServer(storeName, billAmount.getText().toString());

                try {

                    Thread.sleep(2000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                if(success) {

                    hideSoftKeyboard(billAmount);

                   // saveInLocal();

                   // Log.e(LOG, selectQuery);

                } else {
                    Toast.makeText(getApplicationContext(), "Failed to upload points.", Toast.LENGTH_LONG).show();
                }

                Intent i = new Intent(EarnBillAmountActivity.this, QRCode.class);
                startActivity(i);
            }
        });
    }

    public void initBluetoothAdapter() {

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {

            Toast.makeText(EarnBillAmountActivity.this, "Bluetooth is not available", Toast.LENGTH_LONG).show();

        }

        // Enable the bluetooth adapter.
        if (!mBluetoothAdapter.isEnabled()) {

            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        }

        int state = mBluetoothAdapter.getState();

        try {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        } catch (Exception ex) {

        }

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


    private boolean updateInMongoDBServer(String storeName, String billAmount) {

        boolean success = false;
        try {

            PointHubMessage msg = new PointHubMessage("Earn",billAmount, "khaizar", "");

            Gson gson = new Gson();
            String message = gson.toJson(msg);
            // Toast.makeText(getApplicationContext(), "Message: " + message, Toast.LENGTH_LONG).show();
           // BluetoothSocketUtil util = new BluetoothSocketUtil();

            writeToServer(message);

           // BluetoothUtil.getInstance().write(message);

            /*int state = mChatService.getState();
            if(state == 0) {
                Toast.makeText(getApplicationContext(), "Message: we're doing nothing", Toast.LENGTH_LONG).show();
                return success;
            }
            if(state == 1) {
                Toast.makeText(getApplicationContext(), "Message: now listening for incoming connections", Toast.LENGTH_LONG).show();
                return success;
            }
            if(state == 2) {
                Toast.makeText(getApplicationContext(), "Message: now initiating an outgoing connection", Toast.LENGTH_LONG).show();
                return success;
            }
            if(state == 3) {
                Toast.makeText(getApplicationContext(), "Message: now connected to a remote device", Toast.LENGTH_LONG).show();
            }



            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);*/

            success = true;
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return success;
    }

    public void writeToServer(String message) {
        try {

            Toast.makeText(getApplicationContext(), "Message: " + message , Toast.LENGTH_LONG).show();
                // clientThread.write(message.getBytes());
            BluetoothSocketUtil.getInstance().writeToClient(message.getBytes());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveInLocal(PointHubMessage pointHubMessage) {
        try {

// int bill = Integer.parseInt(billAmount.getText().toString());

            Toast.makeText(getApplicationContext(), "You have received " + pointHubMessage.getPoints() + " Points.", Toast.LENGTH_LONG).show();


            String lastUpdate = DatabaseHelper.getInstance(getApplicationContext()).getDateTime();
            Points pnts = DatabaseHelper.getInstance(getApplicationContext()).getPoints(storeName);

            if(null == pnts) {

                pnts = new Points(storeName, pointHubMessage.getPoints(), lastUpdate);
                DatabaseHelper.getInstance(getApplicationContext()).createPoints(pnts);
            } else {

                int lastPoints = Integer.parseInt(pnts.getPoints());
                Integer presentPoints = lastPoints + Integer.parseInt(pointHubMessage.getPoints());
                pnts.setPoints(presentPoints.toString());
                pnts.setLastVisited(lastUpdate);

                DatabaseHelper.getInstance(getApplicationContext()).updatePoints(pnts);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    Toast.makeText(EarnBillAmountActivity.this, "Bluetooth enabled.", Toast.LENGTH_SHORT).show();
                    // setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    // Log.d(TAG, "BT not enabled");
                    Toast.makeText(EarnBillAmountActivity.this, "Bluetooth not enabled.", Toast.LENGTH_SHORT).show();
                    // getActivity().finish();
                }
        }
    }

    private class SingBroadcastReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {

            // May need to chain this to a recognizing function.
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action) || BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                // Get the Bluetooth Device object from the Intent.
                BluetoothDevice bdDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(null == bdDevice ) {
                    return;
                }

                String deviceName = bdDevice.getName();
                if(null != deviceName && deviceName.contains("5010")) {

                    BluetoothSocketUtil.getInstance().injectBluetoothDevice(bdDevice);

                    BluetoothSocketUtil.getInstance().getClientThread();

                }
            }
        }
    }

    /**
     * Hide keyboard.
     *
     * @param input
     */
    protected void hideSoftKeyboard(EditText input) {
        input.setInputType(0);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
    }

       /* public void bluetoothList() {

        pairedDevices = bluetoothAdapter.getBondedDevices();
        ArrayList list = new ArrayList();

        for(BluetoothDevice bt : pairedDevices) {
            list.add(bt.getName());
        }

        Toast.makeText(getApplicationContext(),"Showing Paired Devices",Toast.LENGTH_SHORT).show();

    }*/

}
