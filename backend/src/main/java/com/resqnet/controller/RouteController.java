package com.resqnet.controller;

import com.resqnet.dto.RouteRequest;
import com.resqnet.service.RouteEngine;
import com.resqnet.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @PostMapping("/calculate")
    public ResponseEntity<RouteEngine.RouteResult> calculate(@RequestBody RouteRequest request) {
        return ResponseEntity.ok(routeService.calculateRoute(request.getSourceNode(), request.getDestinationNode()));
    }

    @PostMapping("/recalculate")
    public ResponseEntity<RouteEngine.RouteResult> recalculate(@RequestBody RouteRequest request) {
        return ResponseEntity.ok(routeService.recalculateRoute(request.getSourceNode(), request.getDestinationNode()));
    }
}
