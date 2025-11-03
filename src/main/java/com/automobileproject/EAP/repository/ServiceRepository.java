package com.automobileproject.EAP.repository;

import com.automobileproject.EAP.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    // You can add custom queries here if needed, for example:
    Optional<Service> findByName(String name);
}