package com.revature.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import com.revature.beans.Store;
import com.revature.repositories.StoreRepository;
import com.revature.utils.IdGenerator;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StoreServiceImpl implements StoreService {
	private static Logger logger = LogManager.getLogger(StoreServiceImpl.class);

	private StoreRepository storeRepository;
	
	@Autowired
	public void setStoreRepo(StoreRepository storeRepo) {
		this.storeRepository=storeRepo;
	}

	@Override
	public Flux<Store> getAllStores() {
		return storeRepository.findAll();
	}

	@Override
	public Mono<Void> deleteStore(int id) {
		return storeRepository.deleteById(id);
		
	}

	@Override
	public Mono<Store> getStoreById(int id) {
		return storeRepository.findById(id);
	}

	@Override
	public Mono<Store> addStore(Store store) {
		store.setId(IdGenerator.generate(storeRepository));
		return storeRepository.save(store);
	}
	
	@Override
	public Mono<Store> updateStore(Store store) {
		return storeRepository.save(store);
	}
	
	@Override
	public Flux<Store> addStoresFromFile(Flux<FilePart> fileFlux) {
		Flux<Store> storesToAdd = fileFlux.flatMap(this::readStringFromFile) // This flat map reads each whole file emitted as a string
				.map(string -> {
					
					//Split the string on new lines, build an item for each, and add to the list
					List<Store> stores = new ArrayList<>();
					for(String line : string.split("\\r?\\n")) {
						try {
							Store store = buildStoreFromCsvLine(line);
							stores.add(store);
						}catch(Exception e) {
							logger.warn(e);
						}
					}
					
					return stores;
				})
				.flatMapIterable(Function.identity()); //Flattens it from Flux<List<Item>> to Flux<Item> contains all items from that list
		return storeRepository.saveAll(storesToAdd);
	}
	
	private Flux<String> readStringFromFile(FilePart file){
		return file.content().map(buffer -> {
			byte[] bytes = new byte[buffer.readableByteCount()];
             buffer.read(bytes);
             DataBufferUtils.release(buffer);

             return new String(bytes);
		});
	}
	
	private Store buildStoreFromCsvLine(String csvLine){
		Store store = new Store();
		String[] values = csvLine.split(",");
		Map<String, Double> coordinates = new HashMap<>();
		coordinates.put("latitude", Double.parseDouble(values[2]));
		coordinates.put("longitude", Double.parseDouble(values[3]));
		store.setLocation(coordinates);
		store.setId(Integer.parseInt(values[0]));
		store.setName(values[1]);
		return store;
	}

}
