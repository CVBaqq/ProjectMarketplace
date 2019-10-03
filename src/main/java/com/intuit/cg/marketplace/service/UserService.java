package com.intuit.cg.marketplace.service;

import com.intuit.cg.marketplace.model.entity.Buyer;
import com.intuit.cg.marketplace.model.entity.Seller;
import com.intuit.cg.marketplace.model.entity.User;
import com.intuit.cg.marketplace.model.request.UserRequest;
import com.intuit.cg.marketplace.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserService {

  private UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User add(UserRequest userRequest) {
    User user = convertFromRequest(userRequest);
    user.setBuyer(new Buyer());
    user.setSeller(new Seller());
    return userRepository.save(user);
  }

  public User get(long id) {
    Optional<User> optional = userRepository.findById(id);
    return optional.orElseThrow(() -> new ResourceNotFoundException("Could not find user with ID " + id));
  }

  public List<User> getAll() {
    return (List<User>) userRepository.findAll();
  }

  private User convertFromRequest(UserRequest userRequest) {
    User user = new User();
    user.setName(userRequest.getName());
    return user;
  }
}
