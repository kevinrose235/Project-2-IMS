package com.revature.services;

import org.springframework.http.codec.multipart.FilePart;

import com.revature.beans.Item;
import com.revature.beans.ItemPrimaryKey;
import com.revature.beans.SoldItem;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ItemService {

	public Mono<Item> addItem(Item item);
	
	public Flux<Item> addItemsFromFile(Flux<FilePart> fileFlux, int storeid);
	
	public Flux<Item> updateItems(Flux<Item> items);
	
	public Mono<Void> removeItem(Flux<ItemPrimaryKey> itemIds);
	
	public Flux<Item> getItems(int storeid);
	
	public Mono<Item> getItem(ItemPrimaryKey itemKey);

	Flux<Item> soldItems(Flux<SoldItem> soldItems, int storeid);
}
