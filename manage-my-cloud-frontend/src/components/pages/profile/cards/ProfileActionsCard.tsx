import React from "react";
import './ProfileActions.css';
import './Card.css';

function ProfileActionsCard() {
    return (
        <div className="small-card">
            <div className="card-content">
                <div className={"card-title"}>
                    Account Actions
                </div>
                <button className="actions-button">Delete Account</button>
                <button className="actions-button">Request Data</button>
                <button className="actions-button">View Terms of Service</button>
                <button className="actions-button">Download Terms of Service</button>
            </div>
        </div>
    );
}

export default ProfileActionsCard;
