import React, { useState, useEffect, useRef } from 'react';
import styled from 'styled-components';
import io from 'socket.io-client';
import GameStatus from './GameStatus';
import ActionMenu from './ActionMenu';
import ItemsModal from './ItemsModal';

// Emojis matching the original Java codebase
const EMOTES = {
  YES: '✅ ',
  NO: '❌ ',
  DISTORTION: '💫 ',
  ATTACK: '⚔️ ',
  DEFEND: '🛡️ ',
  SKILL: '🌸 ',
  GOLD: '💰 ',
  DEFEAT: '☠️ ',
  VICTORY: '🏆 ',
  WARN: '⚠️ ',
  ENERGY: '🔋 ',
  BLEED: '🩸 ',
  CRIPPLE: '💔 ',
  POTION: '🧪 ',
  SILENCE: '💬 ',
  WEAKEN: '📉 ',
  WOUND: '💕 ',
  GUN: '🔫 ',
  KNIFE: '🔪 ',
  BUFF: '✨ ',
  DODGE: '💨 ',
  SHIELD: '✨ ',
  NINJA: '🥷 ',
  REFRESH: '♻️ ',
  TIME: '⏳ ',
  TRACE: '〽️ ',
  HEAL: '💚 ',
};

// Convert Discord markdown to HTML
const formatMessage = (content: string): string => {
  // Replace **text** with <strong>text</strong>
  let formatted = content.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
  
  // Replace __text__ with <em>text</em> (underline in Discord)
  formatted = formatted.replace(/__(.*?)__/g, '<em>$1</em>');
  
  // Add emojis at the start of messages based on content
  if (/attacked.*for.*damage/i.test(formatted)) {
    formatted = EMOTES.ATTACK + formatted;
  } else if (/Game Over!/i.test(formatted)) {
    formatted = EMOTES.DEFEAT + formatted;
  } else if (/wins!/i.test(formatted)) {
    formatted = EMOTES.VICTORY + formatted;
  } else if (/turn ended/i.test(formatted)) {
    formatted = EMOTES.TIME + formatted;
  } else if (/turn!/i.test(formatted)) {
    formatted = EMOTES.TIME + formatted;
  } else if (/used.*Slash/i.test(formatted)) {
    formatted = EMOTES.SKILL + formatted;
  } else if (/used.*Execute/i.test(formatted)) {
    formatted = EMOTES.SKILL + formatted;
  } else if (/used.*Barrage/i.test(formatted)) {
    formatted = EMOTES.SKILL + formatted;
  } else if (/healed/i.test(formatted)) {
    formatted = EMOTES.HEAL + formatted;
  } else if (/shielded/i.test(formatted)) {
    formatted = EMOTES.SHIELD + formatted;
  } else if (/purchased.*for.*gold/i.test(formatted)) {
    formatted = EMOTES.GOLD + formatted;
  } else if (/forfeited/i.test(formatted)) {
    formatted = EMOTES.DEFEAT + formatted;
  } else if (/joined the game/i.test(formatted)) {
    formatted = EMOTES.YES + formatted;
  } else if (/picked/i.test(formatted)) {
    formatted = EMOTES.YES + formatted;
  } else if (/Game started/i.test(formatted)) {
    formatted = EMOTES.VICTORY + formatted;
  }
  
  // Replace CRIT with explosion emoji
  formatted = formatted.replace(/CRIT/gi, `💥CRIT`);
  
  return formatted;
};

interface Message {
  type: 'chat' | 'system' | 'error';
  username?: string;
  content: string;
}

interface Player {
  id: string;
  username: string;
  health: number;
  maxHealth: number;
  energy: number;
  maxEnergy: number;
  gold: number;
  unit?: {
    name: string;
  };
  isAlive: boolean;
}

interface GameState {
  players: Player[];
  currentTurn: number;
  status: 'waiting' | 'picking' | 'playing' | 'finished';
}

const Container = styled.div`
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: ${props => props.theme.colors.background};
  color: ${props => props.theme.colors.text};
`;

const MessagesContainer = styled.div`
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
`;

const MessageContent = styled.div<{ type: string }>`
  padding: 8px 12px;
  border-radius: 4px;
  background-color: ${(props) => {
    switch (props.type) {
      case 'system': return props.theme.colors.backgroundSecondary + '20';
      case 'error': return props.theme.colors.error + '20';
      default: return 'transparent';
    }
  }};
  color: ${(props) => props.type === 'error' ? props.theme.colors.error : 'inherit'};
  
  strong {
    font-weight: bold;
    color: ${props => props.theme.colors.primary};
  }
  
  em {
    font-style: italic;
    text-decoration: underline;
    color: ${props => props.theme.colors.textSecondary};
  }
`;

const Username = styled.span`
  color: ${props => props.theme.colors.primary};
  font-weight: bold;
`;

const Input = styled.input`
  padding: 12px;
  margin: 16px;
  background-color: ${props => props.theme.colors.backgroundTertiary};
  border: none;
  border-radius: 4px;
  color: ${props => props.theme.colors.text};
  font-size: 1em;

  &:focus {
    outline: none;
    box-shadow: 0 0 0 2px ${props => props.theme.colors.primary};
  }
`;

interface ChatProps {
  socket: ReturnType<typeof io>;
  username: string;
}

const Chat: React.FC<ChatProps> = ({ socket, username }) => {
  const [messages, setMessages] = useState<Message[]>([]);
  const [input, setInput] = useState('');
  const [gameState, setGameState] = useState<GameState>({
    players: [],
    currentTurn: 0,
    status: 'waiting'
  });
  const [isItemsModalOpen, setIsItemsModalOpen] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    socket.on('message', (message: Message) => {
      setMessages(prev => [...prev, message]);
    });

    socket.on('gameState', (state: GameState) => {
      setGameState(state);
    });

    return () => {
      socket.off('message');
      socket.off('gameState');
    };
  }, [socket]);

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (input.trim()) {
      socket.emit('message', input);
      setInput('');
    }
  };

  return (
    <Container>
      <GameStatus 
        players={gameState.players}
        currentTurn={gameState.currentTurn}
        status={gameState.status}
      />
      <MessagesContainer>
        {messages.map((message, index) => (
          <MessageContent key={index} type={message.type}>
            {message.type === 'chat' && (
              <Username>{message.username}: </Username>
            )}
            <span dangerouslySetInnerHTML={{ __html: formatMessage(message.content) }} />
          </MessageContent>
        ))}
        <div ref={messagesEndRef} />
      </MessagesContainer>
      <ActionMenu 
        socket={socket}
        username={username}
        gameState={gameState}
        onOpenItems={() => setIsItemsModalOpen(true)}
      />
      <form onSubmit={handleSubmit}>
        <Input
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder="Send a message..."
        />
      </form>
      <ItemsModal
        socket={socket}
        username={username}
        gameState={gameState}
        isOpen={isItemsModalOpen}
        onClose={() => setIsItemsModalOpen(false)}
      />
    </Container>
  );
};

export default Chat; 