import React, { useState } from "react";
import axios from "axios";

const baseURL = process.env.REACT_APP_API_BASE_URL;

const SearchComponent = ({ data, setFilteredData }) => {
  const [query, setQuery] = useState("");
  const [suggestions, setSuggestions] = useState([]);

  const handleInputChange = async (event) => {
    const searchTerm = event.target.value;
    setQuery(searchTerm);

    if (searchTerm.length > 2) {
      try {
        const response = await axios.get(
          `${baseURL}/autocomplete?term=${searchTerm}`
        );
        setSuggestions(response.data);
        const filteredItems = data.filter((item) =>
          response.data.includes(item.name)
        );
        setFilteredData(filteredItems); // Filter products based on search results
      } catch (error) {
        console.error("Error fetching suggestions:", error);
      }
    } else {
      setSuggestions([]);
      setFilteredData(data); // Reset to all products when search is cleared
    }
  };

  const handleSuggestionClick = (suggestion) => {
    setQuery(suggestion);
    setSuggestions([]);
  };

  return (
    <div>
      <input
        type="text"
        value={query}
        onChange={handleInputChange}
        placeholder="Search for a product"
      />
      {suggestions.length > 0 && (
        <ul>
          {suggestions.map((suggestion, index) => (
            <li key={index} onClick={() => handleSuggestionClick(suggestion)}>
              {suggestion}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default SearchComponent;
