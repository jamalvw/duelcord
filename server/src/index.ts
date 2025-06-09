import express from 'express';
import { createServer } from 'http';
import { Server } from 'socket.io';
import cors from 'cors';
import { GameManager, GameStatus } from './game/GameManager';
import { getAllUnits } from './game/units';
import { Game, Player } from './types/game';

const app = express();
const httpServer = createServer(app);
const io = new Server(httpServer, {
  cors: {
    origin: ["http://localhost:3000", "https://enigma-82d54.web.app"],
    methods: ["GET", "POST"]
  }
});

app.use(cors());
app.use(express.json());

const gameManager = new GameManager();
const users = new Map<string, Player>();

// Command handler setup
const commands = new Map<string, (args: string[], socket: any) => void>();

// Register commands
commands.set('units', (args, socket) => {
  const units = getAllUnits();
  io.to(socket.id).emit('message', {
    type: 'system',
    content: `Available units: ${units.map(u => u.name).join(', ')}`
  });
});

commands.set('pick', (args, socket) => {
  const player = users.get(socket.id);
  if (!player) return;

  const unitName = args.join(' ');
  const success = gameManager.pickUnit(player.id, unitName);

  if (success) {
    const game = gameManager.getPlayerGame(player.id);
    if (game) {
      // Notify all players in the game
      game.players.forEach(p => {
        io.to(p.id).emit('message', {
          type: 'system',
          content: `**${player.username}** picked **${unitName}**`
        });
        
        if (game.status === 'playing') {
          io.to(p.id).emit('message', {
            type: 'system',
            content: `Game started! **${game.players[game.currentTurn].username}'s** turn`
          });
        }
      });
      sendGameState(game);
    }
  } else {
    io.to(socket.id).emit('message', {
      type: 'error',
      content: 'Invalid unit selection'
    });
  }
});

commands.set('attack', (args, socket) => {
  const player = users.get(socket.id);
  if (!player) return;

  const game = gameManager.getPlayerGame(player.id);
  if (!game || game.status !== 'playing') {
    io.to(socket.id).emit('message', {
      type: 'error',
      content: 'No active game or not in playing state'
    });
    return;
  }

  const target = game.players.find(p => p.id !== player.id);
  if (!target) return;

  const currentEnergy = game.players[game.currentTurn].energy;
  
  const result = gameManager.attack(player.id, target.id);
  
  if (result.success) {
    
    game.players.forEach(p => {
      io.to(p.id).emit('message', {
        type: 'system',
        content: `**${player.username}** attacked **${target.username}** for **${result.damage}** damage!`
      });

      // If energy was depleted, notify about automatic turn end
      if (currentEnergy >= 25 && game.players[game.currentTurn].id !== player.id) {
        io.to(p.id).emit('message', {
          type: 'system',
          content: `**${player.username}'s** turn ended (out of energy). **${game.players[game.currentTurn].username}'s** turn!`
        });
      }

      if (game.status === 'finished') {
        io.to(p.id).emit('message', {
          type: 'system',
          content: `Game Over! **${game.winner?.username}** wins!`
        });
      }
    });
    sendGameState(game);
  } else {
    io.to(socket.id).emit('message', {
      type: 'error',
      content: 'Not enough energy for this action'
    });
  }
});

commands.set('end', (args, socket) => {
  const player = users.get(socket.id);
  if (!player) return;

  const game = gameManager.getPlayerGame(player.id);
  if (!game || game.status !== 'playing') {
    io.to(socket.id).emit('message', {
      type: 'error',
      content: 'No active game or not in playing state'
    });
    return;
  }

  if (game.players[game.currentTurn].id !== player.id) {
    io.to(socket.id).emit('message', {
      type: 'error',
      content: 'It is not your turn'
    });
    return;
  }

  const playerBeforeEndTurn = game.players[game.currentTurn];
  const goldBefore = playerBeforeEndTurn.gold;
  
  gameManager.endTurn(game.id);
  
  const currentPlayer = game.players[game.currentTurn];
  const goldAfter = currentPlayer.gold;
  const goldGained = goldAfter - goldBefore;
  const baseGold = 125 + game.turnCount - 1; // turnCount was incremented
  const unitGold = currentPlayer.unit?.stats.GOLD_PER_TURN || 0;
  
  game.players.forEach(p => {
    let turnMessage = `**${currentPlayer.username}'s** turn! `;
    if (goldGained > 0) {
      turnMessage += `Gained **${goldGained}** gold (**${baseGold}** base`;
      if (unitGold > 0) {
        turnMessage += ` + **${unitGold}** from unit`;
      }
      turnMessage += `)`;
    }
    
    io.to(p.id).emit('message', {
      type: 'system',
      content: turnMessage
    });
  });
  sendGameState(game);
});

commands.set('ff', (args, socket) => {
  const player = users.get(socket.id);
  if (!player) return;

  const game = gameManager.getPlayerGame(player.id);
  if (!game || game.status !== 'playing') {
    io.to(socket.id).emit('message', {
      type: 'error',
      content: 'No active game or not in playing state'
    });
    return;
  }

  if (gameManager.forfeit(player.id)) {
    game.players.forEach(p => {
      io.to(p.id).emit('message', {
        type: 'system',
        content: `**${player.username}** has forfeited the game!`
      });
      if (game.status === 'finished') {
        io.to(p.id).emit('message', {
          type: 'system',
          content: `Game Over! **${game.winner?.username}** wins!`
        });
      }
    });
    sendGameState(game);
  }
});

commands.set('buy', (args, socket) => {
  const player = users.get(socket.id);
  if (!player) return;

  const itemName = args.join(' ');
  const result = gameManager.buyItem(player.id, itemName);

  if (result.success) {
    const game = gameManager.getPlayerGame(player.id);
    if (game) {
      game.players.forEach(p => {
        io.to(p.id).emit('message', {
          type: 'system',
          content: `**${player.username}** purchased **${itemName}** for **${result.cost}** gold!`
        });
      });
      sendGameState(game);
    }
  } else {
    io.to(socket.id).emit('message', {
      type: 'error',
      content: result.message || 'Failed to buy item'
    });
  }
});

// Helper function to send game state to all players
function sendGameState(game: Game) {
  game.players.forEach(p => {
    io.to(p.id).emit('gameState', {
      players: game.players,
      currentTurn: game.currentTurn,
      status: game.status
    });
  });
}

io.on('connection', (socket) => {
  console.log('User connected:', socket.id);

  socket.on('join', (username: string) => {
    const player: Player = {
      id: socket.id,
      username,
      health: 0,
      maxHealth: 0,
      energy: 0,
      maxEnergy: 0,
      gold: 0,
      items: [],
      isReady: false,
      isAlive: true
    };
    users.set(socket.id, player);

    // Create a new game or join an existing one
    const existingGame = gameManager.findWaitingGame();

    let game: Game | null;
    if (existingGame) {
      game = gameManager.joinGame(player, existingGame.id);
      if (game) {
        game.players.forEach(p => {
          io.to(p.id).emit('message', {
            type: 'system',
            content: `**${username}** joined the game. Pick your units with **>pick [unit name]**`
          });
        });
        sendGameState(game);
      }
    } else {
      game = gameManager.createGame(player);
      io.to(socket.id).emit('message', {
        type: 'system',
        content: 'Game created. Waiting for another player...'
      });
      sendGameState(game);
    }
  });

  socket.on('message', (message: string) => {
    const player = users.get(socket.id);
    if (!player) return;

    if (message.startsWith('>')) {
      const [command, ...args] = message.slice(1).split(' ');
      const handler = commands.get(command.toLowerCase());
      
      if (handler) {
        handler(args, socket);
      } else {
        io.to(socket.id).emit('message', {
          type: 'error',
          content: 'Unknown command'
        });
      }
    } else {
      const game = gameManager.getPlayerGame(player.id);
      if (game) {
        // Only broadcast to players in the same game
        game.players.forEach(p => {
          io.to(p.id).emit('message', {
            type: 'chat',
            username: player.username,
            content: message
          });
        });
      }
    }
  });

  socket.on('disconnect', () => {
    console.log('User disconnected:', socket.id);
    const player = users.get(socket.id);
    if (player) {
      const game = gameManager.getPlayerGame(player.id);
      if (game && game.status === 'playing') {
        gameManager.forfeit(player.id);
        game.players.forEach(p => {
          if (p.id !== player.id) {
            io.to(p.id).emit('message', {
              type: 'system',
              content: `${player.username} has disconnected and forfeited the game!`
            });
          }
        });
        sendGameState(game);
      }
    }
    users.delete(socket.id);
  });
});

const PORT = process.env.PORT || 3001;
httpServer.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
}); 