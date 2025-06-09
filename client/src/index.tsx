import React from 'react';
import ReactDOM from 'react-dom/client';
import { ThemeProvider, DefaultTheme } from 'styled-components';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import './theme';

export const theme: DefaultTheme = {
  colors: {
    background: '#0a0e1a',
    backgroundSecondary: '#1a1f3a',
    backgroundTertiary: '#252946',
    text: '#e8e3d3',
    textSecondary: '#b8b3a3',
    primary: '#4169e1',
    error: '#dc143c',
    success: '#228b22'
  }
};

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);
root.render(
  <React.StrictMode>
    <ThemeProvider theme={theme}>
      <App />
    </ThemeProvider>
  </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
