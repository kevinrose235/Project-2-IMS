package com.revature.services;

import org.springframework.http.codec.multipart.FilePart;

import com.revature.beans.Contract;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ContractService {
	
	public Mono<Contract> getContract(int id);
	
	public Mono<Contract> addContract(Contract newContract);
	
	public Flux<Contract> updateContract(Mono<Contract> updatedContract);
	
	public Flux<Contract> addContractsFromFile(Flux<FilePart> fileFlux, String supplier);
	
	public double costToOrder(int itemId);
	
	public int orderVolume(int itemId);

}
