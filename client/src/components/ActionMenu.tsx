import React, { useState } from 'react';
import styled from 'styled-components';
import io from 'socket.io-client';
import { UNITS, Unit, UnitAbility } from '../data/units';

interface ActionMenuProps {
  socket: ReturnType<typeof io>;
  username: string;
  gameState: {
    players: Player[];
    currentTurn: number;
    status: 'waiting' | 'picking' | 'playing' | 'finished';
  };
  onOpenItems: () => void;
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

const MenuContainer = styled.div`
  display: flex;
  gap: 8px;
  padding: 8px 16px;
  background-color: ${props => props.theme.colors.backgroundSecondary};
  border-top: 1px solid ${props => props.theme.colors.backgroundTertiary};
  justify-content: space-between;
  align-items: center;
`;

const LeftControls = styled.div`
  display: flex;
  gap: 8px;
  align-items: center;
`;

const RightControls = styled.div`
  display: flex;
  gap: 8px;
  align-items: center;
`;

const Button = styled.button<{ disabled?: boolean }>`
  padding: 8px 16px;
  background-color: ${props => props.disabled ? props.theme.colors.backgroundTertiary : props.theme.colors.primary};
  color: ${props => props.disabled ? props.theme.colors.textSecondary : '#ffffff'};
  border: none;
  border-radius: 4px;
  cursor: ${props => props.disabled ? 'not-allowed' : 'pointer'};
  opacity: ${props => props.disabled ? 0.5 : 1};
  transition: all 0.2s;

  &:hover:not(:disabled) {
    opacity: 0.9;
  }
`;

const AttackButton = styled(Button)`
  background: ${props => props.disabled ? 
    'linear-gradient(to bottom, #404040, #2a2a2a)' : 
    'linear-gradient(to bottom, #daa520, #b8860b)'};
  border-color: #daa520;
  color: ${props => props.disabled ? props.theme.colors.textSecondary : '#ffffff'};
  
  &:hover:not(:disabled) {
    background: linear-gradient(to bottom, #ffd700, #daa520);
    box-shadow: 0 6px 12px rgba(0, 0, 0, 0.4), inset 0 1px 0 rgba(255, 255, 255, 0.3), 0 0 12px rgba(218, 165, 32, 0.6);
  }
`;

const ForfeitButton = styled(Button)`
  background: ${props => props.disabled ? 
    'linear-gradient(to bottom, #404040, #2a2a2a)' : 
    'linear-gradient(to bottom, #dc143c, #a0102a)'};
  border-color: #dc143c;
  color: ${props => props.disabled ? props.theme.colors.textSecondary : '#ffffff'};
  
  &:hover:not(:disabled) {
    background: linear-gradient(to bottom, #ff1744, #dc143c);
    box-shadow: 0 6px 12px rgba(0, 0, 0, 0.4), inset 0 1px 0 rgba(255, 255, 255, 0.3), 0 0 12px rgba(220, 20, 60, 0.6);
  }
`;

const Select = styled.select`
  padding: 8px 16px;
  background-color: ${props => props.theme.colors.backgroundTertiary};
  color: ${props => props.theme.colors.text};
  border: none;
  border-radius: 4px;
  cursor: pointer;

  &:disabled {
    cursor: not-allowed;
    opacity: 0.5;
  }
`;

const AbilityButton = styled(Button)`
  background-color: ${props => props.disabled ? props.theme.colors.backgroundTertiary : props.theme.colors.primary};
  color: ${props => props.disabled ? props.theme.colors.textSecondary : '#ffffff'};
  position: relative;

  &:hover::after {
    content: attr(data-tooltip);
    position: absolute;
    bottom: 100%;
    left: 50%;
    transform: translateX(-50%);
    padding: 8px;
    background-color: ${props => props.theme.colors.backgroundSecondary};
    border-radius: 4px;
    font-size: 0.9em;
    white-space: nowrap;
    z-index: 1;
  }
`;

const ItemsButton = styled(Button)`
  background: ${props => props.disabled ? 
    'linear-gradient(to bottom, #404040, #2a2a2a)' : 
    'linear-gradient(to bottom, #9b59b6, #7d3c98)'} !important;
  border-color: #9b59b6 !important;
  color: ${props => props.disabled ? props.theme.colors.textSecondary : '#ffffff'} !important;
  
  &:hover:not(:disabled) {
    background: linear-gradient(to bottom, #b370cf, #9b59b6) !important;
    box-shadow: 0 6px 12px rgba(0, 0, 0, 0.4), inset 0 1px 0 rgba(255, 255, 255, 0.3), 0 0 12px rgba(155, 89, 182, 0.6) !important;
  }
`;

const ActionMenu: React.FC<ActionMenuProps> = ({ socket, username, gameState, onOpenItems }) => {
  const [selectedUnit, setSelectedUnit] = useState('');
  
  const currentPlayer = gameState.players.find(p => p.username === username);
  const isCurrentTurn = currentPlayer && gameState.players[gameState.currentTurn].username === username;
  const opponent = gameState.players.find(p => p.username !== username);

  const handleCommand = (command: string, ...args: string[]) => {
    socket.emit('message', `>${command} ${args.join(' ')}`);
  };

  const renderPickPhaseControls = () => {
    if (gameState.status !== 'picking') return null;

    return (
      <>
        <Select 
          value={selectedUnit}
          onChange={(e) => setSelectedUnit(e.target.value)}
          disabled={!isCurrentTurn}
        >
          <option value="">Select a unit...</option>
          {Object.values(UNITS).map(unit => (
            <option key={unit.name} value={unit.name}>
              {unit.name} - {unit.description}
            </option>
          ))}
        </Select>
        <Button 
          onClick={() => handleCommand('pick', selectedUnit)}
          disabled={!isCurrentTurn || !selectedUnit}
        >
          Pick Unit
        </Button>
      </>
    );
  };

  const renderUnitAbilities = () => {
    if (!currentPlayer?.unit || gameState.status !== 'playing') return null;

    const unit = Object.values(UNITS).find(u => u.name === currentPlayer.unit?.name);
    if (!unit) return null;

    return unit.abilities.map(ability => (
      <AbilityButton
        key={ability.name}
        onClick={() => handleCommand(ability.command)}
        disabled={!isCurrentTurn || currentPlayer.energy < ability.energyCost || !currentPlayer.isAlive || !opponent?.isAlive}
        data-tooltip={`${ability.description} (${ability.energyCost} energy)`}
      >
        {ability.name}
      </AbilityButton>
    ));
  };

  const renderPlayingPhaseControls = () => {
    if (gameState.status !== 'playing' || !currentPlayer || !opponent) return null;

    const hasEnoughEnergy = currentPlayer.energy >= 25; // Basic attack cost

    const leftControls = (
      <>
        <AttackButton 
          onClick={() => handleCommand('attack')}
          disabled={!isCurrentTurn || !hasEnoughEnergy || !currentPlayer.isAlive || !opponent.isAlive}
        >
          Attack
        </AttackButton>
        {renderUnitAbilities()}
      </>
    );

    const rightControls = (
      <>
        <Button 
          onClick={() => handleCommand('end')}
          disabled={!isCurrentTurn || !currentPlayer.isAlive}
        >
          End Turn
        </Button>
        <ForfeitButton 
          onClick={() => handleCommand('ff')}
          disabled={!currentPlayer.isAlive}
        >
          Forfeit
        </ForfeitButton>
      </>
    );

    return { leftControls, rightControls };
  };

  const renderItemsButton = () => {
    // Show Items button only during playing phase
    if (gameState.status !== 'playing' || gameState.players.length < 2) return null;
    
    return (
      <ItemsButton onClick={onOpenItems}>
        Items
      </ItemsButton>
    );
  };

  if (gameState.status === 'waiting') {
    return <MenuContainer><LeftControls>Waiting for players...</LeftControls></MenuContainer>;
  }

  if (gameState.status === 'finished') {
    return <MenuContainer><LeftControls>Game Over!</LeftControls></MenuContainer>;
  }

  const playingControls = renderPlayingPhaseControls();

  return (
    <MenuContainer>
      <LeftControls>
        {renderPickPhaseControls()}
        {playingControls?.leftControls}
        {renderItemsButton()}
      </LeftControls>
      <RightControls>
        {playingControls?.rightControls}
      </RightControls>
    </MenuContainer>
  );
};

export default ActionMenu; 