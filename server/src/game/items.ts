import { Item } from '../types/game';

const ITEMS: { [key: string]: Item } = {
  // Basic Items
  RING: {
    name: 'Ring',
    cost: 200,
    tree: 'BASIC',
    stats: { SKILL_POWER: 10 },
    isBuyable: true
  },
  KNIFE: {
    name: 'Knife',
    cost: 250,
    tree: 'BASIC',
    stats: { ATTACK_POWER: 10 },
    isBuyable: true
  },

  // Advanced Items
  BONE_SPEAR: {
    name: 'Bone Spear',
    cost: 500,
    tree: 'ADVANCED',
    tip: 'Lower enemy healing',
    stats: { ATTACK_POWER: 10 },
    buildFrom: ['Knife'],
    isBuyable: true
  },
  KORAS_AMULET: {
    name: "Kora's Amulet",
    cost: 525,
    tree: 'ADVANCED',
    tip: 'More skill damage',
    stats: { SKILL_POWER: 20 },
    buildFrom: ['Ring'],
    isBuyable: true
  },
  HOLY_BAND: {
    name: 'Holy Band',
    cost: 550,
    tree: 'ADVANCED',
    tip: 'Shield when defending',
    stats: { SKILL_POWER: 25 },
    buildFrom: ['Ring'],
    isBuyable: true
  },
  BLOODLUST_BLADE: {
    name: 'Bloodlust Blade',
    cost: 550,
    tree: 'ADVANCED',
    stats: { ATTACK_POWER: 20, LIFE_STEAL: 0.07 },
    buildFrom: ['Knife'],
    isBuyable: true
  },
  BRONZE_CUTLASS: {
    name: 'Bronze Cutlass',
    cost: 600,
    tree: 'ADVANCED',
    stats: { ATTACK_POWER: 25, CRIT_CHANCE: 0.2 },
    buildFrom: ['Knife'],
    isBuyable: true
  },
  MIDNIGHT_DAGGER: {
    name: 'Midnight Dagger',
    cost: 600,
    tree: 'ADVANCED',
    tip: 'More multi-attack damage',
    stats: { ATTACK_POWER: 15 },
    buildFrom: ['Knife'],
    isBuyable: true
  },

  // Complete Items
  FAITHBREAKER: {
    name: 'Faithbreaker',
    cost: 1150,
    tree: 'COMPLETE',
    tip: 'More skill damage',
    stats: { SKILL_POWER: 45 },
    buildFrom: ["Kora's Amulet", 'Ring'],
    isBuyable: true
  },
  CRIMSON_MIGHT: {
    name: 'Crimson Might',
    cost: 1175,
    tree: 'COMPLETE',
    tip: 'Lower cooldowns',
    stats: { SKILL_POWER: 40 },
    buildFrom: ["Kora's Amulet", 'Ring'],
    isBuyable: true
  },
  DAWN_HAMMER: {
    name: 'Dawn Hammer',
    cost: 1200,
    tree: 'COMPLETE',
    tip: 'Bonus energy',
    stats: { ATTACK_POWER: 25, SKILL_POWER: 30 },
    buildFrom: ['Holy Band', 'Knife'],
    isBuyable: true
  },
  WOLFS_FANG: {
    name: "Wolf's Fang",
    cost: 1175,
    tree: 'COMPLETE',
    tip: 'Attacks Weaken enemy',
    stats: { ATTACK_POWER: 35 },
    buildFrom: ['Bone Spear', 'Knife'],
    isBuyable: true
  },
  SOULSTEALER: {
    name: 'Soulstealer',
    cost: 1200,
    tree: 'COMPLETE',
    tip: 'Attacks shield',
    stats: { ATTACK_POWER: 55, LIFE_STEAL: 0.2 },
    buildFrom: ['Bloodlust Blade', 'Knife'],
    isBuyable: true
  },
  IRON_SCIMITAR: {
    name: 'Iron Scimitar',
    cost: 1200,
    tree: 'COMPLETE',
    tip: 'Crits Cripple enemy',
    stats: { ATTACK_POWER: 50, CRIT_CHANCE: 0.4 },
    buildFrom: ['Bronze Cutlass', 'Knife'],
    isBuyable: true
  },
  SHADOW_REAVER: {
    name: 'Shadow Reaver',
    cost: 1225,
    tree: 'COMPLETE',
    tip: 'More multi-attack damage',
    stats: { ATTACK_POWER: 30, SKILL_POWER: 30 },
    buildFrom: ['Midnight Dagger', 'Ring'],
    isBuyable: true
  },

  // Consumables
  POTION: {
    name: 'Potion',
    description: 'Heal for 120 over 2 turns',
    cost: 50,
    tree: 'BASIC',
    isBuyable: false,
    canUse: true,
    removeOnUse: true
  }
};

export const getItem = (name: string): Item | undefined => {
  // Try exact match first
  const exactMatch = Object.values(ITEMS).find(item => 
    item.name.toLowerCase() === name.toLowerCase()
  );
  if (exactMatch) return exactMatch;

  // Try partial match (3+ characters)
  if (name.length >= 3) {
    return Object.values(ITEMS).find(item => 
      item.name.toLowerCase().startsWith(name.toLowerCase())
    );
  }

  return undefined;
};

export const calculateBuildCost = (item: Item, inventory: Item[]): { cost: number; reduction: number; missingComponents: string[] } => {
  if (!item.buildFrom || item.buildFrom.length === 0) {
    return { cost: item.cost, reduction: 0, missingComponents: [] };
  }

  let reduction = 0;
  const missingComponents: string[] = [];
  const inventoryNames = inventory.map(i => i.name);

  for (const componentName of item.buildFrom) {
    const component = getItem(componentName);
    if (!component) continue;

    if (inventoryNames.includes(component.name)) {
      reduction += component.cost;
    } else {
      missingComponents.push(component.name);
    }
  }

  return { cost: item.cost - reduction, reduction, missingComponents };
};

export const getAllItems = (): Item[] => Object.values(ITEMS); 