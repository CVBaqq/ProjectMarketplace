package com.intuit.cg.marketplace.controller;

import com.intuit.cg.marketplace.exception.UnsatisfactoryBidException;
import com.intuit.cg.marketplace.model.dto.AutoBidDTO;
import com.intuit.cg.marketplace.model.dto.BidDTO;
import com.intuit.cg.marketplace.model.request.AutoBidRequest;
import com.intuit.cg.marketplace.model.request.BidRequest;
import com.intuit.cg.marketplace.service.BidService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bids")
@Api(value = "/bids", description = "API to handle bidding related requests.")
@Slf4j
public class BidController {

  private final BidService service;

  @Autowired
  public BidController(BidService service) {
    this.service = service;
  }

  @GetMapping
  public BidDTO get(@RequestParam long id) {
    return BidDTO.convertFromEntity(service.get(id));
  }

  @PostMapping
  public BidDTO bid(@RequestBody BidRequest bid) throws UnsatisfactoryBidException {
    log.info("Received request to place a bid.");
    return BidDTO.convertFromEntity(service.add(bid));
  }

  @GetMapping("/all")
  public List<BidDTO> getAll() {
    log.info("Received request to retrieve all bids.");
    return service.getAll().stream().map(bid -> BidDTO.convertFromEntity(bid)).collect(Collectors.toList());
  }

  @GetMapping("/autobid")
  public AutoBidDTO getAutobid(@RequestParam long id) {
    return AutoBidDTO.convertFromEntity(service.getAutobid(id));
  }

  @PostMapping("/autobid")
  public AutoBidDTO autoBid(@RequestBody AutoBidRequest request) throws UnsatisfactoryBidException {
    log.info("Received request to place an auto bid");
    return AutoBidDTO.convertFromEntity(service.add(request));
  }
}
