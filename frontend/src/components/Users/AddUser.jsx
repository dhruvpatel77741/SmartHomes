import React, { useState } from "react";
import axios from "axios";
import "../List.css";

const baseURL = process.env.REACT_APP_API_BASE_URL;

const AddCustomer = ({ onClose }) => {
  const userType = localStorage.getItem("userType");

  const [newUser, setNewUser] = useState({
    name: "",
    username: "",
    password: "",
    userType: userType === "StoreManager" ? "Salesman" : "Customer",
  });
  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    if (type === "checkbox") {
      setNewUser({ ...newUser, [name]: checked });
    } else {
      setNewUser({ ...newUser, [name]: value });
    }
  };

  const handleSubmit = async () => {
    try {
      const response = await axios.post(`${baseURL}/signup`, newUser);
      if (response.status === 200 || response.status === 201) {
        window.alert("User Added Successfully");
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
            <h3 style={{ display: "flex", gap: "10px" }}>
              Add {userType === "StoreManager" ? `Salesman` : `Customer`}
            </h3>
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
              <b>
                {userType === "StoreManager" ? `Salesman` : `Customer`}{" "}
                Information
              </b>
              <div style={{ display: "flex", flexDirection: "column" }}>
                <input
                  type="text"
                  name="name"
                  className="inputFieldCustomer"
                  value={newUser.name}
                  onChange={handleChange}
                  style={{ marginTop: "5px" }}
                  placeholder="Name"
                />
                <input
                  type="text"
                  name="username"
                  className="inputFieldCustomer"
                  value={newUser.username}
                  onChange={handleChange}
                  style={{ marginTop: "5px" }}
                  placeholder="Username"
                />
                <input
                  type="text"
                  name="password"
                  className="inputFieldCustomer"
                  value={newUser.password}
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
