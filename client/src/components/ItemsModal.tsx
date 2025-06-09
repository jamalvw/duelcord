import React, { useState, useEffect, useRef } from 'react';
import styled from 'styled-components';
import io from 'socket.io-client';
import { Item, getItemsByTree, calculateBuildCost, ITEMS } from '../data/items';

interface ItemsModalProps {
  socket: ReturnType<typeof io>;
  username: string;
  gameState: {
    players: Player[];
    currentTurn: number;
    status: 'waiting' | 'picking' | 'playing' | 'finished';
  };
  isOpen: boolean;
  onClose: () => void;
}

interface Player {
  id: string;
  username: string;
  health: number;
  maxHealth: number;
  energy: number;
  maxEnergy: number;
  gold: number;
  items?: Item[];
  unit?: {
    name: string;
  };
  isAlive: boolean;
}

const Overlay = styled.div<{ isClosing: boolean }>`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: ${props => props.isClosing ? 'fadeOut' : 'fadeIn'} 0.15s ease;

  @keyframes fadeIn {
    from {
      opacity: 0;
    }
    to {
      opacity: 1;
    }
  }

  @keyframes fadeOut {
    from {
      opacity: 1;
    }
    to {
      opacity: 0;
    }
  }
`;

const Modal = styled.div<{ isClosing: boolean }>`
  background-color: ${props => props.theme.colors.background};
  border-radius: 8px;
  padding: 24px;
  min-width: 600px;
  max-width: 800px;
  max-height: 80vh;
  overflow-y: auto;
  position: relative;
  animation: ${props => props.isClosing ? 'modalSlideOut' : 'modalSlideIn'} 0.15s ease;

  @keyframes modalSlideIn {
    from {
      opacity: 0;
      transform: scale(0.8) translateY(-50px);
    }
    to {
      opacity: 1;
      transform: scale(1) translateY(0);
    }
  }

  @keyframes modalSlideOut {
    from {
      opacity: 1;
      transform: scale(1) translateY(0);
    }
    to {
      opacity: 0;
      transform: scale(0.8) translateY(-50px);
    }
  }
`;

const CloseButton = styled.button`
  position: absolute;
  top: 12px;
  right: 12px;
  background: none;
  border: none;
  color: ${props => props.theme.colors.text};
  font-size: 24px;
  cursor: pointer;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;

  &:hover {
    background-color: ${props => props.theme.colors.backgroundTertiary};
    border-radius: 4px;
  }
`;

const Section = styled.div`
  margin-bottom: 24px;
`;

const SectionTitle = styled.h3`
  color: ${props => props.theme.colors.text};
  margin: 0 0 12px 0;
  font-size: 1.2em;
`;

const InventoryGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 8px;
  margin-bottom: 16px;
`;

const ItemCard = styled.div<{ canAfford?: boolean; clickable?: boolean }>`
  background-color: ${props => props.theme.colors.backgroundSecondary};
  border: 1px solid ${props => props.theme.colors.backgroundTertiary};
  border-radius: 6px;
  padding: 12px;
  cursor: ${props => props.clickable ? 'pointer' : 'default'};
  opacity: ${props => props.canAfford === false ? 0.6 : 1};
  transition: all 0.2s;

  &:hover {
    ${props => props.clickable && `
      background-color: ${props.theme.colors.backgroundTertiary};
      border-color: ${props.theme.colors.primary};
    `}
  }
`;

const ItemName = styled.div`
  color: ${props => props.theme.colors.text};
  font-weight: bold;
  margin-bottom: 4px;
`;

const ItemCost = styled.div`
  color: #f1c40f;
  font-size: 0.9em;
  margin-bottom: 8px;
`;

const ItemStats = styled.div`
  color: ${props => props.theme.colors.textSecondary};
  font-size: 0.8em;
  margin-bottom: 4px;
`;

const ItemTip = styled.div`
  color: ${props => props.theme.colors.primary};
  font-style: italic;
  font-size: 0.8em;
`;

const BuildPath = styled.div`
  color: ${props => props.theme.colors.textSecondary};
  font-size: 0.8em;
  margin-bottom: 4px;
`;

const TreeSection = styled.div`
  margin-bottom: 20px;
`;

const TreeTitle = styled.h4<{ tree: string }>`
  color: ${props => {
    switch (props.tree) {
      case 'BASIC': return '#3498db';
      case 'ADVANCED': return '#e67e22';
      case 'COMPLETE': return '#9b59b6';
      default: return props.theme.colors.text;
    }
  }};
  margin: 0 0 8px 0;
  font-size: 1em;
`;

const ItemGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 8px;
`;

const BuyButton = styled.button<{ disabled?: boolean }>`
  background-color: ${props => props.disabled ? props.theme.colors.backgroundTertiary : '#27ae60'};
  color: ${props => props.theme.colors.text};
  border: none;
  border-radius: 4px;
  padding: 6px 12px;
  cursor: ${props => props.disabled ? 'not-allowed' : 'pointer'};
  opacity: ${props => props.disabled ? 0.5 : 1};
  margin-top: 8px;
  width: 100%;

  &:hover:not(:disabled) {
    background-color: #2ecc71;
  }
`;

const BuildPathContainer = styled.div`
  margin: 16px 0;
  padding: 16px;
  background-color: ${props => props.theme.colors.backgroundSecondary};
  border-radius: 8px;
  border: 2px solid ${props => props.theme.colors.primary};
`;

const BuildPathTitle = styled.h4`
  color: ${props => props.theme.colors.primary};
  margin: 0 0 12px 0;
  text-align: center;
`;

const BuildPathTree = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
`;

const BuildPathLevel = styled.div`
  display: flex;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
  justify-content: center;
`;

const BuildPathItem = styled.div<{ owned: boolean; target: boolean; clickable: boolean; canBuy: boolean; isShimmering?: boolean }>`
  background-color: ${props => props.target ? props.theme.colors.primary : props.theme.colors.backgroundSecondary};
  border: 2px solid ${props => props.owned ? '#27ae60' : props.theme.colors.backgroundTertiary};
  border-radius: 6px;
  padding: 12px;
  color: ${props => props.target ? '#ffffff' : props.theme.colors.text};
  font-size: 0.9em;
  font-weight: ${props => props.target ? 'bold' : 'normal'};
  opacity: ${props => (props.owned || (!props.owned && !props.canBuy)) ? 0.6 : 1};
  min-width: ${props => props.target ? '200px' : '120px'};
  text-align: center;
  cursor: ${props => props.clickable ? 'pointer' : 'default'};
  transition: all 0.2s;
  position: relative;
  overflow: hidden;

  &:hover {
    ${props => props.clickable && !props.owned && `
      background-color: ${props.theme.colors.backgroundTertiary};
      border-color: ${props.theme.colors.primary};
    `}
  }

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(
      90deg,
      transparent,
      rgba(255, 255, 255, 0.4),
      transparent
    );
    animation: ${props => props.isShimmering ? 'shimmer 0.6s ease-in-out' : 'none'};
  }

  @keyframes shimmer {
    0% {
      left: -100%;
    }
    100% {
      left: 100%;
    }
  }
`;

const BuildPathConnector = styled.div`
  width: 2px;
  height: 16px;
  background-color: ${props => props.theme.colors.primary};
  margin: 0 auto;
`;

const ClearBuildPathButton = styled.button`
  background: none;
  border: 1px solid ${props => props.theme.colors.primary};
  color: ${props => props.theme.colors.primary};
  border-radius: 4px;
  padding: 4px 8px;
  cursor: pointer;
  font-size: 0.8em;
  margin-left: 12px;

  &:hover {
    background-color: ${props => props.theme.colors.primary};
    color: ${props => props.theme.colors.background};
  }
`;

const ItemsModal: React.FC<ItemsModalProps> = ({ socket, username, gameState, isOpen, onClose }) => {
  const [recentlyPurchased, setRecentlyPurchased] = useState<Set<string>>(new Set());
  const [buildPathTarget, setBuildPathTarget] = useState<Item | null>(null);
  const [isVisible, setIsVisible] = useState(false);
  const [isClosing, setIsClosing] = useState(false);
  const [isShimmering, setIsShimmering] = useState(false);
  const buildPathRef = useRef<HTMLDivElement>(null);
  const modalRef = useRef<HTMLDivElement>(null);

  // Handle modal open/close animations
  useEffect(() => {
    if (isOpen) {
      setIsVisible(true);
      setIsClosing(false);
    } else if (isVisible) {
      setIsClosing(true);
      const timer = setTimeout(() => {
        setIsVisible(false);
        setIsClosing(false);
      }, 150);
      return () => clearTimeout(timer);
    }
  }, [isOpen, isVisible]);

  // Trigger shimmer effect when build path target changes
  useEffect(() => {
    if (buildPathTarget) {
      setIsShimmering(true);
      const timer = setTimeout(() => {
        setIsShimmering(false);
      }, 600); // Faster shimmer duration
      return () => clearTimeout(timer);
    }
  }, [buildPathTarget]);

  const handleClose = () => {
    onClose();
  };

  // Auto-scroll when build path target changes
  useEffect(() => {
    if (buildPathTarget && buildPathRef.current) {
      const timer = setTimeout(() => {
        if (buildPathRef.current) {
          buildPathRef.current.scrollIntoView({ 
            behavior: 'smooth', 
            block: 'start',
            inline: 'nearest'
          });
        }
      }, 100);
      
      return () => clearTimeout(timer);
    }
  }, [buildPathTarget]);

  if (!isVisible) return null;

  const currentPlayer = gameState.players.find(p => p.username === username);
  const isCurrentTurn = currentPlayer && gameState.players[gameState.currentTurn].username === username;
  
  const handleBuyItem = (item: Item) => {
    if (!isCurrentTurn || !currentPlayer) return;
    
    const { cost } = calculateBuildCost(item, currentPlayer.items || []);
    if (currentPlayer.gold < cost) return;
    
    // Add item to recently purchased set to disable button
    setRecentlyPurchased(prev => new Set(prev).add(item.name));
    
    // Re-enable button after 250ms
    setTimeout(() => {
      setRecentlyPurchased(prev => {
        const newSet = new Set(prev);
        newSet.delete(item.name);
        return newSet;
      });
    }, 250);
    
    socket.emit('message', `>buy ${item.name}`);
    // Don't close modal anymore - keep it open
  };

  const handleItemClick = (item: Item) => {
    if (item.buildFrom && item.buildFrom.length > 0) {
      const newTarget = buildPathTarget?.name === item.name ? null : item;
      setBuildPathTarget(newTarget);
    }
  };

  const formatStats = (stats?: any) => {
    if (!stats) return '';
    
    const formatted = Object.entries(stats)
      .map(([key, value]) => {
        const displayKey = key.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase());
        if (key.includes('CHANCE') || key.includes('STEAL')) {
          return `${displayKey}: ${Math.round((value as number) * 100)}%`;
        }
        return `${displayKey}: +${value}`;
      })
      .join(', ');
    
    return formatted;
  };

  const renderInventory = () => {
    const inventory = currentPlayer?.items || [];
    
    if (inventory.length === 0) {
      return <div style={{ color: '#999', fontStyle: 'italic' }}>No items</div>;
    }
    
    return (
      <InventoryGrid>
        {inventory.map((item, index) => (
          <ItemCard key={index}>
            <ItemName>{item.name}</ItemName>
            {item.stats && <ItemStats>{formatStats(item.stats)}</ItemStats>}
            {item.tip && <ItemTip>{item.tip}</ItemTip>}
          </ItemCard>
        ))}
      </InventoryGrid>
    );
  };

  const getAllComponents = (item: Item): Item[] => {
    const components: Item[] = [];
    
    const collectComponents = (currentItem: Item) => {
      if (currentItem.buildFrom) {
        for (const componentKey of currentItem.buildFrom) {
          const component = ITEMS[componentKey];
          if (component) {
            components.push(component);
            collectComponents(component);
          }
        }
      }
    };
    
    collectComponents(item);
    return components;
  };

  const handleBuildPathItemClick = (item: Item) => {
    if (!isCurrentTurn || !currentPlayer) return;
    
    const { cost } = calculateBuildCost(item, currentPlayer.items || []);
    if (currentPlayer.gold < cost) return;
    
    handleBuyItem(item);
  };

  const renderBuildPathTree = () => {
    if (!buildPathTarget) return null;
    
    const ownedItemsArray = currentPlayer?.items || [];
    
    // Create a map of owned items by name with their counts
    const ownedItemCounts = new Map<string, number>();
    ownedItemsArray.forEach(item => {
      ownedItemCounts.set(item.name, (ownedItemCounts.get(item.name) || 0) + 1);
    });
    
    // Build dependency tree structure
    interface TreeNode {
      item: Item;
      children: TreeNode[];
      id: string;
      x: number;
      y: number;
      level: number;
    }
    
    let nodeId = 0;
    const createTreeNode = (item: Item, level: number = 0): TreeNode => {
      const children: TreeNode[] = [];
      const currentId = `node-${nodeId++}`;
      
      if (item.buildFrom) {
        item.buildFrom.forEach(componentKey => {
          const component = ITEMS[componentKey];
          if (component) {
            children.push(createTreeNode(component, level + 1));
          }
        });
      }
      
      return {
        item,
        children,
        id: currentId,
        x: 0,
        y: 0,
        level
      };
    };
    
    const tree = createTreeNode(buildPathTarget);
    
    // Calculate layout positions
    const levelHeight = 150;
    const nodeWidth = 150;
    const nodeSpacing = 40;
    
    const calculateLayout = (node: TreeNode, startX: number = 0): number => {
      if (node.children.length === 0) {
        node.x = startX;
        node.y = node.level * levelHeight;
        return nodeWidth;
      }
      
      let currentX = startX;
      node.children.forEach(child => {
        const width = calculateLayout(child, currentX);
        currentX += width + nodeSpacing;
      });
      
      // Center parent over children
      const firstChildX = node.children[0].x;
      const lastChildX = node.children[node.children.length - 1].x;
      node.x = (firstChildX + lastChildX) / 2;
      node.y = node.level * levelHeight;
      
      return Math.max(currentX - startX - nodeSpacing, nodeWidth);
    };
    
    calculateLayout(tree);
    
    // Collect all nodes for rendering
    const allNodes: TreeNode[] = [];
    const collectNodes = (node: TreeNode) => {
      allNodes.push(node);
      node.children.forEach(collectNodes);
    };
    collectNodes(tree);
    
    // Calculate SVG dimensions
    const minX = Math.min(...allNodes.map(n => n.x)) - nodeWidth/2;
    const maxX = Math.max(...allNodes.map(n => n.x)) + nodeWidth/2;
    const maxY = Math.max(...allNodes.map(n => n.y)) + 60;
    const svgWidth = maxX - minX + 40;
    const svgHeight = maxY + 40;
    
    // Check ownership for a specific node instance
    const checkNodeOwnership = (node: TreeNode): boolean => {
      // First check direct ownership
      const ownedCount = ownedItemCounts.get(node.item.name) || 0;
      const sameNameNodes = allNodes.filter(n => n.item.name === node.item.name && n.level >= node.level);
      const instanceIndex = sameNameNodes.indexOf(node);
      const directlyOwned = instanceIndex < ownedCount;
      
      if (directlyOwned) return true;
      
      // Check if this specific node is consumed by being a child of an owned item
      const isConsumedByParent = (currentNode: TreeNode): boolean => {
        // Find the parent of this node by traversing the tree
        const findParent = (searchNode: TreeNode, targetNode: TreeNode): TreeNode | null => {
          if (searchNode.children.includes(targetNode)) {
            return searchNode;
          }
          for (const child of searchNode.children) {
            const result = findParent(child, targetNode);
            if (result) return result;
          }
          return null;
        };
        
        const parent = findParent(tree, currentNode);
        if (!parent) return false;
        
        // If the direct parent is owned, this item is consumed
        if (ownedItemCounts.has(parent.item.name)) {
          return true;
        }
        
        // Recursively check if any ancestor is owned
        return isConsumedByParent(parent);
      };
      
      return isConsumedByParent(node);
    };
    
    const renderTreeNode = (node: TreeNode) => {
      const { cost } = calculateBuildCost(node.item, currentPlayer?.items || []);
      const canAfford = (currentPlayer?.gold || 0) >= cost;
      const isOwned = checkNodeOwnership(node);
      const isTarget = node === tree;
      const canBuy = Boolean(isCurrentTurn && canAfford && gameState.status === 'playing' && !isOwned);
      
      // Make target item wider to accommodate more details
      const adjustedNodeWidth = isTarget ? 220 : nodeWidth;
      
      return (
        <g key={node.id}>
          <foreignObject
            x={node.x - adjustedNodeWidth/2}
            y={node.y}
            width={adjustedNodeWidth}
            height="200"
          >
            <BuildPathItem 
              owned={isOwned}
              target={isTarget}
              clickable={canBuy}
              canBuy={canBuy}
              isShimmering={isTarget && isShimmering}
              onClick={() => canBuy && handleBuildPathItemClick(node.item)}
              style={{
                width: '100%',
                height: 'auto',
                minWidth: 'unset',
                margin: 0,
                padding: '12px',
                fontSize: isTarget ? '0.9em' : '0.85em'
              }}
            >
              {isOwned && (
                <div style={{
                  position: 'absolute',
                  top: '6px',
                  right: '6px',
                  background: '#27ae60',
                  color: 'white',
                  borderRadius: '50%',
                  width: '18px',
                  height: '18px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: '11px',
                  fontWeight: 'bold'
                }}>
                  ✓
                </div>
              )}
              <div style={{ fontSize: isTarget ? '0.9em' : '0.8em', fontWeight: isTarget ? 'bold' : 'normal', marginBottom: '4px' }}>
                {node.item.name}
              </div>
              <div style={{ 
                color: isTarget ? (isOwned ? '#27ae60' : '#f1c40f') : '#f1c40f',
                fontSize: isTarget ? '0.8em' : '0.75em',
                marginBottom: isTarget ? '6px' : '0'
              }}>
                {cost}g
              </div>
              {isTarget && (
                <>
                  {node.item.stats && (
                    <div style={{ 
                      fontSize: '0.7em', 
                      marginBottom: '4px',
                      color: 'rgba(255,255,255,0.8)'
                    }}>
                      {formatStats(node.item.stats)}
                    </div>
                  )}
                  {node.item.tip && (
                    <div style={{ 
                      fontSize: '0.7em', 
                      fontStyle: 'italic',
                      color: 'rgba(255,255,255,0.9)'
                    }}>
                      {node.item.tip}
                    </div>
                  )}
                </>
              )}
            </BuildPathItem>
          </foreignObject>
        </g>
      );
    };
    
    const renderConnections = () => {
      const connections: React.ReactElement[] = [];
      
      const addConnections = (node: TreeNode) => {
        node.children.forEach(child => {
          // Estimate node heights based on content
          const isParentTarget = node === tree;
          const parentHeight = isParentTarget ? 
            (60 + (node.item.stats ? 20 : 0) + (node.item.tip ? 20 : 0)) : // Target item with dynamic content
            50; // Regular item height estimate
          
          const startX = node.x;
          const startY = node.y + parentHeight;
          const endX = child.x;
          const endY = child.y;
          
          // Create curved path
          const midY = startY + (endY - startY) / 2;
          const path = `M ${startX} ${startY} Q ${startX} ${midY} ${endX} ${endY}`;
          
          connections.push(
            <path
              key={`${node.id}-${child.id}`}
              d={path}
              stroke="#3498db"
              strokeWidth="2"
              fill="none"
              strokeDasharray={checkNodeOwnership(child) ? "none" : "5,5"}
              opacity={checkNodeOwnership(child) ? 1 : 0.6}
            />
          );
          
          addConnections(child);
        });
      };
      
      addConnections(tree);
      return connections;
    };
    
    return (
      <BuildPathContainer ref={buildPathRef}>
        <BuildPathTitle>
          Build Path for {buildPathTarget.name}
          <ClearBuildPathButton onClick={() => setBuildPathTarget(null)}>
            Clear
          </ClearBuildPathButton>
        </BuildPathTitle>
        <div style={{ display: 'flex', justifyContent: 'center', overflow: 'auto', padding: '20px' }}>
          <svg 
            width={svgWidth} 
            height={svgHeight}
            style={{ minWidth: svgWidth }}
          >
            <g transform={`translate(${-minX + 20}, 20)`}>
              {renderConnections()}
              {allNodes.map(node => renderTreeNode(node))}
            </g>
          </svg>
        </div>
      </BuildPathContainer>
    );
  };

  const renderShopItems = (tree: 'BASIC' | 'ADVANCED' | 'COMPLETE') => {
    const items = getItemsByTree(tree);
    
    return (
      <ItemGrid>
        {items.map(item => {
          const { cost, missingComponents } = calculateBuildCost(item, currentPlayer?.items || []);
          const canAfford = (currentPlayer?.gold || 0) >= cost;
          const isRecentlyPurchased = recentlyPurchased.has(item.name);
          const canBuy = isCurrentTurn && canAfford && gameState.status === 'playing' && !isRecentlyPurchased;
          const hasBuildPath = item.buildFrom && item.buildFrom.length > 0;
          const isSelected = buildPathTarget?.name === item.name;
          
          return (
            <ItemCard 
              key={item.name} 
              canAfford={canAfford}
              clickable={canBuy || hasBuildPath}
              onClick={() => {
                if (canBuy) {
                  handleBuyItem(item);
                } else if (hasBuildPath) {
                  handleItemClick(item);
                }
              }}
              style={{
                borderColor: isSelected ? '#3498db' : undefined,
                borderWidth: isSelected ? '2px' : '1px'
              }}
            >
              <ItemName>{item.name}</ItemName>
              <ItemCost>{cost}g</ItemCost>
              {item.buildFrom && item.buildFrom.length > 0 && (
                <BuildPath>
                  Builds from: {item.buildFrom.join(', ')}
                  {missingComponents.length > 0 && (
                    <div style={{ color: '#e74c3c' }}>
                      Missing: {missingComponents.join(', ')}
                    </div>
                  )}
                </BuildPath>
              )}
              {item.stats && <ItemStats>{formatStats(item.stats)}</ItemStats>}
              {item.tip && <ItemTip>{item.tip}</ItemTip>}
              {canBuy && (
                <BuyButton 
                  onClick={(e) => { e.stopPropagation(); handleBuyItem(item); }}
                  disabled={isRecentlyPurchased}
                >
                  {isRecentlyPurchased ? 'Purchasing...' : `Buy for ${cost}g`}
                </BuyButton>
              )}
              {!canAfford && !isRecentlyPurchased && (
                <BuyButton disabled>
                  Need {cost - (currentPlayer?.gold || 0)}g more
                </BuyButton>
              )}
              {hasBuildPath && !canBuy && (
                <div style={{ 
                  marginTop: '8px', 
                  fontSize: '0.8em', 
                  color: '#3498db',
                  fontStyle: 'italic'
                }}>
                  Set as build path
                </div>
              )}
            </ItemCard>
          );
        })}
      </ItemGrid>
    );
  };

  return (
    <Overlay isClosing={isClosing} onClick={handleClose}>
      <Modal ref={modalRef} onClick={(e) => e.stopPropagation()} isClosing={isClosing}>
        <CloseButton onClick={handleClose}>&times;</CloseButton>
        
        <Section>
          <SectionTitle>Your Inventory ({currentPlayer?.gold || 0} gold)</SectionTitle>
          {renderInventory()}
        </Section>

        {renderBuildPathTree()}

        <Section>
          <SectionTitle>Item Shop</SectionTitle>
          
          <TreeSection>
            <TreeTitle tree="BASIC">Basic Items</TreeTitle>
            {renderShopItems('BASIC')}
          </TreeSection>
          
          <TreeSection>
            <TreeTitle tree="ADVANCED">Advanced Items</TreeTitle>
            {renderShopItems('ADVANCED')}
          </TreeSection>
          
          <TreeSection>
            <TreeTitle tree="COMPLETE">Complete Items</TreeTitle>
            {renderShopItems('COMPLETE')}
          </TreeSection>
        </Section>
      </Modal>
    </Overlay>
  );
};

export default ItemsModal; 