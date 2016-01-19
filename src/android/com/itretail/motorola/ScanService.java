package com.itretail.motorola;

import android.util.Log;

//import com.symbol.emdk.barcode.ScanDataCollection;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import com.symbol.emdk.EMDKManager;
//import com.symbol.emdk.EMDKManager.EMDKListener;
//import com.symbol.emdk.EMDKResults;
//import com.symbol.emdk.barcode.BarcodeManager;
//import com.symbol.emdk.barcode.ScanDataCollection;
//import com.symbol.emdk.barcode.Scanner;
//import com.symbol.emdk.barcode.Scanner.DataListener;
//import com.symbol.emdk.barcode.Scanner.StatusListener;
//import com.symbol.emdk.barcode.ScannerException;
//import com.symbol.emdk.barcode.ScannerResults;
//import com.symbol.emdk.barcode.StatusData;

import java.util.ArrayList;

public class ScanService extends CordovaPlugin { //implements EMDKListener, StatusListener, DataListener {

//    // Declare a variable to store EMDKManager object
//    private EMDKManager emdkManager = null;
//
//    // Declare a variable to store Barcode Manager object
//    private BarcodeManager barcodeManager = null;
//
//    // Declare a variable to hold scanner device to scan
//    private Scanner scanner = null;

    protected static String TAG = "MotorolaBarcodeAPIPlugin";

    protected ScanCallback<BarcodeScan> scanCallback;

    @Override
    public boolean execute(String action, JSONArray data, final CallbackContext callbackContext) throws JSONException {

        if ("register".equals(action)) {
            scanCallback = new ScanCallback<BarcodeScan>() {
                @Override
                public void execute(BarcodeScan scan) {
                    Log.i(TAG, "Scan result [" + scan.LabelType + "-" + scan.Barcode + "].");

                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("type", scan.LabelType);
                        obj.put("barcode", scan.Barcode);
                        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, obj);
                        pluginResult.setKeepCallback(true);
                        callbackContext.sendPluginResult(pluginResult);
                    } catch(JSONException e){
                        Log.e(TAG, "Error building json object", e);

                    }
                }
            };
        }
        else if ("start".equals(action)){

//            // The EMDKManager object will be created and returned in the callback.
//            EMDKResults results = EMDKManager.getEMDKManager(this.cordova.getActivity().getApplicationContext(), this);
//            // Check the return status of getEMDKManager and update the status Text
//            // View accordingly
//            if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
//                return false;
//            }

        }
        else if ("trigger".equals(action)){
            if (scanCallback != null){
                scanCallback.execute(new BarcodeScan("UPCA", "000000000001"));
            }
        }

        return true;
    }

//    @Override
//    public void onData(ScanDataCollection scanDataCollection) {
//        BarcodeScan barcode = getBarcode(scanDataCollection);
//        if (barcode != null){
//            scanCallback.execute(barcode);
//        }
//    }
//
//    @Override
//    public void onOpened(EMDKManager emdkManager) {
//        this.emdkManager = emdkManager;
//        try {
//            // Call this method to enable Scanner and its listeners
//            initializeScanner();
//        } catch (ScannerException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onClosed() {
//        // The EMDK closed abruptly. // Clean up the objects created by EMDK
//        // manager
//        if (this.emdkManager != null) {
//            this.emdkManager.release();
//            this.emdkManager = null;
//        }
//    }
//
//    @Override
//    public void onStatus(StatusData statusData) {
//
//    }
//    @Override
//    public void onStop() {
//        super.onStop();
//        try {
//            if (scanner != null) {
//                // releases the scanner hardware resources for other application
//                // to use. You must call this as soon as you're done with the
//                // scanning.
//                scanner.disable();
//                scanner = null;
//            }
//        } catch (ScannerException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (emdkManager != null) {
//
//            // Clean up the objects created by EMDK manager
//            emdkManager.release();
//            emdkManager = null;
//
//
//        }
//    }

//    protected BarcodeScan getBarcode(ScanDataCollection... params) {
//        ScanDataCollection scanDataCollection = params[0];
//
//        // Status string that contains both barcode data and type of barcode
//        // that is being scanned
//        String statusStr = "";
//
//        // The ScanDataCollection object gives scanning result and the
//        // collection of ScanData. So check the data and its status
//        if (scanDataCollection != null && scanDataCollection.getResult() == ScannerResults.SUCCESS) {
//
//            ArrayList<ScanDataCollection.ScanData> scanData = scanDataCollection.getScanData();
//
//            // Iterate through scanned data and prepare the statusStr
//            for (ScanDataCollection.ScanData data : scanData) {
//                // Get the scanned data
//                String barcodeData = data.getData();
//                // Get the type of label being scanned
//                ScanDataCollection.LabelType labelType = data.getLabelType();
//                // Concatenate barcode data and label type
//                return new BarcodeScan(barcodeData, labelType.toString());
//            }
//        }
//        return null;
//    }
//
//    // Method to initialize and enable Scanner and its listeners
//    private void initializeScanner() throws ScannerException {
//
//        if (scanner == null) {
//
//            // Get the Barcode Manager object
//            barcodeManager = (BarcodeManager) this.emdkManager
//                    .getInstance(EMDKManager.FEATURE_TYPE.BARCODE);
//
//            // Get default scanner defined on the device
//            scanner = barcodeManager.getDevice(BarcodeManager.DeviceIdentifier.DEFAULT);
//
//            // Add data and status listeners
//            scanner.addDataListener(this);
//            scanner.addStatusListener(this);
//
//            // Hard trigger. When this mode is set, the user has to manually
//            // press the trigger on the device after issuing the read call.
//            scanner.triggerType = Scanner.TriggerType.HARD;
//
//            // Enable the scanner
//            scanner.enable();
//
//            // Starts an asynchronous Scan. The method will not turn ON the
//            // scanner. It will, however, put the scanner in a state in which
//            // the scanner can be turned ON either by pressing a hardware
//            // trigger or can be turned ON automatically.
//            scanner.read();
//        }
//
//
//    }
}
