import React from 'react';
import './DownloadTermsOfServiceCard.css';
import { Card, CardContent, CardActions, Typography } from '@mui/material';

const DownloadTermsOfServiceCard = () => {
    return (
        <Card sx={{ maxWidth: '46vw', maxHeight: '60vh', marginTop: 5, minHeight: '5vh', display: 'flex', flexDirection: 'column', marginLeft: '51vw'}}>
            <CardContent sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', fontFamily: 'Montserrat', fontSize: '18px' }}>
                <Typography>
                <div className='download-terms-service-title'>Terms of Service</div>
                </Typography>
                <button className="button1">
                    View Online
                </button>
                <button className="button3">
                    Download as PDF
                </button>
            </CardContent>
            <CardActions disableSpacing>
            </CardActions>
        </Card>
    );
}


export default DownloadTermsOfServiceCard;