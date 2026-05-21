package br.edu.ifba.lagos.operacoes;

import br.edu.ifba.lagos.impl.Lago;
import br.edu.ifba.lagos.ordenador.TipoOrdenacao;

import java.util.List;
import java.util.Map;

public interface Operacoes<Monitorado, Dado> {
    // implementando d.1
    void imprimir(List<Monitorado> lagosMonitorados);

    // implementando d.2
    void imprimir(Map<Monitorado, List<Dado>> amostras);

    // implementando d.3
    Map<Monitorado, List<Dado>> ordenar(Map<Monitorado, List<Dado>> amostras, TipoOrdenacao tipoOrdenacao);

    // implementando d.4
    void buscarTriosDePHNeutro(Map<Lago, List<Dado>> amostras);

    // extra
    void analisarQualidadeLagos(Map<Lago, List<Dado>> amostras);
}
