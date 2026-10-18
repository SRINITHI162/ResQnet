import React, { useEffect, useState } from 'react';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';

const types = ['AMBULANCE','RESCUE_TEAM_UNIT','BOAT','FIRE_TRUCK','MEDICAL_TEAM','RESCUE_VEHICLE','FOOD_SUPPLY','WATER_SUPPLY','MEDICAL_SUPPLY','TEMPORARY_SHELTER_EQUIPMENT'];

export default function Resources() {
  const { user } = useAuth();
  const [resources, setResources] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [form, setForm] = useState({ resourceName: '', resourceType: 'AMBULANCE', quantity: 1, location: '', latitude: 10.96, longitude: 78.08 });

  const load = async () => {
    try {
      setResources((await api.get('/resources')).data);
      setError('');
    } catch (err) {
      setError(err.response?.data?.message || 'Unable to load resources.');
    }
  };
  useEffect(() => { load(); }, []);

  const update = (k, v) => setForm((f) => ({ ...f, [k]: v }));

  const submit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    setError('');
    try {
      await api.post('/resources', { ...form, availableQuantity: form.quantity });
      setShowForm(false);
      setForm({ resourceName: '', resourceType: 'AMBULANCE', quantity: 1, location: '', latitude: 10.96, longitude: 78.08 });
      await load();
    } catch (err) {
      setError(err.response?.data?.message || 'Unable to save resource.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const remove = async (id) => {
    setError('');
    try {
      await api.delete(`/resources/${id}`);
      await load();
    } catch (err) {
      setError(err.response?.data?.message || 'Unable to delete resource.');
    }
  };

  const canManageResources = user?.role === 'ADMIN';

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Resources</h1>
        {canManageResources && (
          <button className="btn btn-primary" onClick={() => setShowForm((s) => !s)}>{showForm ? 'Cancel' : '+ Add Resource'}</button>
        )}
      </div>
      {!canManageResources && <p className="hint">Only admins can add or delete resources.</p>}
      {error && <div className="alert-error">{error}</div>}

      {showForm && (
        <form className="card-form inline-form" onSubmit={submit}>
          <input placeholder="Name" value={form.resourceName} onChange={(e) => update('resourceName', e.target.value)} required />
          <select value={form.resourceType} onChange={(e) => update('resourceType', e.target.value)}>
            {types.map((t) => <option key={t} value={t}>{t.replace(/_/g, ' ')}</option>)}
          </select>
          <input type="number" min={1} placeholder="Quantity" value={form.quantity} onChange={(e) => update('quantity', Number(e.target.value))} />
          <input placeholder="Location name" value={form.location} onChange={(e) => update('location', e.target.value)} />
          <input type="number" step="0.0001" placeholder="Latitude" value={form.latitude} onChange={(e) => update('latitude', Number(e.target.value))} />
          <input type="number" step="0.0001" placeholder="Longitude" value={form.longitude} onChange={(e) => update('longitude', Number(e.target.value))} />
          <button className="btn btn-secondary" type="submit" disabled={isSubmitting}>
            {isSubmitting ? 'Saving...' : 'Save'}
          </button>
        </form>
      )}

      <table className="data-table">
        <thead><tr><th>Name</th><th>Type</th><th>Available</th><th>Status</th><th>Location</th><th></th></tr></thead>
        <tbody>
          {resources.map((r) => (
            <tr key={r.id}>
              <td>{r.resourceName}</td>
              <td>{r.resourceType.replace(/_/g, ' ')}</td>
              <td>{r.availableQuantity}/{r.quantity}</td>
              <td><span className={`badge badge-${r.status === 'AVAILABLE' ? 'low' : 'medium'}`}>{r.status}</span></td>
              <td>{r.location}</td>
              <td>{canManageResources && <button className="btn btn-sm btn-danger" onClick={() => remove(r.id)}>Delete</button>}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
