import { Unit, Stats } from '../types/game';

const UNITS: { [key: string]: Unit } = {
  ASSASSIN: {
    name: 'Assassin',
    description: 'A stealthy fighter that excels at dealing burst damage and executing low health targets.',
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
    stats: {
      MAX_ENERGY: 125,
      MAX_HEALTH: 1090,
      ATTACK_POWER: 19,
      HEALTH_PER_TURN: 12,
      GOLD_PER_TURN: 0
    }
  }
};

export const getUnit = (name: string): Unit | undefined => {
  const upperName = name.toUpperCase();
  return UNITS[upperName] || Object.values(UNITS).find(unit => 
    unit.name.toLowerCase() === name.toLowerCase() ||
    (name.length >= 3 && unit.name.toLowerCase().startsWith(name.toLowerCase()))
  );
};

export const getAllUnits = (): Unit[] => Object.values(UNITS);

export default UNITS; 