import React from 'react';
import './App.css';
import {BrowserRouter as Router} from 'react-router-dom';
import AppRouting from "./components/routing/AppRouting";

function App() {
    return (
        <Router>
            <AppRouting/>
        </Router>
    );
}

export default App;