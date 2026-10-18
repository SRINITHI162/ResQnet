import React, { useEffect, useState } from 'react';
import api from '../api/axios';

const conditions = ['NORMAL','TRAFFIC','DAMAGED','FLOODED','BLOCKED'];

export default function Roads() {
  const [roads, setRoads] = useState([]);

  const load = async () => setRoads((await api.get('/roads')).data);
  useEffect(() => { load(); }, []);

  const updateCondition = async (id, condition) => {
    await api.put(`/roads/${id}/status?condition=${condition}${condition === 'BLOCKED' ? '&blocked=true' : '&blocked=false'}`);
    load();
  };

  return (
    <div>
      <h1 className="page-title">Road Conditions</h1>
      <table className="data-table">
        <thead><tr><th>Source</th><th>Destination</th><th>Distance (km)</th><th>Condition</th><th>Blocked</th><th>Update</th></tr></thead>
        <tbody>
          {roads.map((r) => (
            <tr key={r.id}>
              <td>{r.source}</td>
              <td>{r.destination}</td>
              <td>{r.distanceKm}</td>
              <td><span className={`badge badge-${r.roadCondition === 'BLOCKED' ? 'critical' : r.roadCondition === 'FLOODED' ? 'high' : 'low'}`}>{r.roadCondition}</span></td>
              <td>{r.blocked ? 'Yes' : 'No'}</td>
              <td>
                <select defaultValue="" onChange={(e) => e.target.value && updateCondition(r.id, e.target.value)}>
                  <option value="">Change...</option>
                  {conditions.map((c) => <option key={c} value={c}>{c}</option>)}
                </select>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
