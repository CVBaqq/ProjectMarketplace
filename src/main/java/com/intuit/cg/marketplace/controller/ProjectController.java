package com.intuit.cg.marketplace.controller;

import com.intuit.cg.marketplace.model.dto.ProjectDTO;
import com.intuit.cg.marketplace.model.request.ProjectRequest;
import com.intuit.cg.marketplace.service.ProjectService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/projects")
@Api(value = "/projects", description = "API to handle project related requests.")
@Slf4j
public class ProjectController {

  public final ProjectService service;

  @Autowired
  public ProjectController(ProjectService service) {
    this.service = service;
  }

  @PostMapping
  public ProjectDTO create(@RequestBody ProjectRequest project) {
    log.info("Received request to create project.");
    return ProjectDTO.convertFromEntity(service.add(project));
  }

  @GetMapping
  public ProjectDTO get(long id)  {
    log.info("Received request to retrieve project.");
    return service.get(id);
  }

  @GetMapping("/all")
  public List<ProjectDTO> getAll() {
    log.info("Received request to get all projects.");
    return service.getAll().stream().map(project -> ProjectDTO.convertFromEntity(project)).collect(Collectors.toList());
  }
}
