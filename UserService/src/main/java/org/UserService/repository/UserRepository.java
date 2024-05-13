package org.UserService.repository;

import org.UserService.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
    User findByPhoneNo(String phoneNo);
}
