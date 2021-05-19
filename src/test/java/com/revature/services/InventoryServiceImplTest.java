package com.revature.services;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.revature.beans.Contract;
import com.revature.beans.Item;
import com.revature.beans.ItemPrimaryKey;
import com.revature.beans.Order;
import com.revature.beans.Store;
import com.revature.repositories.ContractRepository;
import com.revature.repositories.ItemRepository;
import com.revature.repositories.StoreRepository;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

@ExtendWith(SpringExtension.class)
class InventoryServiceImplTest {
	@TestConfiguration
	static class Configuration {
		@Bean
		public InventoryServiceImpl getInventoryService(ItemRepository itemRepo, StoreRepository storeRepo, ContractRepository conRepo) {
			InventoryServiceImpl inventoryService = new InventoryServiceImpl();
			inventoryService.setItemRepo(itemRepo);
			inventoryService.setContractRepo(conRepo);
			inventoryService.setStoreRepo(storeRepo);
			return inventoryService;
		}
	}
	
	@Autowired
	private InventoryServiceImpl invService;
	
	@MockBean
	private StoreRepository storeRepo;
	@MockBean
	private ContractRepository contractRepo;
	@MockBean
	private ItemRepository itemRepo;
	
	private static List<Store> stores;
	private static List<Contract> contracts;
	private static List<Item> items;
	
	@BeforeAll
	public static void addItems() { 
		items = new ArrayList<>();
		
		Item item1 = new Item();
		ItemPrimaryKey key1= new ItemPrimaryKey();
		key1.setId(1);
		key1.setStoreId(1);
		item1.setPrimaryKey(key1);
		item1.setName("who cares");
		item1.setWeight(0);
		item1.setPopularity(0);
		item1.setOverstockThreshold(10);
		item1.setLowThreshold(4);
		item1.setQuantity(20);
		items.add(item1);
		
		Item item2 = new Item();
		ItemPrimaryKey key2= new ItemPrimaryKey();
		key2.setId(1);
		key2.setStoreId(2);
		item2.setPrimaryKey(key2);
		item2.setName("who cares");
		item2.setWeight(0);
		item2.setPopularity(0);
		item2.setOverstockThreshold(10);
		item2.setLowThreshold(4);
		item2.setQuantity(4);
		items.add(item2);
		
		Item item3 = new Item();
		ItemPrimaryKey key3= new ItemPrimaryKey();
		key3.setId(1);
		key3.setStoreId(3);
		item3.setPrimaryKey(key3);
		item3.setName("who cares");
		item3.setWeight(0);
		item3.setPopularity(0);
		item3.setOverstockThreshold(10);
		item3.setLowThreshold(4);
		item3.setQuantity(4);
		items.add(item3);
		
		Item item4 = new Item();
		ItemPrimaryKey key4= new ItemPrimaryKey();
		key4.setId(1);
		key4.setStoreId(4);
		item4.setPrimaryKey(key4);
		item4.setName("who cares");
		item4.setWeight(0);
		item4.setPopularity(0);
		item4.setOverstockThreshold(10);
		item4.setLowThreshold(4);
		item4.setQuantity(20);
		items.add(item4);
		
	}
	
	@BeforeAll
	public static void addStores() {
		stores = new ArrayList<>();
		
		Store store1 = new Store();
		Map<String, Double> store1Location = new HashMap<>();
		store1.setId(1);
		store1.setName("closeSurplus");
		store1Location.put("longitude", 23.0);
		store1Location.put("latitude", 10.0);
		store1.setLocation(store1Location);
		stores.add(store1);
		
		Store store2 = new Store();
		Map<String, Double> store2Location = new HashMap<>();
		store2.setId(2);
		store2.setName("need1");
		store2Location.put("longitude", 24.0);
		store2Location.put("latitude", 11.0);
		store2.setLocation(store2Location);//156 miles from store 1
		stores.add(store2);
		
		Store store3 = new Store();
		Map<String, Double> store3Location = new HashMap<>();
		store3.setId(3);
		store3.setName("need2");
		store3Location.put("longitude", 22.0);
		store3Location.put("latitude", 09.0);
		store3.setLocation(store3Location);//156 kms from store 1, 313 kms from store2
		stores.add(store3);
		
		Store store4 = new Store();
		Map<String, Double> store4Location = new HashMap<>();
		store4.setId(4);
		store4.setName("farSurplus");
		store4Location.put("longitude", 20.0);
		store4Location.put("latitude", 07.0);
		store4.setLocation(store4Location);
		stores.add(store4);
	}
	
	 @Test
	 void freeShippingBothRedistribute() {
	 	contracts= new ArrayList<>();
	 	Contract con= new Contract();
	 	con.setItemId(1);
	 	con.setItemMSRP(200);
	 	con.setItemCost(100);
	 	con.setSupplier("unimportant but needed for testing");
	 	con.setMinOrderCost(0);
	 	con.setShippingCost(0);
	 	con.setQuantity(10);
	 	contracts.add(con);
	 	//I have to do it for each test since i want to change parameters
		
	 	Flux<Store> storeFlux = Flux.fromIterable(stores);
	 	Flux<Item> itemFlux = Flux.fromIterable(items);
	 	Flux<Item> low= itemFlux.filter(i->i.getQuantity()<=i.getLowThreshold());
	 	Flux<Item> high= itemFlux.filter(i->i.getQuantity()>i.getOverstockThreshold());
	 	Flux<Contract> conFlux = Flux.fromIterable(contracts);
		
	 	Mockito.when(storeRepo.findAll()).thenReturn(storeFlux);
	 	Mockito.when(contractRepo.findAll()).thenReturn(conFlux);
	 	AtomicInteger orders=new AtomicInteger();
	 	
	 	invService.orderAlg(low,high).subscribe(order->{
	 		assertFalse(order.getFromDistributor(),"Each Order should have false due to free shipping");
	 		orders.incrementAndGet();
	 	});
	 	assertEquals(2,orders.get(), "There should be two orders total");
	 }

	 @Test
	 void interestingShipping() {
	 	/*
	 	 * okay so the two stores need 6 items
	 	 * they must buy 10
	 	 * so they would be over-ordering by $400
	 	 * but they only make 100 dollars on each item so they also only expect to make 600 dollars when all is sold 
	 	 * and they need to make a profit so the shipping costs should be under 300 so they can still make 300 off of it
	 	 * 156< ~200/dollarperkmperamount/amount<313-->~200=200/(6*x)-> shipping cost per mile per amount should be ~0.17
	 	 * */
	 	contracts= new ArrayList<>();
	 	Contract con= new Contract();
	 	con.setItemId(1);
	 	con.setItemMSRP(200);
	 	con.setItemCost(100);
	 	con.setSupplier("unimportant but needed for testing");
	 	con.setMinOrderCost(0);
	 	con.setShippingCost(0.17);
	 	con.setQuantity(10);
	 	contracts.add(con);
	 	//I have to do it for each test since i want to change parameters
		
	 	Flux<Store> storeFlux = Flux.fromIterable(stores);
	 	Flux<Item> itemFlux = Flux.fromIterable(items);
	 	Flux<Item> low= itemFlux.filter(i->i.getQuantity()<=i.getLowThreshold());
	 	Flux<Item> high= itemFlux.filter(i->i.getQuantity()>i.getOverstockThreshold());
	 	Flux<Contract> conFlux = Flux.fromIterable(contracts);
		
	 	Mockito.when(storeRepo.findAll()).thenReturn(storeFlux);
	 	Mockito.when(contractRepo.findAll()).thenReturn(conFlux);
	 	AtomicInteger buy=new AtomicInteger();
	 	AtomicInteger redis=new AtomicInteger();

	 	ConnectableFlux<Order> orders= invService.orderAlg(low,high).publish();
	 	Flux.from(orders).filter(o->o.getFromDistributor()).subscribe(o->buy.incrementAndGet());
	 	Flux.from(orders).filter(o->!o.getFromDistributor()).subscribe(o->redis.incrementAndGet());
	 	orders.connect();
	 	assertEquals(1,buy.get(), "There should be 1 order that is bought new");
	 	assertEquals(1,redis.get(), "There should be 1 order that is redistributed");
	 }

}
