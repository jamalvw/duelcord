import React from 'react';
import styled from 'styled-components';

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

interface GameStatusProps {
  players: Player[];
  currentTurn: number;
  status: 'waiting' | 'picking' | 'playing' | 'finished';
}

const Container = styled.div`
  background-color: ${props => props.theme.colors.backgroundSecondary};
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  color: ${props => props.theme.colors.text};
`;

const Header = styled.div`
  font-size: 1.2em;
  font-weight: bold;
  margin-bottom: 12px;
  color: #ffffff;
  border-bottom: 1px solid ${props => props.theme.colors.backgroundTertiary};
  padding-bottom: 8px;
`;

const PlayerInfo = styled.div<{ isCurrentTurn: boolean }>`
  display: flex;
  align-items: center;
  padding: 8px;
  margin: 4px 0;
  background-color: ${props => props.isCurrentTurn ? props.theme.colors.backgroundTertiary : 'transparent'};
  border-radius: 4px;
  ${props => props.isCurrentTurn && `border-left: 4px solid ${props.theme.colors.primary}`};
`;

const StatBar = styled.div`
  flex: 1;
  display: flex;
  align-items: center;
  gap: 16px;
  margin-left: 12px;
`;

const Stat = styled.div`
  display: flex;
  align-items: center;
  gap: 4px;
`;

const HealthBar = styled.div<{ percentage: number }>`
  width: 100px;
  height: 8px;
  background-color: ${props => props.theme.colors.backgroundTertiary};
  border-radius: 4px;
  overflow: hidden;
  
  &::after {
    content: '';
    display: block;
    width: ${props => props.percentage}%;
    height: 100%;
    background-color: ${props => props.theme.colors.error};
    transition: width 0.3s ease;
  }
`;

const EnergyBar = styled(HealthBar)`
  &::after {
    background-color: ${props => props.theme.colors.primary};
  }
`;

const UnitBadge = styled.span`
  background-color: ${props => props.theme.colors.backgroundTertiary};
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 0.9em;
`;

const GameStatus: React.FC<GameStatusProps> = ({ players, currentTurn, status }) => {
  if (status === 'waiting') {
    return (
      <Container>
        <Header>Waiting for players...</Header>
      </Container>
    );
  }

  if (status === 'picking') {
    return (
      <Container>
        <Header>Pick your units!</Header>
        {players.map((player, index) => (
          <PlayerInfo key={player.id} isCurrentTurn={false}>
            {player.username} {player.unit && `- ${player.unit.name}`}
          </PlayerInfo>
        ))}
      </Container>
    );
  }

  return (
    <Container>
      <Header>
        {status === 'finished' ? 'Game Over!' : 'Battle in Progress'}
      </Header>
      {players.map((player, index) => (
        <PlayerInfo key={player.id} isCurrentTurn={currentTurn === index && status === 'playing'}>
          <div style={{ minWidth: '120px' }}>
            {player.username}
            {player.unit && (
              <UnitBadge>{player.unit.name}</UnitBadge>
            )}
          </div>
          <StatBar>
            <Stat>
              HP: {player.health}/{player.maxHealth}
              <HealthBar percentage={(player.health / player.maxHealth) * 100} />
            </Stat>
            <Stat>
              EP: {player.energy}/{player.maxEnergy}
              <EnergyBar percentage={(player.energy / player.maxEnergy) * 100} />
            </Stat>
            <Stat>🪙 {player.gold}</Stat>
          </StatBar>
        </PlayerInfo>
      ))}
    </Container>
  );
};

export default GameStatus; 