package com.intuit.cg.marketplace.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(exclude = {"seller", "bids"})
public class Project {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(length = 100)
  private String title;

  @Column(length = 1000)
  private String description;

  @Digits(integer = 10, fraction = 2)
  private BigDecimal maxBudget;

  private Date bidStop;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seller_id", nullable = false)
  private Seller seller;

  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
  private Set<Bid> bids;

  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
  private Set<AutoBid> autoBids;
}
