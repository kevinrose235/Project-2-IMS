package com.revature.services;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import com.revature.beans.Contract;
import com.revature.repositories.ContractRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ContractServiceImpl implements ContractService {
	private static Logger logger = LogManager.getLogger(ContractServiceImpl.class);

	private ContractRepository contractRepo;
	
	@Autowired
	public void setContractRepo(ContractRepository repo) {
		this.contractRepo = repo;
	}
	
	@Override
	public Mono<Contract> getContract(int id) {
		return contractRepo.findById(id);
	}

	@Override
	public Mono<Contract> addContract(Contract newContract) {
		return contractRepo.save(newContract);
	}

	@Override
	public Flux<Contract> updateContract(Mono<Contract> updatedContract) {
		return contractRepo.saveAll(updatedContract);
	}
	
	@Override
	public Flux<Contract> addContractsFromFile(Flux<FilePart> fileFlux, String supplier) {
		Flux<Contract> contractsToAdd = fileFlux.flatMap(this::readStringFromFile) // This flat map reads each whole file emitted as a string
				.map(string -> {
					
					//Split the string on new lines, build an item for each, and add to the list
					List<Contract> contracts = new ArrayList<>();
					for(String line : string.split("\\r?\\n")) {
						try {
							Contract contract = buildContractFromCsvLine(line, supplier);
							contracts.add(contract);
						}catch(Exception e) {
							logger.warn(e);
						}
					}
					
					return contracts;
				})
				.flatMapIterable(Function.identity()); //Flattens it from Flux<List<Item>> to Flux<Item> contains all items from that list
		return contractRepo.saveAll(contractsToAdd);
	}
	
	private Flux<String> readStringFromFile(FilePart file){
		return file.content().map(buffer -> {
			byte[] bytes = new byte[buffer.readableByteCount()];
             buffer.read(bytes);
             DataBufferUtils.release(buffer);

             return new String(bytes);
		});
	}
	
	private Contract buildContractFromCsvLine(String csvLine, String  supplier){
		Contract contract = new Contract();
		String[] values = csvLine.split(",");
		contract.setItemId(Integer.parseInt(values[0]));
		contract.setItemCost(Double.parseDouble(values[1]));
		contract.setItemMSRP(Double.parseDouble(values[2]));
		contract.setSupplier(supplier);
		contract.setMinOrderCost(Double.parseDouble(values[3]));
		contract.setQuantity(Integer.parseInt(values[4]));
		contract.setShippingCost(Double.parseDouble(values[5]));
		return contract;
	}
	
	@Override
	public double costToOrder(int itemId) {
		Mono<Contract> contractMono = getContract(itemId);
		Contract contract = contractMono.block();
		double result = (contract.getItemCost() * contract.getQuantity() * minimumOrderInfo(itemId).get(0)) + contract.getShippingCost();
		return result;
	}
	
	@Override
	public int orderVolume(int itemId) {
		return minimumOrderInfo(itemId).get(1);
	}
	
	private List<Integer> minimumOrderInfo(int itemId) {
		List<Integer> result = new ArrayList<Integer>();
		Mono<Contract> contractMono = getContract(itemId);
		Contract contract = contractMono.block();
		double minOrderCost = contract.getMinOrderCost();
		double orderCost = contract.getItemCost() * contract.getQuantity();
		int volume =  (int) (minOrderCost/orderCost) + 1;
		int orderSize = volume * contract.getQuantity();
		result.add(volume);
		result.add(orderSize);
		return result;
	}

}
