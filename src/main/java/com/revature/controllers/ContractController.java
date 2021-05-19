package com.revature.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.revature.aspects.CorporateAccess;
import com.revature.beans.Contract;
import com.revature.exceptions.DataAlreadyExistsException;
import com.revature.exceptions.InvalidDataException;
import com.revature.exceptions.NoContractFoundException;
import com.revature.services.ContractService;
import com.revature.utils.ResponseBuilder;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/contracts")
public class ContractController {
	private ContractService contractService;

	@Autowired
	public void setContractService(ContractService contractService) {
		this.contractService = contractService;
	}
	
	// NEW CONTRACT
	@PostMapping()
	@CorporateAccess
	public Mono<ResponseEntity<Object>> newContract (ServerWebExchange exchange, @RequestBody Contract newContract) {
		// variables for readability
		int itemId = newContract.getItemId();
		double itemCost = newContract.getItemCost();
		double itemMSRP = newContract.getItemMSRP();
		double minOrderCost = newContract.getMinOrderCost();
		double shippingCost = newContract.getShippingCost();
		int quantity = newContract.getQuantity();
		String supplier = newContract.getSupplier();
		
		// incoming field validation
		if (itemId == 0) throw new InvalidDataException("Must provide an item id");
		if (itemCost == 0) throw new InvalidDataException("Must provide an item cost");
		if (itemMSRP == 0) throw new InvalidDataException("Must provide an item MSRP");
		if (minOrderCost == 0) throw new InvalidDataException("Must provide a minimum order cost");
		if (shippingCost == 0) throw new InvalidDataException("Must provide a shipping cost");
		if (quantity == 0) throw new InvalidDataException("Must provide a minimum quantity to order");
		if (supplier == null || supplier.isEmpty()) throw new InvalidDataException("Must provide a supplier");

		return contractService.getContract(itemId).switchIfEmpty(Mono.just(new Contract())).flatMap(c -> {
			if (c.getItemId() != 0) throw new DataAlreadyExistsException("Contract with provided id already exists");
			
			return contractService.addContract(newContract).flatMap(ResponseBuilder::created);
		});
	}
	
	// GET Contract
	@GetMapping("{contractId}")
	@CorporateAccess
	public Mono<ResponseEntity<Object>> getContractById(ServerWebExchange exchange, @PathVariable("contractId") int id){
		return contractService.getContract(id).switchIfEmpty(Mono.just(new Contract())).flatMap(c -> {
			if (c.getQuantity() == 0) throw new NoContractFoundException("No contract with id: " + id + " found");
			return ResponseBuilder.ok(c);
		});
	}
	
	// UPDATE CONTRACT
	@PutMapping("{contractId}")
	@CorporateAccess
	public Mono<ResponseEntity<Object>> updateContract(ServerWebExchange exchange, @RequestBody Contract updatedContract,
			@PathVariable("contractId") int contractId) throws Exception {

		Mono<Contract> updated = contractService.getContract(contractId).map(oldContract -> {
			
			if (updatedContract.getSupplier() != null && !updatedContract.getSupplier().isEmpty())
				oldContract.setSupplier(updatedContract.getSupplier());

			if (updatedContract.getItemCost() != 0)
				oldContract.setItemCost(updatedContract.getItemCost());

			if (updatedContract.getItemMSRP() != 0)
				oldContract.setItemMSRP(updatedContract.getItemMSRP());

			if (updatedContract.getMinOrderCost() != 0)
				oldContract.setMinOrderCost(updatedContract.getMinOrderCost());

			if (updatedContract.getQuantity() != 0)
				oldContract.setQuantity(updatedContract.getQuantity());

			if (updatedContract.getShippingCost() != 0)
				oldContract.setShippingCost(updatedContract.getShippingCost());
			
			return oldContract;
		});

		Flux<Contract> contracts = contractService.updateContract(updated);
		return ResponseBuilder.ok(contracts);
	}

	// SUBMIT NEW CONTRACT CSV
	@PostMapping("{supplier}")
	@CorporateAccess
	public Mono<ResponseEntity<Object>> submitInventory(ServerWebExchange exchange, @PathVariable("supplier") String supplier,
			@RequestPart("file") Flux<FilePart> fileFlux) {

		Flux<Contract> contracts = contractService.addContractsFromFile(fileFlux, supplier);
		
		return ResponseBuilder.created(contracts);
	}

}
