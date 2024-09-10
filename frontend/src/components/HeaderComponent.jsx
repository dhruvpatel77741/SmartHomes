import React from "react";
import "./HeaderComponent.css";
import { useNavigate } from "react-router-dom";

const image = process.env.PUBLIC_URL;

const HeaderComponent = () => {
  const navigate = useNavigate();

  const NameInitials = (name) => {
    name = localStorage.getItem("name");
    return name?.match(/(\b\S)?/g).join("");
  };
  return (
    <div className="header-container">
      <div className="dashboard-title">Dashboard</div>
      <div className="header-right">
        <img
          src={`${image}/Assets/cart.svg`}
          alt="Cart"
          className="cart-icon"
          onClick={() => {
            navigate("/cart");
          }}
        />
        <button className="profile-button">{NameInitials()}</button>
      </div>
    </div>
  );
};

export default HeaderComponent;
