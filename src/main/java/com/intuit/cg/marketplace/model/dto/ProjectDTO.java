package com.intuit.cg.marketplace.model.dto;

import com.intuit.cg.marketplace.model.entity.Project;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProjectDTO {

  private Long id;

  private Long sellerId;

  private String title;

  private String description;

  private BigDecimal maxBudget;

  private Date bidStop;

  private Long currentWinnerId;

  private BigDecimal winningAmount;

  private List<BidDTO> bids;

  public ProjectDTO(Long id,
                    Long sellerId,
                    String title,
                    String description,
                    BigDecimal maxBudget,
                    Date bidStop,
                    Long currentWinnerId,
                    BigDecimal winningAmount,
                    List<BidDTO> bids) {
    this.id = id;
    this.sellerId = sellerId;
    this.title = title;
    this.description = description;
    this.maxBudget = maxBudget;
    this.bidStop = bidStop;
    this.currentWinnerId = currentWinnerId;
    this.winningAmount = winningAmount;
    this.bids = bids;
  }

  public static ProjectDTO convertFromEntity(Project project) {
    return new ProjectDTO(project.getId(),
            project.getSeller().getId(),
            project.getTitle(),
            project.getDescription(),
            project.getMaxBudget(),
            project.getBidStop(),
            null,
            null,
            project.getBids() != null ?
                    project.getBids().stream().map(bid -> BidDTO.convertFromEntity(bid)).collect(Collectors.toList()) :
                    Collections.emptyList()
    );
  }
}
