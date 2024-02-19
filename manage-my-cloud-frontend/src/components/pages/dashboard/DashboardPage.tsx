import Navbar from "../../nav/Navbar";
import React from "react";
import ConnectedDrivesCard from "./cards/ConnectedDrivesCard";
import './DashboardPage.css';
import {useNavigate} from "react-router-dom";
import {ROUTES} from "../../../constants/RouteConstants";
import {useTranslation} from "react-i18next";
import {AuthData} from "../../routing/AuthWrapper";

const DashboardPage = () => {

    const navigate = useNavigate();
    const {t} = useTranslation();
    const {user} = AuthData();

    const linkedAccountsArray = Object.entries(user?.linkedAccounts || {})
        .filter(([key]) => key !== 'linkedAccountsCount')
        .filter(([key, value]) => value)
        .map(([key, value]) => ({key, value}));

    function navigateToManageConnections() {
        navigate(ROUTES.MANAGE_CONNECTIONS);
    }

    return (
        <>
            <Navbar/>
            <div className={"dashboard-page-content-grid"}>
                <div className="dashboard-page-title-container">
                    {t('main.dashboard.title')}
                </div>

                <div className={"connected-drives-overflow-container"}>
                    {
                        linkedAccountsArray.map(({
                                                     key,
                                                     value
                                                 }) => (
                                <ConnectedDrivesCard key={key} connectionProvider={key}/>
                        ))}
                </div>

                <div className="text-under-line">
                    <p>
                        {t('main.dashboard.manageConnectionsTextBeforeLink')}
                        <button className={'manage-connections-hyperlink'} onClick={navigateToManageConnections}>
                            {t('main.dashboard.manageConnectionsLink')}
                        </button>
                    </p>
                </div>
            </div>
        </>

    )
};

export default DashboardPage;