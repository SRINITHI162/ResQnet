import React, { useEffect, useState } from 'react';
import api from '../api/axios';
import DisasterMap from '../components/DisasterMap';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Dashboard() {
  const { user } = useAuth();
  const [stats, setStats] = useState(null);
  const [incidents, setIncidents] = useState([]);
  const [resources, setResources] = useState([]);
  const [rescueTeams, setRescueTeams] = useState([]);
  const [shelters, setShelters] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!['ADMIN', 'COORDINATOR'].includes(user?.role)) return undefined;

    const load = async () => {
      const results = await Promise.allSettled([
        api.get('/dashboard/statistics'),
        api.get('/incidents/priority-queue'),
        api.get('/resources'),
        api.get('/rescue-teams'),
        api.get('/shelters'),
      ]);

      const failures = [];
      const setters = [
        (data) => setStats(data),
        (data) => setIncidents(data),
        (data) => setResources(data),
        (data) => setRescueTeams(data),
        (data) => setShelters(data),
      ];
      const labels = ['dashboard statistics', 'incidents', 'resources', 'rescue teams', 'shelters'];

      results.forEach((result, index) => {
        if (result.status === 'fulfilled') {
          setters[index](result.value.data);
        } else {
          failures.push(`${labels[index]}: ${result.reason.response?.data?.message || result.reason.message}`);
        }
      });

      setError(failures.length ? `Some dashboard data could not be loaded (${failures.join('; ')}).` : '');
    };

    load();
    const interval = setInterval(load, 15000);
    return () => clearInterval(interval);
  }, [user?.role]);

  if (!['ADMIN', 'COORDINATOR'].includes(user?.role)) {
    return <div className="alert-error">The dashboard is available to admins and coordinators.</div>;
  }

  return (
    <div>
      <h1 className="page-title">Real-Time Dashboard</h1>
      {error && <div className="alert-error">{error}</div>}

      <div className="stat-grid">
        <StatCard label="Total Incidents" value={stats?.incidents?.total} color="#2563eb" icon="⚠️" />
        <StatCard label="Critical" value={stats?.incidents?.critical} color="#dc2626" icon="🚨" />
        <StatCard label="High Priority" value={stats?.incidents?.high} color="#f97316" icon="📌" />
        <StatCard label="Resolved" value={stats?.incidents?.resolved} color="#16a34a" icon="✅" />
        <StatCard label="Resources Available" value={stats?.resources?.available} color="#7c3aed" icon="🚑" />
        <StatCard label="Teams Available" value={stats?.rescueTeams?.available} color="#0891b2" icon="🧑‍🚒" />
        <StatCard label="Shelter Capacity Left" value={stats?.shelters?.availableCapacity} color="#059669" icon="🏠" />
        <StatCard label="Total Shelters" value={stats?.shelters?.total} color="#ca8a04" icon="🛏️" />
      </div>

      <div className="panel">
        <div className="panel-header">
          <h2>Live Situation Map</h2>
          <Link className="btn btn-secondary" to="/map">Open full map →</Link>
        </div>
        <DisasterMap incidents={incidents} resources={resources} rescueTeams={rescueTeams} shelters={shelters} height="450px" />
      </div>

      <div className="panel">
        <div className="panel-header">
          <h2>Priority Queue — Active Incidents</h2>
          <Link className="btn btn-secondary" to="/incidents">View all →</Link>
        </div>
        <table className="data-table">
          <thead>
            <tr><th>#</th><th>Title</th><th>Type</th><th>Priority</th><th>Status</th><th>Affected</th></tr>
          </thead>
          <tbody>
            {incidents.slice(0, 8).map((inc) => (
              <tr key={inc.id}>
                <td>{inc.id}</td>
                <td><Link to={`/incidents/${inc.id}`}>{inc.title}</Link></td>
                <td>{inc.disasterType}</td>
                <td><span className={`badge badge-${(inc.priorityLevel || 'low').toLowerCase()}`}>{inc.priorityLevel || 'PENDING'}</span></td>
                <td>{inc.status}</td>
                <td>{inc.affectedPeople ?? '-'}</td>
              </tr>
            ))}
            {incidents.length === 0 && <tr><td colSpan={6} className="empty-row">No active incidents</td></tr>}
          </tbody>
        </table>
      </div>
    </div>
  );
}

function StatCard({ label, value, color, icon }) {
  return (
    <div className="stat-card" style={{ borderTopColor: color }}>
      <div className="stat-header">
        <span className="stat-icon" style={{ background: `${color}18`, color }}>{icon}</span>
        <span className="stat-trend">Live</span>
      </div>
      <div className="stat-value">{value ?? '—'}</div>
      <div className="stat-label">{label}</div>
    </div>
  );
}
