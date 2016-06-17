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
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import br.com.comoestou.ed.bd.ArquivoPreferencias;
import br.com.comoestou.ed.bd.Controlador;

/**
 * Created by Danielle Emygdio on 17/06/2016.
 */
public class UploadCadastro extends IntentService {
    private String urlBaseCadastro = "http://52.67.12.155/cadastrar.php"; // ?imei=123&sexo=m&ano=1990

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public UploadCadastro(String name) {
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
        NetworkInfo networkInfo;

        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isConnected()) {
                submeterCadastro();

                String caminho = Environment. getExternalStorageDirectory().getAbsolutePath()
                        + "/media/audio/notifications/facebook_ringtone_pop.m4a";
                Ringtone ringtone = RingtoneManager. getRingtone(
                        getBaseContext(), Uri. parse(caminho));
                ringtone.play(); /* Dispara o sinal sonoro */
            }
        }
    }

    private void submeterCadastro() {
        Controlador controlador = new Controlador(getBaseContext());
        String lista = controlador.selecionarNaoEnviado();

        // verifica se existem dados nao enviados ainda
        if( !lista.equals("") ) {
            // caso existam dados a serem enviados, enviar para o bd
            StringBuilder stringBuilder = null;
            InputStream stream = null;

            ArquivoPreferencias.initializeInstance(getApplicationContext());
            ArquivoPreferencias dados = ArquivoPreferencias.getInstance();

            try {
                URL url = new URL(urlBaseCadastro + "?imei="+dados.getImei()+"&sexo="+dados.getSexo()+"&ano="+dados.getAno());
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
                Log.e("AAA", e.getMessage());
            } finally {
                     /*tem de garantir que o InputStrem seja fechado*/
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        Log.e("AAA", e.getMessage());
                    }
                }
            }
        }
    }

}
