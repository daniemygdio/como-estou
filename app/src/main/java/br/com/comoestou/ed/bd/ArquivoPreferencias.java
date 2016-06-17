package br.com.comoestou.ed.bd;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Danielle Emygdio on 17/06/2016.
 */
public class ArquivoPreferencias {
    private static final String DADOS_PESSOAIS = "br.com.comoestou.bd.DADOS_PESSOAIS";
    private static final String SEXO = "br.com.comoestou.bd.SEXO";
    private static final String IMEI = "br.com.comoestou.bd.IMEI";
    private static final String ANO = "br.com.comoestou.bd.ANO";
    private static final String TAG = "ArquivoPreferencias";

    private static ArquivoPreferencias sInstance;
    private final SharedPreferences mPref;

    private ArquivoPreferencias(Context context) {
        mPref = context.getSharedPreferences(DADOS_PESSOAIS, Context.MODE_PRIVATE);
    }

    public static synchronized void initializeInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ArquivoPreferencias(context);
        }
    }

    public static synchronized ArquivoPreferencias getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException(ArquivoPreferencias.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }
        return sInstance;
    }

    public void setSexo(String value) {
        mPref.edit()
                .putString(SEXO, value)
                .commit();
        Log.i(TAG, "sexo: "+value);
    }

    public String getSexo() {
        return mPref.getString(SEXO, "");
    }

    public void setImei(String value) {
        mPref.edit()
                .putString(IMEI, value)
                .commit();

        Log.i(TAG, "imei: "+value);
    }

    public String getImei() {
        return mPref.getString(IMEI, "");
    }

    public void setAno(String value) {
        mPref.edit()
                .putString(ANO, "")
                .commit();
        Log.i(TAG, "ano: "+value);
    }

    public String getAno() {
        return mPref.getString(ANO, null);
    }

    public void remove(String key) {
        mPref.edit()
                .remove(key)
                .commit();
    }

    public boolean clear() {
        return mPref.edit()
                .clear()
                .commit();
    }
}
