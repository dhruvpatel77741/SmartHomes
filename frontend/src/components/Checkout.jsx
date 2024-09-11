import React, { useState } from "react";
import Aside from "./Aside";
import HeaderComponent from "./HeaderComponent";
import "./Checkout.css";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";

const baseURL = process.env.REACT_APP_API_BASE_URL;

const Checkout = () => {
  const navigate = useNavigate();

  const name = localStorage.getItem("name");
  const location = useLocation();
  const { totalAmount, cartItems } = location.state;
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

  const clearCart = async () => {
    const userId = localStorage.getItem("userId");

    try {
      await axios.post(`${baseURL}/cart`, {
        action: "clearCart",
        userId: userId,
      });
    } catch (error) {
      console.error("Error adding address:", error);
    }
  };
  const handleProceed = async () => {
    if (tab === "pickup") {
      const userId = localStorage.getItem("userId");
      const orderData = {
        userId: userId,
        orderData: {
          product: cartItems[0]?.productName,
          price: totalAmount,
          quantity: cartItems[0]?.quantity,
        },
        checkout: "Pick Up",
        paymentMode: "",
        paymentDetails: {},
        address: {},
      };
      try {
        await axios.post(`${baseURL}/orders`, orderData);
        window.alert(
          "Order placed Successfully. You can pick up your order from the store and pay by cash or card."
        );
      } catch (error) {
        console.error("Error placing order:", error);
      }
      window.alert(
        "Order placed Succesfully. You can pickup your order from store and pay by cash or card."
      );
      clearCart();
      navigate("/dashboard");
    } else if (tab === "homeDelivery") {
      const userId = localStorage.getItem("userId");

      const data = {
        id: userId,
        phone: formData?.phone,
        address: {
          addressLine1: formData?.addressLine1,
          addressLine2: formData?.addressLine2,
          city: formData?.city,
          state: formData?.state,
          zipCode: formData?.zipCode,
        },
      };

      try {
        await axios.post(`${baseURL}/updateUser`, data);
      } catch (error) {
        console.error("Error clearing cart:", error);
      }
      navigate("/payment", {
        state: { totalAmount, address: data?.address, cartItems, formData },
      });
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
              <p>
                Total Amount: <b>${totalAmount.toFixed(2)}</b>
              </p>
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
              <p>
                Total Amount: <b>${totalAmount.toFixed(2)}</b>
              </p>
              <form>
                <input
                  type="text"
                  name="name"
                  placeholder="Full Name"
                  value={formData.name}
                  onChange={handleInputChange}
                  readOnly
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
