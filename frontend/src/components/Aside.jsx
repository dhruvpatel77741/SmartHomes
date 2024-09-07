import React from "react";
import "./Aside.css";
import { useNavigate, useLocation } from "react-router-dom";

const image = process.env.PUBLIC_URL;

const Aside = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const isHomeActive = location.pathname === "/dashboard";

  return (
    <div className="left-panel">
      <div className="dashboard-logo" style={{marginLeft: "20px"}}>
        <h1>Smart Homes</h1>
      </div>
      <div className="sideBarMenu">
        <button
          className={`left-part ${isHomeActive ? "active" : ""}`}
          style={{ border: "none" }}
          onClick={() => {
            navigate("/dashboard");
          }}
        >
          <div className="left-img">
            <img
              src={`${image}/Assets/LeftPanel/teams.svg`}
              alt=""
              className={`default-image ${isHomeActive ? "hide" : ""}`}
            />
            <img
              src={`${image}/Assets/LeftPanel/teams-white.svg`}
              alt=""
              className={`selected-image ${isHomeActive ? "" : "hide"}`}
            />
          </div>
          <div className="left-text">Dashboard</div>
        </button>
        
        <button
          className={`left-part`}
          style={{ border: "none" }}
          onClick={() => {
            localStorage.clear();
            navigate("/");
          }}
        >
          <div className="left-img">
            <img
              src={`${image}/Assets/LeftPanel/logout.svg`}
              alt=""
              className={`default-image`}
            />
            <img
              src={`${image}/Assets/LeftPanel/logout-white.svg`}
              alt=""
              className={`selected-image`}
            />
          </div>
          <div className="left-text">Log Out</div>
        </button>
      </div>
    </div>
  );
};

export default Aside;
