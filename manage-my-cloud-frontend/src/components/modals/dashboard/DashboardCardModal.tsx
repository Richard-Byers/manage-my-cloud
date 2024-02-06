import React from "react";
import '../Modal.css';

interface DashboardCardModalProps {
    showModal: boolean;
    setShowModal: (arg0: boolean) => void;
    connectionProvider: string;
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

const DashboardCardModal: React.FC<DashboardCardModalProps> = ({showModal, setShowModal, connectionProvider}) => {

    const FileNode: React.FC<FileNodeProps> = ({ node }) => {

        if (node.type !== "Folder") {
            return (
                <div>
                    <p>{node.name} ({node.type})</p>
                </div>
            );
        }

        return (
            <>
                {node.children.map(childNode => (
                    <FileNode key={childNode.name} node={childNode} />
                ))}
            </>
        );
    };

    const FileTree: React.FC<FileTreeProps> = ({ data }) => {
        return <FileNode node={data} />;
    };

    const data = {
        "name": "root",
        "type": "Folder",
        "createdDateTime": null,
        "webUrl": null,
        "children": [
            {
                "name": "Attachments",
                "type": "Folder",
                "createdDateTime": 1.707220084E9,
                "webUrl": "https://onedrive.live.com?cid=759b6403818efcac&id=01KEQROECS5CVVLNQNAREZGLP3RKRGJJEY",
                "children": []
            },
            {
                "name": "Documents",
                "type": "Folder",
                "createdDateTime": 1.707220084E9,
                "webUrl": "https://onedrive.live.com?cid=759b6403818efcac&id=01KEQROEFUL7SQC2LRURHLQRHUMOHTHN6N",
                "children": [
                    {
                        "name": "Folder 2",
                        "type": "Folder",
                        "createdDateTime": 1.707221713E9,
                        "webUrl": "https://onedrive.live.com?cid=759b6403818efcac&id=01KEQROEF7KMCV6Z2J4VFY5HXY6YCBPTUB",
                        "children": [
                            {
                                "name": "Test doc.docx",
                                "type": "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                "createdDateTime": 1.707220139E9,
                                "webUrl": "https://onedrive.live.com?cid=759b6403818efcac&id=01KEQROEDE7MLTHMKSUFFZTALKPQKGB3OG",
                                "children": []
                            }
                        ]
                    }
                ]
            },
            {
                "name": "Test Folder",
                "type": "Folder",
                "createdDateTime": 1.707220072E9,
                "webUrl": "https://onedrive.live.com?cid=759b6403818efcac&id=01KEQROEG3MUZVYVJKV5D3E73G3OIWFBWO",
                "children": [
                    {
                        "name": "Test doc.docx",
                        "type": "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        "createdDateTime": 1.707220103E9,
                        "webUrl": "https://onedrive.live.com?cid=759b6403818efcac&id=01KEQROECU7QZQMDXNSJAIJTDUO7ZKMBTX",
                        "children": []
                    }
                ]
            },
            {
                "name": "Getting started with OneDrive.pdf",
                "type": "application/pdf",
                "createdDateTime": 1.669331463E9,
                "webUrl": "https://onedrive.live.com?cid=759b6403818efcac&id=01KEQROEFM7SHICA3ETMQIA5K7AYAAAAAA",
                "children": []
            }
        ]
    }

    const toggleModal = () => {
        setShowModal(!showModal);
    };

    const stopPropagation = (e: React.MouseEvent<HTMLDivElement>) => {
        e.stopPropagation();
    };

    return (
        <div className={"modal-overlay"} onClick={toggleModal}>
            <div className={"modal"} onClick={stopPropagation}>
                <FileTree data={data} />
            </div>
        </div>
    )

}

export default DashboardCardModal;