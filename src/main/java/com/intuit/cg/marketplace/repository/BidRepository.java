package com.intuit.cg.marketplace.repository;

import com.intuit.cg.marketplace.model.entity.Bid;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BidRepository extends CrudRepository<Bid, Long> {

}
