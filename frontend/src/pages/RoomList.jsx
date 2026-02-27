import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

const s = {
  container: { padding: '2rem', maxWidth: '600px', margin: '0 auto' },
  heading: { color: '#7c6af7', marginBottom: '1.5rem', fontSize: '1.5rem' },
  list: { listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '0.75rem' },
  card: {
    background: '#1a1a2e',
    border: '1px solid #2a2a4a',
    borderRadius: '10px',
    padding: '1rem 1.5rem',
    textDecoration: 'none',
    color: '#e0e0e0',
    display: 'block',
    transition: 'border-color 0.2s',
  },
  error: { color: '#f87171', marginTop: '1rem' },
};

export default function RoomList() {
  const [rooms, setRooms] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetch('/api/rooms')
      .then(r => r.json())
      .then(setRooms)
      .catch(e => setError('Failed to load rooms'));
  }, []);

  return (
    <div style={s.container}>
      <h1 style={s.heading}>Rooms</h1>
      {error && <p style={s.error}>{error}</p>}
      <ul style={s.list}>
        {rooms.map(room => (
          <li key={room.id}>
            <Link
              to={`/rooms/${room.id}`}
              style={s.card}
              onMouseEnter={e => (e.currentTarget.style.borderColor = '#7c6af7')}
              onMouseLeave={e => (e.currentTarget.style.borderColor = '#2a2a4a')}
            >
              <strong>{room.name}</strong>
              <div style={{ color: '#888', fontSize: '0.8rem', marginTop: '4px' }}>#{room.id}</div>
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
}
