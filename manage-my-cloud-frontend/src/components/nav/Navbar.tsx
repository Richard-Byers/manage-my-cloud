import React, {useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import './Navbar.css';
import logo from '../images/managemycloudlogo.png';
import {ROUTES} from "../../constants/RouteConstants";
import {Popover} from "@mui/material";
import {AuthData} from "../routing/AuthWrapper";
import MenuIcon from '@mui/icons-material/Menu';
import CloseIcon from '@mui/icons-material/Close';
import DashboardIcon from '@mui/icons-material/Dashboard';
import SettingsInputComponentIcon from '@mui/icons-material/SettingsInputComponent';
import PersonIcon from '@mui/icons-material/Person';
import LogoutIcon from '@mui/icons-material/Logout';
import {useTranslation} from "react-i18next";

const Navbar: React.FC = () => {
    const {t} = useTranslation();
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
                    <button className={"sidebar-button-close"}><CloseIcon className={"svg_icons"}/></button>
                </li>

                <li>
                    <img src={profileImage} alt="Profile" className="popover-style"/>
                </li>

                <li>
                    <Link to={ROUTES.DASHBOARD} id={"dashboard-nav-link-mobile"}>
                        <DashboardIcon/>
                        {t("main.navbar.dashboard")}
                    </Link>
                </li>
                <li>
                    <Link to={ROUTES.MANAGE_CONNECTIONS} id={"manage-connections-nav-link-mobile"}>
                        <SettingsInputComponentIcon/>
                        {t("main.navbar.manageConnections")}
                    </Link>
                </li>
                <li>
                    <Link to={ROUTES.PROFILE} id={"profile-nav-link-mobile"}>
                        <PersonIcon/>
                        {t("main.navbar.profile")}
                    </Link>
                </li>
                <li>
                    <button onClick={logout}>
                        <LogoutIcon/>
                        {t("main.navbar.logout")}
                    </button>
                </li>
            </ul>

            <ul className="navbar-nav">
                <li className="logo">
                    <Link to={ROUTES.LANDING} className={"logo-container"}>
                        <img src={logo} alt="manage my cloud logo" height="100" width="100"/>
                    </Link>
                </li>
                <li className="hide-on-mobile">
                    <Link to={ROUTES.DASHBOARD} className="nav-link" id={"dashboard-nav-link"}>
                        {t("main.navbar.dashboard")}
                    </Link>
                </li>
                <li className="hide-on-mobile">
                    <Link to={ROUTES.MANAGE_CONNECTIONS} className="nav-link" id={"manage-connections-nav-link"}>
                        {t("main.navbar.manageConnections")}
                    </Link>
                </li>
                <li className="hide-on-mobile">
                    <Link to={ROUTES.PROFILE} className="nav-link" id={"profile-nav-link"}>
                        {t("main.navbar.profile")}
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
