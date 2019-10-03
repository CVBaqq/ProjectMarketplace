package com.intuit.cg.marketplace.repository;

import com.intuit.cg.marketplace.model.entity.Seller;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerRepository extends CrudRepository<Seller, Long> {
}
