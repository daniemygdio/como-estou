package br.com.comoestou.ed.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import br.com.comoestou.ed.R;
import br.com.comoestou.ed.bd.Avaliacao;
import br.com.comoestou.ed.bd.Controlador;

/**
 * Created by Danielle Emygdio on 20/05/2016.
 */
public class ActAvaliacao extends Activity {
    private ListView lvAvaliacao;
    private ArrayAdapter<Avaliacao> adpAvaliacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_avaliacao);

        Controlador controlador = new Controlador(getBaseContext());
        lvAvaliacao = (ListView) findViewById(R.id.lvAvaliacao);
        adpAvaliacao = new ArrayAdapter<Avaliacao>(this, android.R.layout.simple_list_item_1, controlador.selecionar());

        lvAvaliacao.setAdapter(adpAvaliacao);
    }
}
