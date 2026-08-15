package com.br.login_jwt.controller;

import com.br.login_jwt.DTO.CategoriaDTO;
import com.br.login_jwt.DTO.CategoriaRequestDTO;
import com.br.login_jwt.model.Categoria;
import com.br.login_jwt.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    @GetMapping
    public List<CategoriaDTO> listar() {
        return service.listar().stream().map(CategoriaDTO::from).toList();
    }

    @PostMapping
    public CategoriaDTO criar(@Valid @RequestBody CategoriaRequestDTO request) {
        return CategoriaDTO.from(service.criar(toEntity(request)));
    }

    @PutMapping("/{id}")
    public CategoriaDTO atualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequestDTO request) {
        return CategoriaDTO.from(service.atualizar(id, toEntity(request)));
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }

    private Categoria toEntity(CategoriaRequestDTO request) {
        Categoria categoria = new Categoria();
        categoria.setNome(request.getNome());
        categoria.setDescricao(request.getDescricao());
        return categoria;
    }
}
