import Navbar from "../../nav/Navbar";
import React, {useEffect, useRef} from "react";
import ConnectedDrivesCard from "./cards/ConnectedDrivesCard";
import './DashboardPage.css';
import {useNavigate} from "react-router-dom";
import {ROUTES} from "../../../constants/RouteConstants";
import {useTranslation} from "react-i18next";
import {AuthData} from "../../routing/AuthWrapper";
import WelcomeModal from "../../ui_components/WelcomeModal";
import ToolTip from "../../ui_components/ToolTip";
import HelpOutlineIcon from "@mui/icons-material/HelpOutline";

const DashboardPage = () => {

    const navigate = useNavigate();
    const {t} = useTranslation();
    const {user, refreshUser} = AuthData();

    function navigateToManageConnections() {
        navigate(ROUTES.MANAGE_CONNECTIONS);
    }

    const shouldRun = useRef(true);
    useEffect(() => {
        if (!shouldRun.current) return;
        shouldRun.current = false;

        if (user?.firstLogin === true) return;
        refreshUser(user?.email);
    }, []);

    return (
        <>
            <Navbar/>
            <div className={"dashboard-page-content-grid"}>
                <div className="dashboard-page-title-container">
                    {t('main.dashboard.title')}
                    <ToolTip
                        message={t("main.tooltip.dashboard.dashboardMain")}
                        children={<HelpOutlineIcon/>}
                    />
                </div>
                {user?.firstLogin === false ? null : <WelcomeModal/>}

                {user?.linkedAccounts.linkedAccountsCount === 0 ? null :
                    <div className={"connected-drives-overflow-container"}>
                        {
                            user?.linkedAccounts.linkedDriveAccounts.map(({
                                                                              accountEmail,
                                                                              accountType
                                                                          }) => (
                                <ConnectedDrivesCard key={accountEmail} connectionProvider={accountType}
                                                     driveEmail={accountEmail}/>
                            ))}
                    </div>
                }

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