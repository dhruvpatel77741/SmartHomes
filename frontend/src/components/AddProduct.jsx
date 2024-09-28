import React, { useState } from "react";
import axios from "axios";
import "./List.css";

const baseURL = process.env.REACT_APP_API_BASE_URL;

const AddProduct = ({ onClose }) => {
  const [newProduct, setNewProduct] = useState({
    name: "",
    category: "",
    price: 0.0,
    description: "",
    accessories: [],
    warranty: false,
    warrantyPrice: 0.0,
    specialDiscount: false,
    discountPrice: 0.0,
    manufacturerRebate: false,
    rebatePrice: 0.0,
    likes: 0,
  });

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    if (type === "checkbox") {
      setNewProduct({ ...newProduct, [name]: checked });
    } else {
      setNewProduct({ ...newProduct, [name]: value });
    }
  };

  const handleAccessoryChange = (index, e) => {
    const { name, value } = e.target;
    const accessories = [...newProduct.accessories];
    accessories[index][name] = value;
    setNewProduct({ ...newProduct, accessories });
  };

  const handleAddAccessory = () => {
    setNewProduct({
      ...newProduct,
      accessories: [...newProduct.accessories, { name: "", price: 0 }],
    });
  };

  const handleRemoveAccessory = (index) => {
    const accessories = newProduct.accessories.filter((_, i) => i !== index);
    setNewProduct({ ...newProduct, accessories });
  };

  const handleSubmit = async () => {
    const productToSend = {
      ...newProduct,
      price: parseFloat(newProduct.price),
      warrantyPrice: parseFloat(newProduct.warrantyPrice),
      discountPrice: parseFloat(newProduct.discountPrice),
      rebatePrice: parseFloat(newProduct.rebatePrice),
      likes: parseFloat(newProduct.likes),
    };

    delete productToSend.accessories;

    try {
      const response = await axios.post(
        `${baseURL}/manageProducts`,
        productToSend
      );
      if (response.status === 200 || response.status === 201) {
        window.alert("Product Added Successfully");
        window.location.reload();
      }
    } catch (err) {
      console.log("Error adding product:", err);
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
            <h3 style={{ display: "flex", gap: "10px" }}>Add Product</h3>
            <button
              className="invite-model-close-btn"
              onClick={() => onClose()}
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
                  value={newProduct.name}
                  onChange={handleChange}
                  style={{ marginTop: "5px" }}
                  placeholder="Product Name"
                />
                <select
                  name="category"
                  value={newProduct.category}
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
                  value={newProduct.description}
                  onChange={handleChange}
                  style={{ marginTop: "5px" }}
                  placeholder="Description"
                />
                <input
                  type="number"
                  name="price"
                  value={newProduct.price}
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
              {newProduct?.accessories?.length > 0 ? (
                newProduct.accessories.map((accessory, index) => (
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
          <br />

          <br />
          <div className="want-serve">
            <b>Warranty</b>
            <div style={{ display: "flex", flexDirection: "column" }}>
              <label>
                <input
                  type="checkbox"
                  name="warranty"
                  checked={newProduct.warranty}
                  onChange={handleChange}
                />
                Warranty Available
              </label>
              {newProduct.warranty && (
                <input
                  type="number"
                  name="warrantyPrice"
                  value={newProduct.warrantyPrice}
                  onChange={handleChange}
                  placeholder="Warranty Price"
                  style={{ marginTop: "5px" }}
                />
              )}
            </div>
          </div>

          <br />
          <div className="want-serve">
            <b>Special Discount</b>
            <div style={{ display: "flex", flexDirection: "column" }}>
              <label>
                <input
                  type="checkbox"
                  name="specialDiscount"
                  checked={newProduct.specialDiscount}
                  onChange={handleChange}
                />
                Special Discount
              </label>
              {newProduct.specialDiscount && (
                <input
                  type="number"
                  name="discountPrice"
                  value={newProduct.discountPrice}
                  onChange={handleChange}
                  placeholder="Discount Price"
                  style={{ marginTop: "5px" }}
                />
              )}
            </div>
          </div>

          <br />
          <div className="want-serve">
            <b>Manufacturer Rebate</b>
            <div style={{ display: "flex", flexDirection: "column" }}>
              <label>
                <input
                  type="checkbox"
                  name="manufacturerRebate"
                  checked={newProduct.manufacturerRebate}
                  onChange={handleChange}
                />
                Manufacturer Rebate Available
              </label>
              {newProduct.manufacturerRebate && (
                <input
                  type="number"
                  name="rebatePrice"
                  value={newProduct.rebatePrice}
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
              <button onClick={() => onClose()} className="submit-hover">
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

export default AddProduct;
