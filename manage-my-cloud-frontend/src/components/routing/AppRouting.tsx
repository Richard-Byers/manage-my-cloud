import React from 'react';
import {Navigate, Route, Routes, useLocation} from "react-router-dom";
import {AuthData} from "./AuthWrapper";
import {nav} from "./Navigation";
import {ROUTES} from "../../constants/RouteConstants";
import LoginModal from '../modals/login/LoginModal';


const AppRouting = () => {
    const { user, loading } = AuthData();
    const location = useLocation();

    if (loading) {
        return <div>Loading...</div>;
    }

    return (
        <Routes>
            {nav.map((link, i) => (
                <Route
                    key={i}
                    path={link.path}
                    element={
                        link.isPrivate && !user && location.pathname === link.path ? (
                            <Navigate to={ROUTES.LANDING} replace/>
                        ) : (
                            link.element
                        )
                    }
                />
            ))}
            <Route path="/login" element={<LoginModal />} />
        </Routes>
    );
};

export default AppRouting;