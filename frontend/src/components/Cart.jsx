import React, { useState, useEffect } from "react";
import axios from "axios";
import Aside from "./Aside";
import HeaderComponent from "./HeaderComponent";
import "./Cart.css";

const Cart = () => {
  const [cartItems, setCartItems] = useState([]);
  const [loading, setLoading] = useState(true);

  // Function to fetch the cart items for the user
  const fetchCartItems = async () => {
    const userId = localStorage.getItem("userId"); // Assuming userId is stored in localStorage

    try {
      const response = await axios.get(`/cart?userId=${userId}`);
      setCartItems(response.data);
      setLoading(false);
    } catch (error) {
      console.error("Error fetching cart items:", error);
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCartItems();
  }, []);

  // Function to handle removing an item from the cart
  const removeItemFromCart = async (productId) => {
    const userId = localStorage.getItem("userId");

    try {
      await axios.post("/cart", {
        action: "removeFromCart",
        userId: userId,
        productId: productId,
      });
      // After removing the item, refresh the cart
      fetchCartItems();
    } catch (error) {
      console.error("Error removing item from cart:", error);
    }
  };

  // Function to clear the entire cart
  const clearCart = async () => {
    const userId = localStorage.getItem("userId");

    try {
      await axios.post("/cart", {
        action: "clearCart",
        userId: userId,
      });
      // After clearing the cart, refresh the cart
      fetchCartItems();
    } catch (error) {
      console.error("Error clearing cart:", error);
    }
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
              <ul>
                {cartItems.map((item, index) => (
                  <li key={index} className="cart-item">
                    <p><strong>Product:</strong> {item.productId}</p>
                    <p><strong>Quantity:</strong> {item.quantity}</p>
                    <p><strong>Total Price:</strong> ${item.totalPrice.toFixed(2)}</p>
                    <button
                      className="remove-btn"
                      onClick={() => removeItemFromCart(item.productId)}
                    >
                      Remove
                    </button>
                  </li>
                ))}
              </ul>
              <button className="clear-cart-btn" onClick={clearCart}>
                Clear Cart
              </button>
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default Cart;
