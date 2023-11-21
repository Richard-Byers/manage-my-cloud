import React from 'react';
import { Card, CardHeader, CardContent, CardActions, Avatar, Typography } from '@mui/material';
import './UserProfileCard.css';
import EditIcon from '@mui/icons-material/Edit';

const UserProfileCard = () => {
    return (
        <Card sx={{ maxWidth: '46vw', maxHeight: '60vh', marginTop: 10, minHeight: '60vh', display: 'flex', flexDirection: 'column', marginLeft: '2.5vw'}}>
            <div style={{ display: 'flex', justifyContent: 'center' }}>
                <CardHeader
                    avatar={
                        <Avatar sx={{ height: '10vw', width: '10vw', position:'relative', display:'flex'}} aria-label="recipe">
                        </Avatar>
                    }
                />
            </div>
            <CardContent>
                <Typography>
                    <div className='user-profile-card-email'>Email Address: johndoe@gmail.com</div>
                </Typography>
                <Typography>
                    <div className='user-profile-card-password'>Password: ********</div>
                </Typography>
                <Typography>
                    <div className='user-profile-card-fname'>First Name: John</div>
                </Typography>
                <Typography>
                    <div className='user-profile-card-lname'>Last Name: Doe</div>
                </Typography>
                <EditIcon sx={{display: 'flex', marginLeft:23.5, marginTop:-8}}></EditIcon>
                <EditIcon sx={{display: 'flex', marginLeft:25.5, marginTop:-13}}></EditIcon>
                <button className="button4">
                    Update
                </button>
            </CardContent>
            <CardActions disableSpacing>
            </CardActions>
        </Card>
    );
}

export default UserProfileCard;