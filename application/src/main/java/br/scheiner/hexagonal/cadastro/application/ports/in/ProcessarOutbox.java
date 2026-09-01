package br.scheiner.hexagonal.cadastro.application.ports.in;

public interface ProcessarOutbox {
    void processarPendentes();
}
