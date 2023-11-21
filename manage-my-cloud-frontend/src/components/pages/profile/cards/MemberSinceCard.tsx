import React from 'react';
import './MemberSinceCard.css';
import { Card, CardContent, CardActions, Typography } from '@mui/material';

const MemberSinceCard = () => {
    return (
        <Card sx={{ maxWidth: '46vw', maxHeight: '10vh', marginTop: 5, minHeight: '3vh', display: 'flex', flexDirection: 'column', marginLeft: '51vw'}}>
            <CardContent sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center' }}>
                <Typography>
                    <div className='delete-account-card-title'>Member Since</div>
                </Typography>
                <div className ='member-since-skeleton'>
                Member since: November 2023
                </div>
            </CardContent>
            <CardActions disableSpacing>
            </CardActions>
        </Card>
    );
}


export default MemberSinceCard;