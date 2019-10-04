package com.intuit.cg.marketplace.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.intuit.cg.marketplace.model.entity.AutoBid;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class AutoBidDTO {

  private Long id;

  private Long projectId;

  private Long buyerId;

  private BigDecimal targetAmount;

  private BigDecimal increment;

  private List<BidDTO> bids;

  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private Date created;

  public static AutoBidDTO convertFromEntity(AutoBid autobid) {
    AutoBidDTO autoBidDTO = new AutoBidDTO();
    autoBidDTO.setId(autobid.getId());
    autoBidDTO.setBuyerId(autobid.getBuyer().getId());
    autoBidDTO.setIncrement(autobid.getIncrement());
    autoBidDTO.setTargetAmount(autobid.getTargetAmount());
    autoBidDTO.setProjectId(autobid.getProject().getId());
    autoBidDTO.setCreated(autobid.getCreated());
    autoBidDTO.setBids(autobid.getBids() != null ?
            autobid.getBids().stream().map(bid -> BidDTO.convertFromEntity(bid)).collect(Collectors.toList()) :
            Collections.emptyList());

    return autoBidDTO;
  }
}
