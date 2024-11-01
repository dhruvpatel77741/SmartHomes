import React, { useState } from "react";
import Aside from "../Aside";
import axios from "axios";
import "./OpenTicket.css";

const baseURL = process.env.REACT_APP_API_BASE_URL;

const OpenTicket = () => {
  const userId = localStorage.getItem("userId");
  const [orderId, setOrderId] = useState("");
  const [description, setDescription] = useState("");
  const [image, setImage] = useState(null);
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    const formData = new FormData();
    formData.append("userId", userId);
    formData.append("orderId", orderId);
    formData.append("description", description);
    if (image) {
      formData.append("image", image);
    }

    try {
      const res = await axios.post(`${baseURL}/ticket`, formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });

      window.alert(
        `Ticket Number: ${res.data.ticketNumber}\n ${res.data.decision}\n Copy and keep this ticket number with for further assistant.`
      );

      setOrderId("");
      setDescription("");
      setImage(null);
      setError(null);
    } catch (error) {
      setError(error.response?.data?.message || "Failed to submit ticket");
    }
  };

  return (
    <div className="MainOuterContainer">
      <Aside />
      <div className="open-ticket-form">
        <h2>Open a Ticket</h2>
        <form onSubmit={handleSubmit} className="ticket-form">
          <label>
            Order ID:
            <input
              type="text"
              value={orderId}
              onChange={(e) => setOrderId(e.target.value)}
              required
            />
          </label>
          <label>
            Description of Issue:
            <textarea
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              required
            />
          </label>
          <label>
            Upload an Image:
            <input
              type="file"
              accept="image/*"
              onChange={(e) => setImage(e.target.files[0])}
            />
          </label>
          <button type="submit">Submit Ticket</button>
        </form>
        {error && <p className="error">{error}</p>}
      </div>
    </div>
  );
};

export default OpenTicket;
