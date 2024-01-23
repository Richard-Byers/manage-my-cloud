import Navbar from "../../nav/Navbar";
import React, {useEffect} from "react";
import './ManageConnectionsPage.css';
import {AuthData} from "../../routing/AuthWrapper";
import AddConnectionsModal from "../../modals/addConnections/AddConnectionsModal"
import {buildAxiosRequest, buildAxiosRequestWithHeaders} from "../../helpers/AxiosHelper";
import Connection from "./Connection";

const ManageConnectionsPage = () => {

    const {user, refreshUser} = AuthData();

    const linkedAccountsArray = Object.entries(user?.linkedAccounts || {})
        .filter(([key]) => key !== 'linkedAccountsCount')
        .map(([key, value]) => ({key, value}));

    const linkAccountsClassname = user?.linkedAccounts.linkedAccountsCount === 0 ?
        "manage-connections-page-link-accounts-container-center" :
        "manage-connections-page-link-accounts-container";

    useEffect(() => {
        const urlSearchParams = new URLSearchParams(window.location.search);
        const code = urlSearchParams.get('code');
        const newUrlSearchParams = new URLSearchParams(window.location.search);
        newUrlSearchParams.delete('code');
        const newUrl = `${window.location.pathname}${newUrlSearchParams.toString()}`;
        window.history.replaceState({}, document.title, newUrl);
        const headers = {
            Authorization: `Bearer ${user?.token}`
        }

        if (code) {
            buildAxiosRequestWithHeaders('GET', `/onedrive-store-tokens?code=${code}&email=${user?.email}`,headers ,{}).then(() => {
                refreshUser(user?.email);
                window.history.replaceState({}, document.title, newUrl);
            });
        }
    }, []);

    return (
        <div>
            <Navbar/>
            <div className={"manage-connections-page-content-grid"}>
                <div className="manage-connections-page-title-container">
                    Manage Connections
                </div>

                <div className={linkAccountsClassname}>
                    {user?.linkedAccounts.linkedAccountsCount === 0 ? (
                        <>
                            <div className={"manage-connections-page-link-text"}> To link an account press the button
                                below
                            </div>
                            <AddConnectionsModal
                                oneDrive={user?.linkedAccounts.oneDrive}/> </>) : linkedAccountsArray.map(({
                                                                                                               key,
                                                                                                               value
                                                                                                           }) => (
                        <Connection key={key} connectionProvider={key} isConnected={value}/>
                    ))}
                </div>

                {user?.linkedAccounts.linkedAccountsCount !== undefined && user?.linkedAccounts.linkedAccountsCount >= 1 ? (
                    <div className={"manage-connections-page-link-button-container"}>
                        <AddConnectionsModal oneDrive={user?.linkedAccounts.oneDrive}/>
                    </div>) : null}
            </div>
        </div>
    )
};

export default ManageConnectionsPage;
