package com.revature.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.revature.beans.Contract;
import com.revature.repositories.ContractRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
public class ContractServiceImplTests {

	@TestConfiguration
	static class Configuration{
		@Bean
		public ContractServiceImpl getContractService(ContractRepository repo) {
			ContractServiceImpl service = new ContractServiceImpl();
			service.setContractRepo(repo);
			return service;
		}
	}
	
	@Autowired
	private ContractServiceImpl contractService;
	
	@MockBean
	private ContractRepository contractRepo;
	
	private static List<Contract> contracts;
	
	@BeforeAll
	public static void addContracts() { 
		contracts = new ArrayList<>();
		
		Contract contract1 = new Contract();
		contract1.setItemId(0);
		contract1.setItemCost(23.19);
		contract1.setItemMSRP(49.99);
		contract1.setMinOrderCost(231.90);
		contract1.setQuantity(10);
		contract1.setShippingCost(32.19);
		contract1.setSupplier("Amazon");
		
		contracts.add(contract1);
		
		Contract contract2 = new Contract();
		contract2.setItemId(1);
		contract2.setItemCost(26.50);
		contract2.setItemMSRP(69.49);
		contract2.setMinOrderCost(265.00);
		contract2.setQuantity(10);
		contract2.setShippingCost(32.19);
		contract2.setSupplier("Lowe's DC");		
		
		contracts.add(contract2);
	}
	
	@Test
	public void getContractByIdReturnsContractMono() {
		Contract contract = contracts.get(0);
		Mono<Contract> contractMono = Mono.just(contract);
		when(contractRepo.findById(contract.getItemId())).thenReturn(contractMono);
		
		assertEquals(contractMono, contractService.getContract(contract.getItemId()), "get contract should return a contract mono");
	}
	
	@Test
	public void addContractReturnsContractMono() {
		Contract contract = contracts.get(1);
		Mono<Contract> contractMono = Mono.just(contract);
		
		when(contractRepo.save(contract)).thenReturn(contractMono);
		
		assertEquals(contractMono, contractService.addContract(contract), 
				"Add contract should return contract mono");
		
	}

	@Test
	public void updateContractReturnsFluxContract() {
		Mono<Contract> contract = Mono.just(contracts.get(0));
		Flux<Contract> contractFlux = contract.flux();
		
		when(contractRepo.saveAll(contract)).thenReturn(contractFlux);
		
		assertEquals(contractFlux, contractService.updateContract(contract), "Update contract should return a flux of one contract");
	}

	public void addContractsFromFileReturnsAFluxofContracts() {
		
	}
}
