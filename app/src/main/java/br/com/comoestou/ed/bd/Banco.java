package br.com.comoestou.ed.bd;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/* SQLiteDatabase: classe que contém os métodos de manipulação dos dados no banco;
* SQLiteOpenHelper: classe responsável pela criação do BD e também responsável
* pelo versionamento do mesmo. */
public class Banco extends SQLiteOpenHelper {
    /* Nome do BD e a versão atual. A versão é usada para indicar
    * que o aplicativo não esteja usando tabelas com esquemas desatualizados */
    private static final String BD_NOME = "bdCadastro";
    private static final int VERSAO = 1;
    /* O BD é por aplicativo, por isso precisamos fornecer o Context */
    public Banco(Context context) {
        super(context, BD_NOME, null, VERSAO);
    }
    /* É chamado quando a aplicação cria o BD pela primeira vez.
    Nesse método devem estar as cláusulas de criação das tabelas do BD */
    @Override public void onCreate(SQLiteDatabase bd) {
        String sql = "CREATE TABLE if not exists tbAvaliacao (" +
                " dataAvaliacao text primary key," +
                " horario text not null," +
                " avaliacao integer not null," +
                " enviado integer not null," +
                " latitude REAL NULL," +
                " longitude REAL NULL)";
        bd.execSQL(sql);
    }
    /* Este método é chamado automaticamente a cada vez que a versão do BD é incrementada.
     Aqui estamos usando a VERSAO = 1, mas se quisermos chamar o método onUpgrade basta
    mudar a VERSAO para 2 ou mais.
    Esse mecanismo existe para garantir que não exista inconsistência de dados entre o BD
    existente no aparelho e o novo que a aplicação irá utilizar */
    @Override public void onUpgrade(SQLiteDatabase bd, int oldVersion, int newVersion) {
        try {
            bd.execSQL("drop table if exists tbAvaliacao");
        } catch( SQLException e ){
            Log. e("ERRO", e.getMessage() );
        }
        onCreate(bd);
    }
}

