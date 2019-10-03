package com.intuit.cg.marketplace.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"project", "buyer", "autobid"})
public class Bid {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "project_id")
  private Project project;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "buyer_id", nullable = false)
  private Buyer buyer;

  @Digits(integer = 10, fraction = 2)
  private BigDecimal amount;

  @CreationTimestamp
  private Date created;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "autobid_id")
  private AutoBid autobid;

  public Bid(Project project, Buyer buyer, BigDecimal amount) {
    this.project = project;
    this.buyer = buyer;
    this.amount = amount;
  }
}
