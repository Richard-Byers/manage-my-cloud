import React from 'react';
import { Link } from 'react-router-dom';
import './Navbar.css';
import logo from '../images/managemycloudlogo.svg';

const Navbar: React.FC = () => {
    return (
        <nav className="navbar">
            <ul className="navbar-nav">
                <li className="logo">
                    <Link to="/" className="nav-link-logo">
                        <img src={logo} alt="manage my cloud logo" height="150" width="150" />
                    </Link>
                </li>
                <li className="nav-item">
                    <Link to="/" className="nav-link">
                        Dashboard
                    </Link>
                </li>
                <li className="nav-item">
                    <Link to="/manage-connections" className="nav-link">
                        Manage Connections
                    </Link>
                </li>
                <li className="nav-item">
                    <Link to="/preferences" className="nav-link">
                        Contact
                    </Link>
                </li>
                <li className="nav-item">
                    <Link to="/profile" className="nav-link">
                        Profile
                    </Link>
                </li>
                <li className="nav-item">
                    <Link to="/settings" className="nav-link">
                        Settings
                    </Link>
                </li>
            </ul>
        </nav>
    );
};

export default Navbar;
