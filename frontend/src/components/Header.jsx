import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { getGuestId, getGuestName, setGuestName } from '../utils/guest';

const s = {
  header: {
    background: 'linear-gradient(135deg, #1a1a2e 0%, #16213e 100%)',
    padding: '0 1.5rem',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    height: '56px',
    borderBottom: '1px solid #2a2a4a',
    flexShrink: 0,
  },
  brand: {
    textDecoration: 'none',
    color: '#7c6af7',
    fontSize: '1.4rem',
    fontWeight: 700,
    letterSpacing: '0.04em',
  },
  identity: { display: 'flex', alignItems: 'center', gap: '0.5rem' },
  label: { color: '#888', fontSize: '0.8rem' },
  nameInput: {
    background: '#1e1e3a',
    border: '1px solid #3a3a5c',
    color: '#e0e0e0',
    borderRadius: '6px',
    padding: '4px 8px',
    fontSize: '0.85rem',
    width: '150px',
  },
};

export default function Header() {
  const [name, setName] = useState(getGuestName);

  function handleBlur(e) {
    const val = e.target.value.trim();
    if (val) {
      setGuestName(val);
      setName(val);
    }
  }

  return (
    <header style={s.header}>
      <Link to="/" style={s.brand}>migmereborn</Link>
      <div style={s.identity}>
        <span style={s.label}>You:</span>
        <input
          style={s.nameInput}
          value={name}
          onChange={e => setName(e.target.value)}
          onBlur={handleBlur}
          placeholder="Your name"
        />
      </div>
    </header>
  );
}
