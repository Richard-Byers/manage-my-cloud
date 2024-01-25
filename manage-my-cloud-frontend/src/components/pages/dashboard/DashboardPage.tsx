import Navbar from "../../nav/Navbar";
import React from "react";
import ConnectedDrivesCard from "./cards/ConnectedDrivesCard";
import './DashboardPage.css';
import {useNavigate} from "react-router-dom";
import {ROUTES} from "../../../constants/RouteConstants";
import DashboardPageButtons from "./DashboardPageButtons";
import { useTranslation } from "react-i18next";

const DashboardPage = () => {

    const navigate = useNavigate();
    const { t } = useTranslation();

    function navigateToManageConnections() {
        navigate(ROUTES.MANAGE_CONNECTIONS);
    }

    return (
        <div>
            <Navbar/>
            <div className={"dashboard-page-content-grid"}>
                <div className="dashboard-page-title-container">
                    {t('main.dashboard.title')}
                </div>
                <div className={"connected-drives-container"}>
                    <ConnectedDrivesCard/>
                </div>
                <div className={"dashboard-page-buttons-container"}>
                    <DashboardPageButtons/>
                </div>
                <div className={"connect-drives-container"}>
                    <div className="text-under-line">
                        <p>
                        {t('main.dashboard.manageConnectionsText', { 
                            manageConnectionsLink: (
                            <button className={'manage-connections-hyperlink'}
                                    onClick={navigateToManageConnections}>Manage
                                Connections
                            </button>
                            ),
                        })}
                        </p>

                    </div>
                </div>
            </div>
        </div>

    )
};

export default DashboardPage;