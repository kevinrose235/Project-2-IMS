// package com.revature.services;

// import static org.junit.Assert.assertEquals;

// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.MockedStatic;
// import org.mockito.Mockito;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.TestConfiguration;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.context.annotation.Bean;
// import org.springframework.test.context.junit.jupiter.SpringExtension;

// import com.revature.beans.Store;
// import com.revature.repositories.StoreRepository;
// import com.revature.utils.IdGenerator;

// import reactor.core.publisher.Flux;
// import reactor.core.publisher.Mono;

// @ExtendWith(SpringExtension.class)
// public class StoreServiceImplTest {
	
// 	@TestConfiguration
// 	static class Configuration {
// 		@Bean
// 		public StoreServiceImpl getStoreService(StoreRepository repo) {
// 			StoreServiceImpl storeService = new StoreServiceImpl();
// 			storeService.setStoreRepo(repo);
// 			return storeService;
// 		}
// 	}
	
// 	@Autowired
// 	private StoreServiceImpl storeService;
	
// 	@MockBean
// 	private StoreRepository storeRepo;
	
// 	private static List<Store> stores;
	
// 	@BeforeAll
// 	public static void addStores() {
// 		stores = new ArrayList<>();
		
// 		Store store1 = new Store();
// 		Map<String, Double> store1Location = new HashMap<>();
// 		store1.setId(1);
// 		store1.setName("petco 1");
// 		store1Location.put("longitude", 23.123123);
// 		store1Location.put("latitude", 10.234123);
// 		store1.setLocation(store1Location);
		
// 		stores.add(store1);
		
// 		Store store2 = new Store();
// 		Map<String, Double> store2Location = new HashMap<>();
// 		store2.setId(1);
// 		store2.setName("petco 2");
// 		store2Location.put("longitude", 26.123123);
// 		store2Location.put("latitude", 12.234123);
// 		store2.setLocation(store2Location);
// 		stores.add(store2);
// 	}
	
// 	@Test
// 	public void testDeleteStoreDeletesStore() {
// 		Void v = Mockito.mock(Void.class);
// 		Mono<Void> voidMono = Mono.just(v);
// 		int id = 2;
// 		Mockito.when(storeRepo.deleteById(id)).thenReturn(voidMono);
		
// 		assertEquals("Delete Store should return a Mono<Void>", voidMono, storeService.deleteStore(id));
		
// 	}
	
// 	@Test
// 	public void getAllStoresReturnsStoresFlux() {
// 		Flux<Store> storeFlux = Flux.fromIterable(stores);
// 		Mockito.when(storeRepo.findAll()).thenReturn(storeFlux);
		
// 		assertEquals("Get All Stores should return a flux of all stores", 
// 				storeFlux, storeService.getAllStores());
// 	}
	
// 	@Test
// 	public void getStoreByIdReturnsStoreMono() {
// 		Mono<Store> storeMono = Mono.just(stores.get(0));
// 		int storeId = stores.get(0).getId();
// 		Mockito.when(storeRepo.findById(storeId)).thenReturn(storeMono);
		
// 		assertEquals("Get Store by id returns store mono",
// 				storeMono, storeService.getStoreById(storeId));
// 	}
	
// 	@Test
// 	public void AddStoreReturnStoreMono() {
// 		Store store = stores.get(0);
// 		Mono<Store> storeMono = Mono.just(store);
		
// 		//Mocking a static class
// 		try(MockedStatic<IdGenerator> generator = Mockito.mockStatic(IdGenerator.class)){
// 			generator.when(() -> IdGenerator.generate(storeRepo)).thenReturn(store.getId());
// 			Mockito.when(storeRepo.save(store)).thenReturn(storeMono);
// 			assertEquals("Add store should return mono of store",
// 					storeMono, storeService.addStore(store));
// 		}
// 	}
	
// 	@Test
// 	public void updateStoreReturnsStoreMono() {
// 		Store store = stores.get(0);
// 		Mono<Store> storeMono = Mono.just(store);
		
// 		Mockito.when(storeRepo.save(store)).thenReturn(storeMono);
// 		assertEquals("Update store should return mono of store",
// 				storeMono, storeService.updateStore(store));
// 	}
	
// }
