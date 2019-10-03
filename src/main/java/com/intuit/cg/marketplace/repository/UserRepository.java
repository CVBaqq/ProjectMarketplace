package com.intuit.cg.marketplace.repository;

import com.intuit.cg.marketplace.model.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}
