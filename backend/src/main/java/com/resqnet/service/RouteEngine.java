package com.resqnet.service;

import com.resqnet.entity.Road;
import com.resqnet.enums.RoadConditionType;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Graph-based route optimization engine.
 *
 * The road network is modelled as a weighted, directed graph (built bidirectionally
 * from the `roads` table). Dijkstra's algorithm finds the lowest-cost path.
 *
 * Route Cost = Distance + Traffic Penalty + Flood Risk + Road Condition Penalty + Blockage Penalty
 *
 * Blocked roads are excluded from the graph entirely (infinite cost / edge removed),
 * so a blocked road can never be selected.
 *
 * Time complexity: O((V + E) log V) using a PriorityQueue-backed Dijkstra implementation.
 * Space complexity: O(V + E).
 *
 * The Graph class is structured so an A* implementation (with a Haversine-based heuristic)
 * could be dropped in later without changing the public API of `findBestRoute`.
 */
@Service
public class RouteEngine {

    public static class Edge {
        public final String to;
        public final double cost;      // computed route cost
        public final double distanceKm;
        public final double timeMin;
        public final Long roadId;

        public Edge(String to, double cost, double distanceKm, double timeMin, Long roadId) {
            this.to = to;
            this.cost = cost;
            this.distanceKm = distanceKm;
            this.timeMin = timeMin;
            this.roadId = roadId;
        }
    }

    public static class RouteResult {
        public List<String> path = new ArrayList<>();
        public double totalDistanceKm;
        public double totalTimeMin;
        public double totalCost;
        public boolean found;
    }

    /** Build an adjacency list graph from road entities, skipping blocked roads. */
    public Map<String, List<Edge>> buildGraph(List<Road> roads) {
        Map<String, List<Edge>> graph = new HashMap<>();
        for (Road road : roads) {
            if (Boolean.TRUE.equals(road.getBlocked())
                    || road.getRoadCondition() == RoadConditionType.BLOCKED) {
                continue; // never route through a blocked road
            }
            double cost = computeEdgeCost(road);
            graph.computeIfAbsent(road.getSource(), k -> new ArrayList<>())
                    .add(new Edge(road.getDestination(), cost, road.getDistanceKm(),
                            road.getEstimatedTimeMin(), road.getId()));
            // roads are treated as bidirectional
            graph.computeIfAbsent(road.getDestination(), k -> new ArrayList<>())
                    .add(new Edge(road.getSource(), cost, road.getDistanceKm(),
                            road.getEstimatedTimeMin(), road.getId()));
        }
        return graph;
    }

    private double computeEdgeCost(Road road) {
        double distance = road.getDistanceKm() == null ? 1 : road.getDistanceKm();
        double trafficPenalty = (road.getTrafficLevel() == null ? 1 : road.getTrafficLevel()) * 2.0;
        double floodPenalty = (road.getFloodLevel() == null ? 0 : road.getFloodLevel()) * 5.0;
        double conditionPenalty = conditionPenalty(road.getRoadCondition());
        double blockagePenalty = Boolean.TRUE.equals(road.getBlocked()) ? 1000 : 0;

        return distance + trafficPenalty + floodPenalty + conditionPenalty + blockagePenalty;
    }

    private double conditionPenalty(RoadConditionType condition) {
        if (condition == null) return 0;
        return switch (condition) {
            case NORMAL -> 0;
            case TRAFFIC -> 3;
            case DAMAGED -> 8;
            case FLOODED -> 15;
            case BLOCKED -> 1000;
        };
    }

    /** Dijkstra's shortest (lowest cost) path from source to destination. */
    public RouteResult findBestRoute(List<Road> roads, String sourceNode, String destinationNode) {
        Map<String, List<Edge>> graph = buildGraph(roads);
        RouteResult result = new RouteResult();

        Map<String, Double> dist = new HashMap<>();
        Map<String, String> prevNode = new HashMap<>();
        Map<String, Double> distKm = new HashMap<>();
        Map<String, Double> timeMin = new HashMap<>();

        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> dist.getOrDefault(n, Double.MAX_VALUE)));
        dist.put(sourceNode, 0.0);
        distKm.put(sourceNode, 0.0);
        timeMin.put(sourceNode, 0.0);
        pq.add(sourceNode);

        Set<String> visited = new HashSet<>();

        while (!pq.isEmpty()) {
            String current = pq.poll();
            if (visited.contains(current)) continue;
            visited.add(current);

            if (current.equals(destinationNode)) break;

            for (Edge edge : graph.getOrDefault(current, Collections.emptyList())) {
                double newCost = dist.get(current) + edge.cost;
                if (newCost < dist.getOrDefault(edge.to, Double.MAX_VALUE)) {
                    dist.put(edge.to, newCost);
                    distKm.put(edge.to, distKm.get(current) + edge.distanceKm);
                    timeMin.put(edge.to, timeMin.get(current) + edge.timeMin);
                    prevNode.put(edge.to, current);
                    pq.add(edge.to);
                }
            }
        }

        if (!dist.containsKey(destinationNode)) {
            result.found = false;
            return result;
        }

        LinkedList<String> path = new LinkedList<>();
        String node = destinationNode;
        while (node != null) {
            path.addFirst(node);
            node = prevNode.get(node);
        }

        result.found = true;
        result.path = path;
        result.totalCost = dist.get(destinationNode);
        result.totalDistanceKm = distKm.get(destinationNode);
        result.totalTimeMin = timeMin.get(destinationNode);
        return result;
    }
}
