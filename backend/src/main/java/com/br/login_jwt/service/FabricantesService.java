package com.br.login_jwt.service;

import com.br.login_jwt.model.Fabricantes;
import com.br.login_jwt.repository.FabricantesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FabricantesService {

    private final FabricantesRepository repository;

    public FabricantesService(FabricantesRepository repository) {
        this.repository = repository;
    }

    public List<Fabricantes> listar() {
        return repository.findAll();
    }

    public Fabricantes criar(Fabricantes fabricante) {
        return repository.save(fabricante);
    }

    public Fabricantes atualizar(Long id, Fabricantes fabricante) {
        Fabricantes existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fabricante não encontrado"));
        existente.setNome(fabricante.getNome());
        existente.setDescricao(fabricante.getDescricao());
        return repository.save(existente);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
