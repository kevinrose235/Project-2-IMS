package com.revature.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.revature.services.SalesGeneratorService;

@RestController
public class DataGeneratorController {
	
	@Autowired SalesGeneratorService salesGeneratorService;
	
	@GetMapping ("sales/generateSales/{days}")
	public String generateSomeSales(ServerWebExchange exchange, @PathVariable int days) {
		return salesGeneratorService.createSalesCsv(days);
	}
}
