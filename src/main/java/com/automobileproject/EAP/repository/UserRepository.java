package com.automobileproject.EAP.repository;

import com.automobileproject.EAP.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // <-- Good to add this
import java.util.Optional; // <-- Make sure this is imported

@Repository // <-- Add this annotation
public interface UserRepository extends JpaRepository<User, Long> {

    // Change this:
    // User findByUsername(String username);
    // TO THIS:
    Optional<User> findByUsername(String username);

    // Change this:
    // User findByEmail(String email);
    // TO THIS:
    Optional<User> findByEmail(String email);
}