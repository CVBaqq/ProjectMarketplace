package com.intuit.cg.marketplace.controller;

import com.intuit.cg.marketplace.model.dto.UserDTO;
import com.intuit.cg.marketplace.model.request.UserRequest;
import com.intuit.cg.marketplace.service.UserService;
import io.swagger.annotations.Api;
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
@RequestMapping("/user")
@Api(value = "/user", description = "API to handle user related requests.")
public class UserController {

  private final UserService service;


  @Autowired
  public UserController(UserService service) {
    this.service = service;
  }

  @GetMapping
  public UserDTO get(@RequestParam long id) {
    return UserDTO.convertFromEntity(service.get(id));
  }

  @GetMapping("/all")
  public List<UserDTO> getAll() {
    return service.getAll().stream().map(user -> UserDTO.convertFromEntity(user)).collect(Collectors.toList());
  }

  @PostMapping
  public UserDTO add(@RequestBody UserRequest userRequest) {
    return UserDTO.convertFromEntity(service.add(userRequest));
  }
}
