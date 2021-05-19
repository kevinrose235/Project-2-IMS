package com.revature.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.revature.beans.Item;

@Service
public class SalesGeneratorServiceImpl implements SalesGeneratorService {
	private static Logger logger = LogManager.getLogger(SalesGeneratorServiceImpl.class);
	private Random rand = new Random();
	
	@Autowired ItemService itemService;
	
	public List<Integer> getArray(){
		List<Item> items = null;
		try {
			items = itemService.getItems(10).collectList().toFuture().get();
		} catch (InterruptedException e) {
			logger.warn(e);
		    Thread.currentThread().interrupt();
		} catch (ExecutionException e){
		    logger.warn(e);
		}
		ArrayList<Integer> array = new ArrayList<>();
		for(Item item : items) {
			for (int i = 0; i < item.getPopularity(); i++) {
				array.add(item.getId());
			}
		}
		return array;
	}
	
	public int getRandomNumber(int min, int max) {
		int bound = max - min;
		return rand.nextInt(bound) + min;
	}
	
	public List<String> generateSales() {
		List<Integer> popularityArray = getArray();
		ArrayList<String> sales = new ArrayList<>();
		int salesNo = getRandomNumber(50,100);
		int len = popularityArray.size();
		for (int i = 0; i < salesNo; i++) {
			sales.add(popularityArray.get(getRandomNumber(1, len)).toString());
		}
		return sales;
	}
	
	@Override
	public String createSalesCsv(int days) {
		StringBuilder bld = new StringBuilder();
		for (int i = 0; i < days; i++) {
			List<String> data = generateSales();
			String csvLine = String.join(",", data);
			String app = csvLine + "\n";
			bld.append(app);
		}
		return bld.toString();
	}
}
