import React, {useState} from "react";
import './DashboardPageButtons.css';
import {useTranslation} from "react-i18next";
import DeletionRecommendationsModal from "../../modals/dashboard/DeletionRecommendationsModal";
import {CONNECTION_TITLE} from "../../../constants/ConnectionConstants";

interface DashboardPageButtonsProps {
    data: FileNode;
    connectionProvider: string;
    setShowModal: (arg0: boolean) => void;
    driveEmail: string;
    setHaveFilesBeenDeleted: (arg0: boolean) => void;
}

interface Email {
    emailSubject: string;
    receivedDate: number;
    webUrl: string;
}

interface FileNode {
    name: string;
    type: string;
    id: string;
    webUrl: string;
    thumbnailUrl: string;
    children: FileNode[];
    emails: Email[];
    googlePhotos: FileNode[];
}

const DashboardPageButtons: React.FC<DashboardPageButtonsProps> = ({
                                                                       data,
                                                                       connectionProvider,
                                                                       setShowModal,
                                                                       driveEmail,
                                                                       setHaveFilesBeenDeleted
                                                                   }) => {
    const {t} = useTranslation();

    const [showDeletionModal, setShowDeletionModal] = useState(false);
    const [deleteRecommendedClicked, setDeleteRecommendedClicked] = useState(false);

    const handleDeleteRecommended = () => {
        setDeleteRecommendedClicked(true);
        setShowDeletionModal(true);
    }

    const hideDeletionModal = () => {
        setShowDeletionModal(false);
        setDeleteRecommendedClicked(false);
    }

    return (
        <div className={"dashboard-button-container"}>
            <button className={"dashboard-button"}>{t('main.dashboard.dashboardPageButtons.deleteDuplicates')}</button>
            <button className={"dashboard-button"}
                    onClick={(handleDeleteRecommended)}>{t('main.dashboard.dashboardPageButtons.deleteRecommended')}</button>
            {showDeletionModal &&
                <DeletionRecommendationsModal data={data}
                                              connectionProvider={CONNECTION_TITLE[connectionProvider]}
                                              setShowModal={setShowModal}
                                              driveEmail={driveEmail}
                                              setHaveFilesBeenDeleted={setHaveFilesBeenDeleted}
                                              setShowDeletionModal={hideDeletionModal}
                                              deleteRecommendedClicked={deleteRecommendedClicked}
                                              setDeleteRecommendedClicked={setDeleteRecommendedClicked}
                />
            }
        </div>
    )
};

export default DashboardPageButtons;