import React, { useState } from "react";
import axios from "axios";
import "./List.css";

const baseURL = process.env.REACT_APP_API_BASE_URL;

const AddProduct = ({ onClose }) => {
  const [newProduct, setNewProduct] = useState({
    name: "",
    category: "",
    price: 0,
    description: "",
    accessories: [],
    warranty: {
      available: false,
      price: 0,
    },
    specialDiscount: false,
    manufacturerRebate: false,
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
      accessories: newProduct.accessories.map((accessory) => ({
        ...accessory,
        price: parseFloat(accessory.price),
      })),
      warranty: {
        ...newProduct.warranty,
        price: parseFloat(newProduct.warranty.price),
      },
    };

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
          style={{ height: "500px", width: "600px" }}
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
                <input
                  type="text"
                  name="category"
                  value={newProduct.category}
                  onChange={handleChange}
                  style={{ marginTop: "5px" }}
                  placeholder="Category"
                />
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
          <br />
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
          <div className="want-serve">
            <b>Warranty</b>
            <div style={{ display: "flex", flexDirection: "column" }}>
              <label>
                <input
                  type="checkbox"
                  name="warranty.available"
                  checked={newProduct.warranty.available}
                  onChange={(e) =>
                    setNewProduct({
                      ...newProduct,
                      warranty: {
                        ...newProduct.warranty,
                        available: e.target.checked,
                      },
                    })
                  }
                />
                Warranty Available
              </label>
              {newProduct.warranty.available && (
                <input
                  type="number"
                  name="warranty.price"
                  value={newProduct.warranty.price}
                  onChange={(e) =>
                    setNewProduct({
                      ...newProduct,
                      warranty: {
                        ...newProduct.warranty,
                        price: e.target.value,
                      },
                    })
                  }
                  placeholder="Warranty Price"
                />
              )}
            </div>
          </div>
          <div className="want-serve">
            <label>
              <input
                type="checkbox"
                name="specialDiscount"
                checked={newProduct.specialDiscount}
                onChange={handleChange}
              />
              Special Discount
            </label>
            <label>
              <input
                type="checkbox"
                name="manufacturerRebate"
                checked={newProduct.manufacturerRebate}
                onChange={handleChange}
              />
              Manufacturer Rebate
            </label>
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
