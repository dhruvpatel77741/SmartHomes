import React, { useState, useEffect } from "react";
import axios from "axios";
import "./ReviewPopup.css";

const baseURL = process.env.REACT_APP_API_BASE_URL;

const ReviewPopup = ({ product, userId, onClose }) => {
  const manufacturerRebate = product.manufacturerRebate;
  const [userAge, setUserAge] = useState(30);
  const [userGender, setUserGender] = useState("Male");
  const [userOccupation, setUserOccupation] = useState("Engineer");
  const [reviewRating, setReviewRating] = useState(5);
  const [reviewText, setReviewText] = useState("");

  const [order, setOrder] = useState({});
  const [store, setStore] = useState({});

  useEffect(() => {
    const fetchProduct = async () => {
      const productId = product?.productId;
      try {
        const productRes = await axios.get(`${baseURL}/manageProducts`);
        const filteredProduct = productRes?.data.filter(
          (product) => product.id === productId
        );
        setOrder(filteredProduct[0]);
      } catch (err) {
        console.error("Error fetching product:", err);
      }
    };

    const fetchStore = async () => {
      const storeId = product?.storeId;
      try {
        const storeRes = await axios.get(`${baseURL}/stores`);
        const filteredStore = storeRes?.data.filter(
          (store) => store.storeId === storeId
        );
        setStore(filteredStore[0]);
      } catch (err) {
        console.error("Error fetching store:", err);
      }
    };

    fetchProduct();
    fetchStore();
  }, [product]);

  const handleSubmit = async () => {
    const reviewData = {
      productModelName: order?.name || "Samsung Galaxy S21",
      productCategory: order?.category || "phone",
      productPrice: order?.price || 799.99,
      storeID: String(store?.storeId) || "SmartPortables of Chicago",
      storeZip: store?.zipCode || "60616",
      storeCity: store?.city || "Chicago",
      storeState: store?.state || "IL",
      productOnSale: order?.onSale || true,
      manufacturerName: "Jinko" || "Samsung",
      manufacturerRebate: manufacturerRebate || true,
      userID: userId || "user123",
      userAge: userAge,
      userGender: userGender,
      userOccupation: userOccupation,
      reviewRating: reviewRating,
      reviewDate: new Date().toISOString().split("T")[0],
      reviewText: reviewText || "Excellent performance and good quality.",
    };
    try {
      await axios.post(`${baseURL}/submitReview`, reviewData);
      alert("Review submitted successfully");
      onClose();
    } catch (err) {
      console.error("Error submitting review:", err);
    }
  };

  return (
    <div className="popup-overlay">
      <div className="popup-content">
        <h2>Submit Review</h2>
        <div className="form-group">
          <label>Product Name:</label>
          <input type="text" value={order?.name} readOnly />
        </div>
        <div className="form-group">
          <label>Product Category:</label>
          <input type="text" value={order?.category} readOnly />
        </div>
        <div className="form-group">
          <label>Product Price:</label>
          <input type="number" value={order?.price} readOnly />
        </div>
        <div className="form-group">
          <label>Store ID:</label>
          <input type="text" value={store?.storeId} readOnly />
        </div>
        <div className="form-group">
          <label>Store Zip:</label>
          <input type="text" value={store?.zipCode} readOnly />
        </div>
        <div className="form-group">
          <label>Store City:</label>
          <input type="text" value={store?.city} readOnly />
        </div>
        <div className="form-group">
          <label>Store State:</label>
          <input type="text" value={store?.state} readOnly />
        </div>
        <div className="form-group">
          <label>Manufacturer Name:</label>
          <input type="text" value="Jinko" readOnly />
        </div>
        <div className="form-group">
          <label>User Age:</label>
          <input
            type="number"
            value={userAge}
            onChange={(e) => setUserAge(Number(e.target.value))}
          />
        </div>
        <div className="form-group">
          <label>User Gender:</label>
          <select
            value={userGender}
            onChange={(e) => setUserGender(e.target.value)}
          >
            <option value="">Select Gender</option>
            <option value="Male">Male</option>
            <option value="Female">Female</option>
            <option value="Other">Other</option>
          </select>
        </div>
        <div className="form-group">
          <label>User Occupation:</label>
          <input
            type="text"
            value={userOccupation}
            onChange={(e) => setUserOccupation(e.target.value)}
          />
        </div>
        <div className="form-group">
          <label>Rating (1-5):</label>
          <input
            type="number"
            min="1"
            max="5"
            value={reviewRating}
            onChange={(e) => setReviewRating(Number(e.target.value))}
          />
        </div>
        <div className="form-group">
          <label>Review:</label>
          <textarea
            value={reviewText}
            onChange={(e) => setReviewText(e.target.value)}
          />
        </div>
        <div className="button-group">
          <button onClick={handleSubmit} className="submit-button">
            Submit
          </button>
          <button onClick={onClose} className="close-button">
            Close
          </button>
        </div>
      </div>
    </div>
  );
};

export default ReviewPopup;
