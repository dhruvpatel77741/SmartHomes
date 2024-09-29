import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import Aside from "./Aside";
import HeaderComponent from "./HeaderComponent";
import "./ProductDetails.css";
import axios from "axios";

const baseURL = process.env.REACT_APP_API_BASE_URL;

const ProductDetails = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { product } = location?.state || {};

  const [quantity, setQuantity] = useState(0);
  const [totalPrice, setTotalPrice] = useState(0);
  const [warrantyAdded, setWarrantyAdded] = useState(false);
  const [selectedAccessories, setSelectedAccessories] = useState([]);

  const [reviews, setReviews] = useState([]);

  const calculateProductPrice = () => {
    let finalPrice = product.price;

    if (product?.specialDiscount && product?.manufacturerRebate) {
      finalPrice = Math.min(product.discountPrice, product.rebatePrice);
    } else if (product?.specialDiscount) {
      finalPrice = product.discountPrice;
    } else if (product?.manufacturerRebate) {
      finalPrice = product.rebatePrice;
    }

    return finalPrice;
  };

  const getData = async () => {
    let apiUrl = `${baseURL}/getAllReviews`;
    try {
      const resp = await axios.get(apiUrl);
      const data = resp.data.reviews;
      const filteredReview = data.filter(
        (review) => review.productModelName === product.name
      );
      setReviews(filteredReview);
    } catch (err) {
      console.log("Error:", err);
    }
  };

  useEffect(() => {
    getData();
  });

  if (!product) {
    return <p>Loading...</p>;
  }

  const toggleWarranty = () => {
    if (warrantyAdded) {
      setTotalPrice(totalPrice - product?.warrantyPrice);
    } else {
      setTotalPrice(totalPrice + product?.warrantyPrice);
    }
    setWarrantyAdded(!warrantyAdded);
  };

  const toggleAccessory = (accessory) => {
    const isAccessorySelected = selectedAccessories.includes(accessory.name);
    if (isAccessorySelected) {
      setTotalPrice(totalPrice - accessory.price);
      setSelectedAccessories(
        selectedAccessories.filter((name) => name !== accessory.name)
      );
    } else {
      setTotalPrice(totalPrice + accessory.price);
      setSelectedAccessories([...selectedAccessories, accessory.name]);
    }
  };

  const incrementQuantity = () => {
    const productPrice = calculateProductPrice();
    setQuantity(quantity + 1);
    setTotalPrice(totalPrice + productPrice);
  };

  const decrementQuantity = () => {
    const productPrice = calculateProductPrice();
    if (quantity > 0) {
      setQuantity(quantity - 1);
      totalPrice - productPrice === 0.0
        ? setTotalPrice(0)
        : setTotalPrice(totalPrice - productPrice);
    }
  };

  const handleSubmit = async () => {
    const data = {
      action: "addToCart",
      userId: localStorage.getItem("userId"),
      productId: product.id,
      productName: product.name,
      productPrice: product.price,
      quantity: quantity,
      warrantyAdded: warrantyAdded,
      warrantyPrice: product?.warrantyPrice || 0,
      accessories: selectedAccessories.map((name) => {
        const accessory = product.accessories.find(
          (accessory) => accessory.name === name
        );
        return {
          name: accessory.name,
          price: accessory.price,
        };
      }),
    };

    try {
      const response = await axios.post(`${baseURL}/cart`, data);
      console.log("Success:", response.data);

      navigate("/cart");
    } catch (error) {
      console.error("Error:", error);
    }
  };

  console.log(reviews);

  return (
    <div className="MainOuterContainer">
      <Aside />
      <div className="main-part-ratailer">
        <HeaderComponent />
        <div className="product-detail-container">
          <div className="product-detail-left">
            <h2>{product.name}</h2>
            <p>{product.description}</p>
            <p style={{ fontWeight: "bold" }}>Price: ${product.price}</p>
            <p>Category: {product.category}</p>
            <p>
              Warranty:{" "}
              {product?.warranty === true
                ? `Available for $${product?.warrantyPrice}`
                : "Not Available"}
            </p>
            <p>
              Discounted Price:{" "}
              {product?.specialDiscount === true
                ? `$${product?.discountPrice}`
                : "Not Available"}
            </p>
            <p>
              Manufacturer Rebated Price:{" "}
              {product?.manufacturerRebate === true
                ? `$${product?.rebatePrice}`
                : "Not Available"}
            </p>
          </div>

          <div className="product-detail-right" style={{ marginLeft: "300px" }}>
            <p style={{ fontWeight: "bold", fontSize: "18px" }}>
              Total: ${totalPrice.toFixed(2)}
            </p>
            {quantity === 0 ? (
              <button
                className="add-to-cart-btn"
                onClick={() => {
                  setQuantity(1);
                  setTotalPrice(calculateProductPrice());
                }}
              >
                Add to Cart
              </button>
            ) : (
              <div className="quantity-control">
                <button onClick={decrementQuantity}>-</button>
                <span>{quantity}</span>
                <button onClick={incrementQuantity}>+</button>
              </div>
            )}

            {product?.warranty && (
              <div>
                <input
                  type="checkbox"
                  id="add-warranty"
                  checked={warrantyAdded}
                  onChange={toggleWarranty}
                />
                <label htmlFor="add-warranty">
                  Add Warranty for ${product?.warrantyPrice}
                </label>
              </div>
            )}
            <br />
            <button
              className="add-to-cart-btn"
              onClick={() => {
                handleSubmit();
              }}
            >
              Open Cart
            </button>
          </div>
        </div>

        <div className="accessories-section">
          <h3>Accessories</h3>
          <div className="accessory-cards">
            {product.accessories.map((accessory, index) => (
              <div
                key={index}
                className={`accessory-card ${
                  selectedAccessories.includes(accessory.name) ? "selected" : ""
                }`}
                onClick={() => toggleAccessory(accessory)}
              >
                <p>{accessory.name}</p>
                <p>Price: ${accessory.price}</p>
                <div style={{ display: "flex", justifyContent: "center" }}>
                  <span>
                    {selectedAccessories.includes(accessory.name)
                      ? "Click to Remove"
                      : "Click to Add"}
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>
        <br />

        <div className="reviews-section">
          <h3>Customer's Reviews</h3>
          {reviews.length > 0 ? (
            <div className="review-cards">
              {reviews.map((review, index) => (
                <div key={index} className="review-card">
                  <p style={{ fontWeight: "bold" }}>
                    Rating: {review.reviewRating}/5
                  </p>
                  <p>{review.reviewText}</p>
                  <p>
                    <span style={{ fontStyle: "italic" }}>
                      {review.userOccupation}, Age: {review.userAge},{" "}
                      {review.userGender}
                    </span>
                  </p>
                  <p style={{ fontSize: "12px", color: "gray" }}>
                    Review Date:{" "}
                    {new Date(review.reviewDate).toLocaleDateString()}
                  </p>
                </div>
              ))}
            </div>
          ) : (
            <p>No reviews available for this product.</p>
          )}
        </div>
      </div>
    </div>
  );
};

export default ProductDetails;
