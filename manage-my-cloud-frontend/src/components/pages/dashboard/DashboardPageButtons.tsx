import React, {useEffect, useState} from "react";
import './DashboardPageButtons.css';
import {useTranslation} from "react-i18next";
import LoadingSpinner from "../../helpers/LoadingSpinner";
import CloseIcon from "@mui/icons-material/Close";
import {getFileType} from "../../../constants/FileTypesConstants";
import {buildAxiosRequestWithHeaders} from "../../helpers/AxiosHelper";
import {AuthData} from "../../routing/AuthWrapper";
import {Success} from "../../helpers/Success";
import {Failure} from "../../helpers/Failure";
import {NothingFound} from "../../helpers/NothingFound";

interface DashboardPageButtonsProps {
    data: FileNode;
    connectionProvider: string;
    setSowModal: (arg0: boolean) => void;
}

interface FilesToBeDeleted {
    children: FileNode[];
}

interface FileNode {
    name: string;
    type: string;
    webUrl: string;
    id: string;
    children: FileNode[];
}

interface FileNodeProps {
    node: FileNode;
    setFilesToBeDeleted: (arg0: FilesToBeDeleted) => void;
    filesToBeDeleted: FilesToBeDeleted;
    selectAll: boolean;
}

interface FileTreeProps {
    data: FileNode;
    setFilesToBeDeleted: (arg0: FilesToBeDeleted) => void;
    filesToBeDeleted: FilesToBeDeleted;
    selectAll: boolean;
}

const FileNode: React.FC<FileNodeProps> = ({node, setFilesToBeDeleted, filesToBeDeleted, selectAll}) => {

    const handleCheckboxChange = (event: React.ChangeEvent<HTMLInputElement>, node: FileNode) => {
        if (event.target.checked) {
            setFilesToBeDeleted({children: [...filesToBeDeleted.children, node]});
        } else {
            setFilesToBeDeleted({children: filesToBeDeleted.children.filter((file) => file !== node)});
        }
    }

    const isFileSelected = filesToBeDeleted.children.includes(node);

    if (node.type !== "Folder") {
        return (
            <div className={"dashboard-card-modal-file-container"}>
                <img className="svg-icon" src={getFileType(node.name.split('.').pop() as string)}
                     alt={`File Type`} onClick={() => {
                    window.open(node.webUrl, "_blank");
                }}/>
                <span>{node.name}</span>
                <input className={"dashboard-page-buttons-checkbox"} type="checkbox"
                       checked={isFileSelected}
                       onChange={(event) => handleCheckboxChange(event, node)}/>
            </div>
        );
    }

    return (
        <>
            {node.children.map(childNode => (
                <FileNode key={childNode.id} node={childNode} setFilesToBeDeleted={setFilesToBeDeleted}
                          filesToBeDeleted={filesToBeDeleted} selectAll={selectAll}/>
            ))}
        </>
    );
};

const FileTree: React.FC<FileTreeProps> = ({data, setFilesToBeDeleted, filesToBeDeleted, selectAll}) => {
    return <FileNode node={data} setFilesToBeDeleted={setFilesToBeDeleted} filesToBeDeleted={filesToBeDeleted}
                     selectAll={selectAll}/>;
};

const DashboardPageButtons: React.FC<DashboardPageButtonsProps> = ({
                                                                       data,
                                                                       connectionProvider,
                                                                       setSowModal
                                                                   }) => {
    const {user} = AuthData();
    const {t} = useTranslation();

    const [driveData, setDriveData] = useState<FileNode>();
    const [loading, setLoading] = useState(false);
    const [showDeletionModal, setShowDeletionModal] = useState(false);
    const [deleteRecommendedClicked, setDeleteRecommendedClicked] = useState(false);
    const [filesToBeDeleted, setFilesToBeDeleted] = useState<FilesToBeDeleted>({children: []});
    const [successfulDeletionMessage, setSuccessfulDeletionMessage] = useState("");
    const [unsuccessfulDeletionMessage, setUnsuccessfulDeletionMessage] = useState("");
    const [selectAll, setSelectAll] = useState(false);

    useEffect(() => {
        if (deleteRecommendedClicked) {
            const fetchDriveData = async () => {
                const info = await getRecommendedDeletions(user, connectionProvider);
                setDriveData(info);
                setShowDeletionModal(true); // Move setShowDeletionModal here
            };

            fetchDriveData();
        }
    }, [deleteRecommendedClicked]);

    const handleSelectAll = (event: React.ChangeEvent<HTMLInputElement>) => {
        setSelectAll(event.target.checked);
        if (event.target.checked) {
            setFilesToBeDeleted({children: [...driveData?.children || []]});
        } else {
            setFilesToBeDeleted({children: []});
        }
    }

    const handleDeleteRecommended = async () => {
        setDeleteRecommendedClicked(true);
    }

    const closeModal = () => {
        setSowModal(false);
        setShowDeletionModal(false);
        setDeleteRecommendedClicked(false);
        setFilesToBeDeleted({children: []})
        setSuccessfulDeletionMessage("");
        setUnsuccessfulDeletionMessage("");
    };

    const closeRecommendationModal = () => {
        setShowDeletionModal(false);
        setDeleteRecommendedClicked(false);
        setFilesToBeDeleted({children: []})
        setSuccessfulDeletionMessage("");
        setUnsuccessfulDeletionMessage("");
    };

    const stopPropagation = (e: React.MouseEvent<HTMLDivElement>) => {
        e.stopPropagation();
    };

    async function getRecommendedDeletions(user: any, connectionProvider: string): Promise<FileNode> {

        const headers = {
            'Authorization': `Bearer ${user.token}`
        }

        setLoading(true);
        const response = await buildAxiosRequestWithHeaders('POST', `/recommend-deletions?email=${user.email}`, headers, data)

        if (!response.data) {
            setLoading(false);
            throw new Error('Invalid response data');
        }
        setLoading(false);
        return response.data;
    }

    async function deleteRecommendedFiles(user: any, connectionProvider: string, filesToDelete: FilesToBeDeleted): Promise<void> {

        const headers = {
            'Authorization': `Bearer ${user.token}`
        }

        setLoading(true);
        await buildAxiosRequestWithHeaders('POST', `/delete-recommended?email=${user.email}&provider=${connectionProvider}`, headers, filesToDelete)
            .then((response) => {
                setLoading(false);
                setSuccessfulDeletionMessage(`Successfully deleted ${response.data.filesDeleted} file(s) from your drive.`);
            })
            .catch((error) => {
                setLoading(false);
                setUnsuccessfulDeletionMessage("Item(s) were not deleted");
            });

        setLoading(false);
    }

    return (
        <div className={"dashboard-button-container"}>
            <button className={"dashboard-button"}>{t('main.dashboard.dashboardPageButtons.deleteDuplicates')}</button>
            <button className={"dashboard-button"}
                    onClick={(handleDeleteRecommended)}>{t('main.dashboard.dashboardPageButtons.deleteRecommended')}</button>
            {showDeletionModal &&
                <div className={"modal-overlay"} onClick={closeRecommendationModal}>
                    {loading ? <LoadingSpinner/> :
                        <div className={"modal"} onClick={stopPropagation}>

                            <button className={"modal-close-button"} onClick={closeRecommendationModal}><CloseIcon
                                className="svg_icons"/>
                            </button>

                            {/*If items we're deleted then render successfulDeletionMessage*/}
                            {successfulDeletionMessage !== "" &&
                                <div className={"recommended-file-button-container"}>
                                    <p>{successfulDeletionMessage}</p>
                                    <Success/>
                                    <button className={"dashboard-button"} onClick={closeModal}>
                                        {t('main.dashboard.deletionModals.deleteRecommended.closeRecommendation')}
                                    </button>
                                </div>
                            }

                            {/*If items weren't deleted then render unsuccessfulDeletionMessage*/}
                            {unsuccessfulDeletionMessage !== "" &&
                                <div className={"recommended-file-button-container"}>
                                    <p>{unsuccessfulDeletionMessage}</p>
                                    <Failure/>
                                    <p></p>
                                    <button className={"dashboard-button"} onClick={closeModal}>
                                        {t('main.dashboard.deletionModals.deleteRecommended.closeRecommendationTryAgain')}
                                    </button>
                                </div>
                            }


                            {successfulDeletionMessage === "" && unsuccessfulDeletionMessage === "" &&
                                <div className={"dashboard-page-buttons-modal-grid"}>
                                    <div className={"deletion-recommendation-container"}>
                                        {driveData && driveData?.children.length > 0 ?
                                            <>
                                                <h2> {driveData?.children.length} {t('main.dashboard.deletionModals.deleteRecommended.title')}</h2>
                                                <p
                                                    className={"deletion-recommendation-description"}>{t('main.dashboard.deletionModals.deleteRecommended.mainText')}</p>
                                                <p className={"deletion-recommendation-select-all-description"}>
                                                    Select All
                                                    <input
                                                        className={"dashboard-page-buttons-select-all-checkbox"}
                                                        type="checkbox"
                                                        onChange={handleSelectAll}/>
                                                </p>
                                            </> : <NothingFound/>}
                                    </div>

                                    {driveData && driveData?.children.length > 0 ?
                                        <div className={"deletion-recommendation-file-container"}>
                                            <div className={"deletion-recommendation-files-grid"}>
                                                {driveData ?
                                                    <FileTree data={driveData}
                                                              setFilesToBeDeleted={setFilesToBeDeleted}
                                                              filesToBeDeleted={filesToBeDeleted}
                                                              selectAll={selectAll}/> : "No files found"}
                                            </div>
                                        </div> : null
                                    }

                                    {driveData && driveData?.children.length <= 0
                                        ?
                                        <div className={"recommended-file-button-container"}>
                                            <button className={"dashboard-button"} onClick={closeModal}>
                                                {t('main.dashboard.deletionModals.deleteRecommended.closeRecommendation')}
                                            </button>
                                        </div>
                                        :
                                        <div className={"recommended-file-button-container"}>
                                            <button className={"dashboard-button"} onClick={() => {
                                                deleteRecommendedFiles(user, connectionProvider, filesToBeDeleted)
                                            }}>
                                                {t('main.dashboard.deletionModals.deleteRecommended.deleteButton')}
                                            </button>
                                        </div>}

                                </div>
                            }
                        </div>
                    }
                </div>
            }
        </div>
    )
};

export default DashboardPageButtons;