import './App.css';
import { Route, Routes } from 'react-router-dom';
import Login from './components/Login';
import SignUp from './components/SignUp';
import Dashboard from './components/Dashboard';
import CustomerList from './components/CustomerList';
import ProductsList from './components/ProductsList';
import ProductDetails from './components/ProductDetails';

function App() {
  return (
    <Routes>
      <Route path='/' element={<Login/>}/>
      <Route path='/signup' element={<SignUp/>}/>
      <Route path='/dashboard' element={<Dashboard/>}/>
      <Route path='/dashboard/product/:id' element={<ProductDetails/>}/>
      <Route path='/customer-list' element={<CustomerList/>}/>
      <Route path='/product-list' element={<ProductsList/>}/>
    </Routes>
  );
}

export default App;
