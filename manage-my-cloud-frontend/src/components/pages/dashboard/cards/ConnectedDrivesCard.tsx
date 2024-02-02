import React from 'react';
import './ConnectedDrivesCard.css';
import {AuthData} from "../../../routing/AuthWrapper";
import {buildAxiosRequestWithHeaders} from "../../../helpers/AxiosHelper";
import {CONNECTION_LOGOS, CONNECTION_TITLE} from "../../../../constants/ConnectionConstants";
import LoadingSpinner from "../../../helpers/LoadingSpinner";
import StorageProgressBar from "../storage_bar/StorageProgressBar";

interface DriveInformation {
    displayName: string,
    driveType: string,
    total: number,
    used: number,
}

interface ConnectedDrivesCardProps {
    connectionProvider: string,
}

const CardContainer: React.FC<ConnectedDrivesCardProps> = ({connectionProvider}) => {

    const {user} = AuthData();
    const [driveInformation, setDriveInformation] = React.useState<DriveInformation | null>(null);

    React.useEffect(() => {
        const fetchDriveInformation = async () => {
            const info = await getUserDrives(user, connectionProvider);
            setDriveInformation(info);
        };

        fetchDriveInformation();
    }, [user]);

    if (!driveInformation) {
        return <LoadingSpinner/>
    }

    return (
        <div className="dashboard-connection-item">
            <img src={CONNECTION_LOGOS[connectionProvider]}
                 alt={`Logo for ${connectionProvider}`}/>
            <div className="item-drive-name">
                <h2>{driveInformation.displayName}</h2>
            </div>
            <div className="item-drive-type">
                <h2>Drive Type:</h2>
                <h2>{driveInformation.driveType}</h2>
            </div>
            <div className='item-storage-used'>
                <h2>Storage Used:</h2>
                <h2>{driveInformation.used}GB/{driveInformation.total}GB</h2>
            </div>
            <StorageProgressBar used={driveInformation.used} total={driveInformation.total}/>
        </div>
    );
}

async function getUserDrives(user: any, connectionProvider: string): Promise<DriveInformation> {

    const headers = {
        'Authorization': `Bearer ${user.token}`
    }

    const connectionProviderTitle = CONNECTION_TITLE[connectionProvider];
    const response = await buildAxiosRequestWithHeaders('GET', `/drive-information?email=${user.email}&provider=${connectionProviderTitle}`, headers, {});

    if (!response.data) {
        throw new Error('Invalid response data');
    }
    return {
        displayName: response.data.displayName,
        driveType: response.data.driveType,
        total: response.data.total,
        used: response.data.used,
    };

}

export default CardContainer