import React, { useState, useEffect } from "react";
import "./List.css";
import axios from "axios";
import Aside from "./Aside";
import ReviewPopup from "./ReviewPopup";

const baseURL = process.env.REACT_APP_API_BASE_URL;

const OrdersList = () => {
  const userType = localStorage.getItem("userType");
  const userId = localStorage.getItem("userId");

  const [dataShow, setDataShow] = useState([]);
  const [users, setUsers] = useState({});
  const [products, setProducts] = useState({});

  const getData = async () => {
    const endPoint =
      userType === "Customer" ? `orders?userId=${userId}` : `orders`;
    let apiUrl = `${baseURL}/${endPoint}`;
    try {
      const resp = await axios.get(apiUrl);
      const data = resp.data.orders;
      setDataShow(data);
    } catch (err) {
      console.log("Error:", err);
    }
  };

  const getUsers = async () => {
    try {
      const resp = await axios.get(`${baseURL}/users`);
      const usersData = resp?.data.reduce((acc, user) => {
        acc[user.id] = user.name;
        return acc;
      }, {});
      setUsers(usersData);
    } catch (err) {
      console.log("Error fetching users:", err);
    }
  };

  const getProducts = async () => {
    try {
      const resp = await axios.get(`${baseURL}/manageProducts`);
      const productsData = resp?.data.reduce((acc, product) => {
        acc[product.id] = product.name;
        return acc;
      }, {});
      setProducts(productsData);
    } catch (err) {
      console.log("Error fetching products:", err);
    }
  };

  useEffect(() => {
    getData();
    getUsers();
    getProducts();
  });

  const reverseData = [...dataShow].reverse();

  const [showDeleteConfirmation, setShowDeleteConfirmation] = useState(false);

  const handleUpdate = async (data) => {
    const status = data?.status === "Order Placed" ? "In-Transit" : "Delivered";
    try {
      const response = await axios.put(`${baseURL}/orders`, {
        orderId: data?.orderId,
        status: status,
      });
      if (response.status === 200 || response.status === 201) {
        window.alert("Order Details Updated Successfully");
        window.location.reload();
      }
    } catch (err) {
      console.log("Error updating order:", err);
    }
  };

  const getIdToDelete = (id) => {
    localStorage.setItem("orderId", id);
    setShowDeleteConfirmation(true);
  };

  const handleDelete = async (e) => {
    e.preventDefault();
    const orderId = localStorage.getItem("orderId");
    try {
      const response = await axios.delete(
        `${baseURL}/orders?orderId=${orderId}`
      );
      if (response.status === 200 || response.status === 201) {
        alert(`The order has been deleted`);
        localStorage.removeItem("orderId");
        window.location.reload();
      } else {
        console.log("Error: " + (response.data || response.statusText));
      }
    } catch (error) {
      console.log("Error status:", error.response?.status);
    }
    setShowDeleteConfirmation(false);
  };

  const [selectedProduct, setSelectedProduct] = useState(null);
  const [showReviewPopup, setShowReviewPopup] = useState(false);

  const openReviewPopup = (order) => {
    setSelectedProduct(order);
    setShowReviewPopup(true);
  };

  const closeReviewPopup = () => {
    setShowReviewPopup(false);
    setSelectedProduct(null);
  };

  return (
    <div id="myModel" className="modal">
      <div style={{ display: "flex" }}>
        <div>
          <Aside />
        </div>
        <div className="main-part-ratailer">
          <div className="row">
            <div className="TemsTableHeadingContainer">
              <div style={{ padding: "10px" }}>
                <b>Orders</b>
              </div>
            </div>
            <div>
              <table
                style={{
                  flexDirection: "column",
                  gap: "10px",
                  textAlign: "center",
                  overflowY: "scroll",
                }}
                className="team-details"
              >
                <thead>
                  <tr
                    className="team-main-bg TeamsTableHeading"
                    style={{ width: "100%" }}
                  >
                    <td className="team-data-main">Order ID</td>
                    <td className="team-data-role">Product Name</td>
                    <td className="team-data-email" style={{ width: "24%" }}>
                      {userType === "Customer" ? "Price" : "Customer Name"}
                    </td>
                    <td className="team-data-actions">
                      {userType === "Customer" ? "Status" : "Actions"}
                    </td>
                    {userType === "Customer" ? (
                      <>
                        <td className="team-data-actions">Date</td>
                        <td className="team-data-actions">Review</td>
                      </>
                    ) : null}
                  </tr>
                </thead>
                <tbody>
                  {reverseData.length > 0 ? (
                    reverseData.map((data, groupIndex) => {
                      const productName =
                        products[data?.productId] || "Unknown Product";
                      const customerName =
                        users[data?.userId] || "Unknown Customer";

                      return (
                        <tr className="TeamDetailsRowData" key={groupIndex}>
                          <td
                            className="team-data-main"
                            style={{
                              overflowWrap: "break-word",
                            }}
                          >
                            {data?.orderId}
                          </td>
                          <td className="team-data-role">{productName}</td>
                          <td className="team-data-email">
                            {userType === "Customer"
                              ? `$${data?.total}`
                              : `${customerName}`}
                          </td>

                          <td className="team-data-actions">
                            {userType === "Customer" ? (
                              data?.status
                            ) : (
                              <>
                                <button
                                  className="notview-action"
                                  onClick={() => handleUpdate(data)}
                                  style={{
                                    backgroundColor: "blue",
                                    color: "white",
                                    border: "none",
                                    padding: "8px 16px",
                                    fontSize: "16px",
                                    borderRadius: "4px",
                                    cursor: "pointer",
                                    fontWeight: "bold",
                                  }}
                                >
                                  Update
                                </button>
                                <button
                                  className="notview-action"
                                  onClick={() => getIdToDelete(data?.orderId)}
                                  style={{
                                    backgroundColor: "#ff4d4d",
                                    color: "white",
                                    border: "none",
                                    padding: "8px 16px",
                                    fontSize: "16px",
                                    borderRadius: "4px",
                                    cursor: "pointer",
                                    fontWeight: "bold",
                                  }}
                                >
                                  DELETE
                                </button>
                              </>
                            )}
                          </td>
                          {userType === "Customer" ? (
                            <>
                              <td className="team-data-actions">
                                {data?.orderDate}
                              </td>
                              <td className="team-data-actions">
                                <button onClick={() => openReviewPopup(data)}>
                                  Review
                                </button>
                              </td>
                            </>
                          ) : null}
                        </tr>
                      );
                    })
                  ) : (
                    <tr>
                      <td colSpan="4">No Orders Found</td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>

          {showReviewPopup && selectedProduct && (
            <ReviewPopup
              product={selectedProduct}
              userId={userId}
              onClose={closeReviewPopup}
            />
          )}

          <div className="row">
            {showDeleteConfirmation && (
              <div className="invite-model-backdrop">
                <div
                  className="delete-model-content"
                  style={{
                    width: "502px",
                    backgroundColor: "white",
                    borderRadius: "10px",
                  }}
                >
                  <h2 style={{ display: "flex", justifyContent: "center" }}>
                    Are you sure you want to delete?
                  </h2>
                  <div
                    style={{
                      display: "flex",
                      justifyContent: "center",
                      alignItems: "center",
                    }}
                  >
                    <button className="btn-cancel" onClick={handleDelete}>
                      Yes
                    </button>
                    <button
                      className="button-invite"
                      onClick={() => {
                        localStorage.removeItem("orderId");
                        window.location.reload();
                      }}
                    >
                      No
                    </button>
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default OrdersList;
