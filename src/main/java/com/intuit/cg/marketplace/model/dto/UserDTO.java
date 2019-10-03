package com.intuit.cg.marketplace.model.dto;

import com.intuit.cg.marketplace.model.entity.User;
import lombok.Data;

@Data
public class UserDTO {

  private Long id;

  private Long sellerId;

  private Long buyerId;

  private String name;

  public UserDTO(Long id,
                 Long sellerId,
                 Long buyerId,
                 String name) {
    this.id = id;
    this.sellerId = sellerId;
    this.buyerId = buyerId;
    this.name = name;
  }

  public static UserDTO convertFromEntity(User user) {
    return new UserDTO(user.getId(),
            user.getSeller().getId(),
            user.getBuyer().getId(),
            user.getName());
  }
}
