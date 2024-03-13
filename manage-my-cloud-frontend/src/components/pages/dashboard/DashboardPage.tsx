import Navbar from "../../nav/Navbar";
import React, {useEffect, useRef} from "react";
import ConnectedDrivesCard from "./cards/ConnectedDrivesCard";
import './DashboardPage.css';
import {useNavigate} from "react-router-dom";
import {ROUTES} from "../../../constants/RouteConstants";
import {useTranslation} from "react-i18next";
import {AuthData} from "../../routing/AuthWrapper";
import {buildAxiosRequestWithHeaders} from "../../helpers/AxiosHelper";

const DashboardPage = () => {

    const navigate = useNavigate();
    const {t} = useTranslation();
    const {user, refreshUser} = AuthData();
    const intervalRef = useRef<NodeJS.Timeout | null>(null);

    function navigateToManageConnections() {
        navigate(ROUTES.MANAGE_CONNECTIONS);
    }

    const checkAndUpdateToken = async () => {
        console.log('checkAndUpdateToken function called');
        const userEmail = user?.email;
        const headers = {
            Authorization: `Bearer ${user?.token}`
        };
        if (userEmail && user?.linkedAccounts.linkedDriveAccounts.some(account => account.accountType === 'OneDrive')) {
            try {
                const response = await buildAxiosRequestWithHeaders('POST', `/onedrive-refresh-access-token`, headers, {email: userEmail});
                if (response.status === 200) {
                    for (let i = 0; i < response.data.length; i++) {
                        if (response.data[i].status === 200) {
                            refreshUser(user?.email);
                        }
                    }
                }
            } catch (error) {
                console.error('Failed to refresh access token:', error);
            }
        }
    };

    // Call checkAndUpdateToken when the component is mounted
    useEffect(() => {
        checkAndUpdateToken();
    }, []);

    return (
        <>
            <Navbar/>
            <div className={"dashboard-page-content-grid"}>
                <div className="dashboard-page-title-container">
                    {t('main.dashboard.title')}
                </div>

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