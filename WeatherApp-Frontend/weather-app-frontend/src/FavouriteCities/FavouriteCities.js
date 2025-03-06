import React, { useState, useEffect } from "react";
import { getFavouriteCitiesApi } from "../util/ApiUtil";
import NoFavouriteCityPresent from "./NoFavouriteCityPresent";
import DisplayFavouriteCity from "./DisplayFavouriteCity";
import TokenExpirationPage from "../components/TokenExpirationPage/TokenExpirationPage";
import LoadingIndicator from "../components/LoadingIndicator/LoadingIndicator";
import toast from "react-hot-toast";

const FavouriteCities = ({ currentUser }) => {
  const [result, setResult] = useState([]);
  const [Loading, setLoading] = useState(true);
  const [tokenExpired, setTokenExpired] = useState(false);
  const [data, setData] = useState(null);

  useEffect(() => {
    getMyFavouriteCities();
  }, []);

  const getMyFavouriteCities = async () => {
    console.log(currentUser.token);
    const apiResponse = await getFavouriteCitiesApi(currentUser.token);
    if (
      apiResponse.status === 1 &&
      apiResponse.payLoad.favouriteCities.length > 0
    ) {
      setResult(apiResponse.payLoad.favouriteCities);
      setLoading(false);
      setData(true);
    } else if (
      apiResponse.status === 1 &&
      apiResponse.payLoad.favouriteCities.length === 0
    ) {
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
    return <TokenExpirationPage />;
  }
  if (data === false) {
    return (
      <div className="flex items-center justify-center">
        <NoFavouriteCityPresent />;
      </div>
    );
  } else {
    return (
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4 p-4">
        {result.map((item, index) => (
          <div
            key={index}
            className="bg-black/20 text-purple-900 backdrop-blur-[80px] py-12 px-6 rounded-lg overflow-hidden"
          >
            <DisplayFavouriteCity
              favouriteCity={item}
              token={currentUser.token}
              loadOnDelete={getMyFavouriteCities}
            />
          </div>
        ))}
      </div>
    );
  }
};

export default FavouriteCities;
