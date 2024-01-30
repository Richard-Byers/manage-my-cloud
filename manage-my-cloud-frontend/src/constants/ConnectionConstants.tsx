
import OneDriveLogo from "../components/images/manage-connections/OneDriveLogo.png";

interface ConnectionLogos {
    [key: string]: string;
}

interface ConnectionTitles {
    [key: string]: string;
}

export const CONNECTION_LOGOS: ConnectionLogos = {
    oneDrive : OneDriveLogo,
}

export const CONNECTION_TITLE: ConnectionTitles = {
    oneDrive : "OneDrive",
}