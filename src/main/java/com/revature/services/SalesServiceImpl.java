package com.revature.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import com.revature.beans.Item;

import reactor.core.publisher.Flux;

@Service
public class SalesServiceImpl implements SalesService {
	private static Logger logger = LogManager.getLogger(SalesServiceImpl.class);
	
	@Autowired ItemService itemService;
	
	@Override
	public Map<Integer, Integer> processSales(List<Integer> salesInput) {
		Map<Integer, Integer> salesMap = new HashMap<>();
		for (Integer element : salesInput) {
			if (salesMap.containsKey(element)) {	//if the map already has this key, up the value by 1
				salesMap.put(element, salesMap.get(element)+1);
			} else {	//if the map doesn't have the key, put value of 1 to said key.
				salesMap.put(element, 1);
			}
		}
		return salesMap;
	}
	
	@Override
	public Flux<Item> updateInventory(Integer storeId, List<Integer> salesInput) {
		Flux<Item> allItems = itemService.getItems(storeId);
		Map<Integer, Integer> salesMap = processSales(salesInput);
		Flux<Item> updated = allItems.map(item -> {
			if (salesMap.containsKey(item.getId())) {
				if (item.getQuantity() <= salesMap.get(item.getId())) {
					item.setQuantity(0);
					return item;
				}
				item.setQuantity(item.getQuantity()-salesMap.get(item.getId()));
			}
			return item;
		});
		return itemService.updateItems(updated);
	}
	
	@Override
	public Flux<List<Integer>> getListsFromFile(Flux<FilePart> fileFlux) {
		return fileFlux.flatMap(this::readStringFromFile)
				.map(string -> {
					List<List<Integer>> allOrders = new ArrayList<>();
					for(String line : string.split("\\r?\\n")) {
						try {
							List<Integer> intLine = buildIntListFromCsvLine(line);
							allOrders.add(intLine);
						}catch(Exception e) {
							logger.warn(e);
						}
					}
					return allOrders;
				})
				.flatMapIterable(Function.identity()); //Flattens it from Flux<List<Item>> to Flux<Item> contains all items from that list
	}
	
	private Flux<String> readStringFromFile(FilePart file){
		return file.content().map(buffer -> {
			byte[] bytes = new byte[buffer.readableByteCount()];
             buffer.read(bytes);
             DataBufferUtils.release(buffer);

             return new String(bytes);
		});
	}
	
	private List<Integer> buildIntListFromCsvLine(String csvLine){
		List<Integer> list = new ArrayList<>();
		String[] values = csvLine.split(",");
		for (String element : values) {
			Integer intElement = Integer.parseInt(element);
			list.add(intElement);
		}
		return list;
	}
}
