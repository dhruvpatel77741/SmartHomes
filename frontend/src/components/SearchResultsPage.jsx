import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import axios from "axios";
import "./SearchResults.css";
import Aside from "./Aside";

const baseURL = process.env.REACT_APP_API_BASE_URL;

const SearchResultsPage = () => {
  const location = useLocation();
  const { query, type } = location.state || {};
  const [searchResults, setSearchResults] = useState([]);

  useEffect(() => {
    if (query && type) {
      fetchResults(query, type);
    }
  }, [query, type]);

  const fetchResults = async (query, type) => {
    try {
      const endpoint =
        type === "reviews"
          ? `${baseURL}/reviewSearch?query=${query}`
          : `${baseURL}/productSearch?query=${query}`;
      const response = await axios.get(endpoint);
      setSearchResults(response.data.results || []);
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };

  return (
    <div className="MainOuterContainer">
      <Aside />
      <div className="search-results-container">
        <h2>
          {type === "reviews"
            ? "Review Search Results"
            : "Recommended Products"}
        </h2>
        {searchResults.length > 0 ? (
          <div className="cards-inner-container">
            {searchResults.map((item, index) => {
              const data = item._source;
              return (
                <div className="card" key={index}>
                  <h3>
                    {data.productModelName ||
                      data.name ||
                      `Review #${index + 1}`}
                  </h3>
                  {type === "products" ? (
                    <p>
                      <strong>Description:</strong>{" "}
                      {data.description || "No description available"}
                    </p>
                  ) : (
                    <p>
                      <strong>Review:</strong>{" "}
                      {data.reviewText || "No review available"}
                    </p>
                  )}
                  {type === "products" && (
                    <>
                      <p>Category: {data.category}</p>
                      <p className="price">Price: ${data.price}</p>
                      {/* {data.discountPrice && (
                      <p className="discount-price">
                        Discount Price: ${data.discountPrice}
                      </p>
                    )} */}
                      <p>Available Items: {data.availableItems}</p>
                    </>
                  )}
                  {type === "reviews" && (
                    <>
                      <p>Model: {data.productModelName}</p>
                      <p>Category: {data.productCategory}</p>
                      <p>Rating: {data.reviewRating} ⭐</p>
                      {data.productPrice && (
                        <p className="price">Price: ${data.productPrice}</p>
                      )}
                    </>
                  )}
                </div>
              );
            })}
          </div>
        ) : (
          <p className="no-results">No results found for "{query}"</p>
        )}
      </div>
    </div>
  );
};

export default SearchResultsPage;
