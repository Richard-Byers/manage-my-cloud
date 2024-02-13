import React, {useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import './Navbar.css';
import logo from '../images/managemycloudlogo.png';
import {ROUTES} from "../../constants/RouteConstants";
import {Popover} from "@mui/material";
import {AuthData} from "../routing/AuthWrapper";
import MenuIcon from '@mui/icons-material/Menu';
import CloseIcon from '@mui/icons-material/Close';

const Navbar: React.FC = () => {
    const {logout} = AuthData();
    const navigate = useNavigate();
    const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
    const [sidebarVisible, setSideBarVisible] = useState(false);
    const open = Boolean(anchorEl);

    const sideBarClass = `sidebar ${sidebarVisible ? 'sidebar-visible' : 'sidebar-hidden'}`;

    const handlePopoverOpen = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorEl(event.currentTarget);
    };

    const handlePopoverClose = () => {
        setAnchorEl(null);
    };

    const showSidebar = () => {
        setSideBarVisible(true);
    }

    const hideSideBar = () => {
        setSideBarVisible(false);
    }

    function navigateToProfile() {
        navigate(ROUTES.PROFILE);
    }

    // Retrieve profile image URL from local storage
    const profileImage = localStorage.getItem('profileImage') || logo;

    return (
        <nav className={"nav"}>

            <ul className={sideBarClass}>
                <li className="nav-item" onClick={hideSideBar}>
                    <button className={"sidebar-button-close"}><CloseIcon/></button>
                </li>

                <li>
                    <img src={profileImage} alt="Profile" className="popover-style"/>
                </li>

                <li>
                    <Link to={ROUTES.DASHBOARD}>
                        Dashboard
                    </Link>
                </li>
                <li>
                    <Link to={ROUTES.MANAGE_CONNECTIONS}>
                        Manage Connections
                    </Link>
                </li>
                <li>
                    <Link to={ROUTES.PROFILE}>
                        Profile
                    </Link>
                </li>
            </ul>

            <ul className="navbar-nav">
                <li className="logo">
                    <Link to={ROUTES.LANDING} className="nav-link-logo">
                        <img src={logo} alt="manage my cloud logo" height="100" width="100"/>
                    </Link>
                </li>
                <li className="hide-on-mobile">
                    <Link to={ROUTES.DASHBOARD} className="nav-link">
                        Dashboard
                    </Link>
                </li>
                <li className="hide-on-mobile">
                    <Link to={ROUTES.MANAGE_CONNECTIONS} className="nav-link">
                        Manage Connections
                    </Link>
                </li>
                <li className="hide-on-mobile">
                    <Link to={ROUTES.PROFILE} className="nav-link">
                        Profile
                    </Link>
                </li>
                <li className="nav-item" onClick={showSidebar}>
                    <button className={"sidebar-button-open"}><MenuIcon/></button>
                </li>
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
            </ul>
        </nav>
    );
};

export default Navbar;
