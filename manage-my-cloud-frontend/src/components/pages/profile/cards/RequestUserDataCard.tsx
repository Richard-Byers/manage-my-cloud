import React from 'react';
import './RequestUserData.css';
import { Card, CardContent, CardActions, Typography } from '@mui/material';

const RequestUserDataCard = () => {
    return (
        <Card sx={{ maxWidth: '46vw', maxHeight: '60vh', marginTop: 5, minHeight: '5vh', display: 'flex', flexDirection: 'column', marginLeft: '51vw'}}>
            <CardContent sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center' }}>
                <Typography variant="h4" component="div" sx={{ mb: 5, textAlign: 'center' }}>
                    Request User Data
                </Typography>
                <button className="button2">
                    Request User Data
                </button>
            </CardContent>
            <CardActions disableSpacing>
            </CardActions>
        </Card>
    );
}


export default RequestUserDataCard;