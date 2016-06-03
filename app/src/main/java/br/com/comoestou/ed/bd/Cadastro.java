package br.com.comoestou.ed.bd;

/**
 * Created by Danielle Emygdio on 2/06/2016.
 */
public class Cadastro {
    private String imei;
    private String sexo;
    private int ano;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }
}
