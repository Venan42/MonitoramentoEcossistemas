package br.edu.ifba.lagos.clientes;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifba.lagos.clientes.impl.ClienteImpl;
import br.edu.ifba.lagos.clientes.impl.Lago;
import br.edu.ifba.lagos.clientes.impl.SensoriamentoImpl;

public class App {

    private static final int TOTAL_NOBREAKS = 10;

    public static void main(String[] args) throws Exception {
        List<Thread> processos = new ArrayList<>();

        for (int i = 0; i < TOTAL_NOBREAKS; i++) {
            int id = i + 1;

            ClienteImpl cliente = new ClienteImpl();
            cliente.configurar(new Lago(id, "Lago 1"), new SensoriamentoImpl());

            Thread processo = new Thread(cliente);
            processos.add(processo);
            processo.start();
        }

        for (Thread processo : processos) {
            processo.join();
        }

        System.out.println("amostras enviadas");
    }
}