import React, { useState, useEffect } from "react";
import "./Dashboard.css";
import Aside from "./Aside";
import HeaderComponent from "./HeaderComponent";
import axios from "axios";
import { Link } from "react-router-dom";

const baseURL = process.env.REACT_APP_API_BASE_URL;

const Dashboard = () => {
  const [data, setData] = useState([]);
  const getData = async () => {
    let apiUrl = `${baseURL}/manageProducts`;
    try {
      const resp = await axios.get(apiUrl);
      const data = resp.data;
      console.log(data);
      setData(data);
    } catch (err) {
      console.log("Error:", err);
    }
  };

  useEffect(() => {
    setData([]);
    getData();
  }, []);

  return (
    <div className="MainOuterContainer">
      <Aside />
      <div className="main-part-ratailer">
        <HeaderComponent />

        <div className="cards-container">
          {data.length > 0 ? (
            data.map((item, index) => (
              <Link
                to={`/dashboard/product/${item?.id}`}
                state={{ product: item }}
                className="card"
                key={index}
                style={{ textDecoration: "none" }}
              >
                <h3 style={{ position: "relative", bottom: "50px" }}>
                  {item.name}
                </h3>
                <p>{item.description}</p>
                <p
                  style={{
                    position: "relative",
                    top: "50px",
                    fontWeight: "bolder",
                  }}
                >
                  ${item.price}
                </p>
              </Link>
            ))
          ) : (
            <p>No data available</p>
          )}
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
