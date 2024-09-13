import React from "react";
import "./Aside.css";
import { useNavigate, useLocation } from "react-router-dom";

const image = process.env.PUBLIC_URL;

const Aside = () => {
  const userType = localStorage.getItem("userType");
  const navigate = useNavigate();
  const location = useLocation();
  const isHomeActive =
    location.pathname.startsWith("/dashboard") ||
    location.pathname === "/cart" ||
    location.pathname === "/checkout";
  const isUserListActive =
    location.pathname === "/customer-list" ||
    location.pathname === "/salesman-list";
  const isProductListActive = location.pathname === "/product-list";
  const isOrderListActive = location.pathname === "/order-list";

  return (
    <div className="left-panel">
      <div className="dashboard-logo" style={{ marginLeft: "20px" }}>
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
        {userType === "Salesman" ? (
          <button
            className={`left-part ${isUserListActive ? "active" : ""}`}
            style={{ border: "none" }}
            onClick={() => {
              navigate("/customer-list");
            }}
          >
            <div className="left-img">
              <img
                src={`${image}/Assets/LeftPanel/teams.svg`}
                alt=""
                className={`default-image ${isUserListActive ? "hide" : ""}`}
              />
              <img
                src={`${image}/Assets/LeftPanel/teams-white.svg`}
                alt=""
                className={`selected-image ${isUserListActive ? "" : "hide"}`}
              />
            </div>
            <div className="left-text">Customers</div>
          </button>
        ) : userType === "StoreManager" ? (
          <button
            className={`left-part ${isUserListActive ? "active" : ""}`}
            style={{ border: "none" }}
            onClick={() => {
              navigate("/salesman-list");
            }}
          >
            <div className="left-img">
              <img
                src={`${image}/Assets/LeftPanel/teams.svg`}
                alt=""
                className={`default-image ${isUserListActive ? "hide" : ""}`}
              />
              <img
                src={`${image}/Assets/LeftPanel/teams-white.svg`}
                alt=""
                className={`selected-image ${isUserListActive ? "" : "hide"}`}
              />
            </div>
            <div className="left-text">Salesman</div>
          </button>
        ) : null}
        {userType === "StoreManager" ? (
          <button
            className={`left-part ${isProductListActive ? "active" : ""}`}
            style={{ border: "none" }}
            onClick={() => {
              navigate("/product-list");
            }}
          >
            <div className="left-img">
              <img
                src={`${image}/Assets/LeftPanel/teams.svg`}
                alt=""
                className={`default-image ${isProductListActive ? "hide" : ""}`}
              />
              <img
                src={`${image}/Assets/LeftPanel/teams-white.svg`}
                alt=""
                className={`selected-image ${
                  isProductListActive ? "" : "hide"
                }`}
              />
            </div>
            <div className="left-text">Products</div>
          </button>
        ) : null}

        <button
          className={`left-part ${isOrderListActive ? "active" : ""}`}
          style={{ border: "none" }}
          onClick={() => {
            navigate("/order-list");
          }}
        >
          <div className="left-img">
            <img
              src={`${image}/Assets/LeftPanel/teams.svg`}
              alt=""
              className={`default-image ${isOrderListActive ? "hide" : ""}`}
            />
            <img
              src={`${image}/Assets/LeftPanel/teams-white.svg`}
              alt=""
              className={`selected-image ${isOrderListActive ? "" : "hide"}`}
            />
          </div>
          <div className="left-text">Orders</div>
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
