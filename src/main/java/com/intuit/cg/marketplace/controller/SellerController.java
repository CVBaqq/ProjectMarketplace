package com.intuit.cg.marketplace.controller;

import com.intuit.cg.marketplace.model.dto.SellerDTO;
import com.intuit.cg.marketplace.service.SellerService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sellers")
@Api(value = "/sellers", description = "API to handle seller related requests.")
@Slf4j
public class SellerController {

  private final SellerService service;

  @Autowired
  public SellerController(SellerService service) {
    this.service = service;
  }

  @GetMapping("/all")
  public List<SellerDTO> getAllSellers() {
    return service.getAll().stream().map(seller -> SellerDTO.convertFromEntity(seller))
            .collect(Collectors.toList());
  }

  @GetMapping
  public SellerDTO getSeller(@RequestParam long id) {
    return SellerDTO.convertFromEntity(service.get(id));
  }
}
