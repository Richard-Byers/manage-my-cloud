import "./SuccessAndFailure.css";

export const Failure = () => {
    return (
        <svg className="cross-container">
            <circle className="outer-circle-failure" cx="75" cy="75" r="50"></circle>
            <line className="cross__line" x1="50" y1="55" x2="100" y2="100"></line>
            <line className="cross__line" x1="100" y1="55" x2="50" y2="100"></line>
        </svg>
    );
};