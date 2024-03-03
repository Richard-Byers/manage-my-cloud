import React from "react";
import '../Modal.css';
import './DashboardCardModal.css';
import {CONNECTION_LOGOS, CONNECTION_TITLE} from "../../../constants/ConnectionConstants";
import {getFileType} from "../../../constants/FileTypesConstants";
import DashboardPageButtons from "../../pages/dashboard/DashboardPageButtons";
import {buildAxiosRequestWithHeaders} from "../../helpers/AxiosHelper";
import {AuthData} from "../../routing/AuthWrapper";
import LoadingSpinner from "../../helpers/LoadingSpinner";
import {useTranslation} from "react-i18next";
import CloseIcon from "@mui/icons-material/Close";
import ArticleIcon from '@mui/icons-material/Article';
import EmailIcon from '@mui/icons-material/Email';
import {DashboardCardModalEmptyFiles} from "./DashboardCardModalEmptyFiles";
import EmailContainer from "./EmailContainer";

interface DashboardCardModalProps {
    showModal: boolean;
    setShowModal: (arg0: boolean) => void;
    connectionProvider: string;
    totalStorage: number;
    usedStorage: number;
    email: string;
    driveEmail: string;
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

interface FileNodeProps {
    node: FileNode;
}

interface FileTreeProps {
    data: FileNode;
}

const DashboardCardModal: React.FC<DashboardCardModalProps> = ({
                                                                   showModal,
                                                                   setShowModal,
                                                                   connectionProvider,
                                                                   totalStorage,
                                                                   usedStorage,
                                                                   email,
                                                                   driveEmail
                                                               }) => {
    const {user} = AuthData();
    const {t} = useTranslation();
    const [driveData, setDriveData] = React.useState<FileNode>();
    const [loading, setLoading] = React.useState(false);
    const [showEmails, setShowEmails] = React.useState(false);
    const [showDriveData, setShowDriveData] = React.useState(true);
    const [haveFilesBeenDeleted, setHaveFilesBeenDeleted] = React.useState(false);

    React.useEffect(() => {

        const fetchDriveData = async () => {
            const info = await getDriveItems(user, connectionProvider);
            setDriveData(info);
        };

        if (haveFilesBeenDeleted || !driveData) {
            fetchDriveData();
            setHaveFilesBeenDeleted(false);
        }
    }, []);

    const FileNode: React.FC<FileNodeProps> = ({node}) => {

        // recursive method call to render the files
        // if the node type is not a folder then render the file
        // if the node is a folder then proceed down to the second return to recursively call the method again and repeat
        // until all children are rendered

        if (node.type !== "Folder") {
            return (
                <div className={"dashboard-card-modal-file-container"} onClick={() => {
                    window.open(node.webUrl, "_blank");
                }}>
                    <img className="svg-icon" src={getFileType(node.name.split('.').pop() as string)}
                         alt={`File Type`}/>
                    <span>{node.name}</span>
                </div>
            );
        }

        return (
            <>
                {node.children.map(childNode => (
                    <FileNode key={childNode.id} node={childNode}/>
                ))}
            </>
        );
    };

    const FileTree: React.FC<FileTreeProps> = ({data}) => {
        return <FileNode node={data}/>;
    };

    const closeModal = () => {
        setShowModal(false);
    };

    const stopPropagation = (e: React.MouseEvent<HTMLDivElement>) => {
        e.stopPropagation();
    };

    const handleShowDriveData = () => {
        setShowDriveData(true);
        setShowEmails(false);
    }

    const handleShowEmails = () => {
        setShowDriveData(false);
        setShowEmails(true);
    }

    async function getDriveItems(user: any, connectionProvider: string): Promise<FileNode> {

        const headers = {
            'Authorization': `Bearer ${user.token}`
        }

        const connectionProviderTitle = CONNECTION_TITLE[connectionProvider];
        setLoading(true);
        const response = await buildAxiosRequestWithHeaders('GET', `/drive-items?email=${user.email}&provider=${connectionProviderTitle}&driveEmail=${driveEmail}`, headers, {})

        if (!response.data) {
            setLoading(false);
            throw new Error('Invalid response data');
        }
        setLoading(false);
        return response.data;
    }

    function areAllChildrenFolders(driveData: FileNode): boolean {
        return driveData.children.every(child => child.type === 'Folder');
    }

    return (
        <div className={"modal-overlay"} onClick={closeModal}>
            {loading ? <LoadingSpinner/> :
                <div className={"modal"} onClick={stopPropagation}>

                    <button className={"modal-close-button"} onClick={closeModal}>
                        <CloseIcon className="svg_icons"/>
                    </button>

                    <div className={"dashboard-card-modal-grid"}>
                        <div className={"dashboard-card-modal-drive-information"}>
                            <img src={CONNECTION_LOGOS[connectionProvider]}
                                 alt={`Logo for ${connectionProvider}`}/>
                            <p>{t('main.dashboard.dashboardCardModal.driveInformation.accountDetails')}</p>
                            <span>{driveEmail}</span>
                            <span>{t('main.dashboard.dashboardCardModal.driveInformation.usedStorage')} {usedStorage > 0.0 ? usedStorage : "< 0"}/GB</span>
                            <span>{t('main.dashboard.dashboardCardModal.driveInformation.totalStorage')} {totalStorage}/GB</span>
                        </div>
                        <div className={"dashboard-card-modal-drive-files-container"}>

                            {connectionProvider === "GoogleDrive" ?
                                <div className={"google-drive-item-type-navigation-container"}>
                                    <button onClick={handleShowDriveData}><ArticleIcon/>{t('main.dashboard.dashboardCardModal.driveInformation.driveFiles')}</button>
                                    <button onClick={handleShowEmails}><EmailIcon/> Gmail</button>
                                </div>
                                : null
                            }

                            {showDriveData &&
                                <div className={"dashboard-card-modal-drive-files-grid"}>
                                    {driveData && !areAllChildrenFolders(driveData) ?
                                        <FileTree data={driveData}/>
                                        : <DashboardCardModalEmptyFiles message={
                                            t('main.dashboard.dashboardCardModal.driveInformation.noFilesFoundMessage')
                                        }/>}
                                </div>
                            }

                            {showEmails &&
                                <div className={"dashboard-card-modal-drive-files-grid"}>
                                    {driveData && driveData.emails.length > 0 ?
                                        <EmailContainer emails={driveData.emails}/> :
                                        <DashboardCardModalEmptyFiles message={
                                            t('main.dashboard.dashboardCardModal.driveInformation.noEmailsFoundMessage')
                                        }/>}
                                </div>
                            }

                        </div>
                        {driveData &&
                            <div className={"dashboard-page-buttons-container"}>
                                <DashboardPageButtons data={driveData}
                                                      connectionProvider={CONNECTION_TITLE[connectionProvider]}
                                                      setShowModal={setShowModal}
                                                      driveEmail={driveEmail}
                                                      setHaveFilesBeenDeleted={setHaveFilesBeenDeleted}
                                />
                            </div>
                        }
                    </div>
                </div>
            }
        </div>
    )

}

export default DashboardCardModal;