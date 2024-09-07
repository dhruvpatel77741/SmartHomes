import React, { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import "./List.css";
import axios from "axios";
import Aside from "./Aside";
import ViewProduct from "./ViewProduct";
import EditProduct from "./EditProduct";
import AddProduct from "./AddProduct";

const baseURL = process.env.REACT_APP_API_BASE_URL;
const image = process.env.PUBLIC_URL;

const ProductsList = () => {
  const location = useLocation();

  const [dataShow, setDataShow] = useState([]);
  const getData = async () => {
    let apiUrl = `${baseURL}/manageProducts`;
    try {
      const resp = await axios.get(apiUrl);
      const data = resp.data;
      console.log(data);
      setDataShow(data);
    } catch (err) {
      console.log("Error:", err);
    }
  };
  useEffect(() => {
    setDataShow([]);
    getData();
  }, [location.pathname]);

  const reverseData = [...dataShow].reverse();

  //Add Product Model Starts
  const [isAddModelOpen, setAddModelOpen] = useState(false);
  const [isViewProductOpen, setViewProductOpen] = useState(false);
  const [isEditProductOpen, setEditProductOpen] = useState(false);
  const [showDeleteConfirmation, setShowDeleteConfirmation] = useState(false);

  //Add Product Model Ends

  //View Product Model Starts
  const viewProduct = (id) => {
    localStorage.setItem("productId", id);

    setViewProductOpen(true);
    document.body.classList.add("page-modal-open");
  };
  //View Product Model Ends

  //Edit Product Model Starts
  const editProduct = (id) => {
    localStorage.setItem("productId", id);

    setEditProductOpen(true);
  };
  //Edit Product Model Ends

  //Delete Product Starts
  const getIdToDelete = (id, status) => {
    localStorage.setItem("productId", id);
    setShowDeleteConfirmation(true);
  };

  const handleDelete = async (e) => {
    e.preventDefault();
    const id = localStorage.getItem("productId");
    try {
      const response = await axios.delete(`${baseURL}/manageProducts`, {
        withCredentials: true,
        data: {
          id: id,
        },
      });

      if (response.status === 200 || response.status === 201) {
        alert(`The product has been deleted`);
        window.location.reload();
      } else {
        console.log("Error: " + (response.data || response.statusText));
      }
    } catch (error) {
      console.log("Error status:", error.response?.status);
    }
    setShowDeleteConfirmation(false);
  };
  //Delete Product Ends

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
                <b>Products</b>
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
                    Price
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
                        <td className="team-data-email">${data?.price}</td>

                        <td className="team-data-actions">
                          <button
                            className="add-action"
                            onClick={() => editProduct(data.id)}
                          >
                            <img
                              src={`${image}/Assets/Teams/add-btn.svg`}
                              alt=""
                            />
                          </button>
                          <button
                            className="view-action"
                            onClick={() => viewProduct(data.id)}
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
                    <p>No Products Found</p>
                  </div>
                )}
              </table>
            </div>
          </div>

          <div className="row">
            {isAddModelOpen && (
              <AddProduct
                onClose={() => {
                  setAddModelOpen(false);
                }}
              />
            )}
            {isEditProductOpen && (
              <EditProduct
                onClose={() => {
                  setEditProductOpen(false);
                  localStorage.removeItem("productId");
                }}
              />
            )}
            {isViewProductOpen && (
              <ViewProduct
                onClose={() => {
                  setViewProductOpen(false);
                  localStorage.removeItem("productId");
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
                        localStorage.removeItem("productId");
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

export default ProductsList;
