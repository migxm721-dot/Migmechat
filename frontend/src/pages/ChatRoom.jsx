import React, { useEffect, useRef, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { getGuestId, getGuestName } from '../utils/guest';

const s = {
  page: { display: 'flex', flexDirection: 'column', height: '100%' },
  topBar: {
    padding: '0.75rem 1.5rem',
    background: '#1a1a2e',
    borderBottom: '1px solid #2a2a4a',
    display: 'flex',
    alignItems: 'center',
    gap: '1rem',
  },
  backLink: { color: '#7c6af7', textDecoration: 'none', fontSize: '0.9rem' },
  roomTitle: { color: '#e0e0e0', fontWeight: 600 },
  messages: {
    flex: 1,
    overflowY: 'auto',
    padding: '1rem 1.5rem',
    display: 'flex',
    flexDirection: 'column',
    gap: '0.5rem',
  },
  msgBubble: {
    maxWidth: '70%',
    padding: '0.5rem 0.9rem',
    borderRadius: '12px',
    fontSize: '0.9rem',
    lineHeight: 1.4,
  },
  msgMine: { alignSelf: 'flex-end', background: '#3b2f7a', color: '#e0e0e0' },
  msgOther: { alignSelf: 'flex-start', background: '#1e1e3a', color: '#e0e0e0' },
  senderLabel: { fontSize: '0.7rem', color: '#888', marginBottom: '2px' },
  inputRow: {
    display: 'flex',
    padding: '0.75rem 1.5rem',
    gap: '0.5rem',
    borderTop: '1px solid #2a2a4a',
    background: '#0f0f14',
  },
  textInput: {
    flex: 1,
    background: '#1e1e3a',
    border: '1px solid #3a3a5c',
    color: '#e0e0e0',
    borderRadius: '8px',
    padding: '0.5rem 0.9rem',
    fontSize: '0.9rem',
    outline: 'none',
  },
  sendBtn: {
    background: '#7c6af7',
    color: '#fff',
    border: 'none',
    borderRadius: '8px',
    padding: '0.5rem 1.2rem',
    cursor: 'pointer',
    fontWeight: 600,
  },
  statusDot: {
    width: 8, height: 8, borderRadius: '50%', display: 'inline-block', marginRight: 6,
  },
};

export default function ChatRoom() {
  const { roomId } = useParams();
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [connected, setConnected] = useState(false);
  const stompRef = useRef(null);
  const bottomRef = useRef(null);
  const myId = getGuestId();

  // Load history
  useEffect(() => {
    fetch(`/api/rooms/${roomId}/messages?limit=50`)
      .then(r => r.json())
      .then(setMessages)
      .catch(() => {});
  }, [roomId]);

  // WebSocket
  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS('/ws'),
      reconnectDelay: 3000,
      onConnect: () => {
        setConnected(true);
        client.subscribe(`/topic/rooms/${roomId}`, frame => {
          const msg = JSON.parse(frame.body);
          setMessages(prev => [...prev, msg]);
        });
      },
      onDisconnect: () => setConnected(false),
    });
    client.activate();
    stompRef.current = client;
    return () => client.deactivate();
  }, [roomId]);

  // Scroll to bottom
  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  function sendMessage() {
    const text = input.trim();
    if (!text || !stompRef.current?.connected) return;
    stompRef.current.publish({
      destination: `/app/rooms/${roomId}/send`,
      body: JSON.stringify({
        senderId: myId,
        senderName: getGuestName(),
        content: text,
      }),
    });
    setInput('');
  }

  function handleKeyDown(e) {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      sendMessage();
    }
  }

  return (
    <div style={s.page}>
      <div style={s.topBar}>
        <Link to="/" style={s.backLink}>← Rooms</Link>
        <span style={s.roomTitle}>#{roomId}</span>
        <span style={{ marginLeft: 'auto', fontSize: '0.75rem', color: connected ? '#4ade80' : '#f87171' }}>
          <span style={{ ...s.statusDot, background: connected ? '#4ade80' : '#f87171' }} />
          {connected ? 'Live' : 'Connecting…'}
        </span>
      </div>

      <div style={s.messages}>
        {messages.map(msg => {
          const mine = msg.senderId === myId;
          return (
            <div key={msg.id} style={{ display: 'flex', flexDirection: 'column', alignItems: mine ? 'flex-end' : 'flex-start' }}>
              <div style={s.senderLabel}>{msg.senderName}</div>
              <div style={{ ...s.msgBubble, ...(mine ? s.msgMine : s.msgOther) }}>
                {msg.content}
              </div>
            </div>
          );
        })}
        <div ref={bottomRef} />
      </div>

      <div style={s.inputRow}>
        <input
          style={s.textInput}
          value={input}
          onChange={e => setInput(e.target.value)}
          onKeyDown={handleKeyDown}
          placeholder="Type a message…"
        />
        <button style={s.sendBtn} onClick={sendMessage}>Send</button>
      </div>
    </div>
  );
}
