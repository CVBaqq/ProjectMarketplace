package com.intuit.cg.marketplace.service;

import com.intuit.cg.marketplace.model.dto.ProjectDTO;
import com.intuit.cg.marketplace.model.entity.Bid;
import com.intuit.cg.marketplace.model.request.ProjectRequest;
import com.intuit.cg.marketplace.model.entity.Project;
import com.intuit.cg.marketplace.model.entity.Seller;
import com.intuit.cg.marketplace.repository.ProjectRepository;
import com.intuit.cg.marketplace.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class ProjectService {

  private final ProjectRepository projectRepository;

  private final SellerRepository sellerRepository;

  @Autowired
  public ProjectService(ProjectRepository projectRepository, SellerRepository sellerRepository) {
    this.projectRepository = projectRepository;
    this.sellerRepository = sellerRepository;
  }

  public Project add(ProjectRequest project) {
    Optional<Seller> optional = sellerRepository.findById(project.getSellerId());
    Seller seller = optional.orElseThrow(() -> new ResourceNotFoundException("Tried to persist new project but seller with ID " +
                          project.getSellerId() + " does not exists"));
    Project projectEntity = convertFromRequest(project);
    projectEntity.setSeller(seller);
    return projectRepository.save(projectEntity);
  }

  public ProjectDTO get(long id) {
    Project project = projectRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Project with id " + id + " was not found"));
    ProjectDTO projectDTO = ProjectDTO.convertFromEntity(project);
    Bid winningBid = project.getBids().stream().min(Comparator.comparing(bid -> bid.getAmount())).orElse(null);
    if (winningBid != null) {
      projectDTO.setCurrentWinnerId(winningBid.getBuyer().getId());
      projectDTO.setWinningAmount(winningBid.getAmount());
    }
    return projectDTO;
  }

  public List<Project> getAll() {
    return (List<Project>) projectRepository.findAll();
  }

  private Project convertFromRequest(ProjectRequest project) {
    Project entity = new Project();
    entity.setTitle(project.getTitle());
    entity.setDescription(project.getDescription());
    entity.setBidStop(project.getBidStop());
    entity.setMaxBudget(project.getMaxBudget());
    return entity;
  }
}
