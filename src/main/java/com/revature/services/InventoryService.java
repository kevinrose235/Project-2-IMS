package com.revature.services;

import com.revature.beans.Item;
import com.revature.beans.Order;

import reactor.core.publisher.Flux;

public interface InventoryService {

	Flux<Order> orderAlg(Flux<Item> low, Flux<Item> high);
	Flux<Item> createManualOrder(Flux<Item> manualOrders);
	Flux<Item> cyclicalOrder();
	
	
	Flux<Item> updateInventoryBasedOffofOrders(Flux<Order> orders, Flux<Item> allItems);
	
}
