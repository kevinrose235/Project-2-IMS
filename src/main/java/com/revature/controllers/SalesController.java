package com.revature.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.revature.beans.Item;
import com.revature.services.SalesService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class SalesController {
	
	@Autowired SalesService salesService;
	
	@PutMapping("items/inventory/sales")
	public Mono<List<List<Integer>>> processAllStoreSales(ServerWebExchange exchange, @RequestPart("file") Flux<FilePart> fileFlux){
		return salesService.getListsFromFile(fileFlux).collectList()
				.map(megaList -> {
			for (int i = 0; i < 10; i++) {
				salesService.updateInventory(i + 1, megaList.get(i)).subscribe();
				}
			return megaList;
			});
	}
	
	@PutMapping ("items/inventory/test")
	public Flux<Item> updateInventoryTest(ServerWebExchange exchange){
		List<Integer> list = new ArrayList<>();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(3);
		list.add(4);
		return salesService.updateInventory(1, list);
	}
}
