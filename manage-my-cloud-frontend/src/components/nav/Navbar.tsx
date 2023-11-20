import React from 'react';
import { Link } from 'react-router-dom';
import './Navbar.css';
import logo from '../images/managemycloudlogo.svg';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';


const Navbar: React.FC = () => {
    return (
        <nav className="navbar">
            <li className="logo">
                <Link to="/" className="nav-link-logo">
                    <img src={logo} alt="manage my cloud logo" height="150" width="150" />
                </Link>
            </li>
            <ul className="navbar-nav">
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
                    <Link to="/profile" className="nav-link">
                        Profile
                    </Link>
                </li>
            </ul>
            <AccountCircleIcon sx={{ fontSize: 50, color: 'white', position: 'relative', top: '50px', right: '20px' }} />
        </nav>
    );
};

export default Navbar;
