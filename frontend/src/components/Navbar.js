import React from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  return (
    <header className="navbar">
      <div className="navbar-title-wrap">
        <span className="navbar-eyebrow">Emergency command</span>
        <div className="navbar-title">Control Room</div>
      </div>

      <div className="navbar-right">
        <span className="navbar-status"><span className="live-dot" /> Live Ops</span>
        {user && (
          <>
            <span className="navbar-user">{user.name} <span className="role-badge">{user.role}</span></span>
            <button className="btn btn-ghost" onClick={() => { logout(); navigate('/login'); }}>Logout</button>
          </>
        )}
      </div>
    </header>
  );
}
