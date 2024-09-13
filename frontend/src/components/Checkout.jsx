import React, { useEffect, useState } from "react";
import Aside from "./Aside";
import HeaderComponent from "./HeaderComponent";
import "./Checkout.css";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";

const baseURL = process.env.REACT_APP_API_BASE_URL;

const Checkout = () => {
  const navigate = useNavigate();
  const userId = localStorage.getItem("userId");
  const [userData, setUserData] = useState();
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

  useEffect(() => {
    const getData = async () => {
      let apiUrl = `${baseURL}/users`;
      try {
        const resp = await axios.get(apiUrl);
        const data = resp.data;
        const userData = data.find((item) => item?._id.toString() === userId);
        setUserData(userData);
      } catch (err) {
        console.log("Error:", err);
      }
    };
    getData();
  }, [userId]);

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
      console.error("Error clearing cart:", error);
    }
  };

  const handleProceed = async () => {
    if (tab === "pickup") {
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
          "Order placed successfully. You can pick up your order from the store and pay by cash or card."
        );
      } catch (error) {
        console.error("Error placing order:", error);
      }
      clearCart();
      navigate("/dashboard");
    } else if (tab === "homeDelivery") {
      if (userData) {
        setFormData({
          name: userData.name || "",
          phone: userData.phone || "",
          addressLine1: userData.addressLine1 || "",
          addressLine2: userData.addressLine2 || "",
          city: userData.city || "",
          state: userData.state || "",
          zipCode: userData.zipCode || "",
        });
      }
      navigate("/payment", {
        state: { totalAmount, address: formData, cartItems },
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
              {userData && (
                <div className="user-address">
                  <h3>Your Saved Address</h3>
                  <p>{`${userData.addressLine1}, ${userData.addressLine2}, ${userData.city}, ${userData.state}, ${userData.zipCode}`}</p>
                </div>
              )}
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
