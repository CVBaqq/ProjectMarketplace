package com.intuit.cg.marketplace.repository;

import com.intuit.cg.marketplace.model.entity.Buyer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuyerRepository extends CrudRepository<Buyer, Long> {
}
