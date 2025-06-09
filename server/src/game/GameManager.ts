import { Game, Player, Unit, GameAction, DamageEvent, Item } from '../types/game';
import { getUnit } from './units';
import { getItem, calculateBuildCost } from './items';
import { v4 as uuidv4 } from 'uuid';

export type GameStatus = 'waiting' | 'picking' | 'playing' | 'finished';

export class GameManager {
  private games: Map<string, Game>;
  private playerGames: Map<string, string>;

  constructor() {
    this.games = new Map();
    this.playerGames = new Map();
  }

  createGame(player: Player): Game {
    const gameId = uuidv4();
    const game: Game = {
      id: gameId,
      players: [player],
      currentTurn: 0,
      turnCount: 0,
      status: 'waiting',
      actions: []
    };
    this.games.set(gameId, game);
    this.playerGames.set(player.id, gameId);
    return game;
  }

  joinGame(player: Player, gameId: string): Game | null {
    const game = this.games.get(gameId);
    if (!game || game.status !== 'waiting' || game.players.length >= 2) return null;

    game.players.push(player);
    this.playerGames.set(player.id, gameId);

    if (game.players.length === 2) {
      game.status = 'picking';
      game.currentTurn = Math.floor(Math.random() * 2); // Random first picker
    }

    return game;
  }

  pickUnit(playerId: string, unitName: string): boolean {
    const gameId = this.playerGames.get(playerId);
    if (!gameId) return false;

    const game = this.games.get(gameId);
    if (!game || game.status !== 'picking') return false;

    const player = game.players.find(p => p.id === playerId);
    if (!player || player !== game.players[game.currentTurn]) return false;

    const unit = getUnit(unitName);
    if (!unit) return false;

    player.unit = unit;
    player.health = unit.stats.MAX_HEALTH;
    player.maxHealth = unit.stats.MAX_HEALTH;
    player.energy = unit.stats.MAX_ENERGY;
    player.maxEnergy = unit.stats.MAX_ENERGY;
    player.gold = 0;
    player.items = []; // Initialize with empty inventory
    player.isReady = true;
    player.isAlive = true;

    // Update stats to reflect any initial bonuses
    this.updatePlayerStats(player);

    // Move to next player's turn
    game.currentTurn = (game.currentTurn + 1) % game.players.length;

    // If both players have picked, start the game
    if (game.players.every(p => p.isReady)) {
      game.status = 'playing';
      game.currentTurn = Math.floor(Math.random() * 2); // Random first turn
      this.startTurn(game.id);
    }

    return true;
  }

  startTurn(gameId: string): void {
    const game = this.games.get(gameId);
    if (!game || game.status !== 'playing') return;

    const currentPlayer = game.players[game.currentTurn];
    if (!currentPlayer.unit) return;

    // Update player stats (in case items were bought)
    this.updatePlayerStats(currentPlayer);

    // Reset energy to max
    currentPlayer.energy = currentPlayer.maxEnergy;

    // Apply healing (base + items)
    const healing = currentPlayer.totalHealthPerTurn || currentPlayer.unit.stats.HEALTH_PER_TURN;
    currentPlayer.health = Math.min(
      currentPlayer.health + healing,
      currentPlayer.maxHealth
    );

    // Give base gold: 125 + turnCount
    currentPlayer.gold += 125 + game.turnCount;

    // Give unit/item bonus gold per turn
    if (currentPlayer.totalGoldPerTurn) {
      currentPlayer.gold += currentPlayer.totalGoldPerTurn;
    }

    game.turnCount++;
  }

  endTurn(gameId: string): void {
    const game = this.games.get(gameId);
    if (!game || game.status !== 'playing') return;

    game.currentTurn = (game.currentTurn + 1) % 2;
    this.startTurn(gameId);
  }

  canPerformAction(playerId: string, energyCost: number): boolean {
    const game = this.getPlayerGame(playerId);
    if (!game || game.status !== 'playing') return false;

    const player = game.players.find(p => p.id === playerId);
    if (!player || !player.isAlive || game.players[game.currentTurn].id !== playerId) {
      return false;
    }

    return player.energy >= energyCost;
  }

  performAction(action: GameAction): boolean {
    const game = this.getPlayerGame(action.player.id);
    if (!game || game.status !== 'playing') return false;

    const player = game.players.find(p => p.id === action.player.id);
    if (!player || !player.isAlive || game.players[game.currentTurn].id !== player.id) {
      return false;
    }

    if (player.energy < action.energyCost) {
      return false;
    }

    // Deduct energy cost
    player.energy -= action.energyCost;

    // Add action to history
    game.actions.push(action);

    // If energy is 0, automatically end turn
    if (player.energy <= 0) {
      this.endTurn(game.id);
    }

    return true;
  }

  attack(attackerId: string, targetId: string): { success: boolean; damage?: number } {
    const game = this.getPlayerGame(attackerId);
    if (!game || game.status !== 'playing') return { success: false };

    const attacker = game.players.find(p => p.id === attackerId);
    const target = game.players.find(p => p.id === targetId);

    if (!attacker || !target || !attacker.unit || !target.unit) {
      return { success: false };
    }

    if (!this.canPerformAction(attackerId, 25)) { // Basic attack costs 25 energy
      return { success: false };
    }

    const damage = attacker.totalAttackPower || attacker.unit.stats.ATTACK_POWER;
    target.health -= damage;

    // Give gold for attacking: 20-30 + (turnCount * 0.5)
    const attackGold = Math.floor(Math.random() * 11) + 20 + Math.floor(game.turnCount * 0.5);
    attacker.gold += attackGold;

    const action: GameAction = {
      type: 'attack',
      player: attacker,
      target: target,
      damage: damage,
      energyCost: 25
    };

    if (!this.performAction(action)) {
      return { success: false };
    }

    if (target.health <= 0) {
      target.health = 0;
      target.isAlive = false;
      
      // Check if game is over
      const alivePlayers = game.players.filter(p => p.isAlive);
      if (alivePlayers.length === 1) {
        game.status = 'finished';
        game.winner = alivePlayers[0];
      }
    }

    return { success: true, damage };
  }

  forfeit(playerId: string): boolean {
    const game = this.getPlayerGame(playerId);
    if (!game || game.status !== 'playing') return false;

    const player = game.players.find(p => p.id === playerId);
    if (!player) return false;

    player.isAlive = false;
    player.health = 0;

    const alivePlayers = game.players.filter(p => p.isAlive);
    if (alivePlayers.length === 1) {
      game.status = 'finished';
      game.winner = alivePlayers[0];
    }

    return true;
  }

  findWaitingGame(): Game | undefined {
    return Array.from(this.games.values()).find(g => g.status === 'waiting');
  }

  getGame(gameId: string): Game | undefined {
    return this.games.get(gameId);
  }

  getPlayerGame(playerId: string): Game | undefined {
    const gameId = this.playerGames.get(playerId);
    return gameId ? this.games.get(gameId) : undefined;
  }

  private updatePlayerStats(player: Player): void {
    if (!player.unit) return;
    
    // Start with base unit stats
    const baseStats = player.unit.stats;
    
    // Initialize calculated stats with base unit stats
    let totalMaxHealth = baseStats.MAX_HEALTH;
    let totalMaxEnergy = baseStats.MAX_ENERGY;
    let totalAttackPower = baseStats.ATTACK_POWER;
    let totalHealthPerTurn = baseStats.HEALTH_PER_TURN;
    let totalGoldPerTurn = baseStats.GOLD_PER_TURN || 0;
    
    // Add item stats
    for (const item of player.items) {
      if (item.stats) {
        totalMaxHealth += item.stats.MAX_HEALTH || 0;
        totalMaxEnergy += item.stats.MAX_ENERGY || 0;
        totalAttackPower += item.stats.ATTACK_POWER || 0;
        totalHealthPerTurn += item.stats.HEALTH_PER_TURN || 0;
        totalGoldPerTurn += item.stats.GOLD_PER_TURN || 0;
      }
    }
    
    // Update player's calculated stats
    const oldMaxHealth = player.maxHealth;
    const oldMaxEnergy = player.maxEnergy;
    
    player.maxHealth = totalMaxHealth;
    player.maxEnergy = totalMaxEnergy;
    player.totalAttackPower = totalAttackPower;
    player.totalHealthPerTurn = totalHealthPerTurn;
    player.totalGoldPerTurn = totalGoldPerTurn;
    
    // If max health increased, heal the player proportionally
    if (totalMaxHealth > oldMaxHealth) {
      const healthIncrease = totalMaxHealth - oldMaxHealth;
      player.health += healthIncrease;
    }
    
    // If max health decreased, cap current health
    if (player.health > player.maxHealth) {
      player.health = player.maxHealth;
    }
    
    // If max energy increased and this is their turn, give them the extra energy
    if (totalMaxEnergy > oldMaxEnergy) {
      const game = this.getPlayerGame(player.id);
      if (game && game.players[game.currentTurn].id === player.id) {
        const energyIncrease = totalMaxEnergy - oldMaxEnergy;
        player.energy += energyIncrease;
      }
    }
    
    // Cap current energy to max energy
    if (player.energy > player.maxEnergy) {
      player.energy = player.maxEnergy;
    }
  }

  buyItem(playerId: string, itemName: string): { success: boolean; cost?: number; message?: string } {
    const gameId = this.playerGames.get(playerId);
    if (!gameId) return { success: false, message: 'No active game' };

    const game = this.games.get(gameId);
    if (!game || game.status !== 'playing') return { success: false, message: 'Game not in playing state' };

    const player = game.players.find(p => p.id === playerId);
    if (!player) return { success: false, message: 'Player not found' };

    // Only current turn player can buy items
    if (game.players[game.currentTurn].id !== playerId) {
      return { success: false, message: 'Not your turn' };
    }

    // Check inventory limit (5 items maximum)
    if (player.items.length >= 5) {
      return { success: false, message: 'Inventory full (5 items maximum)' };
    }

    const item = getItem(itemName);
    if (!item || !item.isBuyable) {
      return { success: false, message: 'Item not found or not buyable' };
    }

    const { cost, missingComponents } = calculateBuildCost(item, player.items);
    
    if (player.gold < cost) {
      return { success: false, message: `Not enough gold (need ${cost}, have ${player.gold})` };
    }

    // Check if we have required components
    if (missingComponents.length > 0) {
      return { success: false, message: `Missing components: ${missingComponents.join(', ')}` };
    }

    // Remove gold and components
    player.gold -= cost;
    
    // Remove consumed components
    if (item.buildFrom && item.buildFrom.length > 0) {
      for (const componentName of item.buildFrom) {
        const componentIndex = player.items.findIndex(i => i.name === componentName);
        if (componentIndex !== -1) {
          player.items.splice(componentIndex, 1);
        }
      }
    }

    // Add the new item
    player.items.push(item);
    
    // Update player stats after buying item
    this.updatePlayerStats(player);

    return { success: true, cost };
  }
} 