import React from "react";
import './EmailContainer.css';
import {useTranslation} from "react-i18next";


interface Email {
    emailSubject: string;
    receivedDate: number;
    webUrl: string;
}

interface EmailContainerProps {
    emails: Email[]
}

interface FileNode {
    emails: Email[];
}

interface FileNodeProps {
    node: FileNode;
}

interface FileTreeProps {
    emails: FileNode;
}

const EmailContainer: React.FC<EmailContainerProps> = (emails) => {

    const {t} = useTranslation();

    const handleEmailClick = (webUrl: string) => {
        window.open(webUrl, "_blank")
    }

    const FileNode: React.FC<FileNodeProps> = ({node}) => {

        return (
            <>
                {node.emails.map(email => (
                    <div className={"email-container"} onClick={() => handleEmailClick(email.webUrl)}
                         key={email.webUrl}>
                        {email.emailSubject === "" ?
                            <span>{t("helpers.emailContainer.noSubject")}</span>
                            :
                            <span>
                                {email.emailSubject.length <= 30 ? email.emailSubject :
                                    `${email.emailSubject.substring(0, 35)}...`
                                }
                            </span>
                        }
                    </div>
                ))}
            </>
        );
    };

    const FileTree: React.FC<FileTreeProps> = ({emails}) => {
        return <FileNode node={emails}/>;
    };

    return (
        <FileTree emails={emails}/>
    );
}

export default EmailContainer;