package com.automobileproject.EAP.repository;

import com.automobileproject.EAP.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    // Find services by category
    List<Service> findByCategoryId(Long categoryId);
}