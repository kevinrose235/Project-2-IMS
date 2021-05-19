package com.revature.services;

import java.util.List;
import java.util.Map;

import org.springframework.http.codec.multipart.FilePart;

import com.revature.beans.Item;

import reactor.core.publisher.Flux;

public interface SalesService {

	Map<Integer, Integer> processSales(List<Integer> salesInput);

	Flux<Item> updateInventory(Integer storeId, List<Integer> salesInput);

	Flux<List<Integer>> getListsFromFile(Flux<FilePart> fileFlux);

}