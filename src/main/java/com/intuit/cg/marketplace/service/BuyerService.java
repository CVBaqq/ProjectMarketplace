package com.intuit.cg.marketplace.service;

import com.intuit.cg.marketplace.model.entity.Buyer;
import com.intuit.cg.marketplace.repository.BuyerRepository;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BuyerService {

  public final BuyerRepository buyerRepository;

  public BuyerService(BuyerRepository buyerRepository) {
    this.buyerRepository = buyerRepository;
  }

  public Buyer get(long id) {
    Optional<Buyer> optional = buyerRepository.findById(id);
    return optional.orElseThrow(() -> new ResourceNotFoundException());
  }

}
