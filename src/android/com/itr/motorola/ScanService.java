package com.itr.motorola;

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
        BarcodeScan barcode = getBarcode(scanDataCollection);
        if (barcode != null){
            scanCallback.execute(barcode);
        }


        //scanner.read() works only for one scan
        try {
            scanner.read();
        } catch (ScannerException e) {
            e.printStackTrace();
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

    protected BarcodeScan getBarcode(ScanDataCollection... params) {


        ScanDataCollection scanDataCollection = params[0];
        String statusStr = "";

        if (scanDataCollection != null && scanDataCollection.getResult() == ScannerResults.SUCCESS) {

            ArrayList<ScanDataCollection.ScanData> scanData = scanDataCollection.getScanData();

            for (ScanDataCollection.ScanData data : scanData) {
                String barcodeData = data.getData();
                ScanDataCollection.LabelType labelType = data.getLabelType();
                return new BarcodeScan(labelType.toString(), barcodeData);
            }
        }
        return null;
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
