package com.neliaoalves.cursomc.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.neliaoalves.cursomc.domain.Cidade;
import com.neliaoalves.cursomc.domain.Cliente;
import com.neliaoalves.cursomc.domain.Endereco;
import com.neliaoalves.cursomc.dto.ClienteDTO;
import com.neliaoalves.cursomc.dto.ClienteNewDTO;
import com.neliaoalves.cursomc.enums.TipoCliente;
import com.neliaoalves.cursomc.repositories.CidadeRepository;
import com.neliaoalves.cursomc.repositories.ClienteRepository;
import com.neliaoalves.cursomc.repositories.EnderecoRepository;
import com.neliaoalves.cursomc.services.exceptions.DataIntegrityException;
import com.neliaoalves.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository repo;
	@Autowired
	private CidadeRepository cidadeRepository;
	@Autowired
	private EnderecoRepository enderecoRepository;

	public Cliente find(Integer id) {
		Optional<Cliente> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objecto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()));
	}

	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj = repo.save(obj);
		enderecoRepository.saveAll(obj.getEnderecos());
		return obj;
	}

	public Cliente update(Cliente obj) {
		Cliente newObj = find(obj.getId());
		updateData(newObj, obj);
		return repo.save(newObj);
	}

	public void delete(Integer id) {
		find(id);
		try {
			repo.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir porque há entidades relacionadas");
		}
	}

	public List<Cliente> findAll() {
		return repo.findAll();
	}

	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return repo.findAll(pageRequest);
	}

	public Cliente fromDTO(ClienteDTO objDto) {
		return new Cliente(objDto.getId(), objDto.getNome(), objDto.getEmail(), null, null);
	}
	
	public Cliente fromDTO(ClienteNewDTO objDto) {
		Cliente cli = new Cliente(null, objDto.getNome(), objDto.getEmail(), objDto.getCpfOuCnpj(), TipoCliente.toEnum(objDto.getTipo()));
		Cidade cid = cidadeRepository.findById(objDto.getCidadeId()).get();
		Endereco end = new Endereco(null, objDto.getLogradouro(), objDto.getNumero(), objDto.getComplemento(), objDto.getBairro(), objDto.getCep(), cli, cid);
		cli.getEnderecos().add(end);
		cli.getTelefone().add(objDto.getTelefone1());
		if(objDto.getTelefone2()!=null) {
			cli.getTelefone().add(objDto.getTelefone2());
		}
		if(objDto.getTelefone3()!=null) {
			cli.getTelefone().add(objDto.getTelefone3());
		}
		return cli;
	}

	private void updateData(Cliente newObj, Cliente obj) {
		newObj.setNome(obj.getNome());
		newObj.setEmail(obj.getEmail());
	}
}
