package com.resqnet.service;

import com.resqnet.repository.RoadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RoadRepository roadRepository;
    private final RouteEngine routeEngine;
    private final SimpMessagingTemplate messagingTemplate;

    public RouteEngine.RouteResult calculateRoute(String sourceNode, String destinationNode) {
        return routeEngine.findBestRoute(roadRepository.findAll(), sourceNode, destinationNode);
    }

    /**
     * Module 8: Dynamic Route Recalculation.
     * Re-runs Dijkstra over the latest road graph (post road-condition-update) and
     * pushes the new recommended route to connected clients via WebSocket.
     */
    public RouteEngine.RouteResult recalculateRoute(String sourceNode, String destinationNode) {
        RouteEngine.RouteResult result = calculateRoute(sourceNode, destinationNode);
        messagingTemplate.convertAndSend("/topic/route-updates", result);
        return result;
    }
}
