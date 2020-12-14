package com.useandroidlibs;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.zebra.adc.decoder.Barcode2DWithSoft;

import es.dmoral.toasty.Toasty;

import com.facebook.react.bridge.Callback;

public class ToastyTest extends ReactContextBaseJavaModule {

    ReactApplicationContext _context;
    Barcode2DWithSoft mReader;
    boolean threadStop = true;
    Thread thread;

    ToastyTest(ReactApplicationContext context) {
        super(context);
        _context = context;
    }

    interface InitSuccess {
       void onInitSuccess(String response);
    }

    @NonNull
    @Override
    public String getName() {
        return "ToastyTest";
    }

    @ReactMethod()
    public void ShowToasty(String message) {
        Toasty.success(_context, message, Toast.LENGTH_SHORT, true).show();
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void InitScanner(Callback callBack) {
        mReader = Barcode2DWithSoft.getInstance();
        new InitTask().execute(new InitSuccess(){
            @Override
            public void onInitSuccess(String response) {
                callBack.invoke(response);
            }
        });
    }
    @ReactMethod(isBlockingSynchronousMethod = true)
    public void onStartScanner(Callback callBack) {
        if (threadStop) {
            boolean bContinuous = false;
            int iBetween = 0;
            thread = new DecodeThread(bContinuous, iBetween);
            thread.start();
        }
        if (mReader != null) {
            mReader.stopScan();
            mReader.setScanCallback(new Barcode2DWithSoft.ScanCallback() {
                @Override
                public void onScanComplete(int i, int length, byte[] data) {
                    if (data != null && data.length != 0) {
                        callBack.invoke(new String(data).trim());

                    }
                }
            });

        }
    }
    @ReactMethod(isBlockingSynchronousMethod = true)
    public void onStopScanner() {
        mReader.stopScan();
    }

    public class InitTask extends AsyncTask<Object, Object, Object> {
        // ProgressDialog mypDialog;
        InitSuccess _callback = null;


        @Override
        protected Boolean doInBackground(Object... params) {
            _callback = (InitSuccess) params[0];
            boolean result = false;

            if (mReader != null) {
                result = mReader.open(_context);
                if (result) {
                    mReader.setParameter(324, 1);
                    mReader.setParameter(300, 0); // Snapshot Aiming
                    mReader.setParameter(361, 0); // I mage Capture Illumination
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            // mypDialog.dismiss();

            _callback.onInitSuccess("Initial Scanner Success");
//            mReader.setScanCallback(doc_scan);


        }


        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            // mypDialog = new ProgressDialog(_context);
            // mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            // mypDialog.setMessage("Loading...");
            // mypDialog.setCanceledOnTouchOutside(false);
            // mypDialog.show();
        }

    }


    private class DecodeThread extends Thread {
        private boolean isContinuous = false;
        private long sleepTime = 1000;

        public DecodeThread(boolean isContinuous, int sleep) {
            this.isContinuous = isContinuous;
            this.sleepTime = sleep;
        }

        @Override
        public void run() {
            super.run();
            do {
                mReader.scan();
                if (isContinuous) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } while (isContinuous && !threadStop);
        }
    }

}
