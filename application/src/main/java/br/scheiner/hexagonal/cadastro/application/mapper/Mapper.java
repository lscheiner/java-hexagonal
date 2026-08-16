package br.scheiner.hexagonal.cadastro.application.mapper;

public interface Mapper<I, O> {
    O map(I input);
}
