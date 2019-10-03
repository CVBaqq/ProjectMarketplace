package com.intuit.cg.marketplace.service;

import com.intuit.cg.marketplace.exception.UnsatisfactoryBidException;
import com.intuit.cg.marketplace.model.entity.Bid;
import com.intuit.cg.marketplace.model.entity.Buyer;
import com.intuit.cg.marketplace.model.entity.AutoBid;
import com.intuit.cg.marketplace.model.entity.Project;
import com.intuit.cg.marketplace.model.request.BidRequest;
import com.intuit.cg.marketplace.model.request.AutoBidRequest;
import com.intuit.cg.marketplace.repository.AutoBidRepository;
import com.intuit.cg.marketplace.repository.BidRepository;
import com.intuit.cg.marketplace.repository.BuyerRepository;
import com.intuit.cg.marketplace.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class BidService {

  private final BidRepository bidRepository;

  private final ProjectRepository projectRepository;

  private final BuyerRepository buyerRepository;

  private final AutoBidRepository autoBidRepository;

  @Autowired
  public BidService(BidRepository bidRepository, ProjectRepository projectRepository, BuyerRepository buyerRepository,
                    AutoBidRepository autoBidRepository) {
    this.bidRepository = bidRepository;
    this.projectRepository = projectRepository;
    this.buyerRepository = buyerRepository;
    this.autoBidRepository = autoBidRepository;
  }

  /**
   * Persists the requested bid. It is required that the buyer and project entity exists
   * and project bidding stop time has not been reached.
   * Other consideration for placing an actual bid:
   * 1. Bid does not exceed project budget.
   * 2. Bidder does not currently hold the lowest bid.
   * 3. Bid has to be lower than the current lowest bid.
   * 4. You can't bid if you are the creator of this project,
   *
   * @param request The request containing data to persist the bid.
   * @return The persisted bid.
   */
  public Bid add(BidRequest request) throws UnsatisfactoryBidException {
    Buyer buyer = getBuyer(request.getBuyerId());
    Project project = getProject(request.getProjectId());

    validateRequest(project, request.getBuyerId(), request.getAmount());

    Bid lowestBid = project.getBids().stream().min(Comparator.comparing(Bid::getAmount)).orElse(null);
    if (lowestBid != null) {
      if (lowestBid.getBuyer().getId() == request.getBuyerId()) {
        throw new UnsatisfactoryBidException("You are already the lowest bidder with bid at " + lowestBid.getAmount());
      }
      if (lowestBid.getAmount().compareTo(request.getAmount()) <= 0) {
        throw new UnsatisfactoryBidException("Bid submitted is not lower or equal to the current lowest bid: " + lowestBid.getAmount());
      }

      // Check if last bid was by an auto bid.
      AutoBid currentAutobid = lowestBid.getAutobid();
      if (currentAutobid != null) {
        Bid bid = convertFromRequest(request);
        bid.setBuyer(buyer);
        bid.setProject(project);

        Bid savedBid = bidRepository.save(bid);
        addNewBid(currentAutobid, getNextLowestBid(currentAutobid, savedBid));
        autoBidRepository.save(currentAutobid);
        return savedBid;
      }
    }
    // No bids have been placed, so persist the requested bid.
    Bid bid = convertFromRequest(request);
    bid.setBuyer(buyer);
    bid.setProject(project);
    return bidRepository.save(bid);
  }

  /**
   * Handles persisting an auto bid request. This will perform existing bid lookup and evaluate against
   * other bids/auto bids to persist bid actions from the auto bid request.
   * Auto bid criteria:
   * 1. Auto bid has to be lower than the current lowest bid or at or lower than project budget.
   * 2. if no pre-existing bid, autobid will start at project budget.
   * 3. Auto bid cannot be from the project seller.
   * 4.
   * Default behavior: To keep track of history of the auto bid, auto bids against other auto bids will
   * perform a round-robin to create bids against each other.
   *
   * @param request The request containing information about the auto bid.
   * @return The persisted auto bid.
   */
  public AutoBid add(AutoBidRequest request) throws UnsatisfactoryBidException {
    Buyer buyer = getBuyer(request.getBuyerId());
    Project project = getProject(request.getProjectId());

    validateRequest(project, request.getBuyerId(), request.getTargetAmount());

    Bid lowestBid = project.getBids().stream().min(Comparator.comparing(Bid::getAmount)).orElse(null);
    if (lowestBid != null) {
      if (lowestBid.getBuyer().getId() == request.getBuyerId()) {
        throw new UnsatisfactoryBidException("You are already the lowest bidder with bid at " + lowestBid.getAmount());
      }
      if (lowestBid.getAmount().compareTo(request.getTargetAmount()) <= 0) {
        throw new UnsatisfactoryBidException("Bid submitted is not lower or equal to the current lowest bid: " +
                lowestBid.getAmount());
      }

      // This is the last auto bid to compete with
      AutoBid lastAutobid = lowestBid.getAutobid();
      AutoBid currentAutobid = new AutoBid(project, buyer, request.getTargetAmount(), request.getIncrement());

      if (lastAutobid == null) {
        // Last bid was not an auto bid, so just subtract increment from the last price
        addNewBid(currentAutobid, getNextLowestBid(currentAutobid, lowestBid));
        autoBidRepository.save(currentAutobid);
      } else {
        // Bidding war happens here
        handleAutoBid(currentAutobid, lastAutobid, lowestBid);
        autoBidRepository.saveAll(Stream.of(currentAutobid, lastAutobid).collect(Collectors.toList()));
      }
      return currentAutobid;
    } else {
      // There was no previous bids so persist a new one by auto bid.
      AutoBid autoBid = new AutoBid(project, buyer, request.getTargetAmount(), request.getIncrement());
      addNewBid(autoBid, project.getMaxBudget());
      return autoBidRepository.save(autoBid);
    }
  }

  public Bid get(long id) {
    Optional<Bid> optional = bidRepository.findById(id);
    return optional.orElseThrow(() -> new ResourceNotFoundException("Bid with ID " + id + " does not exists."));
  }

  public List<Bid> getAll() {
    return (List<Bid>) bidRepository.findAll();
  }

  /**
   * This validates the common requirements for both bid and auto bid requests.
   * Validations:
   * 1. Bidder cannot be the seller of the project.
   * 2. Project bidding end time has not been elapsed.
   * 3. The bid amount does not exceed the project budget.
   * @param project
   * @param buyerId
   * @param bidAmount
   * @throws UnsatisfactoryBidException
   */
  private void validateRequest(Project project, Long buyerId, BigDecimal bidAmount) throws UnsatisfactoryBidException {
    if (project.getSeller().getId() == buyerId) {
      throw new UnsatisfactoryBidException("You cannot bid if you are the creator of this project.");
    }

    if (project.getBidStop().before(new Date())) {
      throw new UnsatisfactoryBidException("Cannot bid on project. The project has a bid ending time of " + project.getBidStop());
    }

    if (bidAmount.compareTo(project.getMaxBudget()) == 1) {
      throw new UnsatisfactoryBidException("Bid submitted is higher than the project budget");
    }
  }

  /**
   * Utility method to add new bid to autobid entity.
   * @param autoBid The AutoBid entity to add to.
   * @param bidAmount The bid amount the new Bid entity will have.
   * @return Return the added bid entity.
   */
  private Bid addNewBid(AutoBid autoBid, BigDecimal bidAmount) {
    Bid bid = new Bid(autoBid.getProject(), autoBid.getBuyer(), bidAmount);
    bid.setAutobid(autoBid);
    if (autoBid.getBids() == null) {
      autoBid.setBids(Stream.of(bid).collect(Collectors.toSet()));
    } else {
      autoBid.getBids().add(bid);
    }
    return bid;
  }

  /**
   * Handle the auto bidding between two auto bids. This will create all the bids necessary by switching bids between
   * the two supplied auto bids; thus creating a bidding war that will also keep track of bid history.
   * @param currentAutobid The auto bid to start the bidding war with.
   * @param previousAutobid The previous winning auto bid that the currentAutobid will compete with.
   * @param lowestBid The current lowest bid that was placed by the previousAutobid.
   */
  private void handleAutoBid(AutoBid currentAutobid, AutoBid previousAutobid, Bid lowestBid) {
    AutoBid current = currentAutobid;
    AutoBid next = previousAutobid;
    Bid currentLowestBid = lowestBid;
    while (currentLowestBid.getBuyer().getId() != current.getBuyer().getId()) {
      Bid newLowBid = addNewBid(current, getNextLowestBid(current, currentLowestBid));

      AutoBid temp = current;
      current = next;
      next = temp;
      currentLowestBid = newLowBid;
    }
  }

  private BigDecimal getNextLowestBid(AutoBid autoBid, Bid lowestBid) {
    BigDecimal nextAmount = lowestBid.getAmount().subtract(autoBid.getIncrement());
    if (nextAmount.compareTo(BigDecimal.valueOf(0)) <= 0) {
      return BigDecimal.valueOf(0);
    }
    return nextAmount;
  }

  private Buyer getBuyer(Long id) {
    return buyerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Buyer with ID " +
            id + " does not exist"));
  }

  private Project getProject(Long id) {
    return projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project with ID " +
            id + " does not exist"));
  }

  private Bid convertFromRequest(BidRequest bid) {
    Bid entity = new Bid();
    entity.setAmount(bid.getAmount());
    return entity;
  }
}
