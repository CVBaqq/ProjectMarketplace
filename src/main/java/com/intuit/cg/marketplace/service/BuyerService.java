package com.intuit.cg.marketplace.service;

import com.intuit.cg.marketplace.model.dto.BuyerDTO;
import com.intuit.cg.marketplace.model.entity.Buyer;
import com.intuit.cg.marketplace.repository.BuyerRepository;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class BuyerService {

  public final BuyerRepository buyerRepository;

  public BuyerService(BuyerRepository buyerRepository) {
    this.buyerRepository = buyerRepository;
  }

  public BuyerDTO get(long id) {
    Optional<Buyer> optional = buyerRepository.findById(id);
    return BuyerDTO.convertFromEntity(optional.orElseThrow(() -> new ResourceNotFoundException()));
  }

  public List<BuyerDTO> getAll() {
    return ((List<Buyer>)buyerRepository.findAll()).stream().map(buyer -> BuyerDTO.convertFromEntity(buyer)).collect(Collectors.toList());
  }

}
