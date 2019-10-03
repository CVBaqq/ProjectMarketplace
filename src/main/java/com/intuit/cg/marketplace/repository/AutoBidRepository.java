package com.intuit.cg.marketplace.repository;

import com.intuit.cg.marketplace.model.entity.AutoBid;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoBidRepository extends CrudRepository<AutoBid, Long> {
}
