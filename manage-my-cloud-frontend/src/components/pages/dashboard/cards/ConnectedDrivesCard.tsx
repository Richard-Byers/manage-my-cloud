import React, {useState} from 'react';
import './ConnectedDrivesCard.css';
import {AuthData} from "../../../routing/AuthWrapper";
import {buildAxiosRequestWithHeaders} from "../../../helpers/AxiosHelper";
import {CONNECTION_LOGOS, CONNECTION_TITLE} from "../../../../constants/ConnectionConstants";
import LoadingSpinner from "../../../helpers/LoadingSpinner";
import StorageProgressBar from "../storage_bar/StorageProgressBar";
import DashboardCardModal from "../../../modals/dashboard/DashboardCardModal";
import {useTranslation} from "react-i18next";
import ErrorModal from "../../../modals/dashboard/ErrorModal";
import { useNavigate } from 'react-router-dom';
interface DriveInformation {
    displayName: string,
    email: string,
    total: number,
    used: number,
}

interface ConnectedDrivesCardProps {
    connectionProvider: string,
    driveEmail: string
}

const CardContainer: React.FC<ConnectedDrivesCardProps> = ({connectionProvider, driveEmail}) => {

    const {user} = AuthData();
    const {t} = useTranslation();
    const [driveInformation, setDriveInformation] = React.useState<DriveInformation | null>(null);
    const [dashboardModal, setShowDashboardModal] = useState(false);
    const [showErrorModal, setShowErrorModal] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');

    const navigate = useNavigate()

    const toggleModal = () => {
        setShowDashboardModal(!dashboardModal);
    }

    const handleCloseErrorModal = () => {
        setShowErrorModal(false);
        navigate('/manage-connections');
    }

    React.useEffect(() => {
        const fetchDriveInformation = async () => {
            try {
                const info = await getUserDrives(user, connectionProvider, driveEmail);
                setDriveInformation(info);
            } catch (error) {
                if (error instanceof Error) {
                    setErrorMessage(error.message);
                    setShowErrorModal(true);
                }
            }
        };
        fetchDriveInformation();
    }, [user, connectionProvider, driveEmail]);

    if (showErrorModal) {
        return (
            <ErrorModal showModal={showErrorModal} handleClose={handleCloseErrorModal} errorMessage={errorMessage} buttonName={t(
                "main.dashboard.manageConnectionsLink"
              )}/>
        );
    }

    if (!driveInformation) {
        return <LoadingSpinner/>
    }

    const convertGBtoTB= (sizeInGB: number) => {
        if (sizeInGB >= 1024) {
            // Convert to TB and format to 2 decimal places
            return `${(sizeInGB / 1024).toFixed(2)}TB`;
        } else {
            return `${sizeInGB}GB`;
        }
    };

    return (
        <>
                        {driveInformation && dashboardModal &&
                <DashboardCardModal setShowModal={setShowDashboardModal} showModal={dashboardModal}
                                    connectionProvider={connectionProvider}
                                    totalStorage={driveInformation.total}
                                    usedStorage={driveInformation.used}
                                    email={driveInformation.email}
                                    driveEmail={driveEmail}/>}
                        <div className="dashboard-connection-item" onClick={toggleModal}
                 id={`${connectionProvider}-connected-item`}>
                <img src={CONNECTION_LOGOS[connectionProvider]}
                     alt={`Logo for ${connectionProvider}`}/>
                <div className="item-drive-name" id={"drive-name"}>
                    <h2>{driveInformation.displayName}</h2>
                </div>
                <div className='item-storage-used' id={"drive-used-storage"}>
                    <h2>{t('main.dashboard.connectedDrivesCard.storageUsed')}:</h2>
                    <h2>{driveInformation.used > 0.0 ? convertGBtoTB(driveInformation.used) : "< 0GB"}/{convertGBtoTB(driveInformation.total)}</h2>
                </div>
                <StorageProgressBar used={driveInformation.used} total={driveInformation.total}/>
            </div>
        </>
    );
}

async function getUserDrives(user: any, connectionProvider: string, driveEmail: string): Promise<DriveInformation> {
    const headers = {
        'Authorization': `Bearer ${user.token}`
    }

    const connectionProviderTitle = CONNECTION_TITLE[connectionProvider];
    const response = await buildAxiosRequestWithHeaders('GET', `/drive-information?email=${user.email}&provider=${connectionProviderTitle}&driveEmail=${driveEmail}`, headers, {});

    if (response.status !== 200) {
        if (connectionProvider === 'GoogleDrive') {
            throw new Error(`Unable to access Google Drive (${driveEmail}). Please re-link your account in Manage Connections.`);
        } else {
            throw new Error(`Unable to access OneDrive (${driveEmail}). Please re-link your account in Manage Connections.`);
        }
    }

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