import React from 'react';
import { Link } from 'react-router-dom';
import './Navbar.css';

const Navbar: React.FC = () => {
    return (
        <nav className="navbar">
            <ul className="navbar-nav">
                <li className="nav-item">
                    <Link to="/" className="nav-link">
                        Dashboard
                    </Link>
                </li>
                <li className="nav-item">
                    <Link to="/manage-connections" className="nav-link">
                        Add/Manage Connections
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
            </ul>
        </nav>
    );
};

export default Navbar;
