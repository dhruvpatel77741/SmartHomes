import "./App.css";
import { Route, Routes } from "react-router-dom";
import Login from "./components/Login";
import SignUp from "./components/SignUp";
import Dashboard from "./components/Dashboard";
import UserList from "./components/UserList";
import ProductsList from "./components/ProductsList";
import ProductDetails from "./components/ProductDetails";
import Cart from "./components/Cart";
import Checkout from "./components/Checkout";
import Payment from "./components/Payment";
import PrivateRoute from "./components/PrivateRoute";
import OrdersList from "./components/OrdersList";
import Profile from "./components/Profile";
import InventoryReport from "./components/InventoryReport";
import SalesReport from "./components/SalesReport";
import CustomerService from "./components/CustomerService";

function App() {
  return (
    <Routes>
      <Route path="/" element={<Login />} />
      <Route path="/signup" element={<SignUp />} />
      <Route
        path="/inventory-report"
        element={
          <PrivateRoute>
            <InventoryReport />
          </PrivateRoute>
        }
      />
      <Route
        path="/sales-report"
        element={
          <PrivateRoute>
            <SalesReport />
          </PrivateRoute>
        }
      />
      <Route
        path="/dashboard"
        element={
          <PrivateRoute>
            <Dashboard />
          </PrivateRoute>
        }
      />
      <Route
        path="/dashboard/product/:id"
        element={
          <PrivateRoute>
            <ProductDetails />
          </PrivateRoute>
        }
      />
      <Route
        path="/customer-list"
        element={
          <PrivateRoute>
            <UserList />
          </PrivateRoute>
        }
      />
      <Route
        path="/salesman-list"
        element={
          <PrivateRoute>
            <UserList />
          </PrivateRoute>
        }
      />
      <Route
        path="/product-list"
        element={
          <PrivateRoute>
            <ProductsList />
          </PrivateRoute>
        }
      />
      <Route
        path="/cart"
        element={
          <PrivateRoute>
            <Cart />
          </PrivateRoute>
        }
      />
      <Route
        path="/checkout"
        element={
          <PrivateRoute>
            <Checkout />
          </PrivateRoute>
        }
      />
      <Route
        path="/payment"
        element={
          <PrivateRoute>
            <Payment />
          </PrivateRoute>
        }
      />
      <Route
        path="/order-list"
        element={
          <PrivateRoute>
            <OrdersList />
          </PrivateRoute>
        }
      />
      <Route
        path="/profile"
        element={
          <PrivateRoute>
            <Profile />
          </PrivateRoute>
        }
      />
      <Route
        path="/customer-service"
        element={
          <PrivateRoute>
            <CustomerService />
          </PrivateRoute>
        }
      />
    </Routes>
  );
}

export default App;
