package br.com.comoestou.ed.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class Controlador {
    private SQLiteDatabase bd;
    private Banco banco;

    public Controlador(Context context) {
        banco = new Banco(context);
    }

    /* Adiciona um registro na tbAvaliacao.
    Retorna o ID gerado pelo SQLite ou -1 no caso de erro */
    public long inserir(Avaliacao avaliacao) {
        ContentValues valores = new ContentValues();
        /* Obtém uma conexão para escrita */
        bd = banco.getWritableDatabase();
        valores.put("dataAvaliacao", avaliacao.getDataAvaliacao());
        valores.put("horario", avaliacao.getHorario());
        valores.put("avaliacao", avaliacao.getAvaliacao());
        valores.put("enviado", avaliacao.getEnviado());
        valores.put("latitude", avaliacao.getLatitude());
        valores.put("longitude", avaliacao.getLongitude());
        long id = bd.insert("tbAvaliacao", null, valores);
        bd.close();
        return id;
    }

    /* Retorna todos as avaliações em ordem de data */
    public List<Avaliacao> selecionar() {
        List<Avaliacao> lista = new ArrayList<>();
        Avaliacao avaliacao;
        Cursor cursor;
        /* Obtém uma conexão para leitura */
        bd = banco.getReadableDatabase();
        cursor = bd.rawQuery("select * from tbAvaliacao order by dataAvaliacao desc", null);
        if (cursor != null) {
            cursor.moveToFirst();
            /* Verifica se chegou ao fim */
            while (!cursor.isAfterLast()) {
                avaliacao = new Avaliacao();
                avaliacao.setDataAvaliacao(cursor.getString(0));
                avaliacao.setHorario(cursor.getString(1));
                avaliacao.setAvaliacao(cursor.getInt(2));
                avaliacao.setEnviado(cursor.getInt(3));
                avaliacao.setLatitude(!cursor.isNull(4) ? cursor.getDouble(4) : null);
                avaliacao.setLongitude(!cursor.isNull(5) ? cursor.getDouble(5) : null);
                lista.add(avaliacao);
                cursor.moveToNext();
            }
            cursor.close();
        }
        bd.close();
        return lista;
    }

    public Avaliacao selecionarUltimaAvaliacao() {
        Avaliacao ultimaAvaliacao = new Avaliacao();

        Cursor cursor;
        bd = banco.getReadableDatabase();
        cursor = bd.rawQuery("select * from tbAvaliacao order by dataAvaliacao desc", null);

        if(cursor != null) {
            cursor.moveToFirst();
            ultimaAvaliacao.setDataAvaliacao(cursor.getString(0));
            ultimaAvaliacao.setHorario(cursor.getString(1));
            ultimaAvaliacao.setAvaliacao(cursor.getInt(2));
            ultimaAvaliacao.setEnviado(cursor.getInt(3));
            ultimaAvaliacao.setLatitude(!cursor.isNull(4) ? cursor.getDouble(4) : null);
            ultimaAvaliacao.setLongitude(!cursor.isNull(5) ? cursor.getDouble(5) : null);
        }

        cursor.close();
        return ultimaAvaliacao;
    }

    /* Retorna o número de linhas afetadas.
    * -1 significa erro */
    public int updateEnviado(String dataAvaliacao) {
        ContentValues valores = new ContentValues();
        valores.put("enviado", 1); /* 1 significa enviado para o servidor */
        bd = banco.getWritableDatabase();
         /* 1o parâmetro é o nome dada tabela
         * 2o parâmetro são os novos valores
         * 3o parâmetro é cláusula WHERE
         * 4o parâmetro são argumentos para subsitutuir */
        int quantAfetado = bd.update("tbAvaliacao", valores, "dataAvaliacao='" + dataAvaliacao + "'", null);
        bd.close();
        return quantAfetado;
    }

    /* Retorna todas as avaliações não enviadas no formato:
    dataAvaliacao,horário,avaliacao,envidado,latitude,longitude;
    dataAvaliacao,horário,avaliacao,envidado,latitude,longitude; ...
    Observe que os campos estão separados por vírgula e os registros por ponto e vírgula */
    public String selecionarNaoEnviado() {
        String saida = "";
        Avaliacao avaliacao;
        Cursor cursor;
        /* Obtém uma conexão para leitura */
        bd = banco.getReadableDatabase();
        cursor = bd.rawQuery(
                "select * from tbAvaliacao where enviado = 0 order by dataAvaliacao asc", null);
        if (cursor != null) {
            cursor.moveToFirst();
            /* Verifica se chegou ao fim */
            while (!cursor.isAfterLast()) {
                avaliacao = new Avaliacao();
                avaliacao.setDataAvaliacao(cursor.getString(0));
                avaliacao.setHorario(cursor.getString(1));
                avaliacao.setAvaliacao(cursor.getInt(2));
                avaliacao.setEnviado(cursor.getInt(3));
                avaliacao.setLatitude(!cursor.isNull(4) ? cursor.getDouble(4) : null);
                avaliacao.setLongitude(!cursor.isNull(5) ? cursor.getDouble(5) : null);
                /* Concatena os registros na variável saida usando ponto e vírgula para separá-los */
                saida += saida.equals("") ? avaliacao : ";" + avaliacao;
                cursor.moveToNext();
            }
            cursor.close();
        }
        bd.close();
        return saida;
    }
}