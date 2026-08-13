package com.br.login_jwt.controller;

import com.br.login_jwt.model.Fabricantes;
import com.br.login_jwt.service.FabricantesService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fabricantes")
public class FabricantesController {

    private final FabricantesService service;

    public FabricantesController(FabricantesService service) {
        this.service = service;
    }

    @GetMapping
    public List<Fabricantes> listar() {
        return service.listar();
    }

    @PostMapping
    public Fabricantes criar(@Valid @RequestBody Fabricantes fabricante) {
        return service.criar(fabricante);
    }

    @PutMapping("/{id}")
    public Fabricantes atualizar(@PathVariable Long id, @Valid @RequestBody Fabricantes fabricante) {
        return service.atualizar(id, fabricante);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
