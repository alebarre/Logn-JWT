package com.br.login_jwt.service;

import com.br.login_jwt.model.Produto;
import com.br.login_jwt.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public List<Produto> listar() {
        return repository.findAll();
    }

    public Produto criar(Produto produto) {
        produto.setDataCadastro(LocalDateTime.now());
        return repository.save(produto);
    }

    public Produto atualizar(Long id, Produto produto) {
        Produto existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        existente.setNome(produto.getNome());
        existente.setCategoria(produto.getCategoria());
        existente.setNumSerie(produto.getNumSerie());
        existente.setPreco(produto.getPreco());

        return repository.save(existente);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
