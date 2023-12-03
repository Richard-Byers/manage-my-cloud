import Navbar from "../../nav/Navbar";
import React from "react";
import HorizontalCards from "./cards/HorizontalCards";
import './DashboardPage.css';
import {useNavigate} from "react-router-dom";
import {ROUTES} from "../../../constants/RouteConstants";

const ButtonComponent = () => {
    return (
        <div className='delete-btns'>
            <button className="delete-btns-button">
                Delete Duplicates
            </button>
            <button className="delete-btns-button">
                Delete Recommended
            </button>
        </div>
    );
};

const DashboardPage = () => {

    const navigate = useNavigate();

    function navigateToManageConnections() {
        navigate(ROUTES.MANAGE_CONNECTIONS);
    }

    return (
        <div>
            <Navbar/>
            <div className="dashboard-page-title">
                <h1>Dashboard</h1>
            </div>
            <HorizontalCards/>
            <button className="refresh-drives-btn">
                Refresh Drives
            </button>
            <ButtonComponent></ButtonComponent>
            <div style={{
                borderTop: '3px solid white',
                textAlign: 'center',
                width: '50%',
                margin: '4% auto',
            }}>
                <div className="text-under-line"
                     style={{width: '100%', textAlign: 'center', display: 'flex', flexDirection: 'column'}}>
                    <p>If you would like to add or remove drives</p>
                    <p style={{marginTop: '-1vh'}}>please head to <button className={'manage-connections'}
                                                                          onClick={navigateToManageConnections}>Manage
                        Connections</button></p>
                </div>
            </div>
        </div>

    )
};

export default DashboardPage;