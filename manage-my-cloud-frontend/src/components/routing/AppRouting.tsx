import React from 'react';
import {Navigate, Route, Routes, useLocation} from "react-router-dom";
import {AuthData} from "./AuthWrapper";
import {nav} from "./Navigation";
import {ROUTES} from "../../constants/RouteConstants";

const AppRouting = () => {
    const { user, loading } = AuthData();
    const location = useLocation(); // Get the current location

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
        </Routes>
    );
};

export default AppRouting;