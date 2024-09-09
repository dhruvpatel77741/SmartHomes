import React, { useState, useEffect } from "react";
import axios from "axios";
import Aside from "./Aside";
import HeaderComponent from "./HeaderComponent";
import "./Cart.css";

const baseURL = process.env.REACT_APP_API_BASE_URL;

const Cart = () => {
  const [cartItems, setCartItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [totalAmount, setTotalAmount] = useState(0);

  const fetchCartItems = async () => {
    const userId = localStorage.getItem("userId");

    try {
      const response = await axios.get(`${baseURL}/cart?userId=${userId}`);
      console.log(response);
      setCartItems(response.data);
      calculateTotalAmount(response.data);
      setLoading(false);
    } catch (error) {
      console.error("Error fetching cart items:", error);
      setLoading(false);
    }
  };

  const calculateTotalAmount = (items) => {
    const total = items.reduce((acc, item) => acc + item.totalPrice, 0);
    setTotalAmount(total);
  };

  useEffect(() => {
    fetchCartItems();
  }, []);

  const removeItemFromCart = async (productId) => {
    const userId = localStorage.getItem("userId");

    try {
      await axios.post(`${baseURL}/cart`, {
        action: "removeFromCart",
        userId: userId,
        productId: productId,
      });
      fetchCartItems();
    } catch (error) {
      console.error("Error removing item from cart:", error);
    }
  };

  const clearCart = async () => {
    const userId = localStorage.getItem("userId");

    try {
      await axios.post(`${baseURL}/cart`, {
        action: "clearCart",
        userId: userId,
      });
      fetchCartItems();
    } catch (error) {
      console.error("Error clearing cart:", error);
    }
  };

  const handleCheckout = () => {
    console.log("Proceeding to checkout...");
  };

  if (loading) {
    return <p>Loading cart...</p>;
  }

  return (
    <div className="MainOuterContainer">
      <Aside />
      <div className="main-part">
        <HeaderComponent />
        <div className="cart-container">
          <h2>Your Cart</h2>
          {cartItems.length === 0 ? (
            <p>Your cart is empty.</p>
          ) : (
            <>
              <div className="total-price">
                <h3>Total Amount: ${totalAmount.toFixed(2)}</h3>
              </div>
              <ul>
                {cartItems.map((item, index) => (
                  <li key={index} className="cart-item">
                    <div className="item-details">
                      <p>
                        <strong>Product:</strong> {item.productName}
                      </p>
                      <p>
                        <strong>Quantity:</strong> {item.quantity}
                      </p>
                      <p className="accessories">
                        {item.accessories.length > 0 && (
                          <>
                            <strong>Accessories:</strong>
                            {item.accessories.map((acc, idx) => (
                              <span key={idx} className="accessory-item">
                                {acc.name} (${acc.price.toFixed(2)})
                                {idx < item.accessories.length - 1 ? ", " : ""}
                              </span>
                            ))}
                          </>
                        )}
                      </p>
                    </div>
                    <p className="item-total-price">
                      <strong>Total Price:</strong> $
                      {item.totalPrice.toFixed(2)}
                    </p>
                    <button
                      className="remove-btn"
                      onClick={() => removeItemFromCart(item.productId)}
                    >
                      Remove
                    </button>
                  </li>
                ))}
              </ul>
              <div className="cart-actions">
                <button className="clear-cart-btn" onClick={clearCart}>
                  Clear Cart
                </button>
                <button className="checkout-btn" onClick={handleCheckout}>
                  Checkout
                </button>
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default Cart;
