package br.recife.bancodealimentos.seguranca;

import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Janela deslizante de 1 minuto, em memória (uma instância da aplicação).
 * Para várias instâncias, troque por um limitador compartilhado (ex.: Redis ou gateway).
 */
@Component
public class LimitadorRequisicoes {

    private static final long JANELA_MS = 60_000L;
    private static final int LIMITE_DE_CHAVES_ANTES_DA_LIMPEZA = 10_000;

    private final Map<String, Deque<Long>> janelas = new ConcurrentHashMap<>();

    public boolean permitir(String chave, int limitePorMinuto) {
        long agora = System.currentTimeMillis();
        if (janelas.size() > LIMITE_DE_CHAVES_ANTES_DA_LIMPEZA) {
            limpar(agora);
        }
        Deque<Long> fila = janelas.computeIfAbsent(chave, k -> new ArrayDeque<>());
        synchronized (fila) {
            while (!fila.isEmpty() && agora - fila.peekFirst() > JANELA_MS) {
                fila.pollFirst();
            }
            if (fila.size() >= limitePorMinuto) {
                return false;
            }
            fila.addLast(agora);
            return true;
        }
    }

    private void limpar(long agora) {
        janelas.entrySet().removeIf(entrada -> {
            Deque<Long> fila = entrada.getValue();
            synchronized (fila) {
                return fila.isEmpty() || agora - fila.peekLast() > JANELA_MS;
            }
        });
    }
}
