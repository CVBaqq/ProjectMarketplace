package com.intuit.cg.marketplace.model.dto;

import com.intuit.cg.marketplace.model.entity.Buyer;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class BuyerDTO {

  private Long id;

  private List<BidDTO> bids;

  public static BuyerDTO convertFromEntity(Buyer buyer) {
    BuyerDTO buyerDTO = new BuyerDTO();
    buyerDTO.setId(buyer.getId());
    buyerDTO.setBids(buyer.getBids().stream().map(bid -> BidDTO.convertFromEntity(bid)).collect(Collectors.toList()));
    return buyerDTO;
  }
}
