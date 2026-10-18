import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const links = [
  { to: '/dashboard', label: 'Dashboard', icon: '📊' },
  { to: '/incidents', label: 'Incidents', icon: '🚨' },
  { to: '/map', label: 'Live Map', icon: '🗺️' },
  { to: '/resources', label: 'Resources', icon: '🚑' },
  { to: '/rescue-teams', label: 'Rescue Teams', icon: '🧑‍🚒' },
  { to: '/shelters', label: 'Shelters', icon: '🏠' },
  { to: '/roads', label: 'Roads', icon: '🛣️' },
  { to: '/analytics', label: 'Analytics', icon: '📈' },
  { to: '/notifications', label: 'Notifications', icon: '🔔' },
];

export default function Sidebar() {
  const { user } = useAuth();
  const visibleLinks = links.filter((link) => (
    link.to !== '/dashboard' || ['ADMIN', 'COORDINATOR'].includes(user?.role)
  ));

  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <span className="brand-icon">🆘</span>
        <div className="brand-stack">
          <span className="brand-text">RESQNET</span>
          <span className="brand-subtext">Operations</span>
        </div>
      </div>
      <nav className="sidebar-nav">
        {visibleLinks.map((l) => (
          <NavLink key={l.to} to={l.to} className={({ isActive }) => 'sidebar-link' + (isActive ? ' active' : '')}>
            <span className="sidebar-icon">{l.icon}</span>
            <span>{l.label}</span>
          </NavLink>
        ))}
      </nav>
      <div className="sidebar-footer">Intelligent Disaster Response System</div>
    </aside>
  );
}
