import Navbar from "../../nav/Navbar";
import React from "react";
import './ManageConnectionsPage.css';
import { useTranslation } from 'react-i18next';


const ManageConnectionsPage = () => {
    const { t } = useTranslation();
    return (
        <div>
            <Navbar/>
            <div className={"manage-connections-page-content-grid"}>
                <div className="manage-connections-page-title-container">
                    {t('main.manageConnections.title')}
                </div>
            </div>
        </div>
    )
};

export default ManageConnectionsPage;