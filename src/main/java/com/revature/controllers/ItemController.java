package com.revature.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.revature.aspects.Authenticated;
import com.revature.beans.Item;
import com.revature.beans.ItemPrimaryKey;
import com.revature.beans.SoldItem;
import com.revature.services.InventoryService;
import com.revature.services.ItemService;
import com.revature.utils.ResponseBuilder;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
//No RequestMapping as this could possibly handle both "/items" and "/store/:storeId/inventory"
public class ItemController {
	
	@Autowired ItemService itemService;
	//I added this for testing purposes
	@Autowired InventoryService invServ;

	@PostMapping("stores/{storeId}/inventory")
	@Authenticated
	public Mono<ResponseEntity<Object>> submitInventory(ServerWebExchange exchange,
			@PathVariable("storeId") int storeid,
			@RequestPart("file") Flux<FilePart> fileFlux){
		
		Flux<Item> items =  itemService.addItemsFromFile(fileFlux, storeid);
		return ResponseBuilder.created(items);
	}
	
	@PutMapping("stores/{storeid}/inventory")
	@Authenticated
	public  Mono<ResponseEntity<Object>> updateInventory(ServerWebExchange exchange, @RequestBody Flux<Item> updatedInventory){
		Flux<Item> items = itemService.updateItems(updatedInventory);
		return ResponseBuilder.ok(items);
	}
	
	@PutMapping("stores/{storeid}/inventory/sales")
	@Authenticated
	public Mono<ResponseEntity<Object>> soldItems(ServerWebExchange exchange, @PathVariable("storeid") int storeid, @RequestBody Flux<SoldItem> itemsSold){

		Flux<Item> items = itemService.soldItems(itemsSold, storeid);
		return ResponseBuilder.ok(items);
	}
	
	@DeleteMapping("stores/{storeid}/inventory")
	@Authenticated
	public Mono<ResponseEntity<Object>> removeItemFromInventory(ServerWebExchange exchange, @PathVariable("storeid") int storeid, @RequestBody Flux<ItemPrimaryKey> itemIds){
		Flux<ItemPrimaryKey> keys =itemIds.map((i)-> {
			i.setStoreId(storeid);
			return i;
		});
		return itemService.removeItem(keys).flatMap(ResponseBuilder::ok);
	}

	@GetMapping("stores/{storeid}/inventory")
	@Authenticated
	public Mono<ResponseEntity<Object>> getInventory(ServerWebExchange exchange, @PathVariable("storeid") int storeid){
		Flux<Item> items = itemService.getItems(storeid);
		return ResponseBuilder.ok(items);
	}

	@GetMapping("test")
	public Flux<Item> testingAutoorder(ServerWebExchange exchange){
		return invServ.cyclicalOrder();
	}
	
	@PostMapping("stores/{storeid}/order")
//	@Authenticated
	public Flux<Item> createManualOrder(ServerWebExchange exchange, @PathVariable("storeid") int storeid, @RequestBody Flux<Item> manualOrders){
	  
	  Flux<Item> orders=manualOrders.map(o ->{
		  ItemPrimaryKey key =new ItemPrimaryKey();
		  key.setStoreId(storeid);
		  key.setId(o.getPrimaryKey().getId());
		  o.setPrimaryKey(key);
		  return o;
	  });
	  //return orders;
	  return invServ.createManualOrder(orders);
	}

}
