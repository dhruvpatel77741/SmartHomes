import React, { useEffect, useState } from "react";
import "./Login.css";
import Cookies from "js-cookie";
import { useNavigate } from "react-router-dom";
import usersData from "../SystemFiles/Users.json";
// import axios from "axios";

// const baseURL = process.env.REACT_APP_API_BASE_URL;
const image = process.env.PUBLIC_URL;

const Login = () => {
  const [userType, setUserType] = useState("StoreManager");
  const [eye, setEye] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [rememberMe, setRememberMe] = useState(false);

  useEffect(() => {
    // Retrieve stored username and password from cookies if available
    const storedUserName = Cookies.get("userName");
    const storedPassword = Cookies.get("password");
    if (storedUserName && storedPassword) {
      setUsername(storedUserName);
      setPassword(storedPassword);
      setRememberMe(true);
    }

    if (!localStorage.getItem("users")) {
      localStorage.setItem("users", JSON.stringify(usersData));
    }
  }, []);

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);

    const requestData = {
      username: username,
      password: password,
      userType: userType,
    };

    if (username === "") {
      setErrorMsg("Please enter username");
      setLoading(false);
    } else if (password === "") {
      setErrorMsg("Please enter password");
      setLoading(false);
    } else {
      if (rememberMe) {
        Cookies.set("userName", username, { expires: 1 });
        Cookies.set("password", password, { expires: 1 });
      } else {
        Cookies.remove("userName");
        Cookies.remove("password");
      }

      try {
        const storedUsers = JSON.parse(localStorage.getItem("users"));

        const user = storedUsers.find(
          (user) =>
            user.username === requestData.username &&
            user.password === requestData.password
        );

        if (user) {
          if (user.userType === requestData.userType) {
            localStorage.setItem("userType", user.userType);
            navigate("/dashboard");
          } else {
            window.alert("Invalid User Type");
          }
        } else {
          window.alert("Invalid username or password.");
        }
      } catch (error) {
        setErrorMsg("An error occurred. Please try again.");
      } finally {
        setLoading(false);
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
    }
  };

  const eyeChange = () => {
    setEye(!eye);
  };

  return (
    <div className="login-container">
      <div>
        <section className="login-box main-box">
          <div className="retailer-logo">
            <p>Login</p>
          </div>
          <div
            style={{
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
            }}
          >
            <button
              type="button"
              style={{
                backgroundColor:
                  userType === "StoreManager"
                    ? "rgba(0, 67, 229, 0.3)"
                    : "transparent",
                color: "#333",
                border: "1px solid #ccc",
                padding: "8px 16px",
                cursor: "pointer",
              }}
              onClick={() => setUserType("StoreManager")}
            >
              Manager
            </button>
            <button
              type="button"
              style={{
                backgroundColor:
                  userType === "Customer"
                    ? "rgba(0, 67, 229, 0.3)"
                    : "transparent",
                color: "#333",
                border: "1px solid #ccc",
                padding: "8px 16px",
                cursor: "pointer",
              }}
              onClick={() => setUserType("Customer")}
            >
              Customer
            </button>
            <button
              type="button"
              style={{
                backgroundColor:
                  userType === "Salesman"
                    ? "rgba(0, 67, 229, 0.3)"
                    : "transparent",
                color: "#333",
                border: "1px solid #ccc",
                padding: "8px 16px",
                cursor: "pointer",
              }}
              onClick={() => setUserType("Salesman")}
            >
              Salesman
            </button>
          </div>
          <br />
          <div className="row">
            <form className="form-container">
              <div className="user-form">
                <input
                  id="username"
                  type="text"
                  value={username || ""}
                  onChange={(e) => setUsername(e.target.value)}
                  placeholder="Username"
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
                  onChange={(e) => setPassword(e.target.value)}
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
                <label className="checkbox">
                  <input
                    type="checkbox"
                    onChange={(e) => setRememberMe(e.target.checked)}
                  />{" "}
                  Remember Me
                </label>
                <button
                  type="button"
                  onClick={() => {
                    navigate("/signup");
                  }}
                  style={{
                    display: userType !== "Customer" ? "none" : "flex",
                    background: "none",
                    border: "none",
                    cursor: "pointer",
                  }}
                >
                  Create Account
                </button>
              </div>

              <button
                loging="true"
                className="login-btn"
                onClick={handleLogin}
                disabled={loading}
              >
                {loading ? "Logging in..." : "LOGIN"}
              </button>

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

export default Login;