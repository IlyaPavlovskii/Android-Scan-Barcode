package com.tiromansev.scanbarcode;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import java.util.ArrayList;
import java.util.Collection;

public final class PreferencesFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private CheckBoxPreference[] checkBoxPrefs;

    public static final String CAMERA_ZXING_SCANNER = "0";
    public static final String EXTERNAL_USB_SCANNER = "1";
    public static final String CAMERA_VISION_SCANNER = "2";

    public static final String KEY_DECODE_1D_PRODUCT = "preferences_decode_1D_product";
    public static final String KEY_DECODE_1D_INDUSTRIAL = "preferences_decode_1D_industrial";
    public static final String KEY_DECODE_QR = "preferences_decode_QR";
    public static final String KEY_DECODE_DATA_MATRIX = "preferences_decode_Data_Matrix";
    public static final String KEY_DECODE_AZTEC = "preferences_decode_Aztec";
    public static final String KEY_DECODE_PDF417 = "preferences_decode_PDF417";

    public static final String KEY_PLAY_BEEP = "preferences_play_beep";
    public static final String KEY_VIBRATE = "preferences_vibrate";
    public static final String KEY_FRONT_LIGHT_MODE = "preferences_front_light_mode";
    public static final String KEY_AUTO_FOCUS = "preferences_auto_focus";
    public static final String KEY_INVERT_SCAN = "preferences_invert_scan";
    public static final String KEY_SCAN_TYPE_INT = "preferences_scan_type_int";
    public static final String KEY_SCAN_LAST_SYMBOL = "preferences_scan_last_symbol";
    public static final String KEY_DISABLE_AUTO_ORIENTATION = "preferences_orientation";

    public static final String KEY_DISABLE_CONTINUOUS_FOCUS = "preferences_disable_continuous_focus";
    public static final String KEY_DISABLE_EXPOSURE = "preferences_disable_exposure";
    public static final String KEY_DISABLE_METERING = "preferences_disable_metering";
    public static final String KEY_DISABLE_BARCODE_SCENE_MODE = "preferences_disable_barcode_scene_mode";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.barcode_zxing_preferences);
        setBarcodePreferences(CAMERA_VISION_SCANNER);//default vision scan type

        PreferenceScreen preferences = getPreferenceScreen();
        checkBoxPrefs = findDecodePrefs(preferences,
                KEY_DECODE_1D_PRODUCT,
                KEY_DECODE_1D_INDUSTRIAL,
                KEY_DECODE_QR,
                KEY_DECODE_DATA_MATRIX,
                KEY_DECODE_AZTEC,
                KEY_DECODE_PDF417);
        disableLastCheckedPref();

        //заголовок
        if (getActivity() != null) {
            getActivity().setTitle(R.string.caption_setting_scan);
        }

        initSummaries();
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }

    private static CheckBoxPreference[] findDecodePrefs(PreferenceScreen preferences, String... keys) {
        CheckBoxPreference[] prefs = new CheckBoxPreference[keys.length];
        for (int i = 0; i < keys.length; i++) {
            prefs[i] = (CheckBoxPreference) preferences.findPreference(keys[i]);
        }
        return prefs;
    }

    @Override
    public void onResume() {
        super.onResume();

        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        disableLastCheckedPref();
        updatePrefSummary(findPreference(key));
        if (key.equals(KEY_SCAN_TYPE_INT)) {
            ListPreference scanPreference = (ListPreference) getPreferenceScreen().findPreference(KEY_SCAN_TYPE_INT);
            if (scanPreference != null) {
                setBarcodePreferences(scanPreference.getValue());
            }
        }
    }

    private void setBarcodePreferences(String scanType) {
        getPreferenceScreen().removeAll();
        if (scanType.equals(CAMERA_ZXING_SCANNER)) {
            addPreferencesFromResource(R.xml.barcode_zxing_preferences);
        }
        else if (scanType.equals(EXTERNAL_USB_SCANNER)) {
            addPreferencesFromResource(R.xml.barcode_external_preferences);
        }
        else if (scanType.equals(CAMERA_VISION_SCANNER)) {
            addPreferencesFromResource(R.xml.barcode_vision_preferences);
        }
        initSummaries();
    }

    private void initSummaries() {
        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            initSummary(getPreferenceScreen().getPreference(i));
        }
    }

    private void initSummary(Preference p) {
        if (p instanceof PreferenceCategory) {
            PreferenceCategory pCat = (PreferenceCategory) p;
            for (int i = 0; i < pCat.getPreferenceCount(); i++) {
                initSummary(pCat.getPreference(i));
            }
        } else {
            updatePrefSummary(p);
        }
    }

    private void updatePrefSummary(Preference p) {
        if (p == null) {
            return;
        }
        if (p instanceof ListPreference) {
            ListPreference listPref = (ListPreference) p;
            p.setSummary(listPref.getEntry());
        }
    }

    private void disableLastCheckedPref() {
        Collection<CheckBoxPreference> checked = new ArrayList<>(checkBoxPrefs.length);
        for (CheckBoxPreference pref : checkBoxPrefs) {
            if (pref != null) {
                if (pref.isChecked()) {
                    checked.add(pref);
                }
            }
        }
        boolean disable = checked.size() <= 1;
        for (CheckBoxPreference pref : checkBoxPrefs) {
            if (pref != null) {
                pref.setEnabled(!(disable && checked.contains(pref)));
            }
        }
    }

}
