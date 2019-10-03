package com.intuit.cg.marketplace.repository;

import com.intuit.cg.marketplace.model.entity.Project;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Long> { }
