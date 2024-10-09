import React, { useEffect, useState } from "react";
import axios from "axios";
import Aside from "./Aside";
import { Chart } from "react-google-charts";

const baseURL = process.env.REACT_APP_API_BASE_URL;

const InventoryReport = () => {
  const [inventoryData, setInventoryData] = useState([]);
  const [view, setView] = useState("allProducts");

  useEffect(() => {
    const fetchInventoryData = async () => {
      try {
        const response = await axios.get(`${baseURL}/inventoryReport`);
        setInventoryData(response.data);
      } catch (error) {
        console.error("Error fetching inventory data:", error);
      }
    };

    fetchInventoryData();
  }, []);

  console.log(inventoryData)
  const barChartData = [
    ["Product Name", "Total Items Available"],
    ...inventoryData.map((item) => [item.name, item.availableItems || 0]),
  ];

  const barChartOptions = {
    title: "Total Items Available by Product",
    chartArea: { width: "50%" },
    hAxis: {
      title: "Total Items Available",
      minValue: 0,
    },
    vAxis: {
      title: "Product Name",
    },
    bars: 'horizontal',
  };

  const onSaleProducts = inventoryData.filter((item) => item.specialDiscount === 1);

  const rebateProducts = inventoryData.filter((item) => item.manufacturerRebate === 1);

  return (
    <div className="MainOuterContainer">
      <Aside />
      <div className="main-part-ratailer">
        <div className="TemsTableHeadingContainer">
          <div style={{ padding: "10px" }}>
            <b>Inventory Report</b>
          </div>

          <div style={{ display: "flex", gap: "10px", marginBottom: "10px" }}>
            <button className={`filter-btn ${
                view === "allProducts" ? "active" : ""
              }`} onClick={() => setView("allProducts")}>All Products</button>
            <button className={`filter-btn ${
                view === "barChart" ? "active" : ""
              }`} onClick={() => setView("barChart")}>Bar Chart</button>
            <button className={`filter-btn ${
                view === "onSale" ? "active" : ""
              }`} onClick={() => setView("onSale")}>On Sale Products</button>
            <button className={`filter-btn ${
                view === "rebates" ? "active" : ""
              }`} onClick={() => setView("rebates")}>Products with Rebates</button>
          </div>

          {view === "allProducts" && (
            <table
              style={{
                flexDirection: "column",
                gap: "10px",
                textAlign: "center",
                overflowY: "scroll",
              }}
              className="team-details"
            >
              <thead>
                <tr className="team-main-bg TeamsTableHeading">
                  <th className="team-data-main" style={{ width: "24%" }}>
                    Product Name
                  </th>
                  <th className="team-data-role" style={{marginLeft:"-35px"}}>Price</th>
                  <th className="team-data-email" style={{ width: "24%" }}>
                    Available
                  </th>
                </tr>
              </thead>
              <tbody>
                {inventoryData.map((item, index) => (
                  <tr className="TeamDetailsRowData" key={index}>
                    <td
                      className="team-data-main"
                      style={{
                        overflowWrap: "break-word",
                        width: "24%"
                      }}
                    >
                      {item.name}
                    </td>
                    <td className="team-data-role" style={{ marginLeft: "-35px" }}>${item.price}</td>
                    <td className="team-data-email">{item.availableItems}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}

          {view === "barChart" && (
            <Chart
              chartType="BarChart"
              width="100%"
              height="400px"
              data={barChartData}
              options={barChartOptions}
            />
          )}

          {view === "onSale" && (
            <table
              style={{
                flexDirection: "column",
                gap: "10px",
                textAlign: "center",
                overflowY: "scroll",
              }}
              className="team-details"
            >
              <thead>
                <tr className="team-main-bg TeamsTableHeading">
                  <th className="team-data-main" style={{ width: "24%" }}>
                    Product Name
                  </th>
                  <th className="team-data-role">Sale Price</th>
                  <th className="team-data-email" style={{ width: "24%" }}>
                    Available
                  </th>
                </tr>
              </thead>
              <tbody>
                {onSaleProducts.map((item, index) => (
                  <tr className="TeamDetailsRowData" key={index}>
                    <td
                      className="team-data-main"
                      style={{
                        overflowWrap: "break-word",
                        width: "24%"
                      }}
                    >
                      {item.name}
                    </td>
                    <td className="team-data-role" style={{ marginLeft: "-35px" }}>${item.discountPrice}</td>
                    <td className="team-data-email">{item.availableItems}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}

          {view === "rebates" && (
            <table
              style={{
                flexDirection: "column",
                gap: "10px",
                textAlign: "center",
                overflowY: "scroll",
              }}
              className="team-details"
            >
              <thead>
                <tr className="team-main-bg TeamsTableHeading">
                  <th className="team-data-main" style={{ width: "24%" }}>
                    Product Name
                  </th>
                  <th className="team-data-role">Rebated Price</th>
                  <th className="team-data-email" style={{ width: "24%" }}>
                    Available
                  </th>
                </tr>
              </thead>
              <tbody>
                {rebateProducts.map((item, index) => (
                  <tr className="TeamDetailsRowData" key={index}>
                    <td
                      className="team-data-main"
                      style={{
                        overflowWrap: "break-word",
                        width: "24%"
                      }}
                    >
                      {item.name}
                    </td>
                    <td className="team-data-role" style={{ marginLeft: "-35px" }}>${item.rebatePrice}</td>
                    <td className="team-data-email">{item.availableItems}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </div>
  );
};

export default InventoryReport;
