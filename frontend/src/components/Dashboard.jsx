import React from "react";
import "./Dashboard.css";
import Aside from "./Aside";
import HeaderComponent from "./HeaderComponent";

const Dashboard = () => {
  const userType = localStorage.getItem("userType");
  return (
    <div className="MainOuterContainer">
      <Aside />
      <div style={{display: "flex", width: "100%", position: "relative", top: "-342px" }}>
        <div className="main-part-ratailer">
          <div><HeaderComponent /></div>

          {/* <div className="search-filter-section">
            <div className="search-container">
              <form action="">
                <input
                  type="text"
                  placeholder="Search by Owner Name, Company Name, CRM ID or SWH Job Number"
                  name="search"
                  className="input-search"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                />
              </form>
            </div>
            <div className="buttons-container">
              <div className="btn-filter">
                <button
                  className="filter-text"
                  onClick={() => {
                    setShowQuickFilters(true);
                  }}
                >
                  Filter
                </button>
              </div>
              <div className="btn-clear">
                <button
                  className="clear-text"
                  onClick={() => {
                    clearFilters();
                  }}
                >
                  Clear
                </button>
              </div>
            </div>
          </div> */}
          <div className="row"></div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
