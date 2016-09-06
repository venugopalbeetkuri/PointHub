package com.pointhub.scanner;


import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.pointhub.EarnRedeem;
import com.pointhub.PointListActivity;
import com.pointhub.R;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Method;


/**
 * Created by Venu on 03-05-2016.
 */
public class QRCode extends Activity {

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";

    String storeName = "";

    private BluetoothAdapter bluetoothAdapter;

    private SingBroadcastReceiver mReceiver;



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //set the main content layout of the Activity
        setContentView(R.layout.qrcode);


        Button button = (Button) findViewById(R.id.scanner);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                /*IntentIntegrator integrator = new IntentIntegrator(QRCode.this);
                integrator.initiateScan();*/
                // scanQR();

                Intent i = new Intent(QRCode.this, EarnRedeem.class);
                if ("".equalsIgnoreCase(storeName)) {
                    storeName = "xyz";
                }
                // i.putExtra("storeName",storeName);

                i.putExtra("storeName","5010");
                startActivity(i);


                // bluetoothList();
            }

            });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mReceiver);
    }

    // Product qr code mode.
    public void scanQR() {

        try {

            // Code for scanning QR code.
           /* Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);*/

            IntentIntegrator integrator = new IntentIntegrator(QRCode.this);
            integrator.initiateScan();

        } catch (Exception anfe) {

            // on catch, show the download dialog
            showDialog(QRCode.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    // Go to point list activity.
    public void pointList(View v) {

        try {

            Intent i = new Intent(QRCode.this, PointListActivity.class);
            startActivity(i);
        } catch (Exception anfe) {
            anfe.printStackTrace();
        }
    }

    // Alert dialog for downloadDialog.
    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {

        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {

                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });

        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        return downloadDialog.show();
    }

    // On ActivityResult method.
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null) {

            storeName = result.getContents();

            Intent i = new Intent(QRCode.this, EarnRedeem.class);
            if ("".equalsIgnoreCase(storeName)) {
                storeName = "xyz";
            }
            i.putExtra("storeName",storeName);
            startActivity(i);
        }
    }

    private Boolean connect(BluetoothDevice bdDevice) {
        Boolean bool = false;
        try {
            Log.i("Log", "service method is called ");
            Class cl = Class.forName("android.bluetooth.BluetoothDevice");
            Class[] par = {};
            Method method = cl.getMethod("createBond", par);
            Object[] args = {};
            bool = (Boolean) method.invoke(bdDevice);//, args);// this invoke creates the detected devices paired.
            //Log.i("Log", "This is: "+bool.booleanValue());
            //Log.i("Log", "devicesss: "+bdDevice.getName());
        } catch (Exception e) {
            Log.i("Log", "Inside catch of serviceFromDevice Method");
            e.printStackTrace();
        }
        return bool.booleanValue();
    };

    public boolean removeBond(BluetoothDevice btDevice)
            throws Exception {

        Class btClass = Class.forName("android.bluetooth.BluetoothDevice");
        Method removeBondMethod = btClass.getMethod("removeBond");
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }


    public boolean createBond(BluetoothDevice btDevice)
            throws Exception {

        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    private void sendFile(String deviceName) {

        try {

            String packageName = "com.android.bluetooth";
            String className = "com.broadcom.bt.app.opp.OppLauncherActivity";

            // bring up Android chooser
            Intent intent1 = new Intent();

            intent1.setAction(Intent.ACTION_SEND);
            intent1.setType("text/plain");

            // List of apps that can handle our intent.
            /*PackageManager pm = getPackageManager();
            List<ResolveInfo> appsList = pm.queryIntentActivities(intent1, 0);

            for (ResolveInfo info : appsList) {

                packageName = info.activityInfo.packageName;
                if (packageName.equals("com.android.bluetooth")) {

                    className = info.activityInfo.name;
                    // found = true;
                    break;// found
                }
            }*/

            // intent1.setClassName(packageName, className);
            intent1.setComponent(new ComponentName("com.android.bluetooth", "com.android.bluetooth.opp.BluetoothOppLauncherActivity"));

            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
            File file_to_transfer = new File(path, "/" + "the-file-name.txt");

            if (!file_to_transfer.exists()) {

                file_to_transfer.createNewFile();
            }

            PrintWriter writer = new PrintWriter(path.getAbsolutePath() + "/the-file-name.txt", "UTF-8");
            writer.println("The first line");
            writer.println("The second line");
            writer.close();

            intent1.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file_to_transfer));

            // startActivity(intent1);Intent.createChooser(sharingIntent, "Share")
            startActivity(Intent.createChooser(intent1, "Share"));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void onBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
            Log.i("Log", "Bluetooth is Enabled");
        }
    }

    private void offBluetooth() {
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
        }
    }

    public void bluetoothList() {


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        int state = bluetoothAdapter.getState();

        /*if(BluetoothAdapter.STATE_ON == state) {
            bluetoothAdapter.disable();
        }*/

        if(BluetoothAdapter.STATE_OFF == state) {

            bluetoothAdapter.enable();
        }

        //cancel any prior BT device discovery
        if (bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
        }
        // re-start discovery
        bluetoothAdapter.startDiscovery();

        // let's make a broadcast receiver to register our things
        mReceiver = new SingBroadcastReceiver();
        IntentFilter ifilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, ifilter);
    }

    private void makeDiscoverable() {

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
        Log.i("Log", "Discoverable ");
    }



    private class SingBroadcastReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {

            // May need to chain this to a recognizing function.
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                // Get the Bluetooth Device object from the Intent.
                BluetoothDevice bdDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String bdName = bdDevice.getName();

                String deviceName = "Redmi";

                if(null != bdName && deviceName.contains(bdName)) {

                    if (bdDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                        connect(bdDevice);
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

    //product barcode mode
   /* public void scanBar(View v) {
        try {
            //start the scanning activity from the com.google.zxing.client.android.SCAN intent
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            //on catch, show the download dialog
            showDialog(QRCode.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }*/

}
