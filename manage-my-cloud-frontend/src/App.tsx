import React, {Suspense} from 'react';
import './App.css';
import {BrowserRouter} from 'react-router-dom';
import {AuthWrapper} from "./components/routing/AuthWrapper";
import {GoogleOAuthProvider} from '@react-oauth/google';
import LoginModal from './components/modals/login/LoginModal';

function App() {
    return (
        <GoogleOAuthProvider clientId="897733650985-7eav0a3orjebhs71q9l6mtrnbukolhfo.apps.googleusercontent.com">
            <BrowserRouter>
                <Suspense fallback="...loading">
                    <AuthWrapper/>
                </Suspense>
            </BrowserRouter>
        </GoogleOAuthProvider>
    );
}

export default App;
