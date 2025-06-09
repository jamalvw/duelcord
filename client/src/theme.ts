import 'styled-components';

declare module 'styled-components' {
  export interface DefaultTheme {
    colors: {
      background: string;
      backgroundSecondary: string;
      backgroundTertiary: string;
      text: string;
      textSecondary: string;
      primary: string;
      error: string;
      success: string;
    }
  }
} 