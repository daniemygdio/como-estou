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
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import br.com.comoestou.ed.bd.Controlador;

/**
 * Created by Danielle Emygdio on 23/05/2016.
 */
public class UploadAvaliacao extends IntentService {
    private String urlBase = "http://52.67.12.155/submeter.php";
    /* É obrigatório ter um construtor */
    public UploadAvaliacao(){
        super(UploadAvaliacao. class.getSimpleName());
    }
    /* Este método é chamado disparar o serviço */
    @Override
    protected void onHandleIntent(Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getBaseContext(). getSystemService(Context. CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isConnected()) {
                submeter();
                String caminho = Environment. getExternalStorageDirectory().getAbsolutePath()
                        + "/media/audio/notifications/facebook_ringtone_pop.m4a";
                Ringtone ringtone = RingtoneManager. getRingtone(
                        getBaseContext(), Uri. parse(caminho));
                ringtone.play(); /* Dispara o sinal sonoro */
            }
        }
    }
    private void submeter(){
 /* Obtém o IMEI (International Mobile Equipment Identity) do dispositivo */
        TelephonyManager telephonyManager =
                (TelephonyManager) getSystemService(Context. TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId() ;
        if( imei != null ) {
            Controlador controlador = new Controlador(getBaseContext());
            String lista = controlador.selecionarNaoEnviado();
            if( !lista.equals("") ) {
                StringBuilder stringBuilder = null;
                InputStream stream = null;
                try {
                    URL url = new URL(urlBase + "?imei=" + imei + "&lista=" + lista);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000); /* em milésimos de seg. */
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.connect();
                    stream = conn.getInputStream();
 /* Ler do fluxo de entrada (stream) e carregar no objeto stringBuilder */
                    BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                    stringBuilder = new StringBuilder();
                    String linha;
                    while ((linha = br.readLine()) != null) {
                        stringBuilder.append(linha + "\n");
                    }
                    br.close();
  /* Transforma de texto para JSON */
                    JSONObject json = new JSONObject(stringBuilder.toString());
 /* Verifica se existe mensagem de erro */
                    if( json.getString("erro").equals("") ){
                        JSONArray datas = (JSONArray) json.get("lista");
                        for( int i = 0; i < datas.length(); i++ ){
                            controlador.updateEnviado( datas.getString(i) );
                        }
                    }
                } catch (Exception e) {
                    Log. e("AAA", e.getMessage());
                } finally {
 /*tem de garantir que o InputStrem seja fechado*/
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException e) {
                            Log. e("AAA", e.getMessage());
                        }
                    }
                }
            }
        }
    }


}
