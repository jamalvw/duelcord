import React, { useState, useEffect } from 'react';
import Chat from './components/Chat';
import './App.css';
import io from 'socket.io-client';

function App() {
  const [socket, setSocket] = useState<ReturnType<typeof io> | null>(null);
  const [username, setUsername] = useState<string>('');
  const [isJoined, setIsJoined] = useState(false);

  useEffect(() => {
    const newSocket = io('http://localhost:3001');
    setSocket(newSocket);
    return () => {
      newSocket.close();
    };
  }, []);

  const handleJoin = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (username.trim() && socket) {
      socket.emit('join', username);
      setIsJoined(true);
    }
  };

  if (!socket) return <div>Connecting...</div>;
  
  if (!isJoined) {
    return (
      <div className="join-container">
        <form onSubmit={handleJoin}>
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            placeholder="Enter your username"
            required
          />
          <button type="submit">Join Game</button>
        </form>
      </div>
    );
  }

  return (
    <div className="App">
      {socket && <Chat socket={socket} username={username} />}
    </div>
  );
}

export default App;
