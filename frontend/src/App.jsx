import React from 'react';
import { HashRouter, Routes, Route } from 'react-router-dom';
import Header from './components/Header';
import RoomList from './pages/RoomList';
import ChatRoom from './pages/ChatRoom';

const styles = {
  app: { display: 'flex', flexDirection: 'column', height: '100vh' },
  main: { flex: 1, overflow: 'hidden' },
};

export default function App() {
  return (
    <HashRouter>
      <div style={styles.app}>
        <Header />
        <main style={styles.main}>
          <Routes>
            <Route path="/" element={<RoomList />} />
            <Route path="/rooms/:roomId" element={<ChatRoom />} />
          </Routes>
        </main>
      </div>
    </HashRouter>
  );
}
