package com.revature.services;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicIntegerArray;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.revature.beans.Contract;
import com.revature.beans.Item;
import com.revature.beans.ItemPrimaryKey;
import com.revature.beans.Order;
import com.revature.beans.Store;
import com.revature.repositories.ContractRepository;
import com.revature.repositories.ItemRepository;
import com.revature.repositories.StoreRepository;
import com.revature.utils.DistanceMath;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Service
public class InventoryServiceImpl implements InventoryService {
	private static Logger log = LogManager.getLogger();
	private ItemRepository itemRepo;
	@Autowired
	public void setItemRepo(ItemRepository itemRepo) {
		this.itemRepo=itemRepo;
	}private ContractRepository conRepo;
	@Autowired
	public void setContractRepo(ContractRepository conRepo) {
		this.conRepo=conRepo;
	}private StoreRepository storeRepo;
	@Autowired
	public void setStoreRepo(StoreRepository storeRepo) {
		this.storeRepo=storeRepo;
	}
	
	
	@Override
	public Flux<Order> orderAlg(Flux<Item> items, Flux<Item> high) {
				
		
		List<Item> highList;
		Map<Integer,Store> stores;
		List<Contract> cons;
		try {
			highList=high.collectList().toFuture().get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("Interrupted surplusInv tolist", e);
			return null;
		} catch (ExecutionException e) {
			log.error("execution surplusInv tolist", e);
			return null;
		}
		try {
			stores= storeRepo.findAll().collectMap(s->s.getId()).toFuture().get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("Interrupted stores tolist", e);
			return null;
		} catch (ExecutionException e) {
			log.error("execution stores tolist", e);

			return null;
		}
		try {
			cons= conRepo.findAll().collectList().toFuture().get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("Interrupted contracts tolist", e);

			return null;
		} catch (ExecutionException e) {
			log.error("execution contracts tolist", e);

			return null;
		}
		//Atomic integer array counts as effectively final but can still be modified in lambda
		AtomicIntegerArray nuclear= new AtomicIntegerArray(highList.stream().mapToInt(i->i.getQuantity()-i.getOverstockThreshold()).toArray());
		
		
		
		
		return items.map(i->{
			//couldn't do it the other way cause it ddos'd aws
			Contract c=cons.stream().filter(con->con.getItemId()==i.getId()).findFirst().orElseGet(null);
			if (c==null) {
				log.error("someone didn't enforce relationships, contract is null... id is {0}",i.toString());
				return null;
			}
			
			//gets amount by finding what it would take to go from current to 100%
			int amount= i.getOverstockThreshold()-i.getQuantity();
			//but we order by palette
			int cAmount= amount/c.getQuantity()+((amount%c.getQuantity()==0)? 0:1);//amount of palettes
			cAmount=cAmount*c.getQuantity();//back to amount of items
			double price=cAmount*c.getItemCost();
			//but gotta see if this is at least min order cost
			if(price<c.getMinOrderCost()) {
				//how many items we need to get to cost
				cAmount= (int) (c.getMinOrderCost()/c.getItemCost()+((c.getMinOrderCost()%c.getItemCost()==0)? 0:1));
				//rounding up by palette size
				cAmount= cAmount/c.getQuantity()+((cAmount%c.getQuantity()==0)? 0:1);//amount of palettes
				cAmount=cAmount*c.getQuantity();//back to amount of items
				//new price
				price=cAmount*c.getItemCost();
			}
			if(nuclear.length()>0) {
				Item bestDis =highList.stream().filter(item->(nuclear.get(highList.indexOf(item))>=amount))//find where a surplus would actually be enough
						//sorts stores by distance 
						.sorted((a,b)->Double.compare(distance(a.getStoreId(),i.getStoreId(),stores),distance(b.getStoreId(),i.getStoreId(),stores))).findFirst().orElseGet(null);
				if (bestDis!=null) {
					Store distribStore=stores.get(bestDis.getStoreId());
					//as far as i know we never defined our inter store shipping cost so it's 0.03
					double dprice=amount*c.getItemCost()+ amount*c.getShippingCost()*distance(bestDis.getStoreId(),i.getStoreId(),stores);
					//so it checks if the cost is cheaper than buying from distributor but it also checks to see if it would be worth it to ship
					//basically if it would cost more than half of the profit per item to ship it's considered non viable
					if (dprice<price&& dprice<(amount*c.getItemCost()+ (c.getItemMSRP()-c.getItemCost())*amount/2)) {
						//update that the surplus has changed.. set atomic array at position of surplus to previous surplus - amount
						nuclear.set(highList.indexOf(bestDis), nuclear.get(highList.indexOf(bestDis))-amount);
						return new Order(i.getPrimaryKey().getId(),i.getPrimaryKey().getStoreId(),amount,dprice,""+distribStore.getId(),false);
					}
				}
			}
			return new Order(i.getPrimaryKey().getId(),i.getPrimaryKey().getStoreId(), cAmount, price,c.getSupplier(),true);
		});
		
	}
	
	private double distance(int id1, int id2, Map<Integer,Store> stores) {
		Store s1= stores.get(id1);
		Store s2= stores.get(id2);
		if(s1==null||s2==null)
			return Double.MAX_VALUE;
		return DistanceMath.kmBetweenTwoCoords(s1.getLocation(), s2.getLocation());		
	}


	@Override
	public Flux<Item> createManualOrder(Flux<Item> manualOrders) {
		Flux<Item> everything= itemRepo.findAll().share();
		Flux<Item> high= everything.filter(i->(i.getQuantity()>i.getOverstockThreshold()));
		return updateInventoryBasedOffofOrders(orderAlg(manualOrders,high),everything);
	}


	@Override
	public Flux<Item> cyclicalOrder() {
		Flux<Item> everything= itemRepo.findAll().share();
		Flux<Item> low= everything.filter(i->(i.getQuantity()<=i.getLowThreshold()));
		Flux<Item> high= everything.filter(i->(i.getQuantity()>i.getOverstockThreshold()));
		return updateInventoryBasedOffofOrders(orderAlg(low,high),everything);
	}


	@Override
	public Flux<Item> updateInventoryBasedOffofOrders(Flux<Order> orders, Flux<Item> allItems) {
		Map<ItemPrimaryKey,Order> orderMap;
		try {
			orderMap=orders.collectMap(o-> new ItemPrimaryKey(o.getItemId(),o.getStoreId())).toFuture().get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("Interrupted orders tolist", e);
			return null;
		} catch (ExecutionException e) {
			log.error("execution orders tolist", e);
			return null;
		}
		return itemRepo.saveAll(allItems.filter(i->(orderMap.containsKey(i.getPrimaryKey())||i.getQuantity()>i.getOverstockThreshold()))

				.flatMap(i->{
					if(orderMap.containsKey(i.getPrimaryKey())) {
						i.setQuantity(i.getQuantity()+orderMap.get(i.getPrimaryKey()).getQuantity());
						return Mono.just(i);
					}else {
						int change=orderMap.values().stream().filter(o->o.getFromDistributor())
								.filter(o->{
									try {
										Integer.parseInt(o.getSupplier());
										return true;
									} catch (NumberFormatException e) {
										return false;
									}
								})
								.filter(o->Integer.parseInt(o.getSupplier())==i.getId()).mapToInt(o->o.getQuantity()).reduce(0, (subtotal, element) -> subtotal + element);
						if(change!=0) {
							i.setQuantity(i.getQuantity()-change);
							return Mono.just(i);
						}
						else {
							return Mono.empty();
						}
					}
						
					}));
	}


	
	
	

}
