import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Register() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ name: '', email: '', phone: '', password: '', location: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const update = (k, v) => setForm((f) => ({ ...f, [k]: v }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await register(form);
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={handleSubmit}>
        <div className="auth-brand">🆘 RESQNET</div>
        <p className="auth-subtitle">Create your account</p>
        {error && <div className="alert-error">{error}</div>}
        <label>Full Name</label>
        <input value={form.name} onChange={(e) => update('name', e.target.value)} required />
        <label>Email</label>
        <input type="email" value={form.email} onChange={(e) => update('email', e.target.value)} required />
        <label>Phone</label>
        <input value={form.phone} onChange={(e) => update('phone', e.target.value)} />
        <label>Password</label>
        <input type="password" value={form.password} onChange={(e) => update('password', e.target.value)} required />
        <p className="hint">New accounts are created with citizen access. Contact an administrator if your role needs to change.</p>
        <label>Location</label>
        <input value={form.location} onChange={(e) => update('location', e.target.value)} />
        <button className="btn btn-primary" type="submit" disabled={loading}>{loading ? 'Creating...' : 'Register'}</button>
        <p className="auth-footer">Already have an account? <Link to="/login">Sign in</Link></p>
      </form>
    </div>
  );
}
