import React, { useState } from "react";
import "../Modal.css";
import "./DashboardCardModal.css";
import { CONNECTION_LOGOS, CONNECTION_TITLE } from "../../../constants/ConnectionConstants";
import { getFileType } from "../../../constants/FileTypesConstants";
import DashboardPageButtons from "../../pages/dashboard/DashboardPageButtons";
import { buildAxiosRequestWithHeaders } from "../../helpers/AxiosHelper";
import { AuthData } from "../../routing/AuthWrapper";
import { useTranslation } from "react-i18next";
import { Cell, Pie, PieChart, Tooltip, TooltipProps } from "recharts";
import CloseIcon from "@mui/icons-material/Close";
import ArticleIcon from '@mui/icons-material/Article';
import EmailIcon from '@mui/icons-material/Email';
import { DashboardCardModalEmptyFiles } from "./DashboardCardModalEmptyFiles";
import EmailContainer from "./EmailContainer";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import LoadingProgress from "../../ui_components/LoadingProgress";
import { useGoogleLogin } from '@react-oauth/google';

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
  gaveGmailPermissions: boolean;
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
  const { user } = AuthData();
  const { t } = useTranslation();
  const [driveData, setDriveData] = React.useState<FileNode>();
  const [pieChartData, setPieChartData] = React.useState<{ name: string; value: number; }[]>([]);
  const [loading, setLoading] = React.useState(false);
  const [showEmails, setShowEmails] = React.useState(false);
  const [showDriveData, setShowDriveData] = React.useState(true);
  const [haveFilesBeenDeleted, setHaveFilesBeenDeleted] = React.useState(false);
  const [progress, setProgress] = useState<number>(0);
  const COLOURS = ["blue", "brown", "red", "orange", "purple", "green"];

  let categories = {
    Images: 0,
    Audio: 0,
    Video: 0,
    Documents: 0,
    Others: 0
  };

  const CustomTooltip = ({active, payload}: TooltipProps<any, any>) => {
    if (active && payload && payload.length) {
      return (
        <div
          style={{
            backgroundColor: "#fff",
            border: "1px solid #999",
            margin: 0,
            padding: 5,
          }}
        >
          <p
            style={{fontSize: "12px"}}
          >{`${payload[0].name}: ${payload[0].value}%`}</p>
        </div>
      );
    }

    return null;
  };

  const handleGmailLink = useGoogleLogin({
    onSuccess: async (codeResponse) => {
      // Get the code from the response
      const authCode = codeResponse.code;

      const headers = {
        Authorization: `Bearer ${user?.token}`
            }

      // Send the code to the server
      try {
        await buildAxiosRequestWithHeaders("POST", `/link-gmail?email=${encodeURIComponent(user?.email ?? '')}`, headers, {authCode});
        window.location.reload();
      } catch (error) {
        // Handle the error
        console.error('Error:', error);
      }
    },
    flow: 'auth-code',
    scope: 'https://www.googleapis.com/auth/drive https://mail.google.com/',
  });

  React.useEffect(() => {

    const fetchDriveData = async () => {
      const info = await getDriveItems(user, connectionProvider);
      setDriveData(info);

      console.log(info.gaveGmailPermissions);

      if (connectionProvider === "GoogleDrive") {
        info.children.forEach((item: FileNode) => {
          if (item.type.startsWith('image')) {
            categories.Images += 1;
          } else if (item.type.startsWith('audio')) {
            categories.Audio += 1;
          } else if (item.type.startsWith('video')) {
            categories.Video += 1;
          } else if (item.type.startsWith('text') || item.type.includes('document')) {
            categories.Documents += 1;
          } else {
            categories.Others += 1;
          }
        });
      } else if (connectionProvider === "OneDrive") {
        const processNode = (node: FileNode) => {
          if (node.type.startsWith('image')) {
            categories.Images += 1;
          } else if (node.type.startsWith('audio')) {
            categories.Audio += 1;
          } else if (node.type.startsWith('video')) {
            categories.Video += 1;
          } else if (node.type.startsWith('text') || node.type.includes('document') || node.type.includes('presentation') || node.type.includes('spreadsheet') || node.type.includes('pdf')) {
            categories.Documents += 1;
          } else if (!node.type.startsWith('Folder')) {
            categories.Others += 1;
          }

          node.children.forEach(processNode);
        };

        info.children.forEach(processNode);
      }

      const totalCount = Object.values(categories).reduce((a, b) => a + b, 0);

      const pieChartData = Object.entries(categories).map(([name, value]) => ({
        name,
        value: parseFloat(((value / totalCount) * 100).toFixed(2)),
      }));

      setPieChartData(pieChartData);
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

    const client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/progress'),
      onConnect: () => {
        client.subscribe(`/user/${user?.email}/queue/progress`, (message) => {
          const progress = JSON.parse(message.body);
          setProgress(progress);
        });
      }
    });

    await client.activate();

    setLoading(true);
    const response = await buildAxiosRequestWithHeaders('GET', `/drive-items?email=${user.email}&provider=${connectionProviderTitle}&driveEmail=${driveEmail}`, headers, {});

    if (!response.data) {
      setLoading(false);
      await client.deactivate();
      throw new Error('Invalid response data');
    }

    await client.deactivate();
    setLoading(false);
    return response.data;
  }

  const convertGBtoTB= (sizeInGB: number) => {
    if (sizeInGB >= 1024) {
        // Convert to TB and format to 2 decimal places
        return `${(sizeInGB / 1024).toFixed(2)}TB`;
    } else {
        return `${sizeInGB}GB`;
    }
};

  function areAllChildrenFolders(driveData: FileNode): boolean {
    return driveData.children.every(child => child.type === 'Folder');
  }

  return (
    <div className={"modal-overlay"} onClick={closeModal}>
      {loading ? (
        <>
          <LoadingProgress filled={progress} />
        </>
      ) : (
        <div className={"modal"} onClick={stopPropagation} id={"connected-drive-modal"}>
          <button className={"modal-close-button"} onClick={closeModal}>
            <CloseIcon className="svg_icons" />
          </button>

          <div className={"dashboard-card-modal-grid"}>
            {/* This div displays the general information about the user's drive */}
            <div className={"dashboard-card-modal-drive-information"}>
              <img src={CONNECTION_LOGOS[connectionProvider]} alt={`Logo for ${connectionProvider}`} />
              <p> {t("main.dashboard.dashboardCardModal.driveInformation.accountDetails")} </p>
              <span>{driveEmail}</span>
              <span> {t("main.dashboard.dashboardCardModal.driveInformation.usedStorage")}{usedStorage > 0.0 ? convertGBtoTB(usedStorage) : "< 0GB"}</span>
              <span>{t("main.dashboard.dashboardCardModal.driveInformation.totalStorage")}
                {convertGBtoTB(totalStorage)}</span>
              {!showEmails && driveData && !areAllChildrenFolders(driveData) ? (
                <PieChart className="dashboard-card-modal-pie-chart" width={200} height={200}>
                  <Pie
                    data={pieChartData}
                    cx="50%"
                    cy="50%"
                    labelLine={false}
                    outerRadius={80}
                    fill="#8884d8"
                    dataKey="value"
                    isAnimationActive={false}
                  >
                    {pieChartData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLOURS[index % COLOURS.length]}/>
                    ))}
                  </Pie>
                  <Tooltip content={<CustomTooltip/>}/>
                </PieChart>
              ) : null}
            </div>

            {/* This div displays all the files inside the user drive */}
            <div className={"dashboard-card-modal-drive-files-container"}>
              {/* If google provider then display options to view emails and files */}
              {connectionProvider === "GoogleDrive" ?
                <div className={"google-drive-item-type-navigation-container"}>
                  <button onClick={handleShowDriveData}>
                    <ArticleIcon />{t("main.dashboard.dashboardCardModal.driveInformation.driveFiles")}</button>
                  <button onClick={handleShowEmails}><EmailIcon /> Gmail</button>
                </div>
               : null}

              {showDriveData && (
                <div className={"dashboard-card-modal-drive-files-grid"}>
                  {driveData && !areAllChildrenFolders(driveData) ?
                    <FileTree data={driveData}/>
                  : <DashboardCardModalEmptyFiles message={
                                            t('main.dashboard.dashboardCardModal.driveInformation.noFilesFoundMessage')
                                        }/>}
                </div>
              )}

              {showEmails && (
                <div className={"dashboard-card-modal-drive-files-grid"}>
                  {driveData && driveData.gaveGmailPermissions === false ? (
                    <div>
                      <DashboardCardModalEmptyFiles message={t(
                        "main.dashboard.dashboardCardModal.driveInformation.gmailNotLinkedMessage"
                      )}/>
                      <div className={"gmail-islinked-container"}>
                        <button onClick={handleGmailLink}>
                          <EmailIcon /> Link Gmail
                        </button>
                      </div>
                    </div>
                  ) : driveData &&
                    driveData.emails !== null &&
                    driveData.emails.length > 0 ? (
                    <EmailContainer emails={driveData.emails} />
                  ) : (
                    <DashboardCardModalEmptyFiles
                      message={t(
                        "main.dashboard.dashboardCardModal.driveInformation.noEmailsFoundMessage"
                      )}
                    />
                  )}
                </div>
              )}
            </div>
            {driveData && (
              <div className={"dashboard-page-buttons-container"}>
                <DashboardPageButtons data={driveData}
                  connectionProvider={CONNECTION_TITLE[connectionProvider]}
                  setShowModal={setShowModal}
                  driveEmail={driveEmail}
                  setHaveFilesBeenDeleted={setHaveFilesBeenDeleted}
                />
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default DashboardCardModal;
