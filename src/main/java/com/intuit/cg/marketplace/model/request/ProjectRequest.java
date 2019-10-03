package com.intuit.cg.marketplace.model.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ProjectRequest {
  private String title;

  private String description;

  private BigDecimal maxBudget;

  private Date bidStop;

  private long sellerId;
}
