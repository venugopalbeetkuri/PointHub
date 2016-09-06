package com.pointhub.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Venu gopal on 07-06-2016.
 */
public class BluetoothSocketUtil {

     private static BluetoothSocketUtil util = null;

    BluetoothAdapter mBluetoothAdapter = null;

    static BluetoothDevice bdevice = null;

    static ConnectThread clientThread = null;

    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private BluetoothSocket mmSocket;

    private BluetoothSocketUtil() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    public static synchronized BluetoothSocketUtil getInstance() {

        if(null == util) {
            util = new BluetoothSocketUtil();
        }
        return util;
    }

    public void injectBluetoothDevice(BluetoothDevice bdDevice) {

        this.bdevice = bdDevice;
    }

    public ConnectThread getClientThread() {
        if(null != bdevice && null == clientThread) {
            clientThread = new ConnectThread(bdevice);
            clientThread.start();
        }
        return clientThread;
    }

    public void writeToClient(byte[] msg) {
        getClientThread().write(msg);
       // return clientThread;
    }



    private class ConnectThread extends Thread {


        //private final BluetoothDevice mmDevice;

        private InputStream mmInStream;
        private OutputStream mmOutStream;

        public ConnectThread(BluetoothDevice device) {

            // Use a temporary object that is later assigned to mmSocket, because mmSocket is final
            BluetoothSocket tmp = null;
            // mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID_INSECURE);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {

            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();

                mmInStream = mmSocket.getInputStream();
                mmOutStream = mmSocket.getOutputStream();

            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening until exception occurs or a socket is returned
            // Keep listening to the InputStream until an exception occurs
            while (true) {


                try {

                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    if(bytes > 0) {

                        String incomingMsg = new String(buffer);
                        //mmOutStream.write(incomingMsg.getBytes());

                        // System.out.println(derp);
                        //Toast.makeText(getApplicationContext(), incomingMsg, Toast.LENGTH_LONG);
                    }

                    // Send the obtained bytes to the UI activity
                    // mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }

            // Do work to manage the connection (in a separate thread)
            // manageConnectedSocket(mmSocket);
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (Exception e) { e.printStackTrace();}
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

}
