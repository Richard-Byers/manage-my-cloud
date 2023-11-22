import React, { useEffect } from "react";
import './UserProfileCard.css';
import {Avatar} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';

function UserProfileCard() {
    return (
        <div className="card-container">
            <div className="card">
                    <div className="card-content">
                        <Avatar sx={{ height: '5vw', width: '5vw', display:'flex', marginLeft:'21vw'}} aria-label="recipe">
                        </Avatar>
                        <div className='user-info'>
                        <div className='user-profile-card-email'>Email Address: johndoe@gmail.com</div>
                        <div className='user-profile-card-password'>Password: ********</div>
                        <div className='user-profile-card-fname'>First Name: John</div>
                        <div className='user-profile-card-lname'>Last Name: Doe</div>
                        </div>
                        <EditIcon sx={{display: 'flex', marginLeft:23.5, marginTop:-8}}></EditIcon>
                        <EditIcon sx={{display: 'flex', marginLeft:25.5, marginTop:-12}}></EditIcon>
                        <button className='update-password-btn'>Update Password</button>
                        <button className='save-changes-btn'>Save Changes</button>
                    </div>
            </div>
        </div>
    );
}

export default UserProfileCard;
