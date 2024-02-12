import React from 'react';
import {Link, useNavigate} from 'react-router-dom';
import './Navbar.css';
import logo from '../images/managemycloudlogo.png';
import {ROUTES} from "../../constants/RouteConstants";
import {Popover} from "@mui/material";
import {AuthData} from "../routing/AuthWrapper";

const Navbar: React.FC = () => {
    const {user, logout} = AuthData();
    const navigate = useNavigate();
    const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
    const open = Boolean(anchorEl);

    const handlePopoverOpen = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorEl(event.currentTarget);
      };

    const handlePopoverClose = () => {
    setAnchorEl(null);
    };

    function navigateToProfile() {
        navigate(ROUTES.PROFILE);
    }

    // Retrieve profile image URL from local storage
    const profileImage = localStorage.getItem('profileImage') || logo;

    return (
        <nav className="navbar">
            <li className="logo">
                <Link to={ROUTES.LANDING} className="nav-link-logo">
                    <img src={logo} alt="manage my cloud logo" height="100" width="100"/>
                </Link>
            </li>
            <ul className="navbar-nav">
                <li className="nav-item">
                    <Link to={ROUTES.DASHBOARD} className="nav-link">
                        Dashboard
                    </Link>
                </li>
                <li className="nav-item">
                    <Link to={ROUTES.MANAGE_CONNECTIONS} className="nav-link">
                        Manage Connections
                    </Link>
                </li>
                <li className="nav-item">
                    <Link to={ROUTES.PROFILE} className="nav-link">
                        Profile
                    </Link>
                </li>
            </ul>
            <div>
    <button className="profile-button" onClick={handlePopoverOpen}>
        <img src={profileImage} alt="Profile" className="popover-style"/>
    </button>
    <Popover
      open={open}
      anchorEl={anchorEl}
      onClose={handlePopoverClose}
      anchorOrigin={{
        vertical: 'bottom',
        horizontal: 'center',
      }}
      transformOrigin={{
        vertical: 'top',
        horizontal: 'center',
      }}
    >
      <div className={'popover-container'}>
        <button className={'popover-button'} onClick={navigateToProfile}>
          Profile
        </button>
        <button className={'popover-button'} onClick={logout}>
          Logout
        </button>
      </div>
    </Popover>
  </div>
        </nav>
    );
};

export default Navbar;
