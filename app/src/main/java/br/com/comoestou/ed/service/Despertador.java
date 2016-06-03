package br.com.comoestou.ed.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/* Esta classe será invocada a cada vez que ocorrer uma alteração no estado da rede.
* Esta definição se encontra na marcação <receiver> do AndroidManifest */
public class Despertador extends BroadcastReceiver {
    /* O método onReceive é chamado quando o objeto entra em execução */
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intencao = new Intent(context, UploadAvaliacao.class);
        context.startService(intencao);
    }
}
