package com.intuit.cg.marketplace.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.intuit.cg.marketplace.model.entity.Bid;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BidDTO {

  private Long id;

  private Long projectId;

  private Long buyerId;

  private BigDecimal amount;

  private Long autobidId;

  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private Date created;

  public static BidDTO convertFromEntity(Bid bid) {
    BidDTO bidDTO = new BidDTO();
    bidDTO.setBuyerId(bid.getBuyer().getId());
    bidDTO.setProjectId(bid.getProject().getId());
    bidDTO.setAmount(bid.getAmount());
    bidDTO.setCreated(bid.getCreated());
    bidDTO.setId(bid.getId());
    bidDTO.setAutobidId(bid.getAutobid() != null ? bid.getAutobid().getId()  : null);
    return bidDTO;
  }
}
