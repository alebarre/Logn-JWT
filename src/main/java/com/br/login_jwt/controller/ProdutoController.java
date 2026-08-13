package com.br.login_jwt.controller;

import com.br.login_jwt.DTO.ProdutoDTO;
import com.br.login_jwt.DTO.ProdutoRequestDTO;
import com.br.login_jwt.model.Categoria;
import com.br.login_jwt.model.Fabricantes;
import com.br.login_jwt.model.Produto;
import com.br.login_jwt.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProdutoDTO> listar() {
        return service.listar().stream().map(ProdutoDTO::from).toList();
    }

    @PostMapping
    public ProdutoDTO criar(@Valid @RequestBody ProdutoRequestDTO request) {
        return ProdutoDTO.from(service.criar(toEntity(request)));
    }

    @PutMapping("/{id}")
    public ProdutoDTO atualizar(@PathVariable Long id, @Valid @RequestBody ProdutoRequestDTO request) {
        return ProdutoDTO.from(service.atualizar(id, toEntity(request)));
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }

    private Produto toEntity(ProdutoRequestDTO request) {
        Produto produto = new Produto();
        produto.setNome(request.getNome());
        produto.setNumSerie(request.getNumSerie());
        produto.setPreco(request.getPreco());

        Categoria categoria = new Categoria();
        categoria.setId(request.getCategoriaId());
        produto.setCategoria(categoria);

        Fabricantes fabricante = new Fabricantes();
        fabricante.setId(request.getFabricanteId());
        produto.setFabricantes(fabricante);

        return produto;
    }
}
