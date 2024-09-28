import React, { useState, useEffect } from "react";
import "./Dashboard.css";
import Aside from "./Aside";
import HeaderComponent from "./HeaderComponent";
import axios from "axios";
import { Link } from "react-router-dom";

const baseURL = process.env.REACT_APP_API_BASE_URL;

const Dashboard = () => {
  const [data, setData] = useState([]);
  const [filteredData, setFilteredData] = useState([]);
  const [categories, setCategories] = useState(["All"]);
  const [selectedCategory, setSelectedCategory] = useState("All");

  const getData = async () => {
    let apiUrl = `${baseURL}/manageProducts`;
    try {
      const resp = await axios.get(apiUrl);
      const data = resp.data;
      setData(data);
      console.log(data);
      setFilteredData(data);
      const uniqueCategories = Array.from(
        new Set(data.map((item) => item.category))
      );
      setCategories(["All", "Trending", ...uniqueCategories]);
    } catch (err) {
      console.log("Error:", err);
    }
  };

  useEffect(() => {
    getData();
  }, []);

  const handleCategoryChange = (category) => {
    setSelectedCategory(category);
    if (category === "All") {
      setFilteredData(data);
    } else {
      const filteredItems = data.filter((item) => item.category === category);
      setFilteredData(filteredItems);
    }
  };

  return (
    <div className="MainOuterContainer">
      <Aside />
      <div className="main-part-ratailer">
        <HeaderComponent />

        <div className="filter-container">
          {categories.map((category) => (
            <button
              key={category}
              onClick={() => handleCategoryChange(category)}
              className={`filter-btn ${
                selectedCategory === category ? "active" : ""
              }`}
            >
              {category}
            </button>
          ))}
        </div>

        <div className="cards-container">
          {filteredData.length > 0 ? (
            filteredData.map((item, index) => (
              <Link
                to={`/dashboard/product/${item.id}`}
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
