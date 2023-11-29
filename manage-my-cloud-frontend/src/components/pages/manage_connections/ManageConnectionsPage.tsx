import Navbar from "../../nav/Navbar";
import React from "react";
import "./ManageConnectionsPage.css";
import AddDriveBtn from "./AddDrive";
import CenteredContainer from "./CenteredContainer";

import DropboxLogo from "../../images/dashboard/DropboxLogo.png";
import OneDriveLogo from "../../images/dashboard/OneDriveLogo.png";
import GoogleDriveLogo from "../../images/dashboard/GoogleDriveLogo1.png";

const ManageConnectionsPage = () => {
    const handleDriveAdded = (drive: { id: number; name: string; email: string }) => {
        console.log("Drive added:", drive);
    };

    return (
        <div className="app-container">
            <Navbar />
            <CenteredContainer>
                <div className="addDriveContainer">
                    <AddDriveBtn onDriveAdded={handleDriveAdded} />
                </div>
            </CenteredContainer>
                <div className="item-container">
                    <h2 style={{ color: "#fff" }}>Supported Services</h2>
                    <hr style={{ backgroundColor: "#fff", width: "200%", margin: "0 0" }} />
                    <div className="icon-container">
                        <img className="icon" src={GoogleDriveLogo} alt="Icon 1" />
                        <img className="icon" src={OneDriveLogo} alt="Icon 2" />
                        <img className="icon" src={DropboxLogo} alt="Icon 3" />
                    </div>
                </div>

        </div>
    );
};

export default ManageConnectionsPage;