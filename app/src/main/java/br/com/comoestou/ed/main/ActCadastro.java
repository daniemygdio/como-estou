package br.com.comoestou.ed.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.comoestou.ed.R;
import br.com.comoestou.ed.bd.ArquivoPreferencias;
import br.com.comoestou.ed.bd.Cadastro;
import br.com.comoestou.ed.service.UploadAvaliacao;
import br.com.comoestou.ed.service.UploadCadastro;

/**
 * Created by Danielle Emygdio on 2/06/2016.
 */
public class ActCadastro extends Activity {
    private Spinner spSexo;
    private EditText etAno;
    private Button btCadastrar;
    private Cadastro cadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_cadastro);

        initializeButtons();
    }

    private void initializeButtons() {
        spSexo = (Spinner) findViewById(R.id.spSexo);
        etAno = (EditText) findViewById(R.id.etAno);
        btCadastrar = (Button) findViewById(R.id.btCadastrar);

        btCadastrar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                salvarCadastro();
                terminar();
                return true;
            }
        });
    }

    private void terminar() {
        Toast.makeText(this, getString(R.string.agreadecimento), Toast.LENGTH_SHORT).show();
        //startActivity(new Intent(this, ActPrincipal.class));
    }

    private void salvarCadastro() {
        /* Obtém o IMEI (International Mobile Equipment Identity) do dispositivo */
        TelephonyManager telephonyManager =
                (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();

        // salvando no preferencias
        ArquivoPreferencias.initializeInstance(getApplicationContext());
        ArquivoPreferencias cadastro = ArquivoPreferencias.getInstance();

        cadastro.setAno(etAno.getText().toString());
        cadastro.setImei(imei);
        cadastro.setSexo(spSexo.getSelectedItem().toString());

        /* Dispara o serviço para fazer o upload dos cadastros */
        Intent intencao = new Intent(this, UploadCadastro.class);
        startService(intencao);
    }
}
