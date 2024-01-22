import Navbar from "../../nav/Navbar";
import React, {useEffect} from "react";
import './ManageConnectionsPage.css';
import {AuthData} from "../../routing/AuthWrapper";
import AddConnectionsModal from "../../modals/addConnections/AddConnectionsModal";
import {buildAxiosRequest} from "../../helpers/AxiosHelper";

const ManageConnectionsPage = () => {

    const {user, refreshUser} = AuthData();
    console.log(user?.linkedAccounts)
    const linkAccoountsClassname = user?.linkedAccounts.linkedAccountsCount === 0 ? "manage-connections-page-link-accounts-container-center" : "manage-connections-page-link-accounts-container";

    useEffect(() => {
        const urlSearchParams = new URLSearchParams(window.location.search);
        const code = urlSearchParams.get('code');
        const newUrlSearchParams = new URLSearchParams(window.location.search);
        newUrlSearchParams.delete('code');
        const newUrl = `${window.location.pathname}${newUrlSearchParams.toString()}`;
        window.history.replaceState({}, document.title, newUrl);

        if (code) {
            buildAxiosRequest('GET', `/onedrive-store-tokens?code=${code}&email=${user?.email}`, {}).then(() => {
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
                <div className={linkAccoountsClassname}>
                    {user?.linkedAccounts.linkedAccountsCount === 0 ? (
                        <div className={"manage-connections-page-link-text"}> To link an account press the button
                            below </div>) : null}
                    <AddConnectionsModal oneDrive={user?.linkedAccounts.oneDrive}/>
                </div>
            </div>
        </div>
    )
};

export default ManageConnectionsPage;
