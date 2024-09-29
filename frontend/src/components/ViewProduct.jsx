import React, { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import axios from "axios";
import "./List.css";

const baseURL = process.env.REACT_APP_API_BASE_URL;

const ViewProduct = ({ onClose }) => {
  const location = useLocation();

  const id = localStorage.getItem("productId");

  const [dataShow, setDataShow] = useState();
  useEffect(() => {
    const getData = async () => {
      const id = localStorage.getItem("productId");
      let apiUrl = `${baseURL}/manageProducts`;
      try {
        const resp = await axios.get(apiUrl);
        const data = resp.data;
        const product = data.find((item) => item?.id.toString() === id);
        setDataShow(product);
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
          style={{ height: "500px", width: "600px" }}
        >
          <div className="profile-model-header">
            <h3 style={{ display: "flex", gap: "10px" }}> View Product</h3>
            <button
              className="invite-model-close-btn"
              onClick={() => {
                localStorage.removeItem("productId");
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
              <b>Product Information</b>
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
                  Category: {dataShow?.category}
                </span>
                <span
                  style={{
                    display: "flex",
                    color: "rgba(44, 58, 100, 1)",
                    fontWeight: "400",
                    fontSize: "16px",
                  }}
                >
                  Description: {dataShow?.description}
                </span>
                <span
                  style={{
                    display: "flex",
                    color: "rgba(44, 58, 100, 1)",
                    fontWeight: "400",
                    fontSize: "16px",
                  }}
                >
                  Price: ${dataShow?.price}
                </span>
              </div>
            </div>
          </div>
          <br />
          <div className="want-serve">
            <b>Accessories</b>
            <div style={{ display: "flex", flexDirection: "column" }}>
              {dataShow?.accessories?.length > 0 ? (
                dataShow.accessories.map((accessory, index) => (
                  <div key={index} style={{ marginTop: "10px" }}>
                    <span
                      style={{
                        display: "flex",
                        color: "rgba(44, 58, 100, 1)",
                        fontWeight: "400",
                        fontSize: "16px",
                      }}
                    >
                      Accessory {index + 1}: {accessory.name}
                    </span>
                    <span
                      style={{
                        display: "flex",
                        color: "rgba(44, 58, 100, 1)",
                        fontWeight: "400",
                        fontSize: "16px",
                      }}
                    >
                      Price: ${accessory.price}
                    </span>
                  </div>
                ))
              ) : (
                <span>No accessories available</span>
              )}
            </div>
          </div>
          <br />
          <div className="row">
            <div className="want-serve">
              <b>Other Information</b>
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
                  {dataShow?.warranty
                    ? `Warranty: $${dataShow.warrantyPrice}`
                    : "No warranty available"}
                </span>
                <span
                  style={{
                    display: "flex",
                    color: "rgba(44, 58, 100, 1)",
                    fontWeight: "400",
                    fontSize: "16px",
                  }}
                >
                  {dataShow?.specialDiscount
                    ? `Discount: $${dataShow.discountPrice}`
                    : "No discount available"}
                </span>
                <span
                  style={{
                    display: "flex",
                    color: "rgba(44, 58, 100, 1)",
                    fontWeight: "400",
                    fontSize: "16px",
                  }}
                >
                  {dataShow?.manufacturerRebate
                    ? `Manufacturer Rebate: $${dataShow.rebatePrice}`
                    : "No rebate available"}
                </span>
              </div>
            </div>
          </div>
          <div className="row">
            <span className="viewbottom-border"></span>
          </div>
        </div>
      </div>
    </>
  );
};

export default ViewProduct;
