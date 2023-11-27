import React from 'react';
import './App.css';
import {BrowserRouter} from 'react-router-dom';
import AppRouting from "./components/routing/AppRouting";
import Navbar from "./components/nav/Navbar";
import { GoogleOAuthProvider } from '@react-oauth/google';
import {AuthWrapper} from "./components/routing/AuthWrapper";

function App() {
    return (
        <GoogleOAuthProvider clientId="897733650985-l3188hg9ni6cfd61kf9jpkcce5b83omt.apps.googleusercontent.com">
        <BrowserRouter>
            <AuthWrapper/>
        </BrowserRouter>
        </GoogleOAuthProvider>
    );
}

export default App;