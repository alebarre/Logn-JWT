package com.br.login_jwt.service;

import com.br.login_jwt.exception.RecursoNaoEncontradoException;
import com.br.login_jwt.model.Categoria;
import com.br.login_jwt.model.Fabricantes;
import com.br.login_jwt.model.Produto;
import com.br.login_jwt.repository.CategoriaRepository;
import com.br.login_jwt.repository.FabricantesRepository;
import com.br.login_jwt.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final FabricantesRepository fabricantesRepository;

    public ProdutoService(ProdutoRepository produtoRepository,
                          CategoriaRepository categoriaRepository,
                          FabricantesRepository fabricantesRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
        this.fabricantesRepository = fabricantesRepository;
    }

    public List<Produto> listar() {
        return produtoRepository.findAll();
    }

    public Produto criar(Produto produto) {
        produto.setCategoria(resolverCategoria(produto.getCategoria()));
        produto.setFabricantes(resolverFabricante(produto.getFabricantes()));
        produto.setDataCadastro(LocalDateTime.now());

        return produtoRepository.save(produto);
    }

    public Produto atualizar(Long id, Produto produto) {
        Produto existente = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        existente.setNome(produto.getNome());
        existente.setCategoria(resolverCategoria(produto.getCategoria()));
        existente.setNumSerie(produto.getNumSerie());
        existente.setPreco(produto.getPreco());
        existente.setFabricantes(resolverFabricante(produto.getFabricantes()));

        return produtoRepository.save(existente);
    }

    public void deletar(Long id) {
        produtoRepository.deleteById(id);
    }

    private Categoria resolverCategoria(Categoria categoria) {
        if (categoria == null || categoria.getId() == null) {
            throw new RecursoNaoEncontradoException("Categoria é obrigatória e deve referenciar um id válido");
        }
        return categoriaRepository.findById(categoria.getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Categoria com id " + categoria.getId() + " não encontrada"));
    }

    private Fabricantes resolverFabricante(Fabricantes fabricante) {
        if (fabricante == null || fabricante.getId() == null) {
            throw new RecursoNaoEncontradoException("Fabricante é obrigatório e deve referenciar um id válido");
        }
        return fabricantesRepository.findById(fabricante.getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Fabricante com id " + fabricante.getId() + " não encontrado"));
    }
}
