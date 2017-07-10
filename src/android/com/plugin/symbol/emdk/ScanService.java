package com.plugin.symbol.emdk;

import android.os.AsyncTask;
import android.util.Log;

import com.symbol.emdk.barcode.ScanDataCollection;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKManager.EMDKListener;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.Scanner.DataListener;
import com.symbol.emdk.barcode.Scanner.StatusListener;
import com.symbol.emdk.barcode.ScannerException;
import com.symbol.emdk.barcode.ScannerResults;
import com.symbol.emdk.barcode.StatusData;

import java.util.ArrayList;

public class ScanService extends CordovaPlugin implements EMDKListener, StatusListener, DataListener {

    private EMDKManager emdkManager = null;
    private BarcodeManager barcodeManager = null;
    private Scanner scanner = null;

    protected static String TAG = "ZebraBarcodeAPIPlugin";

    protected ScanCallback<BarcodeScan> scanCallback;

    // Boolean to explain whether the scanning is in progress or not at any
    // specific point of time
    boolean isScanning = false;

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
            EMDKResults results = EMDKManager.getEMDKManager(this.cordova.getActivity().getApplicationContext(), this);
            if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
                return false;
            }

        }
        else if ("trigger".equals(action)){
            if (scanCallback != null){
                scanCallback.execute(new BarcodeScan("UPCA", "000000000010"));
            }
        }

        return true;
    }

    @Override
    public void onData(ScanDataCollection scanDataCollection) {
        new AsyncDataUpdate().execute(scanDataCollection);
    }

    private class AsyncDataUpdate extends
            AsyncTask<ScanDataCollection, Void, BarcodeScan> {

        @Override
        protected BarcodeScan doInBackground(ScanDataCollection... params) {

            // Status string that contains both barcode data and type of barcode
            // that is being scanned
            BarcodeScan barcode = null;

            try {

                // Starts an asynchronous Scan. The method will not turn ON the
                // scanner. It will, however, put the scanner in a state in
                // which
                // the scanner can be turned ON either by pressing a hardware
                // trigger or can be turned ON automatically.
                scanner.read();

                ScanDataCollection scanDataCollection = params[0];

                // The ScanDataCollection object gives scanning result and the
                // collection of ScanData. So check the data and its status
                if (scanDataCollection != null && scanDataCollection.getResult() == ScannerResults.SUCCESS) {

                    ArrayList<ScanDataCollection.ScanData> scanData = scanDataCollection .getScanData();

                    // Iterate through scanned data and prepare the barcode
                    for (ScanDataCollection.ScanData data : scanData) {
                        // Get the scanned data
                        String barcodeData = data.getData();
                        // Get the type of label being scanned
                        ScanDataCollection.LabelType labelType = data.getLabelType();
                        // Concatenate barcode data and label type
                        barcode = new BarcodeScan(labelType.toString(), barcodeData);
                    }
                }

            } catch (ScannerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // Return result to populate on UI thread
            return barcode;
        }

        @Override
        protected void onPostExecute(BarcodeScan barcode) {
            if (barcode != null && scanCallback != null){
                scanCallback.execute(barcode);
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }


    }

    @Override
    public void onOpened(EMDKManager emdkManager) {
        this.emdkManager = emdkManager;
        try {
            initializeScanner();
        } catch (ScannerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClosed() {
        if (this.emdkManager != null) {
            this.emdkManager.release();
            this.emdkManager = null;
        }
    }

    @Override
    public void onStatus(StatusData statusData) {
        new AsyncStatusUpdate().execute(statusData);

    }

    // AsyncTask that configures the current state of scanner on background
    // thread and updates the result on UI thread
    private class AsyncStatusUpdate extends AsyncTask<StatusData, Void, String> {

        @Override
        protected String doInBackground(StatusData... params) {
            // Get the current state of scanner in background
            StatusData statusData = params[0];
            String statusStr = "";
            StatusData.ScannerStates state = statusData.getState();
            // Different states of Scanner
            switch (state) {
                // Scanner is IDLE
                case IDLE:
                    statusStr = "IDLE";
                    isScanning = false;
                    break;
                // Scanner is SCANNING
                case SCANNING:
                    statusStr = "SCANNING";
                    isScanning = true;
                    break;
                // Scanner is waiting for trigger press
                case WAITING:
                    statusStr = "WAITING";
                    break;
                default:
                    break;
            }
            // Return result to populate on UI thread
            return statusStr;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("IDLE")){
                try{
                    scanner.read();
                }
                catch (Exception ignored){
                }
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (scanner != null) {
                scanner.disable();
                scanner = null;
            }
        } catch (ScannerException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (emdkManager != null) {
            emdkManager.release();
            emdkManager = null;


        }
    }

    private void initializeScanner() throws ScannerException {

        if (scanner == null) {

            // Get the Barcode Manager object
            barcodeManager = (BarcodeManager) this.emdkManager
                    .getInstance(EMDKManager.FEATURE_TYPE.BARCODE);

            // Get default scanner defined on the device
            scanner = barcodeManager.getDevice(BarcodeManager.DeviceIdentifier.DEFAULT);

            // Add data and status listeners
            scanner.addDataListener(this);
            scanner.addStatusListener(this);

            // Hard trigger. When this mode is set, the user has to manually
            // press the trigger on the device after issuing the read call.
            scanner.triggerType = Scanner.TriggerType.HARD;

            // Enable the scanner
            scanner.enable();

            // Starts an asynchronous Scan. The method will not turn ON the
            // scanner. It will, however, put the scanner in a state in which
            // the scanner can be turned ON either by pressing a hardware
            // trigger or can be turned ON automatically.
            scanner.read();
        }


    }
}
