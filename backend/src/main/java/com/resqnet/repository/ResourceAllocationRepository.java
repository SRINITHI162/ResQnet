package com.resqnet.repository;

import com.resqnet.entity.ResourceAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResourceAllocationRepository extends JpaRepository<ResourceAllocation, Long> {
    List<ResourceAllocation> findByIncidentId(Long incidentId);
}
