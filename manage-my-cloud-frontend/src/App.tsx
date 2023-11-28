import React from 'react';
import './App.css';
import {BrowserRouter} from 'react-router-dom';
import AppRouting from "./components/routing/AppRouting";
import {AuthWrapper} from "./components/routing/AuthWrapper";

function App() {
    return (
        <BrowserRouter>
            <AuthWrapper/>
        </BrowserRouter>
    );
}

export default App;