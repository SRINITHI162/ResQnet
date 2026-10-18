import React, { useEffect, useState } from 'react';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';

export default function Shelters() {
  const { user } = useAuth();
  const [shelters, setShelters] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [form, setForm] = useState({ name: '', address: '', latitude: 10.96, longitude: 78.08, totalCapacity: 100, occupiedCapacity: 0, foodAvailable: true, waterAvailable: true, medicalSupport: false, accessibilitySupport: false });

  const load = async () => {
    try {
      setShelters((await api.get('/shelters')).data);
      setError('');
    } catch (err) {
      setError(err.response?.data?.message || 'Unable to load shelters.');
    }
  };
  useEffect(() => { load(); }, []);

  const update = (k, v) => setForm((f) => ({ ...f, [k]: v }));

  const submit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    setError('');
    try {
      await api.post('/shelters', form);
      setShowForm(false);
      await load();
    } catch (err) {
      setError(err.response?.data?.message || 'Unable to save shelter.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const remove = async (id) => {
    setError('');
    try {
      await api.delete(`/shelters/${id}`);
      await load();
    } catch (err) {
      setError(err.response?.data?.message || 'Unable to delete shelter.');
    }
  };

  const canManageShelters = ['ADMIN', 'COORDINATOR'].includes(user?.role);

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Shelters</h1>
        {canManageShelters && (
          <button className="btn btn-primary" onClick={() => setShowForm((s) => !s)}>{showForm ? 'Cancel' : '+ Add Shelter'}</button>
        )}
      </div>
      {!canManageShelters && <p className="hint">Only admins and coordinators can add or delete shelters.</p>}
      {error && <div className="alert-error">{error}</div>}

      {showForm && (
        <form className="card-form inline-form" onSubmit={submit}>
          <input placeholder="Name" value={form.name} onChange={(e) => update('name', e.target.value)} required />
          <input placeholder="Address" value={form.address} onChange={(e) => update('address', e.target.value)} />
          <input type="number" step="0.0001" placeholder="Latitude" value={form.latitude} onChange={(e) => update('latitude', Number(e.target.value))} />
          <input type="number" step="0.0001" placeholder="Longitude" value={form.longitude} onChange={(e) => update('longitude', Number(e.target.value))} />
          <input type="number" min={1} placeholder="Total capacity" value={form.totalCapacity} onChange={(e) => update('totalCapacity', Number(e.target.value))} />
          <button className="btn btn-secondary" type="submit" disabled={isSubmitting}>
            {isSubmitting ? 'Saving...' : 'Save'}
          </button>
        </form>
      )}

      <table className="data-table">
        <thead><tr><th>Name</th><th>Capacity</th><th>Available</th><th>Status</th><th></th></tr></thead>
        <tbody>
          {shelters.map((s) => (
            <tr key={s.id}>
              <td>{s.name}</td>
              <td>{s.occupiedCapacity}/{s.totalCapacity}</td>
              <td>{s.totalCapacity - (s.occupiedCapacity || 0)}</td>
              <td><span className="badge badge-low">{s.status}</span></td>
              <td>{canManageShelters && <button className="btn btn-sm btn-danger" onClick={() => remove(s.id)}>Delete</button>}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
