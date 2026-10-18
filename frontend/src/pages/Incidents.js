import React, { useEffect, useState } from 'react';
import api from '../api/axios';
import { Link } from 'react-router-dom';

export default function Incidents() {
  const [incidents, setIncidents] = useState([]);
  const [filter, setFilter] = useState('ALL');
  const [search, setSearch] = useState('');

  const load = async () => {
    const res = await api.get('/incidents');
    setIncidents(res.data);
  };

  useEffect(() => { load(); }, []);

  const verify = async (id) => {
    await api.put(`/incidents/${id}/verify`);
    load();
  };

  const filtered = incidents.filter((i) =>
    (filter === 'ALL' || i.status === filter) &&
    (i.title?.toLowerCase().includes(search.toLowerCase()) || i.locationName?.toLowerCase().includes(search.toLowerCase()))
  );

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Incidents</h1>
        <Link className="btn btn-primary" to="/incidents/new">+ Report Incident</Link>
      </div>

      <div className="toolbar">
        <input placeholder="Search by title or location..." value={search} onChange={(e) => setSearch(e.target.value)} />
        <select value={filter} onChange={(e) => setFilter(e.target.value)}>
          <option value="ALL">All statuses</option>
          <option value="REPORTED">Reported</option>
          <option value="UNDER_REVIEW">Under Review</option>
          <option value="VERIFIED">Verified</option>
          <option value="RESOURCE_ASSIGNED">Resource Assigned</option>
          <option value="RESCUE_IN_PROGRESS">Rescue In Progress</option>
          <option value="RESOLVED">Resolved</option>
          <option value="REJECTED">Rejected</option>
        </select>
      </div>

      <table className="data-table">
        <thead>
          <tr><th>#</th><th>Title</th><th>Type</th><th>Priority</th><th>Status</th><th>Location</th><th>Affected</th><th>Actions</th></tr>
        </thead>
        <tbody>
          {filtered.map((inc) => (
            <tr key={inc.id}>
              <td>{inc.id}</td>
              <td><Link to={`/incidents/${inc.id}`}>{inc.title}</Link></td>
              <td>{inc.disasterType}</td>
              <td>{inc.priorityLevel ? <span className={`badge badge-${inc.priorityLevel.toLowerCase()}`}>{inc.priorityLevel}</span> : '—'}</td>
              <td>{inc.status}</td>
              <td>{inc.locationName}</td>
              <td>{inc.affectedPeople ?? '-'}</td>
              <td>
                {inc.status === 'REPORTED' && <button className="btn btn-sm btn-secondary" onClick={() => verify(inc.id)}>Verify</button>}
              </td>
            </tr>
          ))}
          {filtered.length === 0 && <tr><td colSpan={8} className="empty-row">No incidents found</td></tr>}
        </tbody>
      </table>
    </div>
  );
}
