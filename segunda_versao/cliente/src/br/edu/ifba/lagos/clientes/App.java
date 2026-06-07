package br.edu.ifba.lagos.clientes;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifba.lagos.clientes.impl.ClienteImpl;
import br.edu.ifba.lagos.clientes.impl.Lago;
import br.edu.ifba.lagos.clientes.impl.SensoriamentoImpl;

public class App {

    // Configura o ecossistema para disparar o monitoramento simultâneo de 10 Lagos via Threads
    private static final int TOTAL_LAGOS = 10;

    public static void main(String[] args) throws Exception {
        List<Thread> processos = new ArrayList<>();

        System.out.println("====================================================================");
        System.out.println("  SISTEMA DE MONITORAMENTO DE LAGOS - DISTRIBUÍDO E PARALELIZADO   ");
        System.out.println("  Disparando nós de processamento de borda (Edge Computing)...      ");
        System.out.println("====================================================================");

        // Criação, configuração e inicialização assíncrona das 10 Threads
        for (int i = 0; i < TOTAL_LAGOS; i++) {
            int id = i + 1;
            String nome = "Lago " + id;
            ClienteImpl cliente = new ClienteImpl();

            cliente.configurar(new Lago(id, nome), new SensoriamentoImpl());

            // Acopla a lógica executável do cliente a uma Thread nativa do sistema operacional
            Thread processo = new Thread(cliente);
            processos.add(processo);

            processo.start();
        }

        System.out.println("\n🚀 Todas as " + TOTAL_LAGOS + " Threads foram disparadas e estão processando na borda...");

        // Sincronização - Aguarda a conclusão de todas as Threads
        for (Thread processo : processos) {
            processo.join();
        }

        System.out.println("\n🏁 [CONCLUÍDO] Todas as amostras e relatórios de trios neutros foram processados, encriptados e transmitidos.");
    }
}