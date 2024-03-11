import {Success} from "./Success";
import "./NothingFound.css";

export const NothingFoundDuplicates = () => {
    return(
        <div className={"nothing-found-container"}>
            <p>All caught up. AI found no duplicates.</p>
            <Success/>
        </div>
    )
}

export const NothingFoundRecommendations = () => {
    return(
        <div className={"nothing-found-container"}>
            <p>All caught up. Nothing to recommend.</p>
            <Success/>
        </div>
    )
}