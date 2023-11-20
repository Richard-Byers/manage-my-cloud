import React from 'react';
import './App.css';
import {BrowserRouter as Router} from 'react-router-dom';
import AppRouting from "./components/routing/AppRouting";
import Navbar from "./components/nav/Navbar";

function App() {
    return (
        <Router>
            <AppRouting/>
        </Router>
    );
}

export default App;