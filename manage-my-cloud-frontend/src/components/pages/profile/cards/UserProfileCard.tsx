import React from 'react';
import './UserProfileCard.css';
import EditIcon from '@mui/icons-material/Edit';
import Avatar from '@mui/material/Avatar';

const UserProfileCard = () => {
    return (
        <div className='upc'>
            <div className='gradient'> </div>
                <div className="title">
                    Personal Details
                </div>
            <div className="profile-avatar">
                <Avatar sx={{ width: '20vw', height: '20vh' }}></Avatar>
            </div>
                    <div className="email">
                        Email
                    </div>
                    <div className="profile-email">
                        remy.sharp@gmail.com
                        <EditIcon></EditIcon>
                    </div>
                    <div className="password">
                        Password
                    </div>
                    <div className="profile-password">
                        ********
                        <EditIcon></EditIcon>
                    </div>
                    <div className="forename">
                        First Name
                    </div>
                    <div className="profile-forename">
                        Remy
                        <EditIcon></EditIcon>
                    </div>
                    <div className="surname">
                        Last Name
                    </div>
                    <div className="profile-surname">
                        Sharp
                        <EditIcon></EditIcon>
                    </div>
            </div>
    );
}

export default UserProfileCard;