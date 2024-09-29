import React, { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import axios from "axios";
import "./List.css";

const baseURL = process.env.REACT_APP_API_BASE_URL;

const EditProduct = ({ onClose }) => {
  const location = useLocation();
  const id = localStorage.getItem("productId");

  const [dataShow, setDataShow] = useState({
    name: "",
    category: "",
    price: 0,
    description: "",
    accessories: [],
    warranty: false,
    warrantyPrice: 0,
    specialDiscount: false,
    discountPrice: 0,
    manufacturerRebate: false,
    rebatePrice: 0,
  });

  const [updatedProduct, setUpdatedProduct] = useState(dataShow);

  useEffect(() => {
    const getData = async () => {
      const id = localStorage.getItem("productId");
      let apiUrl = `${baseURL}/manageProducts`;
      try {
        const resp = await axios.get(apiUrl);
        const data = resp.data;
        const product = data.find((item) => item?.id == id);
        setDataShow(product);
        setUpdatedProduct(product);
      } catch (err) {
        console.log("Error:", err);
      }
    };
    getData();
  }, [id, location.pathname]);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    if (type === "checkbox") {
      setUpdatedProduct({ ...updatedProduct, [name]: checked });
    } else {
      setUpdatedProduct({ ...updatedProduct, [name]: value });
    }
  };

  const handleAccessoryChange = (index, e) => {
    const { name, value } = e.target;
    const accessories = [...updatedProduct.accessories];
    accessories[index][name] = value;
    setUpdatedProduct({ ...updatedProduct, accessories });
  };

  const handleAddAccessory = () => {
    setUpdatedProduct({
      ...updatedProduct,
      accessories: [...updatedProduct.accessories, { name: "", price: 0 }],
    });
  };

  const handleRemoveAccessory = (index) => {
    const accessories = updatedProduct.accessories.filter(
      (_, i) => i !== index
    );
    setUpdatedProduct({ ...updatedProduct, accessories });
  };

  const handleSubmit = async () => {
    const productToSend = {
      ...updatedProduct,
      price: parseFloat(updatedProduct.price),
      warrantyPrice: updatedProduct.warranty
        ? parseFloat(updatedProduct.warrantyPrice)
        : 0,
      discountPrice: updatedProduct.specialDiscount
        ? parseFloat(updatedProduct.discountPrice)
        : 0,
      rebatePrice: updatedProduct.manufacturerRebate
        ? parseFloat(updatedProduct.rebatePrice)
        : 0,
    };

    delete productToSend.accessories;

    try {
      const response = await axios.put(
        `${baseURL}/manageProducts`,
        productToSend
      );
      if (response.status === 200 || response.status === 201) {
        window.alert("Product Details Updated Successfully");
        window.location.reload();
      }
    } catch (err) {
      console.log("Error updating product:", err);
    }
  };

  return (
    <>
      <div className="profileview-model-backdrop">
        <div
          className="profileview-model-content"
          style={{ height: "650px", width: "600px" }}
        >
          <div className="profile-model-header">
            <h3 style={{ display: "flex", gap: "10px" }}>Edit Product</h3>
            <button
              className="invite-model-close-btn"
              onClick={() => {
                localStorage.removeItem("productId");
                onClose();
              }}
            >
              ✕
            </button>
          </div>
          <div className="row">
            <span className="viewbottom-border"></span>
          </div>
          <div className="row">
            <div className="want-serve">
              <b>Product Information</b>
              <div style={{ display: "flex", flexDirection: "column" }}>
                <input
                  type="text"
                  name="name"
                  value={updatedProduct.name}
                  onChange={handleChange}
                  style={{ marginTop: "5px" }}
                  placeholder="Product Name"
                />
                <select
                  name="category"
                  value={updatedProduct.category}
                  onChange={handleChange}
                  style={{ marginTop: "5px" }}
                >
                  <option value="" disabled>
                    Select Category
                  </option>
                  <option value="Smart Doorbells">Smart Doorbells</option>
                  <option value="Smart Doorlocks">Smart Doorlocks</option>
                  <option value="Smart Speakers">Smart Speakers</option>
                  <option value="Smart Lightings">Smart Lightings</option>
                  <option value="Smart Thermostats">Smart Thermostats</option>
                </select>

                <input
                  type="text"
                  name="description"
                  value={updatedProduct.description}
                  onChange={handleChange}
                  style={{ marginTop: "5px" }}
                  placeholder="Description"
                />
                <input
                  type="number"
                  name="price"
                  value={updatedProduct.price}
                  onChange={handleChange}
                  style={{ marginTop: "5px" }}
                  placeholder="Price"
                />
              </div>
            </div>
          </div>

          <div className="want-serve">
            <b>Accessories</b>
            <div style={{ display: "flex", flexDirection: "column" }}>
              {updatedProduct?.accessories?.length > 0 ? (
                updatedProduct.accessories.map((accessory, index) => (
                  <div key={index} style={{ marginTop: "10px" }}>
                    <input
                      type="text"
                      name="name"
                      value={accessory.name}
                      onChange={(e) => handleAccessoryChange(index, e)}
                      placeholder={`Accessory ${index + 1} Name`}
                      style={{ marginRight: "10px" }}
                    />
                    <input
                      type="number"
                      name="price"
                      value={accessory.price}
                      onChange={(e) => handleAccessoryChange(index, e)}
                      placeholder="Accessory Price"
                    />
                    <button
                      onClick={() => handleRemoveAccessory(index)}
                      style={{ marginLeft: "10px" }}
                    >
                      Remove
                    </button>
                  </div>
                ))
              ) : (
                <span>No accessories available</span>
              )}
              <button
                onClick={handleAddAccessory}
                style={{ marginTop: "10px" }}
              >
                Add Accessory
              </button>
            </div>
          </div>

          <div className="want-serve">
            <b>Warranty</b>
            <div style={{ display: "flex", flexDirection: "column" }}>
              <label>
                <input
                  type="checkbox"
                  name="warranty"
                  checked={updatedProduct.warranty}
                  onChange={handleChange}
                />
                Warranty Available
              </label>
              {updatedProduct.warranty && (
                <input
                  type="number"
                  name="warranty.price"
                  value={updatedProduct.warrantyPrice}
                  onChange={handleChange}
                  placeholder="Warranty Price"
                  style={{ marginTop: "5px" }}
                />
              )}
            </div>
          </div>

          <div className="want-serve">
            <b>Special Discount</b>
            <div style={{ display: "flex", flexDirection: "column" }}>
              <label>
                <input
                  type="checkbox"
                  name="specialDiscount"
                  checked={updatedProduct.specialDiscount}
                  onChange={handleChange}
                />
                Special Discount
              </label>
              {updatedProduct.specialDiscount && (
                <input
                  type="number"
                  name="discountPrice"
                  value={updatedProduct.discountPrice}
                  onChange={handleChange}
                  placeholder="Discount Price"
                  style={{ marginTop: "5px" }}
                />
              )}
            </div>
          </div>

          <div className="want-serve">
            <b>Manufacturer Rebate</b>
            <div style={{ display: "flex", flexDirection: "column" }}>
              <label>
                <input
                  type="checkbox"
                  name="manufacturerRebate"
                  checked={updatedProduct.manufacturerRebate}
                  onChange={handleChange}
                />
                Manufacturer Rebate
              </label>
              {updatedProduct.manufacturerRebate && (
                <input
                  type="number"
                  name="rebatePrice"
                  value={updatedProduct.rebatePrice}
                  onChange={handleChange}
                  placeholder="Rebate Price"
                  style={{ marginTop: "5px" }}
                />
              )}
            </div>
          </div>

          <br />
          <div className="row">
            <span className="viewbottom-border"></span>
          </div>
          <div style={{ display: "flex" }}>
            <div className="add-model-actions">
              <button
                onClick={() => {
                  onClose();
                  localStorage.removeItem("productId");
                }}
                className="submit-hover"
              >
                Cancel
              </button>
              <button onClick={handleSubmit} className="submit-hover">
                Submit
              </button>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default EditProduct;
