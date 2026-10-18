import React, { useEffect, useMemo } from 'react';
import { MapContainer, TileLayer, Marker, Popup, Polyline, useMap } from 'react-leaflet';
import L from 'leaflet';

// Fix default marker icon paths (react-leaflet + webpack issue)
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
});

const priorityColor = {
  CRITICAL: '#dc2626',
  HIGH: '#f97316',
  MEDIUM: '#eab308',
  LOW: '#16a34a',
};

function coloredIcon(color) {
  return L.divIcon({
    className: 'custom-pin',
    html: `<div style="background:${color};width:18px;height:18px;border-radius:50%;border:3px solid white;box-shadow:0 0 4px rgba(0,0,0,0.5)"></div>`,
    iconSize: [18, 18],
    iconAnchor: [9, 9],
  });
}

const shelterIcon = coloredIcon('#16a34a');
const teamIcon = coloredIcon('#2563eb');
const resourceIcon = coloredIcon('#7c3aed');

function FitBounds({ points }) {
  const map = useMap();
  useEffect(() => {
    if (points.length > 0) {
      const bounds = L.latLngBounds(points.map((p) => [p.lat, p.lng]));
      map.fitBounds(bounds, { padding: [40, 40] });
    }
  }, [points, map]);
  return null;
}

/**
 * Renders incidents, resources, rescue teams, shelters and an optional
 * recommended route as a Leaflet + OpenStreetMap layer.
 */
export default function DisasterMap({
  incidents = [],
  resources = [],
  rescueTeams = [],
  shelters = [],
  routePath = [], // array of {lat, lng}
  center = [10.9601, 78.0766],
  height = '600px',
}) {
  const allPoints = useMemo(() => {
    const pts = [];
    incidents.forEach((i) => i.latitude && pts.push({ lat: i.latitude, lng: i.longitude }));
    resources.forEach((r) => r.latitude && pts.push({ lat: r.latitude, lng: r.longitude }));
    rescueTeams.forEach((t) => t.latitude && pts.push({ lat: t.latitude, lng: t.longitude }));
    shelters.forEach((s) => s.latitude && pts.push({ lat: s.latitude, lng: s.longitude }));
    return pts;
  }, [incidents, resources, rescueTeams, shelters]);

  return (
    <div style={{ height, width: '100%', borderRadius: '12px', overflow: 'hidden' }}>
      <MapContainer center={center} zoom={11} style={{ height: '100%', width: '100%' }} scrollWheelZoom={true}>
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />

        {incidents.map((incident) => (
          <Marker
            key={`incident-${incident.id}`}
            position={[incident.latitude, incident.longitude]}
            icon={coloredIcon(priorityColor[incident.priorityLevel] || '#6b7280')}
          >
            <Popup>
              <strong>{incident.title}</strong><br />
              {incident.disasterType} — {incident.priorityLevel || 'PENDING'}<br />
              Affected: {incident.affectedPeople ?? 'N/A'}<br />
              Status: {incident.status}
            </Popup>
          </Marker>
        ))}

        {resources.map((res) => (
          res.latitude ? (
            <Marker key={`resource-${res.id}`} position={[res.latitude, res.longitude]} icon={resourceIcon}>
              <Popup>
                <strong>{res.resourceName}</strong><br />
                Type: {res.resourceType}<br />
                Status: {res.status}<br />
                Available: {res.availableQuantity}/{res.quantity}
              </Popup>
            </Marker>
          ) : null
        ))}

        {rescueTeams.map((team) => (
          team.latitude ? (
            <Marker key={`team-${team.id}`} position={[team.latitude, team.longitude]} icon={teamIcon}>
              <Popup>
                <strong>{team.teamName}</strong><br />
                Specialization: {team.specialization}<br />
                Status: {team.availabilityStatus}<br />
                Members: {team.membersCount}
              </Popup>
            </Marker>
          ) : null
        ))}

        {shelters.map((shelter) => (
          shelter.latitude ? (
            <Marker key={`shelter-${shelter.id}`} position={[shelter.latitude, shelter.longitude]} icon={shelterIcon}>
              <Popup>
                <strong>{shelter.name}</strong><br />
                Capacity: {shelter.occupiedCapacity}/{shelter.totalCapacity}<br />
                Status: {shelter.status}
              </Popup>
            </Marker>
          ) : null
        ))}

        {routePath.length > 1 && (
          <Polyline positions={routePath.map((p) => [p.lat, p.lng])} pathOptions={{ color: '#2563eb', weight: 5, opacity: 0.8 }} />
        )}

        <FitBounds points={allPoints} />
      </MapContainer>
    </div>
  );
}
