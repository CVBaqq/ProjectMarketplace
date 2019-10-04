package com.intuit.cg.marketplace.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ProjectRequest {
  private String title;

  private String description;

  private BigDecimal maxBudget;

  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private Date bidStop;

  private long sellerId;
}
