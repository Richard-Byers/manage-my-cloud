import LoadingSpinner from "../../helpers/LoadingSpinner";
import CloseIcon from "@mui/icons-material/Close";
import {Success} from "../../helpers/Success";
import {Failure} from "../../helpers/Failure";
import {NothingFoundRecommendations} from "../../helpers/NothingFound";
import ArticleIcon from "@mui/icons-material/Article";
import EmailIcon from "@mui/icons-material/Email";
import React, {useEffect, useState} from "react";
import {AuthData} from "../../routing/AuthWrapper";
import {useTranslation} from "react-i18next";
import {buildAxiosRequestWithHeaders} from "../../helpers/AxiosHelper";
import {getFileType} from "../../../constants/FileTypesConstants";

interface DeletionRecommendationModalProps {
    data: FileNode;
    connectionProvider: string;
    setShowModal: (arg0: boolean) => void;
    driveEmail: string;
    setHaveFilesBeenDeleted: (arg0: boolean) => void;
    setShowDeletionModal: (arg0: boolean) => void;
    deleteRecommendedClicked: boolean;
    setDeleteRecommendedClicked: (arg0: boolean) => void;
}

interface FilesToBeDeleted {
    children: FileNode[];
    emails: Email[];
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
}

interface FileNodeProps {
    node: FileNode;
    setFilesToBeDeleted: (arg0: (prevState: FilesToBeDeleted) => FilesToBeDeleted) => void;
    filesToBeDeleted: FilesToBeDeleted;
    selectAll: boolean;
}

interface FileNodePropsEmails {
    node: Email[];
    setFilesToBeDeleted: (arg0: (prevState: FilesToBeDeleted) => FilesToBeDeleted) => void;
    filesToBeDeleted: FilesToBeDeleted;
    selectAll: boolean;
}

interface FileTreeProps {
    data: FileNode;
    setFilesToBeDeleted: (arg0: (prevState: FilesToBeDeleted) => FilesToBeDeleted) => void;
    filesToBeDeleted: FilesToBeDeleted;
    selectAll: boolean;
}

interface FileTreePropsEmails {
    data: Email[];
    setFilesToBeDeleted: (arg0: (prevState: FilesToBeDeleted) => FilesToBeDeleted) => void;
    filesToBeDeleted: FilesToBeDeleted;
    selectAll: boolean;
}

const FileNode: React.FC<FileNodeProps> = ({node, setFilesToBeDeleted, filesToBeDeleted, selectAll}) => {

    const handleCheckboxChange = (event: React.ChangeEvent<HTMLInputElement>, node: FileNode) => {
        if (event.target.checked) {
            setFilesToBeDeleted(prevState => ({...prevState, children: [...prevState.children, node]}));
        } else {
            setFilesToBeDeleted(prevState => ({
                ...prevState,
                children: prevState.children.filter((file) => file !== node)
            }));
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

const FileNodeEmails: React.FC<FileNodePropsEmails> = ({node, setFilesToBeDeleted, filesToBeDeleted, selectAll}) => {
    const handleCheckboxChange = (event: React.ChangeEvent<HTMLInputElement>, email: Email) => {
        if (event.target.checked) {
            setFilesToBeDeleted(prevState => ({...prevState, emails: [...prevState.emails, email]}));
        } else {
            setFilesToBeDeleted(prevState => ({...prevState, emails: prevState.emails.filter(e => e !== email)}));
        }
    }

    return (
        <>
            {node.map(email => (
                <div className={"dashboard-card-modal-file-container"}>
                    {email.emailSubject === "" ?
                        <span>No Subject</span>
                        :
                        <span>
                                {email.emailSubject.length < 45 ? email.emailSubject :
                                    `${email.emailSubject.substring(0, 45)}...`
                                }
                            </span>
                    }
                    <input className={"dashboard-page-buttons-checkbox"} type="checkbox"
                           checked={filesToBeDeleted.emails.includes(email)}
                           onChange={(event) => handleCheckboxChange(event, email)}/>
                </div>
            ))}
        </>
    );
};

const FileTree: React.FC<FileTreeProps> = ({data, setFilesToBeDeleted, filesToBeDeleted, selectAll}) => {
    return <FileNode node={data} setFilesToBeDeleted={setFilesToBeDeleted} filesToBeDeleted={filesToBeDeleted}
                     selectAll={selectAll}/>;
};

const FileTreeEmails: React.FC<FileTreePropsEmails> = ({data, setFilesToBeDeleted, filesToBeDeleted, selectAll}) => {
    return <FileNodeEmails node={data} setFilesToBeDeleted={setFilesToBeDeleted} filesToBeDeleted={filesToBeDeleted}
                           selectAll={selectAll}/>;
};

const DeletionRecommendationsModal: React.FC<DeletionRecommendationModalProps> = ({
                                                                                      data,
                                                                                      connectionProvider,
                                                                                      setShowModal,
                                                                                      driveEmail,
                                                                                      setHaveFilesBeenDeleted,
                                                                                      setShowDeletionModal,
                                                                                      deleteRecommendedClicked,
                                                                                      setDeleteRecommendedClicked
                                                                                  }) => {

    const {user} = AuthData();
    const {t} = useTranslation();

    const [driveData, setDriveData] = useState<FileNode>();
    const [loading, setLoading] = useState(false);
    const [filesToBeDeleted, setFilesToBeDeleted] = useState<FilesToBeDeleted>({
        children: [],
        emails: []
    });
    const [successfulDeletionMessage, setSuccessfulDeletionMessage] = useState("");
    const [unsuccessfulDeletionMessage, setUnsuccessfulDeletionMessage] = useState("");
    const [selectAll, setSelectAll] = useState(false);
    const [showEmails, setShowEmails] = React.useState(false);
    const [showDriveData, setShowDriveData] = React.useState(true);

    useEffect(() => {
        if (deleteRecommendedClicked) {
            const fetchDriveData = async () => {
                const info = await getRecommendedDeletions(user, connectionProvider);
                setDriveData(info);
            };

            fetchDriveData();
        }
    }, [deleteRecommendedClicked]);

    useEffect(() => {
        if (showDriveData) {
            setSelectAll(driveData?.children.every(child => filesToBeDeleted.children.includes(child)) || false);
        } else if (showEmails) {
            setSelectAll(driveData?.emails.every(email => filesToBeDeleted.emails.includes(email)) || false);
        }
    }, [filesToBeDeleted, showDriveData, showEmails, driveData]);

    const handleSelectAll = (event: React.ChangeEvent<HTMLInputElement>) => {
        setSelectAll(event.target.checked);
        if (event.target.checked) {
            if (showDriveData) {
                setFilesToBeDeleted(prevState => ({...prevState, children: [...driveData?.children || []]}));
            } else if (showEmails) {
                setFilesToBeDeleted(prevState => ({...prevState, emails: [...driveData?.emails || []]}));
            }
        } else {
            setFilesToBeDeleted({children: [], emails: []});
        }
    }

    const closeModal = () => {
        setShowModal(false);
        setShowDeletionModal(false);
        setDeleteRecommendedClicked(false);
        setSelectAll(false);
        setFilesToBeDeleted({children: [], emails: []})
        setSuccessfulDeletionMessage("");
        setUnsuccessfulDeletionMessage("");
    };

    const closeRecommendationModal = () => {
        setShowDeletionModal(false);
        setDeleteRecommendedClicked(false);
        setSelectAll(false);
        setFilesToBeDeleted({children: [], emails: []})
        setSuccessfulDeletionMessage("");
        setUnsuccessfulDeletionMessage("");
    };

    const stopPropagation = (e: React.MouseEvent<HTMLDivElement>) => {
        e.stopPropagation();
    };

    const handleShowDriveData = () => {
        setShowDriveData(true);
        setShowEmails(false);
        setSelectAll(driveData?.children.every(child => filesToBeDeleted.children.includes(child)) || false);
    }

    const handleShowEmails = () => {
        setShowDriveData(false);
        setShowEmails(true);
        setSelectAll(driveData?.emails.every(email => filesToBeDeleted.emails.includes(email)) || false);
    }

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

    async function deleteRecommendedFiles(user: any, connectionProvider: string, filesToDelete: FilesToBeDeleted, driveEmail: string): Promise<void> {

        const headers = {
            'Authorization': `Bearer ${user.token}`
        }

        setLoading(true);
        await buildAxiosRequestWithHeaders('POST', `/delete-recommended?email=${user.email}&provider=${connectionProvider}&driveEmail=${driveEmail}&isEmail=true`, headers, filesToDelete)
            .then((response) => {
                setLoading(false);
                setHaveFilesBeenDeleted(true);
                setSuccessfulDeletionMessage(`Successfully deleted ${response.data.filesDeleted} file(s) and ${response.data.emailsDeleted} email(s) from your drive.`);
            })
            .catch((error) => {
                setLoading(false);
                setUnsuccessfulDeletionMessage("Item(s) were not deleted");
            });

        setLoading(false);
    }

    return (
        <div className={"modal-overlay"} onClick={closeRecommendationModal}>
            {loading ? <LoadingSpinner/> :
                <div className={"modal"} onClick={stopPropagation} id={"deletion-recommendation-modal"}>

                    <button className={"modal-close-button"} onClick={closeRecommendationModal}><CloseIcon
                        className="svg_icons"/>
                    </button>

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

                    {driveData && successfulDeletionMessage === "" && unsuccessfulDeletionMessage === "" &&
                        <div className={"dashboard-page-buttons-modal-grid"}>
                            <div className={"deletion-recommendation-container"}>
                                {driveData?.children.length > 0 || driveData.emails.length > 0 ?
                                    <>
                                        {connectionProvider === "GoogleDrive" ?
                                            <div className={"google-drive-item-type-navigation-container"}>
                                                <button onClick={handleShowDriveData}>
                                                    <ArticleIcon/> {t('main.dashboard.dashboardCardModal.driveInformation.driveFiles')}
                                                </button>
                                                <button onClick={handleShowEmails}><EmailIcon/> Gmail</button>
                                            </div>
                                            : null
                                        }

                                        {showDriveData && driveData?.children.length > 0 ?
                                            <>
                                                <h2 id={"item-recommendation-count"}>
                                                    {driveData?.children.length} {t('main.dashboard.deletionModals.deleteRecommended.title')}
                                                </h2>
                                                <p
                                                    className={"deletion-recommendation-description"}>{t('main.dashboard.deletionModals.deleteRecommended.mainText')}</p>
                                                <p className={"deletion-recommendation-select-all-description"}
                                                   id={"recommendation-description"}>
                                                    {t('main.dashboard.deletionModals.deleteRecommended.selectAll')}
                                                    <input
                                                        className={"dashboard-page-buttons-select-all-checkbox"}
                                                        type="checkbox"
                                                        checked={selectAll}
                                                        onChange={handleSelectAll}
                                                        id={"select-all-checkbox"}/>
                                                </p>
                                                <div className={"dashboard-card-modal-drive-files-grid"}>
                                                    {driveData ?
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
                                                </div>
                                                <div className={"recommended-file-button-container"}>
                                                    <button className={"dashboard-button"} onClick={() => {
                                                        deleteRecommendedFiles(user, connectionProvider, filesToBeDeleted, driveEmail)
                                                    }} id={"delete-recommendations-button"}>
                                                        {t('main.dashboard.deletionModals.deleteRecommended.deleteButton')}
                                                    </button>
                                                </div>
                                            </>
                                            : showDriveData &&
                                            <>
                                                <NothingFoundRecommendations caughtUpFor={"Drive Files"}/>
                                                <div className={"recommended-file-button-container"}>
                                                    <button className={"dashboard-button"} onClick={closeModal}>
                                                        {t('main.dashboard.deletionModals.deleteRecommended.closeRecommendation')}
                                                    </button>
                                                </div>
                                            </>
                                        }

                                        {showEmails && driveData?.emails.length > 0 ?
                                            <>
                                                <h2> {driveData?.emails.length} {t('main.dashboard.deletionModals.deleteRecommended.titleEmail')}</h2>
                                                <p
                                                    className={"deletion-recommendation-description"}>
                                                    {t('main.dashboard.deletionModals.deleteRecommended.mainTextEmail')}
                                                </p>
                                                <p className={"deletion-recommendation-select-all-description"}>
                                                    {t('main.dashboard.deletionModals.deleteRecommended.selectAll')}
                                                    <input
                                                        className={"dashboard-page-buttons-select-all-checkbox"}
                                                        type="checkbox"
                                                        checked={selectAll}
                                                        onChange={handleSelectAll}/>
                                                </p>
                                                <div className={"dashboard-card-modal-drive-files-grid"}>
                                                    {driveData ?
                                                        <div className={"deletion-recommendation-file-container"}>

                                                            <div className={"deletion-recommendation-files-grid"}>
                                                                {driveData ?
                                                                    <FileTreeEmails data={driveData.emails}
                                                                                    setFilesToBeDeleted={setFilesToBeDeleted}
                                                                                    filesToBeDeleted={filesToBeDeleted}
                                                                                    selectAll={selectAll}/> : "No files found"}
                                                            </div>
                                                        </div> : null
                                                    }
                                                </div>
                                                <div className={"recommended-file-button-container"}>
                                                    <button className={"dashboard-button"} onClick={() => {
                                                        deleteRecommendedFiles(user, connectionProvider, filesToBeDeleted, driveEmail)
                                                    }}>
                                                        {t('main.dashboard.deletionModals.deleteRecommended.deleteButton')}
                                                    </button>
                                                </div>
                                            </>
                                            : showEmails &&
                                            <>
                                                <NothingFoundRecommendations caughtUpFor={"Emails"}/>
                                                <div className={"recommended-file-button-container"}>
                                                    <button className={"dashboard-button"} onClick={closeModal}>
                                                        {t('main.dashboard.deletionModals.deleteRecommended.closeRecommendation')}
                                                    </button>
                                                </div>
                                            </>
                                        }

                                    </> :
                                    <>
                                        <NothingFoundRecommendations caughtUpFor={"Everything"}/>
                                        <div className={"recommended-file-button-container"}>
                                            <button className={"dashboard-button"} onClick={closeModal}
                                                    id={"recommendation-done-button"}>
                                                {t('main.dashboard.deletionModals.deleteRecommended.closeRecommendation')}
                                            </button>
                                        </div>
                                    </>
                                }
                            </div>

                        </div>
                    }
                </div>
            }
        </div>
    )
}

export default DeletionRecommendationsModal;