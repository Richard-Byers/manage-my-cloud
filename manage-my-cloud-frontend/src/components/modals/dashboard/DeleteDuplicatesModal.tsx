import React, {useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import LoadingSpinner from "../../helpers/LoadingSpinner";
import CloseIcon from "@mui/icons-material/Close";
import {getFileType} from "../../../constants/FileTypesConstants";
import {buildAxiosRequestWithHeaders} from "../../helpers/AxiosHelper";
import {AuthData} from "../../routing/AuthWrapper";
import {Success} from "../../helpers/Success";
import {Failure} from "../../helpers/Failure";
import {NothingFoundRecommendations, NothingFoundDuplicates} from "../../helpers/NothingFound";

interface DeleteDuplicatesProps {
    data: FileNode;
    connectionProvider: string;
    setShowModal: (arg0: boolean) => void;
    driveEmail: string;
    setHaveFilesBeenDeleted: (arg0: boolean) => void;
    setShowDeletionModal: (arg0: boolean) => void;
    deleteDuplicatesClicked: boolean; 
    setDeleteDuplicatesClicked: (arg0: boolean) => void; 
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

const DeleteDuplicatesModal: React.FC<DeleteDuplicatesProps> = ({
    data,
    connectionProvider,
    setShowModal,
    driveEmail,
    setHaveFilesBeenDeleted,
    setShowDeletionModal, 
    deleteDuplicatesClicked, 
    setDeleteDuplicatesClicked 
                                                                                  }) => {

    const {user} = AuthData();
    const {t} = useTranslation();

    const [driveData, setDriveData] = useState<FileNode>();
    const [loading, setLoading] = useState(false);
    const [filesToBeDeleted, setFilesToBeDeleted] = useState<FilesToBeDeleted>({children: []});
    const [successfulDeletionMessage, setSuccessfulDeletionMessage] = useState("");
    const [unsuccessfulDeletionMessage, setUnsuccessfulDeletionMessage] = useState("");
    const [selectAll, setSelectAll] = useState(false);
    const [showDuplicateModal, setShowDuplicateModal] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [showErrorModal, setShowErrorModal] = useState(false);

    useEffect(() => {
        if (deleteDuplicatesClicked) {
            const fetchDriveData = async () => {
                const info = await getDuplicates(user, connectionProvider);
                setDriveData(info);
            };

            fetchDriveData();
        }
    }, [deleteDuplicatesClicked]);
    

    const handleSelectAll = (event: React.ChangeEvent<HTMLInputElement>) => {
        setSelectAll(event.target.checked);
        if (event.target.checked) {
            setFilesToBeDeleted({children: [...driveData?.children || []]});
        } else {
            setFilesToBeDeleted({children: []});
        }
    }

    const closeModal = () => {
        setShowModal(false);
        setShowDeletionModal(false);
        setDeleteDuplicatesClicked(false);
        setSelectAll(false);
        setFilesToBeDeleted({children: []});
        setSuccessfulDeletionMessage("");
        setUnsuccessfulDeletionMessage("");
    };

    const closeDuplicateModal = () => {
        setDeleteDuplicatesClicked(false);
        setShowDeletionModal(false);
        setSelectAll(false);
        setFilesToBeDeleted({children: []});
        setSuccessfulDeletionMessage("");
        setUnsuccessfulDeletionMessage("");
    };

    const stopPropagation = (e: React.MouseEvent<HTMLDivElement>) => {
        e.stopPropagation();
    };

    async function deleteRecommendedFiles(user: any, connectionProvider: string, filesToDelete: FilesToBeDeleted, driveEmail: string): Promise<void> {

        const headers = {
            'Authorization': `Bearer ${user.token}`
        }

        setLoading(true);
        await buildAxiosRequestWithHeaders('POST', `/delete-recommended?email=${user.email}&provider=${connectionProvider}&driveEmail=${driveEmail}`, headers, filesToDelete)
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

    async function getDuplicates(user: any, connectionProvider: string): Promise<FileNode | undefined> {
        const headers = {
            'Authorization': `Bearer ${user.token}`
        }
        try {
            setLoading(true);
            const response = await buildAxiosRequestWithHeaders('POST', `/get-duplicates?email=${user.email}&provider=${connectionProvider}&driveEmail=${driveEmail}`, headers, data);
    
            if (response.status !== 200) {
                throw new Error(`Request failed with status code ${response.status}`);
            }
    
            return response.data;
        } catch (error) {
            setLoading(false);
            setError("");
            setShowErrorModal(true);
        } finally {
            setLoading(false);
        }
    }

    return (
            <div className={"modal-overlay"} onClick={closeDuplicateModal}>
                {loading ? <LoadingSpinner/> :
                    <div className={"modal"} onClick={stopPropagation}>

                        
                        {/*If items we're deleted then render successfulDeletionMessage*/}
                    {successfulDeletionMessage !== "" &&
                        <div className={"recommended-file-button-container"}>
                            <p id={"deletion-success-message"}>{successfulDeletionMessage}</p>
                            <Success/>
                            <button className={"dashboard-button"} onClick={closeModal} id={"success-deletion-close-button"}>
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
                            <div className={"deletion-duplicates-container"}>
                                {driveData && driveData?.children.length > 0 ?
                                    <>
                                        <h2> {driveData?.children.length} {t('main.dashboard.deletionModals.deleteDuplicates.title')}</h2>
                                        <p
                                            className={"deletion-duplicates-description"}>{t('main.dashboard.deletionModals.deleteDuplicates.mainText')}</p>
                                        <p className={"deletion-duplicates-select-all-description"}>
                                            Select All
                                            <input
                                                className={"dashboard-page-buttons-select-all-checkbox"}
                                                type="checkbox"
                                                onChange={handleSelectAll}/>
                                        </p>
                                    </> : <NothingFoundDuplicates/>}
                            </div>
                            {driveData != null && driveData?.children.length > 0 ?
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
                            {driveData && driveData?.children.length <= 0 || driveData === undefined
                                ?
                                <div className={"duplicated-file-button-container"}>
                                    <button className={"dashboard-button"} onClick={closeDuplicateModal}>
                                        {t('main.dashboard.deletionModals.deleteDuplicates.closeRecommendation')}
                                    </button>
                                </div>
                                :
                                <div className={"duplicated-file-button-container"}>
                                    <button className={"dashboard-button"} onClick={() => {
                                        deleteRecommendedFiles(user, connectionProvider, filesToBeDeleted, driveEmail)
                                    }}>
                                        {t('main.dashboard.deletionModals.deleteRecommended.deleteButton')}
                                    </button>
                                </div>}
    
                        </div>
                        }
                    </div>
                }
            </div>
    )
        };
    
    export default DeleteDuplicatesModal;