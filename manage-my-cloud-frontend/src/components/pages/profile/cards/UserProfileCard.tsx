import React, {useEffect} from "react";
import './UserProfileCard.css';
import {Avatar} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import {AuthData} from "../../../routing/AuthWrapper";

function UserProfileCard() {
    const {user, logout} = AuthData();

    return (
        <div className="card-container">
            <div className="card">
                <div className="card-content">
                    <Avatar sx={{height: '5vw', width: '5vw', display: 'flex', marginLeft: '21vw'}} aria-label="recipe">
                    </Avatar>
                    <div className='user-info'>
                        <div className='user-profile-card-email'>Email Address: {user?.email}</div>
                        <div className='user-profile-card-password'>
                            Password: ********
                            <button className='update-password-btn'>
                                Update Password
                            </button>
                        </div>
                        <div className='user-profile-card-fname'>First Name: {user?.firstName}<EditIcon/></div>
                        <div className='user-profile-card-lname'>Last Name: {user?.lastName} <EditIcon/></div>
                    </div>
                    <button className='save-changes-btn'>Save Changes</button>
                    <button className='save-changes-btn' onClick={logout}>Log Out</button>
                </div>
            </div>
        </div>
    );
}

export default UserProfileCard;
