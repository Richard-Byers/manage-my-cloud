import {Success} from "./Success";
import "./NothingFound.css";
import {useTranslation} from "react-i18next";

export const NothingFoundDuplicates = () => {
    return(
        <div className={"nothing-found-container"}>
            <p>All caught up. AI found no duplicates.</p>
            <Success/>
        </div>
    )
}

interface NothingFoundProps {
    caughtUpFor: string;
}

export const NothingFoundRecommendations: React.FC<NothingFoundProps> = ({caughtUpFor}) => {

    const {t} = useTranslation();

    return (
        <div className={"nothing-found-container"}>
            <p>{t("helpers.nothingFound.caughtUpOne")} {caughtUpFor}, {t("helpers.nothingFound.caughtUpTwo")}.</p>
            <Success/>
        </div>
    )
}