import React from 'react';
import './App.css';
import {BrowserRouter as Router} from 'react-router-dom';
import AppRouting from "./components/routing/AppRouting";
import Navbar from "./components/nav/Navbar";
import { GoogleOAuthProvider } from '@react-oauth/google';

function App() {
    return (
        <GoogleOAuthProvider clientId="897733650985-l3188hg9ni6cfd61kf9jpkcce5b83omt.apps.googleusercontent.com">
        <Router>
            <AppRouting/>
        </Router>
        </GoogleOAuthProvider>
    );
}

export default App;