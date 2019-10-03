package com.intuit.cg.marketplace.model.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BidRequest {

  private long buyerId;

  private long projectId;

  private BigDecimal amount;
}
