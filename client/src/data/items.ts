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

export const ITEMS: { [key: string]: Item } = {
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
    buildFrom: ['KNIFE'],
    isBuyable: true
  },
  KORAS_AMULET: {
    name: "Kora's Amulet",
    cost: 525,
    tree: 'ADVANCED',
    tip: 'More skill damage',
    stats: { SKILL_POWER: 20 },
    buildFrom: ['RING'],
    isBuyable: true
  },
  HOLY_BAND: {
    name: 'Holy Band',
    cost: 550,
    tree: 'ADVANCED',
    tip: 'Shield when defending',
    stats: { SKILL_POWER: 25 },
    buildFrom: ['RING'],
    isBuyable: true
  },
  BLOODLUST_BLADE: {
    name: 'Bloodlust Blade',
    cost: 550,
    tree: 'ADVANCED',
    stats: { ATTACK_POWER: 20, LIFE_STEAL: 0.07 },
    buildFrom: ['KNIFE'],
    isBuyable: true
  },
  BRONZE_CUTLASS: {
    name: 'Bronze Cutlass',
    cost: 600,
    tree: 'ADVANCED',
    stats: { ATTACK_POWER: 25, CRIT_CHANCE: 0.2 },
    buildFrom: ['KNIFE'],
    isBuyable: true
  },
  MIDNIGHT_DAGGER: {
    name: 'Midnight Dagger',
    cost: 600,
    tree: 'ADVANCED',
    tip: 'More multi-attack damage',
    stats: { ATTACK_POWER: 15 },
    buildFrom: ['KNIFE'],
    isBuyable: true
  },

  // Complete Items
  FAITHBREAKER: {
    name: 'Faithbreaker',
    cost: 1150,
    tree: 'COMPLETE',
    tip: 'More skill damage',
    stats: { SKILL_POWER: 45 },
    buildFrom: ['KORAS_AMULET', 'RING'],
    isBuyable: true
  },
  CRIMSON_MIGHT: {
    name: 'Crimson Might',
    cost: 1175,
    tree: 'COMPLETE',
    tip: 'Lower cooldowns',
    stats: { SKILL_POWER: 40 },
    buildFrom: ['KORAS_AMULET', 'RING'],
    isBuyable: true
  },
  DAWN_HAMMER: {
    name: 'Dawn Hammer',
    cost: 1200,
    tree: 'COMPLETE',
    tip: 'Bonus energy',
    stats: { ATTACK_POWER: 25, SKILL_POWER: 30 },
    buildFrom: ['HOLY_BAND', 'KNIFE'],
    isBuyable: true
  },
  WOLFS_FANG: {
    name: "Wolf's Fang",
    cost: 1175,
    tree: 'COMPLETE',
    tip: 'Attacks Weaken enemy',
    stats: { ATTACK_POWER: 35 },
    buildFrom: ['BONE_SPEAR', 'KNIFE'],
    isBuyable: true
  },
  SOULSTEALER: {
    name: 'Soulstealer',
    cost: 1200,
    tree: 'COMPLETE',
    tip: 'Attacks shield',
    stats: { ATTACK_POWER: 55, LIFE_STEAL: 0.2 },
    buildFrom: ['BLOODLUST_BLADE', 'KNIFE'],
    isBuyable: true
  },
  IRON_SCIMITAR: {
    name: 'Iron Scimitar',
    cost: 1200,
    tree: 'COMPLETE',
    tip: 'Crits Cripple enemy',
    stats: { ATTACK_POWER: 50, CRIT_CHANCE: 0.4 },
    buildFrom: ['BRONZE_CUTLASS', 'KNIFE'],
    isBuyable: true
  },
  SHADOW_REAVER: {
    name: 'Shadow Reaver',
    cost: 1225,
    tree: 'COMPLETE',
    tip: 'More multi-attack damage',
    stats: { ATTACK_POWER: 30, SKILL_POWER: 30 },
    buildFrom: ['MIDNIGHT_DAGGER', 'RING'],
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
  },
  ALCHEMISTS_ELIXIR: {
    name: "Alchemist's Elixir",
    description: 'Grants a random effect',
    cost: 50,
    tree: 'BASIC',
    isBuyable: false,
    canUse: true,
    removeOnUse: true
  }
};

export const getItem = (name: string): Item | undefined => {
  const upperName = name.toUpperCase().replace(/[^A-Z]/g, '_');
  return ITEMS[upperName] || Object.values(ITEMS).find(item => 
    item.name.toLowerCase() === name.toLowerCase() ||
    (name.length >= 3 && item.name.toLowerCase().startsWith(name.toLowerCase()))
  );
};

export const getItemsByTree = (tree: 'BASIC' | 'ADVANCED' | 'COMPLETE'): Item[] => {
  return Object.values(ITEMS).filter(item => item.tree === tree && item.isBuyable);
};

export const calculateBuildCost = (item: Item, inventory: Item[]): { cost: number; missingComponents: string[] } => {
  if (!item.buildFrom || item.buildFrom.length === 0) {
    return { cost: item.cost, missingComponents: [] };
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

  return { cost: item.cost - reduction, missingComponents };
}; 