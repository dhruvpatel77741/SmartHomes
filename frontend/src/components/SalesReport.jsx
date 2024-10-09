import React, { useEffect, useState } from "react";
import axios from "axios";
import Aside from "./Aside";
import { Chart } from "react-google-charts";

const baseURL = process.env.REACT_APP_API_BASE_URL;

const SalesReport = () => {
  const [salesData, setSalesData] = useState([]);
  const [view, setView] = useState("allSales");

  useEffect(() => {
    const fetchSalesData = async () => {
      try {
        const response = await axios.get(`${baseURL}/salesReport`);
        setSalesData(response.data);
      } catch (error) {
        console.error("Error fetching sales data:", error);
      }
    };

    fetchSalesData();
  }, []);

  const productSales = salesData.productSales || [];
  const dailySales = salesData.dailySales || [];

  const barChartData = [
    ["Product Name", "Total Sales"],
    ...productSales.map((item) => [item.name, item.totalSales || 0]),
  ];

  const barChartOptions = {
    title: "Total Sales by Product",
    chartArea: { width: "50%" },
    hAxis: {
      title: "Total Sales",
      minValue: 0,
    },
    vAxis: {
      title: "Product Name",
    },
    bars: 'horizontal',
  };

  return (
    <div className="MainOuterContainer">
      <Aside />
      <div className="main-part-ratailer">
        <div className="TemsTableHeadingContainer">
          <div style={{ padding: "10px" }}>
            <b>Sales Report</b>
          </div>

          <div style={{ display: "flex", gap: "10px", marginBottom: "10px" }}>
            <button className={`filter-btn ${view === "allSales" ? "active" : ""}`} onClick={() => setView("allSales")}>All Product Sales</button>
            <button className={`filter-btn ${view === "barChart" ? "active" : ""}`} onClick={() => setView("barChart")}>Bar Chart</button>
            <button className={`filter-btn ${view === "dailySales" ? "active" : ""}`} onClick={() => setView("dailySales")}>Daily Sales</button>
          </div>

          {view === "allSales" && (
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
                  <th className="team-data-role" style={{ marginLeft: "-35px" }}>Price</th>
                  <th className="team-data-email" style={{ width: "24%" }}>
                    Quantity Sold
                  </th>
                  <th className="team-data-email" style={{ width: "24%" }}>
                    Total Sales
                  </th>
                </tr>
              </thead>
              <tbody>
                {productSales.map((item, index) => (
                  <tr className="TeamDetailsRowData" key={index}>
                    <td className="team-data-main" style={{ overflowWrap: "break-word", width: "24%" }}>
                      {item.name}
                    </td>
                    <td className="team-data-role" style={{ marginLeft: "-35px" }}>${item.price}</td>
                    <td className="team-data-email">{item.totalQuantity}</td>
                    <td className="team-data-email">${item.totalSales.toFixed(2)}</td>
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

          {view === "dailySales" && (
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
                <tr className="team-main-bg TeamsTableHeading" style={{justifyContent:"center"}}>
                  <th className="team-data-main" style={{ width: "24%" }}>
                    Date
                  </th>
                  <th className="team-data-role" style={{ marginLeft: "-35px" }}>Total Sales</th>
                </tr>
              </thead>
              <tbody>
                {dailySales.map((item, index) => (
                  <tr className="TeamDetailsRowData" key={index} style={{justifyContent:"center"}}>
                    <td className="team-data-main" style={{ overflowWrap: "break-word", width: "24%" }}>
                      {item.date}
                    </td>
                    <td className="team-data-role" style={{ marginLeft: "-35px" }}>${item.dailyTotalSales.toFixed(2)}</td>
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

export default SalesReport;
