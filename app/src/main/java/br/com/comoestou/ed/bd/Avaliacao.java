package br.com.comoestou.ed.bd;

/**
 * Created by Danielle Emygdio on 20/05/2016.
 */
public class Avaliacao {
    private String dataAvaliacao;
    private String horario;
    private int avaliacao;
    private int enviado;
    private Double latitude;
    private Double longitude;
    public String getDataAvaliacao() {
        return dataAvaliacao;
    }
    public void setDataAvaliacao(String dataAvaliacao) {
        this. dataAvaliacao = dataAvaliacao;
    }
    public Double getLongitude() {
        return longitude;
    }
    public void setLongitude(Double longitude) {

        this. longitude = longitude;
    }
    public String getHorario() {
        return horario;
    }
    public void setHorario(String horario) {
        this. horario = horario;
    }
    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this. latitude = latitude;
    }
    public int getEnviado() {
        return enviado;
    }
    public void setEnviado(int enviado) {
        this. enviado = enviado;
    }
    public int getAvaliacao() {
        return avaliacao;
    }
    public void setAvaliacao(int avaliacao) {
        this. avaliacao = avaliacao;
    }
    @Override
    public String toString() {
        return "{" +
                "dataAvaliacao:'" + dataAvaliacao + " ' " +
                ", horario:'" + horario + " ' " +
                ", avaliacao:" + avaliacao +
                ", enviado:" + enviado +
                ", latitude:" + latitude +
                ", longitude:" + longitude +
                '}' ;
    }
}
