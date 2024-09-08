import React, { useState } from "react";
import { useLocation } from "react-router-dom";
import Aside from "./Aside";
import HeaderComponent from "./HeaderComponent";
import "./ProductDetails.css";

const ProductDetails = () => {
  const location = useLocation();
  const { product } = location?.state || {};

  // States to handle the cart quantity, total price, warranty, and accessories
  const [quantity, setQuantity] = useState(0);
  const [totalPrice, setTotalPrice] = useState(0);
  const [warrantyAdded, setWarrantyAdded] = useState(false);
  const [selectedAccessories, setSelectedAccessories] = useState([]);

  if (!product) {
    return <p>Loading...</p>;
  }

  // Add or Remove Warranty
  const toggleWarranty = () => {
    if (warrantyAdded) {
      setTotalPrice(totalPrice - product.warranty.price);
    } else {
      setTotalPrice(totalPrice + product.warranty.price);
    }
    setWarrantyAdded(!warrantyAdded);
  };

  // Add or Remove Accessory
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

  // Handle Quantity Increment
  const incrementQuantity = () => {
    setQuantity(quantity + 1);
    setTotalPrice(totalPrice + product.price);
  };

  // Handle Quantity Decrement
  const decrementQuantity = () => {
    if (quantity > 0) {
      setQuantity(quantity - 1);
      totalPrice - product.price === 0.0
        ? setTotalPrice(0)
        : setTotalPrice(totalPrice - product.price);
    }
  };

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
              {product.warranty.available
                ? `Available for $${product.warranty.price}`
                : "Not Available"}
            </p>
          </div>

          <div className="product-detail-right">
            {/* Total Amount Display */}
            <p style={{ fontWeight: "bold", fontSize: "18px" }}>
              Total: ${totalPrice.toFixed(2)}
            </p>

            {/* Add to Cart Counter */}
            {quantity === 0 ? (
              <button
                className="add-to-cart-btn"
                onClick={() => {
                  setQuantity(1);
                  setTotalPrice(product.price);
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

            {/* Option to Add Warranty */}
            {product.warranty.available && (
              <div>
                <input
                  type="checkbox"
                  id="add-warranty"
                  checked={warrantyAdded}
                  onChange={toggleWarranty}
                />
                <label htmlFor="add-warranty">
                  Add Warranty for ${product.warranty.price}
                </label>
              </div>
            )}
          </div>
        </div>

        {/* Accessories Section */}
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
      </div>
    </div>
  );
};

export default ProductDetails;
