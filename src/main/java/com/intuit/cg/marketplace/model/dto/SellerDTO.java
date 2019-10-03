package com.intuit.cg.marketplace.model.dto;

import com.intuit.cg.marketplace.model.entity.Seller;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class SellerDTO {

  private Long id;

  private List<ProjectDTO> projects;

  public static SellerDTO convertFromEntity(Seller seller) {
    SellerDTO sellerDTO = new SellerDTO();
    sellerDTO.setId(seller.getId());
    sellerDTO.setProjects(seller.getProjects().stream().map(
            project -> ProjectDTO.convertFromEntity(project)).collect(Collectors.toList()));
    return sellerDTO;
  }
}
