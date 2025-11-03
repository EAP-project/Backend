package com.automobileproject.EAP.repository;

import com.automobileproject.EAP.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    // JpaRepository gives us save(), deleteById(), findById(), findAll()
}