package br.com.comoestou.ed.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.com.comoestou.ed.R;
import br.com.comoestou.ed.bd.Avaliacao;
import br.com.comoestou.ed.bd.Controlador;
import br.com.comoestou.ed.service.UploadAvaliacao;

/**
 * Created by Thiago on 18/05/2016.
 */
public class ActPrincipal extends Activity {
    private static ImageButton ibMuitoSatisfeito, ibSatisfeito, ibNeutro, ibInsatisfeito, ibMuitoInsatisfeito;
    private static TextView tvAlerta, tvMuitoSatisfeito, tvSatisfeito, tvNeutro, tvInsatisfeito, tvMuitoInsatisfeito;
    private int avaliacaoDeHoje = 0;

    private static final int MUITO_SATISFEITO = 1;
    private static final int SATISFEITO = 2;
    private static final int NEUTRO = 3;
    private static final int INSATISFEITO = 4;
    private static final int MUITO_INSATISFEITO = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_principal);
        Stetho.initializeWithDefaults(this);
        inicializarBotoes();
        inserirTeste(10);
    }

    private void inicializarBotoes() {
        ibMuitoSatisfeito = (ImageButton) findViewById(R.id.ibMuitoSatisfeito);
        ibSatisfeito = (ImageButton) findViewById(R.id.ibSatisfeito);
        ibNeutro = (ImageButton) findViewById(R.id.ibNeutro);
        ibInsatisfeito = (ImageButton) findViewById(R.id.ibInsatisfeito);
        ibMuitoInsatisfeito = (ImageButton) findViewById(R.id.ibMuitoInsatisfeito);
        tvAlerta = (TextView) findViewById(R.id.tvAlerta);
        tvMuitoSatisfeito = (TextView) findViewById(R.id.tvMuitoSatisfeito);
        tvSatisfeito = (TextView) findViewById(R.id.tvSatisfeito);
        tvNeutro = (TextView) findViewById(R.id.tvNeutro);
        tvInsatisfeito = (TextView) findViewById(R.id.tvInsatisfeito);
        tvMuitoInsatisfeito = (TextView) findViewById(R.id.tvMuitoInsatisfeito);

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

        if(jaRepondeuHoje()) {
            desabilitarBotoes(avaliacaoDeHoje);
        }
    }

    /***
     * Retorna verdadeiro caso haja uma entrada no banco de dados de avaliação na data atual (hoje).
     * @return true - caso já tenha avaliação do dia atual (hoje).
     */
    private boolean jaRepondeuHoje() {
        Controlador controlador = new Controlador(getBaseContext());
        Avaliacao avaliacao = controlador.selecionarUltimaAvaliacao();
        if(avaliacao != null) {
            // Recebe última data salva no banco
            String dataAvaliacao = avaliacao.getDataAvaliacao();

            // Recebe a data atual e coloca na string formatada
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String dataFormatada = df.format(c.getTime());

            if(dataAvaliacao != null && dataFormatada != null) {
                if(dataAvaliacao.equals(dataFormatada)) {
                    avaliacaoDeHoje = avaliacao.getAvaliacao();
                    return true;
                }
            }
        }

        return false;
    }

    /* Apenas para testar, adiciona quant registros com data dos últimos quant dias */
    private void inserirTeste(int quant) {
        Controlador controlador = new Controlador(getBaseContext());
        Avaliacao avaliacao = new Avaliacao();
        Calendar cal;
        for (int i = 1; i <= quant; i++) {
            cal = Calendar. getInstance();
            cal.add(Calendar. DATE, -1 * i);
            avaliacao.setDataAvaliacao(new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime()));
            avaliacao.setHorario(new SimpleDateFormat("HH:mm:ss").format(cal.getTime()));
            avaliacao.setAvaliacao( (int)(Math. random() * 5) + 1 );
            avaliacao.setEnviado( (int)(Math. random() * 2) );
            controlador.inserir(avaliacao);
        }
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
        desabilitarBotoes(avaliacaoEscolhida);

        /* Dispara o serviço para fazer o upload das avaliações */
        Intent intencao = new Intent(this, UploadAvaliacao. class);
        startService(intencao);
    }

    public void desabilitarBotoes(int avaliacaoEscolhida) {
        ibMuitoSatisfeito.setEnabled(false);
        ibMuitoSatisfeito.setClickable(false);
        ibMuitoSatisfeito.setImageResource(R.drawable.cinza_otimo);
        tvMuitoSatisfeito.setTextColor(ContextCompat.getColor(this, R.color.desabilitado));
        ibSatisfeito.setEnabled(false);
        ibSatisfeito.setClickable(false);
        ibSatisfeito.setImageResource(R.drawable.cinza_bom);
        tvSatisfeito.setTextColor(ContextCompat.getColor(this, R.color.desabilitado));
        ibNeutro.setEnabled(false);
        ibNeutro.setClickable(false);
        ibNeutro.setImageResource(R.drawable.cinza_regular);
        tvNeutro.setTextColor(ContextCompat.getColor(this, R.color.desabilitado));
        ibInsatisfeito.setEnabled(false);
        ibInsatisfeito.setClickable(false);
        ibInsatisfeito.setImageResource(R.drawable.cinza_ruim);
        tvInsatisfeito.setTextColor(ContextCompat.getColor(this, R.color.desabilitado));
        ibMuitoInsatisfeito.setEnabled(false);
        ibMuitoInsatisfeito.setClickable(false);
        ibMuitoInsatisfeito.setImageResource(R.drawable.cinza_pessimo);
        tvMuitoInsatisfeito.setTextColor(ContextCompat.getColor(this, R.color.desabilitado));
        tvAlerta.setText(getResources().getString(R.string.alerta_ja_selecionou_hoje));

        switch (avaliacaoEscolhida) {
            case MUITO_SATISFEITO:
                tvMuitoSatisfeito.setTextColor(ContextCompat.getColor(this, R.color.mainBackground));
                ibMuitoSatisfeito.setImageResource(R.drawable.otimo);
                break;
            case SATISFEITO:
                tvSatisfeito.setTextColor(ContextCompat.getColor(this, R.color.green));
                ibSatisfeito.setImageResource(R.drawable.bom);
                break;
            case NEUTRO:
                tvNeutro.setTextColor(ContextCompat.getColor(this, R.color.yellow));
                ibNeutro.setImageResource(R.drawable.regular);
                break;
            case INSATISFEITO:
                tvInsatisfeito.setTextColor(ContextCompat.getColor(this, R.color.orange));
                ibInsatisfeito.setImageResource(R.drawable.ruim);
                break;
            case MUITO_INSATISFEITO:
                tvMuitoInsatisfeito.setTextColor(ContextCompat.getColor(this, R.color.red));
                ibMuitoInsatisfeito.setImageResource(R.drawable.pessimo);
                break;
            case 0:
                break;
        }
    }

    public void btAbrirActAvaliacao(View v) {
        startActivity(new Intent(this, ActAvaliacao.class));
    }
}