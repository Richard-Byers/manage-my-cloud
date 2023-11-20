import React from 'react';
import { Card, CardHeader, CardContent, CardActions, Avatar, Typography } from '@mui/material';
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
                <Typography variant="h5" component="div" sx={{ mb: 5 }}>
                    Email Address: user@example.com
                </Typography>
                <Typography variant="h5" component="div" sx={{ mb: 5 }}>
                    Password: ******
                </Typography>
                <Typography variant="h5" component="div" sx={{ mb: 5 }}>
                    First Name: John
                </Typography>
                <Typography variant="h5" component="div" sx={{ mb: 5 }}>
                    Last Name: Doe
                </Typography>
            </CardContent>
            <CardActions disableSpacing>
            </CardActions>
        </Card>
    );
}

export default UserProfileCard;