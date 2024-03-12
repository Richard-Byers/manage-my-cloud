import "./SuccessAndFailure.css";

export const Success = () => {
    return (
        <div className="check-container" id={"check-container"}>
            <svg className="checkmark">
                <circle className="outer-circle" cx="75" cy="75" r="50"></circle>
                <polyline className="checkmark__check" points="50,80 70,100 100,55"></polyline>
            </svg>
        </div>
    );
};