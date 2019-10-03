package com.intuit.cg.marketplace.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

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
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "autobid")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"project", "buyer", "bids"})
public class AutoBid {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "project_id")
  private Project project;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "buyer_id", nullable = false)
  private Buyer buyer;

  @OneToMany(mappedBy = "autobid", cascade = CascadeType.ALL)
  private Set<Bid> bids;

  @Digits(integer = 10, fraction = 2)
  private BigDecimal targetAmount;

  @Digits(integer = 10, fraction = 2)
  private BigDecimal increment;

  @CreationTimestamp
  private Date created;

  public AutoBid(Project project, Buyer buyer, BigDecimal targetAmount, BigDecimal increment) {
    this.project = project;
    this.buyer = buyer;
    this.targetAmount = targetAmount;
    this.increment = increment;
  }

}
