import {Success} from "./Success";
import "./NothingFound.css";
import {useTranslation} from "react-i18next";

interface NothingFoundProps {
    caughtUpFor: string;
}

export const NothingFound: React.FC<NothingFoundProps> = ({caughtUpFor}) => {

    const {t} = useTranslation();

    return (
        <div className={"nothing-found-container"}>
            <p id={"caught-up-with-message"}>{t("helpers.nothingFound.caughtUpOne")} {caughtUpFor}, {t("helpers.nothingFound.caughtUpTwo")}.</p>
            <Success/>
        </div>
    )
}