import React, { useState, useEffect } from "react";
import "./Dashboard.css";
import Aside from "./Aside";
import HeaderComponent from "./HeaderComponent";
import axios from "axios";
import { Link, useNavigate } from "react-router-dom";
// import SearchComponent from "./SearchComponent";

const baseURL = process.env.REACT_APP_API_BASE_URL;

const Dashboard = () => {
  const userType = localStorage.getItem("userType");

  const [data, setData] = useState([]);
  const [filteredData, setFilteredData] = useState([]);
  const [categories, setCategories] = useState(["All"]);
  const [selectedCategory, setSelectedCategory] = useState("All");
  const [trendingData, setTrendingData] = useState({
    topLikedProducts: [],
    topSoldProducts: [],
    topZipProducts: [],
  });

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

  const [reviewQuery, setReviewQuery] = useState("");
  const [productQuery, setProductQuery] = useState("");

  const navigate = useNavigate();

  const handleSearchReviews = () => {
    if (reviewQuery.trim() === "") {
      alert("Please enter a review query.");
      return;
    }
    navigate("/search-results", {
      state: { query: reviewQuery, type: "reviews" },
    });
  };

  const handleRecommendProduct = () => {
    if (productQuery.trim() === "") {
      alert("Please enter a product query.");
      return;
    }
    navigate("/search-results", {
      state: { query: productQuery, type: "products" },
    });
  };

  const handleGenerateProducts = async () => {
    try {
      const response = await axios.post(`${baseURL}/generateProducts`, {
        headers: {
          "Content-Type": "application/json",
        },
      });
      if (response.status === 200) {
        alert("Products generated successfully! and stored in MySQL Database.");
      } else {
        alert(`Unexpected response: ${response.status}`);
      }
    } catch (error) {
      console.error("Error generating products:", error);
      alert("Failed to generate products. Please try again.");
    }
  };

  const handleGenerateReviews = async () => {
    try {
      const response = await axios.post(`${baseURL}/generateReviews`, {
        headers: {
          "Content-Type": "application/json",
        },
      });
      if (response.status === 200) {
        alert("Reviews generated successfully! and stored in MongoDB.");
      } else {
        alert(`Unexpected response: ${response.status}`);
      }
    } catch (error) {
      console.error("Error generating products:", error);
      alert("Failed to generate products. Please try again.");
    }
  };

  return (
    <div className="MainOuterContainer">
      <Aside />
      <div className="main-part-ratailer">
        <HeaderComponent />
        <div style={{ display: "flex", flexDirection: "column", gap: "20px" }}>
          <div
            style={{ display: "flex", justifyContent: "center", gap: "15px" }}
          >
            <input
              type="text"
              value={reviewQuery}
              onChange={(e) => setReviewQuery(e.target.value)}
              placeholder="Search Reviews"
            />
            <button onClick={handleSearchReviews}>Search Reviews</button>
          </div>

          <div
            style={{ display: "flex", justifyContent: "center", gap: "15px" }}
          >
            <input
              type="text"
              value={productQuery}
              onChange={(e) => setProductQuery(e.target.value)}
              placeholder="Recommend Products"
            />
            <button onClick={handleRecommendProduct}>Recommend Product</button>
          </div>
        </div>

        {userType !== "Customer" && (
          <div
            style={{
              display: "flex",
              justifyContent: "center",
              margin: "20px 0",
              gap: "15px",
            }}
          >
            <button
              onClick={handleGenerateProducts}
              style={{
                padding: "10px 20px",
                fontSize: "16px",
                fontWeight: "bold",
                background: "linear-gradient(60deg, #960096, #28a745)",
                color: "#FFFFFF",
                border: "none",
                borderRadius: "5px",
                cursor: "pointer",
                boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)",
                transition: "all 0.3s ease",
              }}
              onMouseOver={(e) =>
                (e.target.style.background =
                  "linear-gradient(60deg, #28a745, #960096)")
              }
              onMouseOut={(e) =>
                (e.target.style.background =
                  "linear-gradient(60deg, #960096, #28a745)")
              }
              onMouseDown={(e) => (e.target.style.transform = "scale(0.95)")}
              onMouseUp={(e) => (e.target.style.transform = "scale(1)")}
            >
              Generate Product
            </button>

            <button
              onClick={handleGenerateReviews}
              style={{
                padding: "10px 20px",
                fontSize: "16px",
                fontWeight: "bold",
                background: "linear-gradient(60deg, #28a745, #960096)",
                color: "#FFFFFF",
                border: "none",
                borderRadius: "5px",
                cursor: "pointer",
                boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)",
                transition: "all 0.3s ease",
              }}
              onMouseOver={(e) =>
                (e.target.style.background =
                  "linear-gradient(60deg, #960096, #28a745)")
              }
              onMouseOut={(e) =>
                (e.target.style.background =
                  "linear-gradient(60deg, #28a745, #960096)")
              }
              onMouseDown={(e) => (e.target.style.transform = "scale(0.95)")}
              onMouseUp={(e) => (e.target.style.transform = "scale(1)")}
            >
              Generate Reviews
            </button>
          </div>
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
