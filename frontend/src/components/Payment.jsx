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
  const { totalAmount } = location.state;

  const [tab, setTab] = useState(null);
  const [formData, setFormData] = useState({
    name: "",
    phone: "",
    creditCardNumber: "",
    expiryDate: "",
    cvv: "",
  });

  const handleTabSelect = (selectedTab) => {
    setTab(selectedTab);
  };

  const handleBack = () => {
    setTab(null);
    window.alert("Payment Failed.");
    navigate("/cart");
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

  const handlePay = async () => {
    if (tab === "cod") {
      console.log("Order Placed.")
    } else {
      const userId = localStorage.getItem("userId");

      const data = {
        id: userId,
        creditCard: {
          creditCardNumber: formData?.creditCardNumber,
          expiryDate: formData?.expiryDate,
          cvv: formData?.cvv,
        },
      };

      try {
        await axios.post(`${baseURL}/updateUser`, data);
      } catch (error) {
        console.error("Error adding credit card:", error);
      }
    }
    window.alert("Order placed Succesfully.");
    navigate("/dashboard");
    clearCart();
  };

  return (
    <div className="MainOuterContainer">
      <Aside />
      <div className="main-part">
        <HeaderComponent />
        <div className="checkout-container">
          {tab === null ? (
            <div className="tab-selection">
              <h2>Choose Payment Method</h2>
              <button
                className="tab-btn"
                onClick={() => handleTabSelect("cod")}
              >
                Cash
              </button>
              <button
                className="tab-btn"
                onClick={() => handleTabSelect("creditCard")}
              >
                Credit Card
              </button>
            </div>
          ) : tab === "cod" ? (
            <div className="pickup-details">
              <h2>{`Hello, ${name}!`}</h2>
              <p>
                Total Amount: <b>${totalAmount.toFixed(2)}</b>
              </p>
              <div className="checkout-actions">
                <button className="back-btn" onClick={handleBack}>
                  Back
                </button>
                <button className="proceed-btn" onClick={handlePay}>
                  Confirm
                </button>
              </div>
            </div>
          ) : (
            <div className="home-delivery-form">
              <h2>Enter Your Credit Card Details</h2>
              <p>
                Total Amount: <b>${totalAmount.toFixed(2)}</b>
              </p>
              <form>
                <input
                  type="text"
                  name="name"
                  placeholder="Card Holder's Name"
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
                  maxLength={10}
                />
                <input
                  type="text"
                  name="creditCardNumber"
                  placeholder="Credit Card Number"
                  value={formData.creditCardNumber}
                  onChange={handleInputChange}
                  required
                  maxLength={16}
                />
                <input
                  type="text"
                  name="expiryDate"
                  placeholder="Expiry Date (MM/YY)"
                  value={formData.expiryDate}
                  onChange={handleInputChange}
                  required
                />
                <input
                  type="password"
                  name="cvv"
                  placeholder="CVV"
                  value={formData.cvv}
                  onChange={handleInputChange}
                  required
                  maxLength={3}
                />
              </form>
              <div className="checkout-actions">
                <button className="back-btn" onClick={handleBack}>
                  Back
                </button>
                <button className="proceed-btn" onClick={handlePay}>
                  Pay
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
