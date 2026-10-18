import React, { useEffect, useState, useRef } from 'react';
import api from '../api/axios';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export default function Notifications() {
  const [notifications, setNotifications] = useState([]);
  const clientRef = useRef(null);

  useEffect(() => {
    api.get('/notifications').then((res) => setNotifications(res.data));

    const client = new Client({
      webSocketFactory: () => new SockJS((process.env.REACT_APP_API_URL || 'http://localhost:8080/api').replace('/api', '') + '/ws'),
      reconnectDelay: 5000,
      onConnect: () => {
        client.subscribe('/topic/notifications', (msg) => {
          const notification = JSON.parse(msg.body);
          setNotifications((prev) => [notification, ...prev]);
        });
      },
    });
    client.activate();
    clientRef.current = client;
    return () => client.deactivate();
  }, []);

  return (
    <div>
      <h1 className="page-title">Notifications</h1>
      <p className="hint">Live updates stream in over WebSocket as new critical incidents, allocations, and road changes occur.</p>
      <div className="notification-list">
        {notifications.map((n) => (
          <div key={n.id} className={`notification-item severity-${(n.severity || 'info').toLowerCase()}`}>
            <div className="notification-title">{n.title}</div>
            <div className="notification-message">{n.message}</div>
            <div className="notification-time">{new Date(n.createdAt).toLocaleString()}</div>
          </div>
        ))}
        {notifications.length === 0 && <p className="empty-row">No notifications yet</p>}
      </div>
    </div>
  );
}
