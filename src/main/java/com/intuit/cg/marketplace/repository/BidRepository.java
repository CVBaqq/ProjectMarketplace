package com.intuit.cg.marketplace.repository;

import com.intuit.cg.marketplace.model.entity.Bid;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface BidRepository extends CrudRepository<Bid, Long> {

  @Query(value = "SELECT min(b.amount) FROM Bid b, Project p where p.id = :projectId")
  Optional<BigDecimal> findLowestBidder(@Param("projectId") Long projectId);


}
