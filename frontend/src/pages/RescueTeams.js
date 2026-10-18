import React, { useEffect, useState } from 'react';
import api from '../api/axios';

const specializations = ['Flood Rescue','Fire Rescue','Medical','Search & Rescue','Mountain Rescue','Disaster Relief'];

export default function RescueTeams() {
  const [teams, setTeams] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({ teamName: '', specialization: specializations[0], membersCount: 5, location: '', latitude: 10.96, longitude: 78.08, contactNumber: '', availabilityStatus: 'AVAILABLE' });

  const load = async () => setTeams((await api.get('/rescue-teams')).data);
  useEffect(() => { load(); }, []);

  const update = (k, v) => setForm((f) => ({ ...f, [k]: v }));

  const submit = async (e) => {
    e.preventDefault();
    await api.post('/rescue-teams', form);
    setShowForm(false);
    load();
  };

  const remove = async (id) => { await api.delete(`/rescue-teams/${id}`); load(); };

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Rescue Teams</h1>
        <button className="btn btn-primary" onClick={() => setShowForm((s) => !s)}>{showForm ? 'Cancel' : '+ Add Team'}</button>
      </div>

      {showForm && (
        <form className="card-form inline-form" onSubmit={submit}>
          <input placeholder="Team name" value={form.teamName} onChange={(e) => update('teamName', e.target.value)} required />
          <select value={form.specialization} onChange={(e) => update('specialization', e.target.value)}>
            {specializations.map((s) => <option key={s} value={s}>{s}</option>)}
          </select>
          <input type="number" min={1} placeholder="Members" value={form.membersCount} onChange={(e) => update('membersCount', Number(e.target.value))} />
          <input placeholder="Location" value={form.location} onChange={(e) => update('location', e.target.value)} />
          <input type="number" step="0.0001" placeholder="Latitude" value={form.latitude} onChange={(e) => update('latitude', Number(e.target.value))} />
          <input type="number" step="0.0001" placeholder="Longitude" value={form.longitude} onChange={(e) => update('longitude', Number(e.target.value))} />
          <input placeholder="Contact" value={form.contactNumber} onChange={(e) => update('contactNumber', e.target.value)} />
          <button className="btn btn-secondary" type="submit">Save</button>
        </form>
      )}

      <table className="data-table">
        <thead><tr><th>Team</th><th>Specialization</th><th>Members</th><th>Status</th><th>Location</th><th></th></tr></thead>
        <tbody>
          {teams.map((t) => (
            <tr key={t.id}>
              <td>{t.teamName}</td>
              <td>{t.specialization}</td>
              <td>{t.membersCount}</td>
              <td><span className="badge badge-low">{t.availabilityStatus}</span></td>
              <td>{t.location}</td>
              <td><button className="btn btn-sm btn-danger" onClick={() => remove(t.id)}>Delete</button></td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
