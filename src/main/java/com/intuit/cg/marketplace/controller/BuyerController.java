package com.intuit.cg.marketplace.controller;

import com.intuit.cg.marketplace.model.dto.BuyerDTO;
import com.intuit.cg.marketplace.service.BuyerService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/buyers")
@Api(value = "/buyers", description = "API to handle buyer related requests.")
public class BuyerController {

  private final BuyerService service;


  public BuyerController(BuyerService service) {
    this.service = service;
  }

  @GetMapping
  public BuyerDTO get(long id) {
    return service.get(id);
  }

  @GetMapping("/all")
  public List<BuyerDTO> getAll() {
    return service.getAll();
  }

}
