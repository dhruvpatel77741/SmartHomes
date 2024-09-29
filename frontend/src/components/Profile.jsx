import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './Profile.css';

const baseURL = process.env.REACT_APP_API_BASE_URL;

const Profile = () => {

    const navigate = useNavigate();

  const [formData, setFormData] = useState({
    zipCode: '',
    cvv: '',
    city: '',
    expiryDate: '',
    phone: '',
    creditCardNumber: '',
    name: '',
    addressLine1: '',
    addressLine2: '',
    id: '',
    userType: '',
    state: '',
    username: ''
  });

  const [isEditing, setIsEditing] = useState(false);

  const getData = async () => {
    const userId = localStorage.getItem("userId");

    try {
      const response = await axios.get(`${baseURL}/customers`);
      const data = response?.data.filter((user) => user?.id.toString() === userId);
      console.log(data[0]);
      setFormData(data[0]);
    } catch (error) {
      console.error("Error fetching cart items:", error);
    }
  };
  useEffect(() => {
    getData();
  }, []);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value
    }));
  };

  const handleSubmit = async () => {
    // e.preventDefault();

    const updatedData = {
      id: formData.id,
      phone: formData.phone,
      address: {
        addressLine1: formData.addressLine1,
        addressLine2: formData.addressLine2,
        city: formData.city,
        state: formData.state,
        zipCode: formData.zipCode,
      },
      creditCard: {
        creditCardNumber: formData.creditCardNumber,
        expiryDate: formData.expiryDate,
        cvv: formData.cvv,
      },
    };

    try {
      const response = await axios.post(`${baseURL}/updateUser`, updatedData);
      console.log('User data updated successfully:', response.data);
      setIsEditing(false);
      window.alert("User Details Updated Successfully");
      navigate("/dashboard");
    } catch (error) {
      console.error('There was an error updating the user data!', error);
    }
  };

  return (
    <div className="profile-container">
      <h2>User Profile</h2>
      <form className="profile-form" onSubmit={handleSubmit}>
        <label>
          Name:
          <input
            type="text"
            name="name"
            value={formData.name}
            onChange={handleInputChange}
            disabled={!isEditing}
          />
        </label>
        <label>
          Username:
          <input
            type="text"
            name="username"
            value={formData.username}
            onChange={handleInputChange}
            disabled={!isEditing}
          />
        </label>
        <label>
          Address Line 1:
          <input
            type="text"
            name="addressLine1"
            value={formData.addressLine1}
            onChange={handleInputChange}
            disabled={!isEditing}
          />
        </label>
        <label>
          Address Line 2:
          <input
            type="text"
            name="addressLine2"
            value={formData.addressLine2}
            onChange={handleInputChange}
            disabled={!isEditing}
          />
        </label>
        <label>
          City:
          <input
            type="text"
            name="city"
            value={formData.city}
            onChange={handleInputChange}
            disabled={!isEditing}
          />
        </label>
        <label>
          State:
          <input
            type="text"
            name="state"
            value={formData.state}
            onChange={handleInputChange}
            disabled={!isEditing}
          />
        </label>
        <label>
          Zip Code:
          <input
            type="text"
            name="zipCode"
            value={formData.zipCode}
            onChange={handleInputChange}
            disabled={!isEditing}
          />
        </label>
        <label>
          Phone:
          <input
            type="text"
            name="phone"
            value={formData.phone}
            onChange={handleInputChange}
            disabled={!isEditing}
          />
        </label>
        <label>
          Credit Card Number:
          <input
            type="text"
            name="creditCardNumber"
            value={formData.creditCardNumber}
            onChange={handleInputChange}
            disabled={!isEditing}
          />
        </label>
        <label>
          CVV:
          <input
            type="text"
            name="cvv"
            value={formData.cvv}
            onChange={handleInputChange}
            disabled={!isEditing}
          />
        </label>
        <label>
          Expiry Date:
          <input
            type="text"
            name="expiryDate"
            value={formData.expiryDate}
            onChange={handleInputChange}
            disabled={!isEditing}
          />
        </label>
        <label>
          User Type:
          <input
            type="text"
            name="userType"
            value={formData.userType}
            onChange={handleInputChange}
            disabled={!isEditing}
          />
        </label>
        {isEditing ? (
          <button type="button" className="profile-save-button" onClick={() => handleSubmit()}>Save</button>
        ) : (
          <button type="button" className="profile-edit-button" onClick={() => setIsEditing(true)}>
            Edit
          </button>
        )}
      </form>
    </div>
  );
};

export default Profile;
