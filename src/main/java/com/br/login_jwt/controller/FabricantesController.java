package com.br.login_jwt.controller;

import com.br.login_jwt.DTO.FabricantesDTO;
import com.br.login_jwt.DTO.FabricantesRequestDTO;
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
    public List<FabricantesDTO> listar() {
        return service.listar().stream().map(FabricantesDTO::from).toList();
    }

    @PostMapping
    public FabricantesDTO criar(@Valid @RequestBody FabricantesRequestDTO request) {
        return FabricantesDTO.from(service.criar(toEntity(request)));
    }

    @PutMapping("/{id}")
    public FabricantesDTO atualizar(@PathVariable Long id, @Valid @RequestBody FabricantesRequestDTO request) {
        return FabricantesDTO.from(service.atualizar(id, toEntity(request)));
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }

    private Fabricantes toEntity(FabricantesRequestDTO request) {
        Fabricantes fabricante = new Fabricantes();
        fabricante.setNome(request.getNome());
        fabricante.setDescricao(request.getDescricao());
        return fabricante;
    }
}
