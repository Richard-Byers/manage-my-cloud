import {Success} from "./Success";
import "./NothingFound.css";

export const NothingFound = () => {
    return(
        <div className={"nothing-found-container"}>
            <p>All caught up, Nothing to recommend.</p>
            <Success/>
        </div>
    )
}