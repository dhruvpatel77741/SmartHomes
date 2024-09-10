import React, { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import axios from "axios";
import "../List.css";

const baseURL = process.env.REACT_APP_API_BASE_URL;

const ViewCustomer = ({ onClose }) => {
  const location = useLocation();

  const id = localStorage.getItem("customerId");

  const [dataShow, setDataShow] = useState();
  useEffect(() => {
    const getData = async () => {
      const id = localStorage.getItem("customerId");
      let apiUrl = `${baseURL}/customers`;
      try {
        const resp = await axios.get(apiUrl);
        const data = resp.data;
        const customer = data.find((item) => item?._id == id);
        console.log(data);
        setDataShow(customer);
      } catch (err) {
        console.log("Error:", err);
      }
    };
    getData();
  }, [id, location.pathname]);

  return (
    <>
      <div className="profileview-model-backdrop">
        <div
          className="profileview-model-content"
          style={{ height: "300px", width: "400px", borderRadius: "10px" }}
        >
          <div className="profile-model-header">
            <h3 style={{ display: "flex", gap: "10px" }}> View Customer</h3>
            <button
              className="invite-model-close-btn"
              onClick={() => {
                localStorage.removeItem("customerId");
                onClose();
              }}
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
                <span
                  style={{
                    display: "flex",
                    marginTop: "5px",
                    color: "rgba(44, 58, 100, 1)",
                    fontWeight: "400",
                    fontSize: "16px",
                  }}
                >
                  Name: {dataShow?.name}
                </span>
                <span
                  style={{
                    display: "flex",
                    color: "rgba(44, 58, 100, 1)",
                    fontWeight: "400",
                    fontSize: "16px",
                  }}
                >
                  Username: {dataShow?.username}
                </span>
              </div>
            </div>
          </div>
          <br />

          <br />

          <div className="row">
            <span className="viewbottom-border"></span>
          </div>
        </div>
      </div>
    </>
  );
};

export default ViewCustomer;
