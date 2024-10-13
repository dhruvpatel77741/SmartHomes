import React, { useState, useEffect } from "react";
import "./Dashboard.css";
import Aside from "./Aside";
import HeaderComponent from "./HeaderComponent";
import axios from "axios";
import { Link } from "react-router-dom";
import SearchComponent from "./SearchComponent";

const baseURL = process.env.REACT_APP_API_BASE_URL;

const Dashboard = () => {
  const [data, setData] = useState([]);
  const [filteredData, setFilteredData] = useState([]);
  const [categories, setCategories] = useState(["All"]);
  const [selectedCategory, setSelectedCategory] = useState("All");
  const [trendingData, setTrendingData] = useState({
    topLikedProducts: [],
    topSoldProducts: [],
    topZipProducts: [],
  });
  console.log(data);

  const getData = async () => {
    let apiUrl = `${baseURL}/manageProducts`;
    let trendingUrl = `${baseURL}/trending`;
    try {
      const resp = await axios.get(apiUrl);
      const trendingResp = await axios.get(trendingUrl);
      const data = resp.data;
      const trendingProducts = trendingResp.data;

      setData(data);

      setTrendingData({
        topLikedProducts: trendingProducts.topLikedProducts,
        topSoldProducts: trendingProducts.topSoldProducts,
        topZipProducts: trendingProducts.topZipProducts,
      });

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
    } else if (category === "Trending") {
      const likedProducts = data
        .filter((item) =>
          trendingData.topLikedProducts.some(
            (prod) => prod.productId === item.id
          )
        )
        .map((item) => ({
          ...item,
          likes:
            trendingData.topLikedProducts.find(
              (prod) => prod.productId === item.id
            )?.likes || 0,
        }))
        .sort((a, b) => b.likes - a.likes);

      const soldProducts = data
        .filter((item) =>
          trendingData.topSoldProducts.some(
            (prod) => prod.productId === item.id
          )
        )
        .map((item) => ({
          ...item,
          totalSold:
            trendingData.topSoldProducts.find(
              (prod) => prod.productId === item.id
            )?.totalSold || 0,
        }));

      const zipProducts = trendingData.topZipProducts.map((zipProduct) => ({
        zipCode: zipProduct.zipCode,
        totalSold: zipProduct.totalSold,
      }));

      setFilteredData({
        likedProducts,
        soldProducts,
        zipProducts,
      });
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
        
        {selectedCategory === "All" && (
          <SearchComponent data={data} setFilteredData={setFilteredData} />
        )}

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
          {selectedCategory === "Trending" ? (
            <div style={{ display: "flex", flexDirection: "column" }}>
              <h2>Top Liked Products</h2>
              <div
                className="cards-inner-container"
                style={{ display: "flex", flexWrap: "wrap" }}
              >
                {filteredData.likedProducts.length > 0 ? (
                  filteredData.likedProducts.map((item, index) => (
                    <Link
                      to={`/dashboard/product/${item.id}`}
                      state={{ product: item }}
                      className="card"
                      key={index}
                      style={{ textDecoration: "none" }}
                    >
                      <h3>{item.name}</h3>
                      <p>{item.description}</p>
                      <p style={{ fontWeight: "bolder" }}>${item.price}</p>
                      <p>Likes: {item.likes}</p>
                    </Link>
                  ))
                ) : (
                  <p>No liked products available</p>
                )}
              </div>
              <br />

              <h2>Top Sold Products</h2>
              <div
                className="cards-inner-container"
                style={{ display: "flex", flexWrap: "wrap" }}
              >
                {filteredData.soldProducts.length > 0 ? (
                  filteredData.soldProducts.map((item, index) => (
                    <Link
                      to={`/dashboard/product/${item.id}`}
                      state={{ product: item }}
                      className="card"
                      key={index}
                      style={{ textDecoration: "none" }}
                    >
                      <h3>{item.name}</h3>
                      <p>{item.description}</p>
                      <p style={{ fontWeight: "bolder" }}>${item.price}</p>
                      <p>Sold: {item.totalSold}</p>
                    </Link>
                  ))
                ) : (
                  <p>No sold products available</p>
                )}
              </div>
              <br />

              <h2>Top Zip Codes for Sold Products</h2>
              <div
                className="cards-inner-container"
                style={{ display: "flex", flexWrap: "wrap" }}
              >
                {filteredData.zipProducts.length > 0 ? (
                  filteredData.zipProducts.map((item, index) => (
                    <div
                      className="card"
                      key={index}
                      style={{ textDecoration: "none" }}
                    >
                      <h3>Zip Code: {item.zipCode}</h3>
                      <p>Total Products Sold: {item.totalSold}</p>{" "}
                    </div>
                  ))
                ) : (
                  <p>No zip code data available</p>
                )}
              </div>
            </div>
          ) : filteredData.length > 0 ? (
            filteredData.map((item, index) => (
              <Link
                to={`/dashboard/product/${item.id}`}
                state={{ product: item }}
                className="card"
                key={index}
                style={{ textDecoration: "none" }}
              >
                <h3>{item.name}</h3>
                <p>{item.description}</p>
                <p style={{ fontWeight: "bolder" }}>${item.price}</p>
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
