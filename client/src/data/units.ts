export interface UnitAbility {
  name: string;
  description: string;
  energyCost: number;
  command: string;
}

export interface Unit {
  name: string;
  description: string;
  abilities: UnitAbility[];
  stats: {
    MAX_ENERGY: number;
    MAX_HEALTH: number;
    ATTACK_POWER: number;
    HEALTH_PER_TURN: number;
    GOLD_PER_TURN: number;
  };
}

export const UNITS: { [key: string]: Unit } = {
  ASSASSIN: {
    name: 'Assassin',
    description: 'A stealthy fighter that excels at dealing burst damage and executing low health targets.',
    abilities: [
      {
        name: 'Execute',
        description: 'Deal massive damage to a low health target.',
        energyCost: 50,
        command: 'execute'
      },
      {
        name: 'Stealth',
        description: 'Become invisible and gain bonus damage on next attack.',
        energyCost: 35,
        command: 'stealth'
      }
    ],
    stats: {
      MAX_ENERGY: 100,
      MAX_HEALTH: 930,
      ATTACK_POWER: 14,
      HEALTH_PER_TURN: 15,
      GOLD_PER_TURN: 0
    }
  },
  GUNSLINGER: {
    name: 'Gunslinger',
    description: 'A ranged fighter that uses precise shots and tactical positioning to outmaneuver opponents.',
    abilities: [
      {
        name: 'Headshot',
        description: 'Fire a precise shot that deals bonus damage.',
        energyCost: 40,
        command: 'headshot'
      },
      {
        name: 'Tactical Roll',
        description: 'Dodge the next attack and gain bonus energy.',
        energyCost: 30,
        command: 'roll'
      }
    ],
    stats: {
      MAX_ENERGY: 125,
      MAX_HEALTH: 1090,
      ATTACK_POWER: 19,
      HEALTH_PER_TURN: 12,
      GOLD_PER_TURN: 0
    }
  }
}; 