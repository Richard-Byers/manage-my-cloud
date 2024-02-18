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
import { PieChart, Pie, Cell, Tooltip, TooltipProps } from 'recharts';


interface DashboardCardModalProps {
    showModal: boolean;
    setShowModal: (arg0: boolean) => void;
    connectionProvider: string;
    totalStorage: number;
    usedStorage: number;
    email: string;
}

interface Node {
    name: string;
    type: string;
    children: Node[];
}

interface FileNodeProps {
    node: Node;
}

interface FileTreeProps {
    data: Node;
}

const DashboardCardModal: React.FC<DashboardCardModalProps> = ({
                                                                   showModal,
                                                                   setShowModal,
                                                                   connectionProvider,
                                                                   totalStorage,
                                                                   usedStorage,
                                                                   email
                                                               }) => {
    const {user} = AuthData();
    const {t} = useTranslation();
    const [driveData, setDriveData] = React.useState<Node>();
    const [loading, setLoading] = React.useState(false);
    const [breakdownData, setBreakdownData] = React.useState<any>();
    const COLOURS = ['blue', 'brown', 'red', 'orange', 'purple', 'green'];

    const data = breakdownData ? Object.entries(breakdownData).map(([name, value]) => ({
        name,
        value: Number(value)
      })) : [];

    const CustomTooltip = ({ active, payload }: TooltipProps<any, any>) => {
        if (active && payload && payload.length) {
          return (
            <div style={{ backgroundColor: '#fff', border: '1px solid #999', margin: 0, padding: 5 }}>
              <p style={{ fontSize: '12px' }}>{`${payload[0].name}: ${payload[0].value}%`}</p>
            </div>
          );
        }
      
        return null;
      };

    React.useEffect(() => {
        const fetchDriveData = async () => {
            const info = await getDriveItems(user, connectionProvider);
            console.log(info)
            setDriveData(info);
        };

        fetchDriveData();
    }, []);

    const FileNode: React.FC<FileNodeProps> = ({node}) => {

        // recursive method call to render the files
        // if the node type is not a folder then render the file
        // if the node is a folder then proceed down to the second return to recursively call the method again and repeat
        // until all children are rendered

        if (node.type !== "Folder") {
            return (
                <div className={"dashboard-card-modal-file-container"}>
                    <img className="svg-icon" src={getFileType(node.name.split('.').pop() as string)}
                         alt={`File Type`}/>
                    <span>{node.name}</span>
                </div>
            );
        }

        return (
            <>
                {node.children.map(childNode => (
                    <FileNode key={childNode.name} node={childNode}/>
                ))}
            </>
        );
    };

    const FileTree: React.FC<FileTreeProps> = ({data}) => {
        return <FileNode node={data}/>;
    };

    const toggleModal = () => {
        setShowModal(!showModal);
    };

    const stopPropagation = (e: React.MouseEvent<HTMLDivElement>) => {
        e.stopPropagation();
    };

    async function getDriveItems(user: any, connectionProvider: string): Promise<Node> {

        const headers = {
            'Authorization': `Bearer ${user.token}`
        }

        const connectionProviderTitle = CONNECTION_TITLE[connectionProvider];
        setLoading(true);
        const response = await buildAxiosRequestWithHeaders('GET', `/drive-items?email=${user.email}&provider=${connectionProviderTitle}`, headers, {})

        if (!response.data) {
            setLoading(false);
            throw new Error('Invalid response data');
        }
        setLoading(false);
        return response.data;
    }

    React.useEffect(() => {
        const fetchBreakdownData = async () => {
            const info = await getDriveBreakdown(user, connectionProvider);
            setBreakdownData(info);
        };
    
        fetchBreakdownData();
    }, []);

    async function getDriveBreakdown(user: any, connectionProvider: string): Promise<any> {
        const headers = {
            'Authorization': `Bearer ${user.token}`
        }
    
        const connectionProviderTitle = CONNECTION_TITLE[connectionProvider];
        setLoading(true);
        const response = await buildAxiosRequestWithHeaders('GET', `/drive-breakdown?email=${user.email}&provider=${connectionProviderTitle}`, headers, {})
    
        if (!response.data) {
            setLoading(false);
            throw new Error('Invalid response data');
        }
        setLoading(false);
        return response.data;
    }

    return (
        <div className={"modal-overlay"} onClick={toggleModal}>
          {loading ? <LoadingSpinner/> :
            <div className={"modal"} onClick={stopPropagation}>
              <div className={"dashboard-card-modal-grid"}>
                <div className={"dashboard-card-modal-drive-information"}>
                  <img src={CONNECTION_LOGOS[connectionProvider]}
                       alt={`Logo for ${connectionProvider}`}/>
                  <p>{t('main.dashboard.dashboardCardModal.driveInformation.accountDetails')}</p>
                  <span>{email}</span>
                  <span>{t('main.dashboard.dashboardCardModal.driveInformation.usedStorage')} {usedStorage > 0.0 ? usedStorage : "< 0"}/GB</span>
                  <span>{t('main.dashboard.dashboardCardModal.driveInformation.totalStorage')} {totalStorage}/GB</span>
                  {breakdownData && 
    <PieChart width={200} height={200} style={{ visibility: breakdownData ? 'visible' : 'hidden' }}>
    <Pie
      data={data}
      cx="50%"
      cy="50%"
      labelLine={false}
      outerRadius={80}
      fill="#8884d8"
      dataKey="value"
      isAnimationActive={false}
    >
      {data.map((entry, index) => (
        <Cell key={`cell-${index}`} fill={COLOURS[index % COLOURS.length]} />
      ))}
    </Pie>
    <Tooltip content={<CustomTooltip />} />
  </PieChart>
  }
                </div>
                <div className={"dashboard-card-modal-drive-files-container"}>
                  <div className={"dashboard-card-modal-drive-files-grid"}>
                    {driveData && <FileTree data={driveData}/>}
                  </div>
                </div>
                <div className={"dashboard-page-buttons-container"}>
                  <DashboardPageButtons/>
                </div>
              </div>
            </div>
          }
        </div>
      );

}

export default DashboardCardModal;