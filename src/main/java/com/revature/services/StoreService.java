package com.revature.services;

import org.springframework.http.codec.multipart.FilePart;

import com.revature.beans.Store;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StoreService {

	public Mono<Void> deleteStore(int storeid);

	public Mono<Store> addStore(Store store);
	
	public Mono<Store> getStoreById(int id);
	
	public Mono<Store> updateStore(Store store);
	
	public Flux<Store> getAllStores();
	
	public Flux<Store> addStoresFromFile(Flux<FilePart> fileFlux);
	
}
