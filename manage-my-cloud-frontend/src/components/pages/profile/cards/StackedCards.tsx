import React, { useEffect } from "react";
import './StackedCards.css';
import {Avatar} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';

function StackedCards() {
    return (
        <div className="card-container">
            <div className="small-card">
                <div className="card-content">
                </div>
            </div>
            <div className="small-card">
                <div className="card-content">
                    <h1 className="delete-account">Delete Account</h1>
                    <h1 className="request-user-data">Request User Data</h1>
                    <h1 className="terms-of-service">Terms of Service</h1>
                    <div className="button-container">
                        <button className="delete-account-btn">Delete Account</button>
                        <button className="request-data-btn">Request Data</button>
                        <button className="terms-of-service-view-btn">View</button>
                        <button className="terms-of-service-download-btn">Download</button>
                    </div>
                </div>
            </div>
        </div>

    );
}

export default StackedCards;
