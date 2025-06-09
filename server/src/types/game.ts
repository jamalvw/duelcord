export interface Stats {
  MAX_ENERGY: number;
  MAX_HEALTH: number;
  ATTACK_POWER: number;
  HEALTH_PER_TURN: number;
  GOLD_PER_TURN?: number;
}

export interface ItemStats {
  MAX_ENERGY?: number;
  MAX_HEALTH?: number;
  ATTACK_POWER?: number;
  SKILL_POWER?: number;
  CRIT_CHANCE?: number;
  CRIT_DAMAGE?: number;
  LIFE_STEAL?: number;
  RESIST?: number;
  DODGE?: number;
  COOLDOWN_REDUCTION?: number;
  GOLD_PER_TURN?: number;
  HEALTH_PER_TURN?: number;
}

export interface Item {
  name: string;
  description?: string;
  tip?: string;
  cost: number;
  tree: 'BASIC' | 'ADVANCED' | 'COMPLETE';
  stats?: ItemStats;
  buildFrom?: string[];
  isBuyable: boolean;
  canUse?: boolean;
  removeOnUse?: boolean;
}

export interface Unit {
  name: string;
  stats: Stats;
  description: string;
}

export interface Player {
  id: string;
  username: string;
  unit?: Unit;
  health: number;
  maxHealth: number;
  energy: number;
  maxEnergy: number;
  gold: number;
  items: Item[];
  isReady: boolean;
  isAlive: boolean;
  // Calculated stats (base + items)
  totalAttackPower?: number;
  totalHealthPerTurn?: number;
  totalGoldPerTurn?: number;
}

export interface Game {
  id: string;
  players: Player[];
  currentTurn: number;
  turnCount: number;
  status: 'waiting' | 'picking' | 'playing' | 'finished';
  winner?: Player;
  actions: GameAction[];
}

export interface GameAction {
  type: string;
  player: Player;
  target?: Player;
  damage?: number;
  healing?: number;
  energyCost: number;
}

export interface DamageEvent {
  attacker: Player;
  target: Player;
  baseDamage: number;
  bonusDamage: number;
  isCrit?: boolean;
} 