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

import br.com.comoestou.ed.bd.ArquivoPreferencias;
import br.com.comoestou.ed.bd.Controlador;
import br.com.comoestou.ed.main.ActCadastro;

/**
 * Created by Danielle Emygdio on 23/05/2016.
 */
public class UploadAvaliacao extends IntentService {
    private String urlBaseAvaliacao = "http://52.67.12.155/submeterAvaliacao.php";


    /* É obrigatório ter um construtor */
    public UploadAvaliacao(){
        super(UploadAvaliacao.class.getSimpleName());
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
                submeterAvaliacao();

                String caminho = Environment. getExternalStorageDirectory().getAbsolutePath()
                        + "/media/audio/notifications/facebook_ringtone_pop.m4a";
                Ringtone ringtone = RingtoneManager. getRingtone(
                        getBaseContext(), Uri. parse(caminho));
                ringtone.play(); /* Dispara o sinal sonoro */
            }
        }
    }

    private boolean jaCadastrouDadosPessoais(String imeiRecuperado) {
        ArquivoPreferencias.initializeInstance(getApplicationContext());
        ArquivoPreferencias dadosPessoais = ArquivoPreferencias.getInstance();

        Log.e("UploadAvaliacao", "imeir "+imeiRecuperado+" imei "+dadosPessoais.getImei());
        if(dadosPessoais.getImei()!= null && imeiRecuperado != null && dadosPessoais.getImei().equals(imeiRecuperado)) {
            return true;
        }

        return false;
    }


    private void submeterAvaliacao(){
        /* Obtém o IMEI (International Mobile Equipment Identity) do dispositivo */
        TelephonyManager telephonyManager =
                (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();

        // VERIFICANDO O ARQUIVO DE PREFERENCIAS SE POSSUI DISPOSITIVO CADASTRADO
        if((imei != null) && (!jaCadastrouDadosPessoais(imei))) {
            Intent dialogIntent = new Intent(this, ActCadastro.class);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(dialogIntent);
        }

        // verifica se o imei foi recuperado com sucesso
        if( imei != null ) {
            Controlador controlador = new Controlador(getBaseContext());
            String lista = controlador.selecionarNaoEnviado();

            // verifica se existem dados nao enviados ainda
            if( !lista.equals("") ) {
                // caso existam dados a serem enviados, enviar para o bd
                StringBuilder stringBuilder = null;
                InputStream stream = null;

                try {
                    URL url = new URL(urlBaseAvaliacao + "?imei=" + imei + "&lista=" + lista);
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
}
