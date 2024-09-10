import React, { useState, useEffect } from "react";
import "./List.css";
import axios from "axios";
import Aside from "./Aside";

const baseURL = process.env.REACT_APP_API_BASE_URL;

const OrdersList = () => {
  const userType = localStorage.getItem("userType");
  const userId = localStorage.getItem("userId");

  const [dataShow, setDataShow] = useState([]);
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
  useEffect(() => {
    getData();
  }, []);

  const reverseData = [...dataShow].reverse();

  const [showDeleteConfirmation, setShowDeleteConfirmation] = useState(false);

  const handleUpdate = async (data) => {
    const status = data?.status === "Order Placed" ? "In-Transit" : "Delivered";
    try {
      const response = await axios.put(`${baseURL}/orders`, {
        orderId: data?.orderId,
        status: status,
      });
      if (response.status === 200 || response.status === 200) {
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
                <th
                  className="team-main-bg TeamsTableHeading"
                  style={{ width: "100%" }}
                >
                  <td className="team-data-main">Sr. No.</td>
                  <td className="team-data-role">Product Name</td>
                  <td className="team-data-email" style={{ width: "24%" }}>
                    {userType === "Customer" ? "Price" : "Customer Name"}
                  </td>
                  <td className="team-data-actions">
                    {userType === "Customer" ? "Status" : "Actions"}
                  </td>
                </th>
                {reverseData.length > 0 ? (
                  reverseData.map((data, groupIndex) => {
                    return (
                      <tr className="TeamDetailsRowData" key={groupIndex}>
                        <td
                          className="team-data-main"
                          style={{
                            overflowWrap: "break-word",
                          }}
                        >
                          {groupIndex + 1}
                        </td>
                        <td className="team-data-role">
                          {data?.orderData?.product}
                        </td>
                        <td className="team-data-email">
                          {userType === "Customer"
                            ? `$${data?.orderData?.price.toFixed(2)}`
                            : `${data?.username}`}
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
                      </tr>
                    );
                  })
                ) : (
                  <div
                    style={{
                      display: "flex",
                      justifyContent: "center",
                      alignItems: "center",
                      padding: "20px 100px 0px 20px",
                    }}
                  >
                    <p>No Orders Found</p>
                  </div>
                )}
              </table>
            </div>
          </div>

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