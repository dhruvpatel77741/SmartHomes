import React, { useState } from "react";
import "./Login.css";
import { useNavigate } from "react-router-dom";
// import axios from "axios";

// const baseURL = process.env.REACT_APP_API_BASE_URL;
const image = process.env.PUBLIC_URL;

const SignUp = () => {
  const [eye, setEye] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const [name, setName] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const handleSignUp = async (e) => {
    e.preventDefault();
    setLoading(true);

    if (!name.trim() || !username.trim() || !password.trim()) {
      window.alert("All fields are required.");
      setLoading(false);
      return;
    }

    const existingUsers = JSON.parse(localStorage.getItem("users") || "[]");
    const isUsernameTaken = existingUsers.some(
      (user) => user.username === username && user.userType === "Customer"
    );
    if (isUsernameTaken) {
      window.alert("Username is already taken");
      setLoading(false);
    } else {
      const requestData = {
        name: name,
        username: username,
        password: password,
        userType: "Customer",
      };

      existingUsers.push(requestData);

      localStorage.setItem("users", JSON.stringify(existingUsers));
      window.alert("User created successfully");
      setName("");
      setUsername("");
      setPassword("");
      setErrorMsg("");
      navigate("/");
    }

    // let apiUrl = `${baseURL}/admin/login`;
    // try {
    //   const response = await axios.post(apiUrl, requestData);
    //   if (response.status === 200) {
    //     const responToken = response.data.token;
    //     const userType = response.data.userType;
    //     const adminId = response.data.adminId;

    //     localStorage.setItem("userToken", responToken);
    //     localStorage.setItem("adminId", adminId);
    //     localStorage.setItem("userType", userType);
    //     navigate("/admin-dashboard");
    //   } else {
    //     setErrorMsg("Login failed. Please try again.");
    //   }
    // } catch (error) {
    //   if (error.response && error.response.status === 401) {
    //     alert(error?.response?.data?.message);
    //     setErrorMsg("Username or password is incorrect.");
    //   } else {
    //     setErrorMsg("An error occurred. Please try again.");
    //   }
    // } finally {
    //   setLoading(false);
    // }
    // }
  };

  const eyeChange = () => {
    setEye(!eye);
  };

  return (
    <div className="login-container">
      <div>
        <section className="login-box main-box">
          <div className="retailer-logo">
            <p>Customer Registration</p>
          </div>
          <div
            style={{
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
            }}
          ></div>
          <br />
          <div className="row">
            <form className="form-container">
              <div className="user-form">
                <input
                  id="username"
                  type="text"
                  value={name || ""}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="Name"
                  required
                />
                <div className="login-form-imag"></div>
              </div>
              <div className="user-form">
                <input
                  id="username"
                  type="text"
                  value={username || ""}
                  onChange={(e) => setUsername(e.target.value)}
                  placeholder="Username"
                  required
                />
                <div className="login-form-imag"></div>
              </div>
              <div className="pas-form">
                <div className="text-password"></div>
                <input
                  id="password"
                  className="psd-toggle"
                  type={eye ? "text" : "password"}
                  value={password || ""}
                  placeholder="Password"
                  pattern="^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,15}$"
                  title="Password must contain at least 8 characters, including uppercase, lowercase, numbers, and special characters."
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
                <div className="login-form-imag lock-password"></div>
                <button onClick={eyeChange} type="button" className="eye-btn">
                  <img
                    className="eye-login"
                    style={{
                      width: "25px",
                      height: "25px",
                      paddingBottom: "10px",
                    }}
                    src={
                      !eye
                        ? `${image}/Assets/Login/openview-eye.svg`
                        : `${image}/Assets/Login/view-eye.svg`
                    }
                    alt=""
                  />
                </button>
              </div>

              <br />
              <div className="Login_Fgpwd_container">
                <button
                  type="button"
                  onClick={() => {
                    navigate("/forgotPassword");
                  }}
                  style={{
                    display: "none",
                    background: "none",
                    border: "none",
                    cursor: "pointer",
                  }}
                >
                  Forgot password?
                </button>
              </div>

              <button
                loging="true"
                className="login-btn"
                onClick={handleSignUp}
                disabled={loading}
              >
                {loading ? "Signing up..." : "SIGN UP"}
              </button>
              <div
                style={{
                  display: "flex",
                  justifyContent: "center",
                  paddingTop: "10px",
                }}
              >
                <button
                  type="button"
                  onClick={() => {
                    navigate("/");
                  }}
                  style={{
                    background: "none",
                    border: "none",
                    cursor: "pointer",
                  }}
                >
                  Login
                </button>
              </div>

              <span
                style={{
                  color: "red",
                  position: "relative",
                  textAlign: "center",
                  margin: "0 auto",
                }}
              >
                {errorMsg}
              </span>
            </form>
          </div>
        </section>
      </div>
    </div>
  );
};

export default SignUp;
