import React from 'react';
import {Link} from 'react-router-dom';
import './Navbar.css';
import logo from '../images/managemycloudlogo.png';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import {ROUTES} from "../../constants/RouteConstants";
import {Popover} from "@mui/material";
import {AuthData} from "../routing/AuthWrapper";
import {useNavigate} from "react-router-dom";

const Navbar: React.FC = () => {
    const {logout} = AuthData();
    const navigate = useNavigate();
    const [showProfilePopover, setShowProfilePopover] = React.useState(false);

    function toggleProfilePopover() {
        setShowProfilePopover(!showProfilePopover);
    }

    function navigateToProfile() {
        navigate(ROUTES.PROFILE);
    }

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
            <button className={"profile-button"} onClick={toggleProfilePopover}>
                <AccountCircleIcon
                    sx={{fontSize: 50, color: 'white'}}/>
                {showProfilePopover && (<Popover
                    anchorOrigin={{
                        vertical: 77,
                        horizontal: 1770,
                    }}
                    transformOrigin={{
                        vertical: 'top',
                        horizontal: 'left',
                    }}
                    open>
                    <div className={'popover-container'}>
                        <button className={'popover-button'} onClick={navigateToProfile}>
                            Profile
                        </button>
                        <button className={'popover-button'} onClick={logout}>
                            Logout
                        </button>
                    </div>
                </Popover>)}
            </button>
        </nav>
    );
};

export default Navbar;
