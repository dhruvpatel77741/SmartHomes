import React, { useState } from "react";
import Aside from "./Aside";
import HeaderComponent from "./HeaderComponent";
import "./Checkout.css";
import { useLocation } from "react-router-dom";

const Checkout = () => {
  const name = localStorage.getItem("name");
  const location = useLocation();
  const { totalAmount } = location.state;

  const [tab, setTab] = useState(null);
  const [formData, setFormData] = useState({
    name: name,
    phone: "",
    addressLine1: "",
    addressLine2: "",
    city: "",
    state: "",
    zipCode: "",
  });

  const handleTabSelect = (selectedTab) => {
    setTab(selectedTab);
  };

  const handleBack = () => {
    setTab(null);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleProceed = () => {
    if (tab === "pickup") {
      console.log("Proceeding with Pickup", { totalAmount });
    } else if (tab === "homeDelivery") {
      console.log("Proceeding with Home Delivery", { formData, totalAmount });
    }
  };

  return (
    <div className="MainOuterContainer">
      <Aside />
      <div className="main-part">
        <HeaderComponent />
        <div className="checkout-container">
          {tab === null ? (
            <div className="tab-selection">
              <h2>Choose Checkout Method</h2>
              <button
                className="tab-btn"
                onClick={() => handleTabSelect("pickup")}
              >
                Pickup
              </button>
              <button
                className="tab-btn"
                onClick={() => handleTabSelect("homeDelivery")}
              >
                Home Delivery
              </button>
            </div>
          ) : tab === "pickup" ? (
            <div className="pickup-details">
              <h2>{`Hello, ${name}!`}</h2>
              <p>Total Amount: <b>${totalAmount.toFixed(2)}</b></p>
              <div className="checkout-actions">
                <button className="back-btn" onClick={handleBack}>
                  Back
                </button>
                <button className="proceed-btn" onClick={handleProceed}>
                  Proceed
                </button>
              </div>
            </div>
          ) : (
            <div className="home-delivery-form">
              <h2>Enter Your Delivery Details</h2>
              <p>Total Amount: <b>${totalAmount.toFixed(2)}</b></p>
              <form>
                <input
                  type="text"
                  name="name"
                  placeholder="Full Name"
                  value={formData.name}
                  onChange={handleInputChange}
                  required
                />
                <input
                  type="text"
                  name="phone"
                  placeholder="Phone Number"
                  value={formData.phone}
                  onChange={handleInputChange}
                  required
                />
                <input
                  type="text"
                  name="addressLine1"
                  placeholder="Address Line 1"
                  value={formData.addressLine1}
                  onChange={handleInputChange}
                  required
                />
                <input
                  type="text"
                  name="addressLine2"
                  placeholder="Address Line 2"
                  value={formData.addressLine2}
                  onChange={handleInputChange}
                />
                <input
                  type="text"
                  name="city"
                  placeholder="City"
                  value={formData.city}
                  onChange={handleInputChange}
                  required
                />
                <input
                  type="text"
                  name="state"
                  placeholder="State"
                  value={formData.state}
                  onChange={handleInputChange}
                  required
                />
                <input
                  type="text"
                  name="zipCode"
                  placeholder="Zip Code"
                  value={formData.zipCode}
                  onChange={handleInputChange}
                  required
                />
              </form>
              <div className="checkout-actions">
                <button className="back-btn" onClick={handleBack}>
                  Back
                </button>
                <button className="proceed-btn" onClick={handleProceed}>
                  Proceed
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Checkout;
