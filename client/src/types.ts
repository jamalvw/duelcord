export interface Message {
  type: 'chat' | 'system' | 'error';
  username?: string;
  content: string;
} 