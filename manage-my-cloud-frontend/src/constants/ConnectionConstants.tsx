
import OneDriveLogo from "../components/images/manage-connections/OneDriveLogo.png";

interface ConnectionLogos {
    [key: string]: string;
}

interface ConnectionTitles {
    [key: string]: string;
}

const CONNECTION_LOGOS: ConnectionLogos = {
    oneDrive : OneDriveLogo,
}

const CONNECTION_TITLE: ConnectionTitles = {
    oneDrive : "OneDrive",
}

export {CONNECTION_LOGOS, CONNECTION_TITLE};