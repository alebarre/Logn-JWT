package com.br.login_jwt.service;

import com.br.login_jwt.exception.RecursoJaExisteException;
import com.br.login_jwt.exception.RecursoNaoEncontradoException;
import com.br.login_jwt.model.Cliente;
import com.br.login_jwt.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Cliente com id " + id + " não encontrado"));
    }

    @Transactional
    public Cliente criar(Cliente cliente) {
        if (clienteRepository.existsByEmailIgnoreCase(cliente.getEmail())) {
            throw new RecursoJaExisteException(
                    "Já existe um cliente cadastrado com o e-mail " + cliente.getEmail());
        }
        cliente.setDataCadastro(LocalDateTime.now());
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente atualizar(Long id, Cliente cliente) {
        Cliente existente = buscarPorId(id);

        if (clienteRepository.existsByEmailIgnoreCaseAndIdNot(cliente.getEmail(), id)) {
            throw new RecursoJaExisteException(
                    "Já existe outro cliente cadastrado com o e-mail " + cliente.getEmail());
        }

        existente.setNome(cliente.getNome());
        existente.setSobrenome(cliente.getSobrenome());
        existente.setTelefone(cliente.getTelefone());
        existente.setEmail(cliente.getEmail());

        // orphanRemoval: substituir a lista remove os endereços antigos do banco
        existente.getEnderecos().clear();
        existente.getEnderecos().addAll(cliente.getEnderecos());

        return clienteRepository.save(existente);
    }

    @Transactional
    public void deletar(Long id) {
        Cliente existente = buscarPorId(id);
        clienteRepository.delete(existente);
    }
}
