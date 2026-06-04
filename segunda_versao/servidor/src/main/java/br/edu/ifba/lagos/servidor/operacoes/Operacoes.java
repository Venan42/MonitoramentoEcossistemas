package br.edu.ifba.lagos.servidor.operacoes;


public interface Operacoes<Lago, Amostra> {
    void gravar(Lago lago, Amostra leitura);

    void gravar(Lago lago, int triosNeutros);

    int detectarTriosNeutros();
}