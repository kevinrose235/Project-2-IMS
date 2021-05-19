package com.revature.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.revature.aspects.Authenticated;
import com.revature.aspects.CorporateAccess;
import com.revature.beans.Store;
import com.revature.exceptions.InvalidDataException;
import com.revature.exceptions.NoStoreFoundException;
import com.revature.services.StoreService;
import com.revature.utils.ResponseBuilder;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/stores")
public class StoreController {

	private StoreService storeService;

	@Autowired
	public void setStoreService(StoreService ss) {
		this.storeService = ss;
	}

	@GetMapping
	@Authenticated
	public Mono<ResponseEntity<Object>> getStores(ServerWebExchange exchange) {
		Flux<Store> stores = storeService.getAllStores();
		return ResponseBuilder.ok(stores);
	}

	@GetMapping("{storeid}")
	@Authenticated
	public Mono<ResponseEntity<Object>> getStoreById(ServerWebExchange exchange, @PathVariable("storeid") int id) {
		return storeService.getStoreById(id).switchIfEmpty(Mono.just(new Store())).flatMap(s -> {
			if (s.getName() == null || s.getName().isEmpty()) throw new NoStoreFoundException("No store found with provided id");
			return ResponseBuilder.ok(s);
		});
	}

	@DeleteMapping("{storeid}")
	@CorporateAccess
	public Mono<ResponseEntity<Object>> deleteStore(ServerWebExchange exchange, @PathVariable("storeid") int storeid) {

		return storeService.deleteStore(storeid).flatMap(d -> ResponseBuilder.ok("Store successfully deleted"));
	}

	@PostMapping
	@CorporateAccess
	public Mono<ResponseEntity<Object>> registerStore(ServerWebExchange exchange, @RequestBody Store store) {
		String storeName = store.getName();
		Map<String, Double> location = store.getLocation();

		if (location == null)
			throw new InvalidDataException("Must provide a valid location");
		if (storeName == null || storeName.trim().isEmpty())
			throw new InvalidDataException("Must provide a store name");
		
		return storeService.addStore(store).flatMap(ResponseBuilder::created);
	}

	// Upload existing stores from CSV
	@PostMapping("existingStores")
	@CorporateAccess
	public Mono<ResponseEntity<Object>> submitInventory(ServerWebExchange exchange,
			@RequestPart("file") Flux<FilePart> fileFlux) {

		Flux<Store> stores = storeService.addStoresFromFile(fileFlux);
		return ResponseBuilder.created(stores);
	}

}
