package com.intuit.cg.marketplace.service;

import com.intuit.cg.marketplace.model.entity.Seller;
import com.intuit.cg.marketplace.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SellerService {

  private final SellerRepository sellerRepository;

  @Autowired
  public SellerService(SellerRepository sellerRepository) {
    this.sellerRepository = sellerRepository;
  }

  public Seller get(long id) {
    Optional<Seller> optional = sellerRepository.findById(id);

    return optional.orElseThrow(() -> new ResourceNotFoundException());
  }

  public List<Seller> getAll() {
    return (List<Seller>) sellerRepository.findAll();
  }


}
