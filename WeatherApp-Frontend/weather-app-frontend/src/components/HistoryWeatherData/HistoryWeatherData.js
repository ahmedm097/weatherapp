import React, { useState, useEffect, use } from "react";
import NoHistoryWeatherPresent from "./NoHistoryWeatherPresent";
import { getHistoryWeatherDataApi } from "../../util/ApiUtil";
import DisplayWeatherData from "../CurrentWeatherData/DisplayWeatherData";
import TokenExpirartionPage from "../TokenExpirationPage/TokenExpirationPage";
import LoadingIndicator from "../LoadingIndicator/LoadingIndicator";
import toast from "react-hot-toast";

const HistoryWeatherData = ({ currentUser }) => {
  const [result, setResult] = useState([]);
  const [Loading, setLoading] = useState(true);
  const [tokenExpired, setTokenExpired] = useState(false);
  const [data, setData] = useState(null);

  useEffect(() => {
    getMyResults();
  }, []);

  const getMyResults = async () => {
    const apiResponse = await getHistoryWeatherDataApi(currentUser.token);
    if (apiResponse.status === 1 && apiResponse.payLoad.length > 0) {
      setResult(apiResponse.payLoad);
      setLoading(false);
      setData(true);
    } else if (apiResponse.status === 1 && apiResponse.payLoad.length === 0) {
      setLoading(false);
      setData(false);
    } else if (
      apiResponse.status === 0 &&
      apiResponse.payLoad === "Token has Expired"
    ) {
      setTokenExpired(true);
      setLoading(false);
    } else {
      toast(apiResponse.payLoad);
    }
  };

  if (Loading) {
    return <LoadingIndicator />;
  }

  if (tokenExpired) {
    return <TokenExpirartionPage />;
  }
  if (data === false) {
    return (
      <div className="flex items-center justify-center">
        <NoHistoryWeatherPresent />;
      </div>
    );
  } else {
    return (
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4 p-4">
        {result.map((item, index) => (
          <div
            key={index}
            className="bg-black/20 text-pruple-900 backdrop-blur-[80px] py-12 px-6 rounded-lg overflow-hidden"
          >
            <DisplayWeatherData apiResponse={item} token={currentUser.token} />
          </div>
        ))}
      </div>
    );
  }
};

export default HistoryWeatherData;
