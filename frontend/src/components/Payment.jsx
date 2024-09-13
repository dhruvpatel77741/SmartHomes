import React, { useEffect, useState } from "react";
import Aside from "./Aside";
import HeaderComponent from "./HeaderComponent";
import "./Checkout.css";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";

const baseURL = process.env.REACT_APP_API_BASE_URL;

const Payment = () => {
  const navigate = useNavigate();
  const userId = localStorage.getItem("userId");
  const [userData, setUserData] = useState();
  const name = localStorage.getItem("name");
  const location = useLocation();
  const { totalAmount, address, cartItems } = location.state;
  const [tab, setTab] = useState(null);
  const [cvvInput, setCvvInput] = useState("");
  const [cvvError, setCvvError] = useState(false);
  const [useSavedCard, setUseSavedCard] = useState(false);
  const [formData, setFormData] = useState({
    name: "",
    phone: "",
    creditCardNumber: "",
    expiryDate: "",
    cvv: "",
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
      console.error("Error clearing cart:", error);
    }
  };

  const handleSavedCardCVV = (e) => {
    setCvvInput(e.target.value);
  };

  const handleUseSavedCard = () => {
    setUseSavedCard(true);
  };

  const handleSavedCardSelect = () => {
    if (cvvInput === userData?.cvv) {
      setFormData({
        name: userData?.name || "",
        phone: userData?.phone || "",
        creditCardNumber: userData?.creditCardNumber || "",
        expiryDate: userData?.expiryDate || "",
        cvv: userData?.cvv,
      });
      setCvvError(false);
      setUseSavedCard(false);
    } else {
      setCvvError(true);
    }
  };

  const handlePay = async () => {
    const userId = localStorage.getItem("userId");
    if (tab === "cod") {
      const orderData = {
        userId: userId,
        orderData: {
          product: cartItems[0]?.productName,
          price: totalAmount,
          quantity: cartItems[0]?.quantity,
        },
        checkout: "HomeDelivery",
        paymentMode: "Cash",
        paymentDetails: {},
        address: {
          addressLine1: address?.addressLine1,
          addressLine2: address?.addressLine2,
          city: address?.city,
          state: address?.state,
          zipCode: address?.zipCode,
        },
      };
      try {
        await axios.post(`${baseURL}/orders`, orderData);
      } catch (error) {
        console.error("Error placing order:", error);
      }
      console.log("Order Placed Successfully.");
    } else {
      const data = {
        id: userId,
        creditCard: {
          creditCardNumber: formData?.creditCardNumber,
          expiryDate: formData?.expiryDate,
          cvv: formData?.cvv,
        },
      };
      const orderData = {
        userId: userId,
        orderData: {
          product: cartItems[0]?.productName,
          price: totalAmount,
          quantity: cartItems[0]?.quantity,
        },
        checkout: "HomeDelivery",
        paymentMode: "Credit Card",
        paymentDetails: {
          creditCardNumber: formData?.creditCardNumber,
          expiryDate: formData?.expiryDate,
          cvv: formData?.cvv,
        },
        address: {
          addressLine1: address?.addressLine1,
          addressLine2: address?.addressLine2,
          city: address?.city,
          state: address?.state,
          zipCode: address?.zipCode,
        },
      };

      try {
        await axios.post(`${baseURL}/updateUser`, data);
      } catch (error) {
        console.error("Error adding credit card:", error);
      }

      try {
        await axios.post(`${baseURL}/orders`, orderData);
      } catch (error) {
        console.error("Error placing order:", error);
      }
    }
    window.alert("Order Placed Successfully.");
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
          ) : tab === "creditCard" ? (
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

              {userData?.creditCardNumber && (
                <div className="saved-card-section">
                  <h3>Or Use Saved Card</h3>
                  <p>
                    Card ending in **** {userData.creditCardNumber.slice(-4)}
                  </p>
                  <button
                    className="use-saved-card-btn"
                    onClick={handleUseSavedCard}
                  >
                    Use this Card
                  </button>
                </div>
              )}

              {useSavedCard && (
                <div className="saved-card-cvv-form">
                  <h4>Enter CVV for Saved Card</h4>
                  <input
                    type="password"
                    name="cvvInput"
                    placeholder="CVV"
                    value={cvvInput}
                    onChange={handleSavedCardCVV}
                    required
                    maxLength={3}
                  />
                  {cvvError && (
                    <p className="error-message">
                      Invalid CVV. Please try again.
                    </p>
                  )}
                  <button
                    className="verify-saved-card-btn"
                    onClick={handleSavedCardSelect}
                  >
                    Verify
                  </button>
                </div>
              )}

              <div className="checkout-actions">
                <button className="back-btn" onClick={handleBack}>
                  Back
                </button>
                <button className="proceed-btn" onClick={handlePay}>
                  Pay
                </button>
              </div>
            </div>
          ) : (
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
          )}
        </div>
      </div>
    </div>
  );
};

export default Payment;
