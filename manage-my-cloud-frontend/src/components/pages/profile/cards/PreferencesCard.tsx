import React from 'react';
import './PreferencesCard.css';
import { Card, CardContent, CardActions, Typography } from '@mui/material';

const PreferencesCard = () => {
    return (
        <Card sx={{ maxWidth: '46vw', maxHeight: '60vh', marginTop: 5, minHeight: '5vh', display: 'flex', flexDirection: 'column', marginLeft: '51vw'}}>
            <CardContent sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center' }}>
                <Typography>
                    <div className='delete-account-card-title'>Preferences</div>
                </Typography>
                <button className="button6">
                    Preferences
                </button>
            </CardContent>
            <CardActions disableSpacing>
            </CardActions>
        </Card>
    );
}


export default PreferencesCard;