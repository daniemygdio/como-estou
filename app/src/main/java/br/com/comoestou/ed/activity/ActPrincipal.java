package br.com.comoestou.ed.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.comoestou.ed.R;
import br.com.comoestou.ed.bd.Avaliacao;
import br.com.comoestou.ed.bd.Controlador;

/**
 * Created by Thiago on 18/05/2016.
 */
public class ActPrincipal extends Activity {
    private ImageButton ibMuitoSatisfeito, ibSatisfeito, ibNeutro, ibInsatisfeito, ibMuitoInsatisfeito;

    private static final int MUITO_SATISFEITO = 1;
    private static final int SATISFEITO = 2;
    private static final int NEUTRO = 3;
    private static final int INSATISFEITO = 4;
    private static final int MUITO_INSATISFEITO = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_principal);

        ibMuitoSatisfeito = (ImageButton) findViewById(R.id.ibMuitoInsatisfeito);
        ibSatisfeito = (ImageButton) findViewById(R.id.ibSatisfeito);
        ibNeutro = (ImageButton) findViewById(R.id.ibNeutro);
        ibInsatisfeito = (ImageButton) findViewById(R.id.ibInsatisfeito);
        ibMuitoInsatisfeito = (ImageButton) findViewById(R.id.ibMuitoInsatisfeito);

        ibMuitoSatisfeito.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                salvarAvaliacao(MUITO_SATISFEITO);
                return true;
            }
        });

        ibSatisfeito.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                salvarAvaliacao(SATISFEITO);
                return true;
            }
        });

        ibNeutro.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                salvarAvaliacao(NEUTRO);
                return true;
            }
        });

        ibInsatisfeito.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                salvarAvaliacao(INSATISFEITO);
                return true;
            }
        });
        ibMuitoInsatisfeito.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                salvarAvaliacao(MUITO_INSATISFEITO);
                return true;
            }
        });
    }

    public void salvarAvaliacao(int avaliacaoEscolhida) {
        Controlador controlador = new Controlador(getBaseContext());
        Avaliacao avaliacao = new Avaliacao();

        avaliacao.setDataAvaliacao(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        avaliacao.setHorario(new SimpleDateFormat("HH:mm:ss").format(new Date()));
        avaliacao.setAvaliacao(avaliacaoEscolhida);
        avaliacao.setEnviado(0); /* zero é o flag de não enviado */

        /* nas API > 22 tem de solicitar a permissão do usuário a
        cada vez que o app usa este tipo de permissão */
        if (android.os.Build.VERSION.SDK_INT > 22) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 12);
            /* 12 é uma constante qualquer */
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            /* PASSIVE_PROVIDER obtém uma localização solicitada por outro aplicativo ou serviço do
            dispositivo, ou seja, não existe garantia de que a leitura seja deste momento.
            Da mesma forma, a leitura pode ser por GPS ou Network */
            Location localizacao = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            /* O resultado será null, caso a localização esteja desabilitada no dispositivo.
            localizacao.getTime() retorna o tempo em milésimos de segundo, por isso dividiu-se por
            1000. Aceita-se uma leitura de localização até 1h atrás */
            if (localizacao != null && ((new Date()).getTime() - localizacao.getTime()) / 1000 < 3600) {
                avaliacao.setLatitude(localizacao.getLatitude());
                avaliacao.setLongitude(localizacao.getLongitude());
            }
        }

        controlador.inserir(avaliacao);

        Toast.makeText(this, getString(R.string.msg_agradecimento), Toast.LENGTH_SHORT).show();
        desabilitarBotoes();
    }

    public void desabilitarBotoes() {
        ibMuitoSatisfeito.setEnabled(false);
        ibMuitoSatisfeito.setClickable(false);
        ibSatisfeito.setEnabled(false);
        ibSatisfeito.setClickable(false);
        ibNeutro.setEnabled(false);
        ibNeutro.setClickable(false);
        ibInsatisfeito.setEnabled(false);
        ibInsatisfeito.setClickable(false);
        ibMuitoInsatisfeito.setEnabled(false);
        ibMuitoInsatisfeito.setClickable(false);
    }

    public void btAbrirActAvaliacao(View v) {
        startActivity(new Intent(this, ActAvaliacao.class));
    }
}