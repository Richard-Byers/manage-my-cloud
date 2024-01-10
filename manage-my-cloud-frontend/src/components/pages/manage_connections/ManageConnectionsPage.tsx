import Navbar from "../../nav/Navbar";
import React from "react";
import './ManageConnectionsPage.css';

const ManageConnectionsPage = () => {
    return (
        <div>
            <Navbar/>
            <div className={"manage-connections-page-content-grid"}>
                <div className="manage-connections-page-title-container">
                    Manage Connections
                </div>
            </div>
        </div>
    )
};

export default ManageConnectionsPage;