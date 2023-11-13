import React from 'react';
import {Route, Routes} from "react-router-dom";
import DashboardPage from "../pages/dashboard/DashboardPage";
import ManageConnectionsPage from "../pages/manage_connections/ManageConnectionsPage";
import PreferencesPage from "../pages/preferences/PreferencesPage";
import ProfilePage from "../pages/profile/ProfilePage";
import {ROUTES} from "../../constants/RouteConstants"

const AppRouting = () => {
    return (
        <Routes>
            <Route path={ROUTES.DASHBOARD} element={<DashboardPage/>}/>
            <Route path={ROUTES.MANAGE_CONNECTIONS} element={<ManageConnectionsPage/>}/>
            <Route path={ROUTES.PREFERENCES} element={<PreferencesPage/>}/>
            <Route path={ROUTES.PROFILE} element={<ProfilePage/>}/>
        </Routes>
    )
};

export default AppRouting;