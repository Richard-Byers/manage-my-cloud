import React, {useState} from 'react';
import './ConnectedDrivesCard.css';
import {AuthData} from "../../../routing/AuthWrapper";
import {buildAxiosRequestWithHeaders} from "../../../helpers/AxiosHelper";
import {CONNECTION_LOGOS, CONNECTION_TITLE} from "../../../../constants/ConnectionConstants";
import LoadingSpinner from "../../../helpers/LoadingSpinner";
import StorageProgressBar from "../storage_bar/StorageProgressBar";
import DashboardCardModal from "../../../modals/dashboard/DashboardCardModal";
import {useTranslation} from "react-i18next";

interface DriveInformation {
    displayName: string,
    email: string,
    total: number,
    used: number,
}

interface ConnectedDrivesCardProps {
    connectionProvider: string,
}

const CardContainer: React.FC<ConnectedDrivesCardProps> = ({connectionProvider}) => {

    const {user} = AuthData();
    const {t} = useTranslation();
    const [driveInformation, setDriveInformation] = React.useState<DriveInformation | null>(null);
    const [dashboardModal, setShowDashboardModal] = useState(false);

    const toggleModal = () => {
        setShowDashboardModal(!dashboardModal);
    }

    React.useEffect(() => {
        const fetchDriveInformation = async () => {
            const info = await getUserDrives(user, connectionProvider);
            setDriveInformation(info);
        };

        fetchDriveInformation();
    }, [user, connectionProvider]);

    if (!driveInformation) {
        return <LoadingSpinner/>
    }

    return (
        <>
            {driveInformation && dashboardModal &&
                <DashboardCardModal setShowModal={setShowDashboardModal} showModal={dashboardModal}
                                    connectionProvider={connectionProvider}
                                    totalStorage={driveInformation.total}
                                    usedStorage={driveInformation.used}
                                    email={driveInformation.email}/>}
            <div className="dashboard-connection-item" onClick={toggleModal}>
                <img src={CONNECTION_LOGOS[connectionProvider]}
                     alt={`Logo for ${connectionProvider}`}/>
                <div className="item-drive-name">
                    <h2>{driveInformation.displayName}</h2>
                </div>
                <div className='item-storage-used'>
                    <h2>{t('main.dashboard.connectedDrivesCard.storageUsed')}:</h2>
                    <h2>{driveInformation.used > 0.0 ? driveInformation.used : "< 0"}GB/{driveInformation.total}GB</h2>
                </div>
                <StorageProgressBar used={driveInformation.used} total={driveInformation.total}/>
            </div>
        </>
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
        email: response.data.email,
        total: response.data.total,
        used: response.data.used,
    };

}

export default CardContainer