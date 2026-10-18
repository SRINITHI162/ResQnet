import React, { useEffect, useState } from 'react';
import api from '../api/axios';
import DisasterMap from '../components/DisasterMap';

export default function MapPage() {
  const [incidents, setIncidents] = useState([]);
  const [resources, setResources] = useState([]);
  const [rescueTeams, setRescueTeams] = useState([]);
  const [shelters, setShelters] = useState([]);
  const [roads, setRoads] = useState([]);
  const [sourceNode, setSourceNode] = useState('');
  const [destNode, setDestNode] = useState('');
  const [routePath, setRoutePath] = useState([]);
  const [routeInfo, setRouteInfo] = useState(null);

  useEffect(() => {
    const load = async () => {
      const [i, r, t, s, rd] = await Promise.all([
        api.get('/incidents'),
        api.get('/resources'),
        api.get('/rescue-teams'),
        api.get('/shelters'),
        api.get('/roads'),
      ]);
      setIncidents(i.data);
      setResources(r.data);
      setRescueTeams(t.data);
      setShelters(s.data);
      setRoads(rd.data);
    };
    load();
  }, []);

  const roadNodes = Array.from(new Set(roads.flatMap((r) => [r.source, r.destination])));

  const calculateRoute = async () => {
    if (!sourceNode || !destNode) return;
    const res = await api.post('/routes/calculate', { sourceNode, destinationNode: destNode });
    setRouteInfo(res.data);
    if (res.data.found) {
      // Build lat/lng path from node names using road endpoints
      const nodeCoords = {};
      roads.forEach((r) => {
        nodeCoords[r.source] = { lat: r.sourceLat, lng: r.sourceLng };
        nodeCoords[r.destination] = { lat: r.destLat, lng: r.destLng };
      });
      setRoutePath(res.data.path.map((n) => nodeCoords[n]).filter(Boolean));
    } else {
      setRoutePath([]);
    }
  };

  return (
    <div>
      <h1 className="page-title">Map-Based Control Center</h1>

      <div className="panel">
        <div className="panel-header"><h2>Route Finder (Dijkstra)</h2></div>
        <div className="route-controls">
          <select value={sourceNode} onChange={(e) => setSourceNode(e.target.value)}>
            <option value="">Source node</option>
            {roadNodes.map((n) => <option key={n} value={n}>{n}</option>)}
          </select>
          <select value={destNode} onChange={(e) => setDestNode(e.target.value)}>
            <option value="">Destination node</option>
            {roadNodes.map((n) => <option key={n} value={n}>{n}</option>)}
          </select>
          <button className="btn btn-primary" onClick={calculateRoute}>Find Best Route</button>
        </div>
        {routeInfo && (
          routeInfo.found ? (
            <p className="route-summary">
              Path: {routeInfo.path.join(' → ')} · Distance: {routeInfo.totalDistanceKm?.toFixed(1)} km ·
              ETA: {routeInfo.totalTimeMin?.toFixed(0)} min · Cost: {routeInfo.totalCost?.toFixed(1)}
            </p>
          ) : <p className="route-summary error">No safe route found between these nodes.</p>
        )}
      </div>

      <div className="panel">
        <DisasterMap
          incidents={incidents}
          resources={resources}
          rescueTeams={rescueTeams}
          shelters={shelters}
          routePath={routePath}
          height="650px"
        />
        <div className="legend">
          <span><i style={{ background: '#dc2626' }} /> Critical</span>
          <span><i style={{ background: '#f97316' }} /> High</span>
          <span><i style={{ background: '#eab308' }} /> Medium</span>
          <span><i style={{ background: '#16a34a' }} /> Shelter</span>
          <span><i style={{ background: '#2563eb' }} /> Rescue Team</span>
          <span><i style={{ background: '#7c3aed' }} /> Resource</span>
        </div>
      </div>
    </div>
  );
}
