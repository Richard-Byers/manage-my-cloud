import LandingPage from "../pages/landing/LandingPage";
import ManageConnectionsPage from "../pages/manage_connections/ManageConnectionsPage";
import ProfilePage from "../pages/profile/ProfilePage";
import {ROUTES} from "../../constants/RouteConstants";
import DashboardPage from "../pages/dashboard/DashboardPage";


export const nav = [
    {path: ROUTES.LANDING, name: "landing", element: <LandingPage/>, isPrivate: false},
    {path: ROUTES.DASHBOARD, name: "dashboard", element: <DashboardPage/>, isPrivate: false},
    {path: ROUTES.MANAGE_CONNECTIONS, name: "manageConnections", element: <ManageConnectionsPage/>, isPrivate: false},
    {path: ROUTES.PROFILE, name: "profile", element: <ProfilePage/>, isPrivate: false},
]