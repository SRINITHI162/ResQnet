package com.resqnet.repository;

import com.resqnet.entity.Resource;
import com.resqnet.enums.ResourceStatus;
import com.resqnet.enums.ResourceType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByStatus(ResourceStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Resource r where r.status = :status order by r.id")
    List<Resource> findByStatusForUpdate(@Param("status") ResourceStatus status);

    List<Resource> findByResourceTypeAndStatus(ResourceType type, ResourceStatus status);
    long countByStatus(ResourceStatus status);
}
