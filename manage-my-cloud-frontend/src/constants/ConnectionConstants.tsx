
import OneDriveLogo from "../components/images/manage-connections/OneDriveLogo.png";
import GoogleDriveLogo from "../components/images/manage-connections/GoogleDriveLogo.png";

interface ConnectionLogos {
    [key: string]: string;
}

interface ConnectionTitles {
    [key: string]: string;
}

export const CONNECTION_LOGOS: ConnectionLogos = {
    oneDrive : OneDriveLogo,
    googleDrive : GoogleDriveLogo
}

export const CONNECTION_TITLE: ConnectionTitles = {
    oneDrive : "OneDrive",
    googleDrive : "GoogleDrive"
}