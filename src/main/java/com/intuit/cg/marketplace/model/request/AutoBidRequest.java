package com.intuit.cg.marketplace.model.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AutoBidRequest {

  private Long buyerId;

  private Long projectId;

  private BigDecimal targetAmount;

  private BigDecimal increment;
}
