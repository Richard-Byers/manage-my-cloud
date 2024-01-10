import React from "react";
import './DashboardPageButtons.css';

const DashboardPageButtons = () => {

    return (
        <div className={"dashboard-button-container"}>
            <button className={"dashboard-button"}>Refresh Drives</button>
            <button className={"dashboard-button"}>Delete Duplicates</button>
            <button className={"dashboard-button"}>Delete Recommended</button>
        </div>
    )
};

export default DashboardPageButtons;