import React, { useState } from "react";
import Aside from "../Aside";
import axios from "axios";
import "./OpenTicket.css";

const baseURL = process.env.REACT_APP_API_BASE_URL;

const formatTicketDate = (ticketDate) => {
  const date = new Date(ticketDate);
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const year = date.getFullYear();
  return `${month}/${day}/${year}`;
};

const TicketStatus = () => {
  const [ticketId, setTicketId] = useState("");
  const [ticketStatus, setTicketStatus] = useState(null);
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await axios.get(`${baseURL}/ticket?ticketId=${ticketId}`);
      setTicketStatus(res?.data);
      setError(null);
    } catch (error) {
      setError(error.response?.data?.message || "Failed to fetch ticket status");
      setTicketStatus(null);
    }
  };

  return (
    <div className="MainOuterContainer">
      <Aside />
      <div className="open-ticket-form">
        <h2>Ticket Status</h2>
        <form onSubmit={handleSubmit} className="ticket-form">
          <label>
            Ticket ID:
            <input
              type="text"
              value={ticketId}
              onChange={(e) => setTicketId(e.target.value)}
              required
            />
          </label>
          <button type="submit">Get Status</button>
        </form>
        {error && <p className="error">{error}</p>}
        {ticketStatus && (
          <div>
            <p>Ticket Number: {ticketStatus.ticketId}</p>
            <p>Decision: {ticketStatus.decision}</p>
            <p>Created Date: {formatTicketDate(ticketStatus.ticketDate)}</p>
            <p>Status: In-Progress</p>
          </div>
        )}
        {!ticketStatus && !error && <p>Your current ticket status will be displayed here.</p>}
      </div>
    </div>
  );
};

export default TicketStatus;
