package com.resqnet.service;

import com.resqnet.entity.Road;
import com.resqnet.enums.RoadConditionType;
import com.resqnet.exception.ResourceNotFoundException;
import com.resqnet.repository.RoadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoadService {

    private final RoadRepository roadRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public Road create(Road road) {
        return roadRepository.save(road);
    }

    public List<Road> getAll() {
        return roadRepository.findAll();
    }

    public Road getById(Long id) {
        return roadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Road not found: " + id));
    }

    public Road updateCondition(Long id, RoadConditionType condition, Boolean blocked) {
        Road road = getById(id);
        road.setRoadCondition(condition);
        if (blocked != null) road.setBlocked(blocked);
        if (condition == RoadConditionType.BLOCKED) road.setBlocked(true);
        Road saved = roadRepository.save(road);

        // Module 10: notify connected users so routes can be recalculated
        messagingTemplate.convertAndSend("/topic/road-updates", saved);
        return saved;
    }

    public void delete(Long id) {
        roadRepository.deleteById(id);
    }
}
