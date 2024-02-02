import Navbar from "../../nav/Navbar";
import React from "react";
import ConnectedDrivesCard from "./cards/ConnectedDrivesCard";
import './DashboardPage.css';
import {useNavigate} from "react-router-dom";
import {ROUTES} from "../../../constants/RouteConstants";
import DashboardPageButtons from "./DashboardPageButtons";
import {AuthData} from "../../routing/AuthWrapper";

const DashboardPage = () => {

    const navigate = useNavigate();
    const {user} = AuthData();

    const linkedAccountsArray = Object.entries(user?.linkedAccounts || {})
        .filter(([key]) => key !== 'linkedAccountsCount')
        .filter(([key, value]) => value)
        .map(([key, value]) => ({key, value}));

    function navigateToManageConnections() {
        navigate(ROUTES.MANAGE_CONNECTIONS);
    }

    return (
        <div>
            <Navbar/>
            <div className={"dashboard-page-content-grid"}>
                <div className="dashboard-page-title-container">
                    Dashboard
                </div>

                {
                    linkedAccountsArray.map(({
                                                 key,
                                                 value
                                             }) => (
                        <div className={"connected-drives-container"}><ConnectedDrivesCard key={key}
                                                                                           connectionProvider={key}/>
                        </div>
                    ))}

                <div className={"dashboard-page-buttons-container"}>
                    <DashboardPageButtons/>
                </div>
                <div className={"connect-drives-container"}>
                    <div className="text-under-line">
                        <p>If you would like to add or remove drives please head to
                            <button className={'manage-connections-hyperlink'}
                                    onClick={navigateToManageConnections}>Manage
                                Connections
                            </button>
                        </p>

                    </div>
                </div>
            </div>
        </div>

    )
};

export default DashboardPage;