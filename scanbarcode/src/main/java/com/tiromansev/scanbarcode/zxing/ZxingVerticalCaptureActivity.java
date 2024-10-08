package com.tiromansev.scanbarcode.zxing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.wrdlbrnft.betterbarcodes.BarcodeFormat;
import com.github.wrdlbrnft.betterbarcodes.views.BarcodeReaderView;
import com.tiromansev.scanbarcode.PreferenceActivity;
import com.tiromansev.scanbarcode.PreferencesFragment;
import com.tiromansev.scanbarcode.R;

public class ZxingVerticalCaptureActivity extends AppCompatActivity {

    private BarcodeReaderView barcodeReaderView;
    private ScanActivityHandler handler;
    private BeepManager beepManager;
    private static final int PREFS_REQUEST = 99;

    static final int[] QR_CODE_FORMATS = {BarcodeFormat.QR_CODE};
    static final int[] DATA_MATRIX_FORMATS = {BarcodeFormat.DATA_MATRIX};
    static final int[] AZTEC_FORMATS = {BarcodeFormat.AZTEC};
    static final int[] PDF417_FORMATS = {BarcodeFormat.PDF_417};
    static final int[] PRODUCT_FORMATS = {BarcodeFormat.UPC_A,
            BarcodeFormat.UPC_E,
            BarcodeFormat.EAN_13,
            BarcodeFormat.EAN_8,
            BarcodeFormat.RSS_14,
            BarcodeFormat.RSS_EXPANDED};
    static final int[] INDUSTRIAL_FORMATS = {BarcodeFormat.CODE_39,
            BarcodeFormat.CODE_93,
            BarcodeFormat.CODE_128,
            BarcodeFormat.ITF,
            BarcodeFormat.CODABAR};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_capture);

        ImageButton btnSettings = findViewById(R.id.btnScanSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getPrefsIntent();
                startActivityForResult(intent, PREFS_REQUEST);
            }
        });

        barcodeReaderView = findViewById(R.id.barcode_reader);
        handler = new ScanActivityHandler(this);
        beepManager = new BeepManager(this);
        barcodeReaderView.setCallback(text -> {
            barcodeReaderView.stopScanning();
            handleDecodeInternally(text);
        });
        setProperties();
    }

    public Intent getPrefsIntent() {
        return new Intent(ZxingVerticalCaptureActivity.this, PreferenceActivity.class);
    }

    public static int[] combineArrays(int[] src, int[] dest) {
        int length = src.length + dest.length;
        int[] result = new int[length];
        System.arraycopy(src, 0, result, 0, src.length);
        System.arraycopy(dest, 0, result, src.length, dest.length);
        return result;
    }

    public void setProperties() {
        int[] decodeFormats = new int[]{};

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(PreferencesFragment.KEY_DECODE_1D_PRODUCT, true)) {
            decodeFormats = combineArrays(PRODUCT_FORMATS, decodeFormats);
        }
        if (prefs.getBoolean(PreferencesFragment.KEY_DECODE_1D_INDUSTRIAL, true)) {
            decodeFormats = combineArrays(INDUSTRIAL_FORMATS, decodeFormats);
        }
        if (prefs.getBoolean(PreferencesFragment.KEY_DECODE_QR, true)) {
            decodeFormats = combineArrays(QR_CODE_FORMATS, decodeFormats);
        }
        if (prefs.getBoolean(PreferencesFragment.KEY_DECODE_DATA_MATRIX, true)) {
            decodeFormats = combineArrays(DATA_MATRIX_FORMATS, decodeFormats);
        }
        if (prefs.getBoolean(PreferencesFragment.KEY_DECODE_AZTEC, false)) {
            decodeFormats = combineArrays(AZTEC_FORMATS, decodeFormats);
        }
        if (prefs.getBoolean(PreferencesFragment.KEY_DECODE_PDF417, false)) {
            decodeFormats = combineArrays(PDF417_FORMATS, decodeFormats);
        }

        barcodeReaderView.setFormat(decodeFormats);
    }

    public void playBeepSoundAndVibrate() {
        beepManager.playBeepSoundAndVibrate();
    }

    public void playFailedSoundAndVibrate() {
        beepManager.playFailedSoundAndVibrate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PREFS_REQUEST) {
                setProperties();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startCapture() {
        barcodeReaderView.startScanning();
    }

    @Override
    protected void onResume() {
        super.onResume();
        beepManager.updatePrefs();
        //barcodeReaderView.start();
        barcodeReaderView.startPreview();
        barcodeReaderView.startScanning();
    }

    @Override
    public void onPause() {
        super.onPause();
        //barcodeReaderView.stop();
        barcodeReaderView.stopPreview();
        barcodeReaderView.stopScanning();
        beepManager.close();
    }

    public void handleDecodeInternally(String rawResult) {

    }

    public void restartPreviewAfterDelay(long delayMS) {
        handler.sendEmptyMessageDelayed(1, delayMS);
    }
}
