package br.com.comoestou.ed.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.telephony.TelephonyManager;

/**
 * Created by Danielle Emygdio on 2/06/2016.
 */
public class UploadCadastro extends IntentService {

    public UploadCadastro() {
        super(UploadCadastro.class.getSimpleName());
    }

    /***
     * Este método é chamado disparar o serviço
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getBaseContext(). getSystemService(Context. CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;

        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isConnected()) {
                //submeter();
                String caminho = Environment. getExternalStorageDirectory().getAbsolutePath()
                        + "/media/audio/notifications/facebook_ringtone_pop.m4a";
                Ringtone ringtone = RingtoneManager. getRingtone(
                        getBaseContext(), Uri. parse(caminho));
                ringtone.play(); /* Dispara o sinal sonoro */
            }
        }
    }

    private void submeter() {
        /* Obtém o IMEI (International Mobile Equipment Identity) do dispositivo */
        TelephonyManager telephonyManager =
                (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
    }

}
