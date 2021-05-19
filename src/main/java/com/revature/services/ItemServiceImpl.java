package com.revature.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import com.revature.beans.Item;
import com.revature.beans.ItemPrimaryKey;
import com.revature.beans.SoldItem;
import com.revature.repositories.ItemRepository;
import com.revature.utils.IdGenerator;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ItemServiceImpl implements ItemService {
	private static Logger logger = LogManager.getLogger(ItemServiceImpl.class);
	
	private ItemRepository itemRepository;
	
	@Autowired
	public void setItemRepository(ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}

	@Override
	public Mono<Item> addItem(Item item) {
		item.getPrimaryKey().setId(IdGenerator.generate(itemRepository));
		return itemRepository.save(item);
	}
	
	@Override
	public Flux<Item> updateItems(Flux<Item> items) {
		return itemRepository.saveAll(items);
	}

	@Override
	public Mono<Void> removeItem(Flux<ItemPrimaryKey> itemIds) {
		Flux<Item> items = itemIds.map(p -> {
			Item i = new Item();
			i.setPrimaryKey(p);
			return i;
		});
		return itemRepository.deleteAll(items);

	}
	
	@Override
	public Flux<Item> getItems(int storeid) {
		return itemRepository.findAll().filter(i ->
		i.getPrimaryKey().getStoreId()==storeid
		);
		
	}

	@Override
	public Flux<Item> addItemsFromFile(Flux<FilePart> fileFlux, int storeid) {
		Flux<Item> itemsToAdd = fileFlux.flatMap(this::readStringFromFile) // This flat map reads each whole file emitted as a string
				.map(string -> {
					
					//Split the string on new lines, build an item for each, and add to the list
					List<Item> items = new ArrayList<>();
					for(String line : string.split("\\r?\\n")) {
						try {
							Item item = buildItemFromCsvLine(line, storeid);
							items.add(item);
						}catch(Exception e) {
							logger.warn(e);
						}
					}
					
					return items;
				})
				.flatMapIterable(Function.identity()); //Flattens it from Flux<List<Item>> to Flux<Item> contains all items from that list
		return itemRepository.saveAll(itemsToAdd);
	}
	
	private Flux<String> readStringFromFile(FilePart file){
		return file.content().map(buffer -> {
			byte[] bytes = new byte[buffer.readableByteCount()];
             buffer.read(bytes);
             DataBufferUtils.release(buffer);

             return new String(bytes);
		});
	}
	
	private Item buildItemFromCsvLine(String csvLine, int storeId){
		Item item = new Item();
		String[] values = csvLine.split(",");
		ItemPrimaryKey itemKey = new ItemPrimaryKey();
		itemKey.setId(Integer.parseInt(values[0]));
		itemKey.setStoreId(storeId);
		item.setPrimaryKey(itemKey);
		item.setName(values[1]);
		item.setWeight(Double.parseDouble(values[2]));
		item.setCost(Double.parseDouble(values[3]));
		item.setPopularity(Integer.parseInt(values[4]));
		item.setOverstockThreshold(Integer.parseInt(values[5]));
		item.setLowThreshold(Integer.parseInt(values[6]));
		item.setQuantity(Integer.parseInt(values[7]));
		
		return item;
	}

	@Override
	public Mono<Item> getItem(ItemPrimaryKey itemKey) {
		return itemRepository.findById(itemKey);
	}

	@Override
	public Flux<Item> soldItems(Flux<SoldItem> soldItems, int storeid) {
		Flux<Item> updatedInventory = soldItems.map(item -> {
			ItemPrimaryKey key = new ItemPrimaryKey();
			key.setId(item.getId());
			key.setStoreId(storeid);
			
			Item oldItem;
			try {
				oldItem = getItem(key).toFuture().get();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return null;
			} catch (ExecutionException e) {
				return null;
			}
			oldItem.setQuantity(oldItem.getQuantity() - item.getQuantity());
			return oldItem;
		});
		return updateItems(updatedInventory);
	}

}
