import React from 'react';
import './App.css';
import {BrowserRouter} from 'react-router-dom';
import {AuthWrapper} from "./components/routing/AuthWrapper";
import {GoogleOAuthProvider} from '@react-oauth/google';
import { Suspense } from 'react';
import { useTranslation} from 'react-i18next';

function App() {
  return (
        <GoogleOAuthProvider clientId="897733650985-l3188hg9ni6cfd61kf9jpkcce5b83omt.apps.googleusercontent.com">
          <BrowserRouter>
              <Suspense fallback="...loading">
              <AuthWrapper/>
          </Suspense>
          <AuthWrapper/>
          </BrowserRouter>
        </GoogleOAuthProvider>
  );
}

export default App;
