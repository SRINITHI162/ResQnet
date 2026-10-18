import React, { useCallback, useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import api from '../api/axios';
import DisasterMap from '../components/DisasterMap';
import { useAuth } from '../context/AuthContext';

export default function IncidentDetail() {
  const { id } = useParams();
  const { user } = useAuth();
  const [incident, setIncident] = useState(null);
  const [timeline, setTimeline] = useState([]);
  const [allocations, setAllocations] = useState([]);
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [isUpdating, setIsUpdating] = useState(false);

  const load = useCallback(async () => {
    try {
      const [i, t, a] = await Promise.all([
        api.get(`/incidents/${id}`),
        api.get(`/incidents/${id}/timeline`),
        api.get(`/allocations/incident/${id}`),
      ]);
      setIncident(i.data);
      setTimeline(t.data);
      setAllocations(a.data);
      setError('');
    } catch (err) {
      setError(err.response?.data?.message || 'Unable to load incident details.');
    } finally {
      setIsLoading(false);
    }
  }, [id]);

  useEffect(() => { load(); }, [load]);

  const runAction = async (action) => {
    setError('');
    setIsUpdating(true);
    try {
      await action();
      await load();
    } catch (err) {
      setError(err.response?.data?.message || 'Unable to update this incident.');
    } finally {
      setIsUpdating(false);
    }
  };

  if (isLoading) return <p>Loading...</p>;
  if (!incident) return <div className="alert-error">{error || 'Incident not found.'}</div>;

  const isAdminOrCoordinator = ['ADMIN', 'COORDINATOR'].includes(user?.role);
  const canResolveIncident = isAdminOrCoordinator || user?.role === 'RESCUE_TEAM';

  return (
    <div>
      <h1 className="page-title">{incident.title}</h1>
      {error && <div className="alert-error">{error}</div>}
      <div className="detail-grid">
        <div>
          <div className="panel">
            <h2>Incident Details</h2>
            <p><strong>Type:</strong> {incident.disasterType}</p>
            <p><strong>Priority:</strong> {incident.priorityLevel} ({incident.priorityScore})</p>
            <p><strong>Status:</strong> {incident.status}</p>
            <p><strong>Location:</strong> {incident.locationName}</p>
            <p><strong>Affected:</strong> {incident.affectedPeople} | <strong>Injured:</strong> {incident.injuredPeople} | <strong>Vulnerable:</strong> {incident.vulnerablePeople}</p>
            <p><strong>Description:</strong> {incident.description}</p>
            <div className="action-row">
              {isAdminOrCoordinator && incident.status === 'REPORTED' && (
                <button
                  className="btn btn-secondary"
                  disabled={isUpdating}
                  onClick={() => runAction(() => api.put(`/incidents/${id}/verify`))}
                >
                  Verify Incident
                </button>
              )}
              <button
                className="btn btn-primary"
                disabled={isUpdating || !isAdminOrCoordinator}
                title={!isAdminOrCoordinator ? 'Only admins and coordinators can allocate resources.' : undefined}
                onClick={() => runAction(() => api.post(`/allocations/incident/${id}/auto-allocate?maxResources=3`))}
              >
                Auto-Allocate Resources
                {!isAdminOrCoordinator && ' (Admin/Coordinator only)'}
              </button>
              {canResolveIncident && incident.status !== 'RESOLVED' && (
                <button
                  className="btn btn-success"
                  disabled={isUpdating}
                  onClick={() => runAction(() => api.put(`/incidents/${id}/status?status=RESOLVED`))}
                >
                  Mark Resolved
                </button>
              )}
            </div>
          </div>

          <div className="panel">
            <h2>Resource Allocations</h2>
            <table className="data-table">
              <thead><tr><th>Resource</th><th>Score</th><th>Distance (km)</th><th>ETA (min)</th></tr></thead>
              <tbody>
                {allocations.map((a) => (
                  <tr key={a.id}>
                    <td>{a.resource?.resourceName}</td>
                    <td>{a.allocationScore?.toFixed(1)}</td>
                    <td>{a.distanceKm?.toFixed(1)}</td>
                    <td>{a.estimatedTravelTimeMin?.toFixed(0)}</td>
                  </tr>
                ))}
                {allocations.length === 0 && <tr><td colSpan={4} className="empty-row">No resources allocated yet</td></tr>}
              </tbody>
            </table>
          </div>

          <div className="panel">
            <h2>Timeline</h2>
            <ul className="timeline">
              {timeline.map((t) => (
                <li key={t.id}><span className="timeline-time">{new Date(t.timestamp).toLocaleTimeString()}</span> {t.event}</li>
              ))}
            </ul>
          </div>
        </div>

        <div>
          <DisasterMap incidents={[incident]} center={[incident.latitude, incident.longitude]} height="400px" />
        </div>
      </div>
    </div>
  );
}
