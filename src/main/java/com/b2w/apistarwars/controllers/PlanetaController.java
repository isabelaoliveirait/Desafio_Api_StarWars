package com.b2w.apistarwars.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.b2w.apistarwars.client.SWAPIRestTeamplate;
import com.b2w.apistarwars.models.Planeta;
import com.b2w.apistarwars.models.PlanetaApiSW;
import com.b2w.apistarwars.models.PlanetaResponse;
import com.b2w.apistarwars.services.PlanetaService;
import com.b2w.apistarwars.util.URL;

@RestController
@RequestMapping(value="/planetas")
public class PlanetaController {

	@Autowired
	private PlanetaService service;
	
	@Autowired
	private SWAPIRestTeamplate swapi;
	
	@PostMapping
	public ResponseEntity<Void> inserePlaneta(@RequestBody Planeta planeta){
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(service.insert(planeta).getId()).toUri();
		return ResponseEntity.created(uri).build();
	}
	
	@GetMapping
	public ResponseEntity<List<PlanetaResponse>> encontraTodos(){
		//Testando caso de erro use insereAparicao, tire o 2
		return ResponseEntity.ok().body(insereAparicao2(service.findAll()));
	}
	
	@GetMapping(value="/buscaid")
	public ResponseEntity<PlanetaResponse> encontraPorID(@RequestParam(value="id", defaultValue="") String id){
		Planeta planeta = service.findById(id);
		return ResponseEntity.ok().body(new PlanetaResponse(planeta.getId(),planeta.getNome(),planeta.getClima(),planeta.getTerreno(),swapi.RetornaAparicoes(planeta.getNome())));
	}
	
	@GetMapping(value="/buscanome")
	public ResponseEntity<List<PlanetaResponse>> encontraPorNome(@RequestParam(value="nome", defaultValue="") String nome){
		return ResponseEntity.ok().body(insereAparicao(service.findByNome(URL.decodeParam(nome))));
	}
	
	@DeleteMapping(value="/{id}")
	public ResponseEntity<Void> deletaPlaneta(@PathVariable String id){
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
	
	public PlanetaResponse insereAparicao(Planeta planeta) {
		return new PlanetaResponse(planeta.getId(),planeta.getNome(),planeta.getClima(),planeta.getTerreno(),swapi.RetornaAparicoes(planeta.getNome()));
	}
	
	public List<PlanetaResponse> insereAparicao(List<Planeta> planetas) {
		List<PlanetaResponse> resposta = new ArrayList<>();
		planetas.forEach((x)->resposta.add(new PlanetaResponse(x.getId(),x.getNome(),x.getClima(),x.getTerreno(),swapi.RetornaAparicoes(x.getNome()))));
		return resposta;
	}
	//Teste
	public List<PlanetaResponse> insereAparicao2(List<Planeta> planetas) {
		List<PlanetaResponse> resposta = new ArrayList<>();
		List<PlanetaApiSW> result = swapi.RetornaAparicoes2().getBody().getResults(); 
		for(Planeta x: planetas ) {
		
			resposta.add(encontraAparicao(result,x));
		}
		return resposta;
	}
	//Teste
	private PlanetaResponse encontraAparicao(List<PlanetaApiSW> result,Planeta planeta) {
		
		for(PlanetaApiSW y: result ) {
			System.out.println(y.getName());
			if(planeta.getNome().equals(y.getName())) {
				
				return new PlanetaResponse(planeta.getId(),planeta.getNome(),planeta.getClima(),planeta.getTerreno(),y.getFilms().size());
			}
		}	
		return new PlanetaResponse(planeta.getId(),planeta.getNome(),planeta.getClima(),planeta.getTerreno(),0);
		
	}
	
}
