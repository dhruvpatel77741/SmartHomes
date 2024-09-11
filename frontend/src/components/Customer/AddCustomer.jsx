import React, { useState } from "react";
import axios from "axios";
import "../List.css";

const baseURL = process.env.REACT_APP_API_BASE_URL;

const AddCustomer = ({ onClose }) => {
  const [newCustomer, setNewCustomer] = useState({
    name: "",
    username: "",
    password: "",
    userType: "Customer"
  });

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    if (type === "checkbox") {
      setNewCustomer({ ...newCustomer, [name]: checked });
    } else {
      setNewCustomer({ ...newCustomer, [name]: value });
    }
  };

  const handleSubmit = async () => {
    try {
      const response = await axios.post(`${baseURL}/signup`, newCustomer);
      if (response.status === 200 || response.status === 201) {
        window.alert("Customer Added Successfully");
        window.location.reload();
      }
    } catch (err) {
      console.log("Error adding customer:", err);
    }
  };

  return (
    <>
      <div className="profileview-model-backdrop">
        <div
          className="profileview-model-content"
          style={{ height: "300px", width: "400px" }}
        >
          <div className="profile-model-header">
            <h3 style={{ display: "flex", gap: "10px" }}>Add Customer</h3>
            <button
              className="invite-model-close-btn"
              onClick={() => onClose()}
            >
              ✕
            </button>
          </div>
          <div className="row">
            <span className="viewbottom-border"></span>
          </div>
          <div className="row">
            <div className="want-serve">
              <b>Customer Information</b>
              <div style={{ display: "flex", flexDirection: "column" }}>
                <input
                  type="text"
                  name="name"
                  className="inputFieldCustomer"
                  value={newCustomer.name}
                  onChange={handleChange}
                  style={{ marginTop: "5px" }}
                  placeholder="Customer Name"
                />
                <input
                  type="text"
                  name="username"
                  className="inputFieldCustomer"
                  value={newCustomer.username}
                  onChange={handleChange}
                  style={{ marginTop: "5px" }}
                  placeholder="Username"
                />
                <input
                  type="text"
                  name="password"
                  className="inputFieldCustomer"
                  value={newCustomer.password}
                  onChange={handleChange}
                  style={{ marginTop: "5px" }}
                  placeholder="Password"
                />
              </div>
            </div>
          </div>
          <div className="row">
            <span className="viewbottom-border"></span>
          </div>
          <div style={{ display: "flex" }}>
            <div className="add-model-actions">
              <button onClick={() => onClose()} className="submit-hover">
                Cancel
              </button>
              <button onClick={handleSubmit} className="submit-hover">
                Submit
              </button>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default AddCustomer;
