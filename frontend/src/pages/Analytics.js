import React, { useEffect, useState } from 'react';
import api from '../api/axios';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, PieChart, Pie, Cell, CartesianGrid, Legend } from 'recharts';

const COLORS = ['#dc2626', '#f97316', '#eab308', '#16a34a', '#2563eb', '#7c3aed', '#0891b2', '#ca8a04'];

export default function Analytics() {
  const [incidents, setIncidents] = useState([]);

  useEffect(() => {
    api.get('/incidents').then((res) => setIncidents(res.data));
  }, []);

  const byType = Object.entries(
    incidents.reduce((acc, i) => { acc[i.disasterType] = (acc[i.disasterType] || 0) + 1; return acc; }, {})
  ).map(([name, value]) => ({ name, value }));

  const byPriority = Object.entries(
    incidents.reduce((acc, i) => { const k = i.priorityLevel || 'PENDING'; acc[k] = (acc[k] || 0) + 1; return acc; }, {})
  ).map(([name, value]) => ({ name, value }));

  const byStatus = Object.entries(
    incidents.reduce((acc, i) => { acc[i.status] = (acc[i.status] || 0) + 1; return acc; }, {})
  ).map(([name, value]) => ({ name, value }));

  return (
    <div>
      <h1 className="page-title">Analytics</h1>
      <div className="analytics-grid">
        <div className="panel">
          <h2>Incidents by Disaster Type</h2>
          <ResponsiveContainer width="100%" height={280}>
            <BarChart data={byType}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" tick={{ fontSize: 11 }} />
              <YAxis allowDecimals={false} />
              <Tooltip />
              <Bar dataKey="value" fill="#2563eb" radius={[6, 6, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>

        <div className="panel">
          <h2>Incidents by Priority</h2>
          <ResponsiveContainer width="100%" height={280}>
            <PieChart>
              <Pie data={byPriority} dataKey="value" nameKey="name" outerRadius={100} label>
                {byPriority.map((entry, index) => <Cell key={entry.name} fill={COLORS[index % COLORS.length]} />)}
              </Pie>
              <Tooltip /><Legend />
            </PieChart>
          </ResponsiveContainer>
        </div>

        <div className="panel">
          <h2>Incidents by Status</h2>
          <ResponsiveContainer width="100%" height={280}>
            <BarChart data={byStatus} layout="vertical">
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis type="number" allowDecimals={false} />
              <YAxis dataKey="name" type="category" width={140} tick={{ fontSize: 11 }} />
              <Tooltip />
              <Bar dataKey="value" fill="#16a34a" radius={[0, 6, 6, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
}
