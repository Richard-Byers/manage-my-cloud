import React from 'react';
import './App.css';
import {BrowserRouter} from 'react-router-dom';
import AppRouting from "./components/routing/AppRouting";
import {AuthWrapper} from "./components/routing/AuthWrapper";
import { Suspense } from 'react';
import { useTranslation} from 'react-i18next';

function App() {
  return (
      <BrowserRouter>
          <Suspense fallback="...loading">
              <AuthWrapper/>
          </Suspense>
          <AuthWrapper/>
      </BrowserRouter>
  );
}

export default App;
