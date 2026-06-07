package br.edu.ifba.lagos.servidor.ordenador;

import java.util.List;

public abstract class Ordenador<TipoDado> {

    protected List<TipoDado> dados;

    protected TipoOrdenacao tipo;

    public Ordenador(List<TipoDado> dados, TipoOrdenacao tipo) {
        this.dados = dados;
        this.tipo = tipo;
    }

    public TipoOrdenacao getTipo() {
        return tipo;
    }

    public List<TipoDado> getDados() {
        return dados;
    }

    public abstract void ordenar();
}
