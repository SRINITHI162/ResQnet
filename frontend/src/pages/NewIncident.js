import React, { useState } from 'react';
import api from '../api/axios';
import { useNavigate } from 'react-router-dom';
import { MapContainer, TileLayer, Marker, useMapEvents } from 'react-leaflet';

const disasterTypes = ['FLOOD','CYCLONE','FIRE','LANDSLIDE','EARTHQUAKE','ACCIDENT','STORM','BUILDING_COLLAPSE','MEDICAL_EMERGENCY','OTHER'];

function LocationPicker({ position, setPosition }) {
  useMapEvents({
    click(e) { setPosition(e.latlng); },
  });
  return position ? <Marker position={position} /> : null;
}

export default function NewIncident() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    disasterType: 'FLOOD', title: '', description: '', locationName: '',
    severity: 5, affectedPeople: 0, injuredPeople: 0, vulnerablePeople: 0, requiredResources: '',
  });
  const [position, setPosition] = useState({ lat: 10.9601, lng: 78.0766 });
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const update = (k, v) => setForm((f) => ({ ...f, [k]: v }));

  const submit = async (e) => {
    e.preventDefault();
    setError('');
    setIsSubmitting(true);
    try {
      const payload = { ...form, latitude: position.lat, longitude: position.lng };
      const res = await api.post('/incidents', payload);
      navigate(`/incidents/${res.data.id}`);
    } catch (err) {
      setError(
        err.response?.data?.message
          || (err.response
            ? err.message
            : 'Cannot reach the backend API. Make sure the backend is running and reachable at http://localhost:8080.')
      );
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div>
      <h1 className="page-title">Report a Disaster</h1>
      {error && <div className="alert-error">{error}</div>}
      <div className="form-map-grid">
        <form className="card-form" onSubmit={submit}>
          <label>Disaster Type</label>
          <select value={form.disasterType} onChange={(e) => update('disasterType', e.target.value)}>
            {disasterTypes.map((t) => <option key={t} value={t}>{t.replace('_', ' ')}</option>)}
          </select>

          <label>Title</label>
          <input value={form.title} onChange={(e) => update('title', e.target.value)} required />

          <label>Description</label>
          <textarea value={form.description} onChange={(e) => update('description', e.target.value)} rows={3} />

          <label>Location Name</label>
          <input value={form.locationName} onChange={(e) => update('locationName', e.target.value)} required />

          <div className="form-row">
            <div>
              <label>Severity (1-10)</label>
              <input type="number" min={1} max={10} value={form.severity} onChange={(e) => update('severity', Number(e.target.value))} />
            </div>
            <div>
              <label>Affected People</label>
              <input type="number" min={0} value={form.affectedPeople} onChange={(e) => update('affectedPeople', Number(e.target.value))} />
            </div>
          </div>

          <div className="form-row">
            <div>
              <label>Injured People</label>
              <input type="number" min={0} value={form.injuredPeople} onChange={(e) => update('injuredPeople', Number(e.target.value))} />
            </div>
            <div>
              <label>Vulnerable People</label>
              <input type="number" min={0} value={form.vulnerablePeople} onChange={(e) => update('vulnerablePeople', Number(e.target.value))} />
            </div>
          </div>

          <label>Required Resources (comma separated)</label>
          <input value={form.requiredResources} onChange={(e) => update('requiredResources', e.target.value)} placeholder="ambulance, boat, rescue team" />

          <button className="btn btn-primary" type="submit" disabled={isSubmitting}>
            {isSubmitting ? 'Submitting...' : 'Submit Report'}
          </button>
        </form>

        <div>
          <label style={{ display: 'block', marginBottom: 8 }}>Click on the map to set the exact location</label>
          <div style={{ height: '480px', borderRadius: '12px', overflow: 'hidden' }}>
            <MapContainer center={position} zoom={12} style={{ height: '100%', width: '100%' }}>
              <TileLayer
                attribution='&copy; OpenStreetMap contributors'
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
              />
              <LocationPicker position={position} setPosition={setPosition} />
            </MapContainer>
          </div>
          <p className="hint">Selected: {position.lat.toFixed(4)}, {position.lng.toFixed(4)}</p>
        </div>
      </div>
    </div>
  );
}
