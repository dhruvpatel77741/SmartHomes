import React, { useState, useEffect } from "react";
import "./List.css";
import axios from "axios";
import Aside from "./Aside";
import ViewCustomer from "./Users/ViewUser";
import AddCustomer from "./Users/AddUser";

const baseURL = process.env.REACT_APP_API_BASE_URL;
const image = process.env.PUBLIC_URL;

const UserList = () => {
  const userType = localStorage.getItem("userType");

  const [dataShow, setDataShow] = useState([]);
  const getData = async () => {
    let apiUrl =
      userType === "Salesman" ? `${baseURL}/customers` : `${baseURL}/salesman`;
    try {
      const resp = await axios.get(apiUrl, {});
      const data = resp.data;
      setDataShow(data);
      console.log(data);
    } catch (err) {
      console.log("Error:", err);
    }
  };
  useEffect(() => {
    getData();
  });

  const reverseData = [...dataShow].reverse();

  const [isAddModelOpen, setAddModelOpen] = useState(false);
  const [isViewCustomerOpen, setViewCustomerOpen] = useState(false);
  // const [isEditCustomerOpen, setEditCustomerOpen] = useState(false);
  const [showDeleteConfirmation, setShowDeleteConfirmation] = useState(false);

  const viewCustomer = (id) => {
    localStorage.setItem("customerId", id);

    setViewCustomerOpen(true);
    document.body.classList.add("page-modal-open");
  };

  // const editCustomer = (id) => {
  //   localStorage.setItem("customerId", id);

  //   setEditCustomerOpen(true);
  // };

  const getIdToDelete = (id) => {
    localStorage.setItem("customerId", id);
    setShowDeleteConfirmation(true);
  };

  const handleDelete = async (e) => {
    e.preventDefault();
    const id = localStorage.getItem("customerId");
    try {
      const response = await axios.delete(`${baseURL}/updateUser?id=${id}`);

      if (response.status === 200 || response.status === 201) {
        alert(`This user has been deleted`);
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
                <b>{userType === "StoreManager" ? `Salesmans` : `Customers`}</b>
              </div>

              <div className="team-btn" style={{ left: "1075px" }}>
                <button
                  className="btn-add"
                  onClick={() => {
                    setAddModelOpen(true);
                  }}
                >
                  Add
                </button>
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
                  <td className="team-data-main">ID</td>
                  <td className="team-data-role">Name</td>
                  <td className="team-data-email" style={{ width: "24%" }}>
                    Username
                  </td>
                  <td className="team-data-actions">Actions</td>
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
                          {data?.id}
                        </td>
                        <td className="team-data-role">{data?.name}</td>
                        <td className="team-data-email">{data?.username}</td>

                        <td className="team-data-actions">
                          {/* <button
                            className="add-action"
                            onClick={() => editCustomer(data._id)}
                          >
                            <img
                              src={`${image}/Assets/Teams/add-btn.svg`}
                              alt=""
                            />
                          </button> */}
                          <button
                            className="view-action"
                            onClick={() => viewCustomer(data.id)}
                          >
                            <img
                              src={`${image}/Assets/Teams/view.svg`}
                              alt=""
                            />
                          </button>
                          <button
                            className="notview-action"
                            onClick={() => getIdToDelete(data.id)}
                          >
                            <img
                              src={`${image}/Assets/Teams/not-view.svg`}
                              alt=""
                            />
                          </button>
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
                    <p>No Salesmans Found</p>
                  </div>
                )}
              </table>
            </div>
          </div>

          <div className="row">
            {isAddModelOpen && (
              <AddCustomer
                onClose={() => {
                  setAddModelOpen(false);
                }}
              />
            )}
            {/* {isEditCustomerOpen && (
              <EditInstallationTeam
                onClose={() => {
                  setEditCustomerOpen(false);
                  localStorage.removeItem("installerId");
                }}
              />
            )} */}
            {isViewCustomerOpen && (
              <ViewCustomer
                onClose={() => {
                  setViewCustomerOpen(false);
                  localStorage.removeItem("customerId");
                }}
              />
            )}

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
                        localStorage.removeItem("customerId");
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

export default UserList;
