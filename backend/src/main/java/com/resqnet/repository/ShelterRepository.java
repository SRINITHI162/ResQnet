package com.resqnet.repository;

import com.resqnet.entity.Shelter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShelterRepository extends JpaRepository<Shelter, Long> {
    @Query("SELECT COALESCE(SUM(s.totalCapacity), 0) FROM Shelter s")
    long sumTotalCapacity();

    @Query("SELECT COALESCE(SUM(s.occupiedCapacity), 0) FROM Shelter s")
    long sumOccupiedCapacity();
}
