import React, { useState } from "react";
import { TbWorldLatitude, TbWorldLongitude } from "react-icons/tb";
import { deleteFavouriteCityApi } from "../util/ApiUtil";

const DisplayFavouriteCity = ({
  favouriteCity,
  token,
  loadOnDelete = undefined,
}) => {
  const [isFetching, setIsFetching] = useState(false);

  const deleteFavouriteCity = async () => {
    if (!isFetching) {
      setIsFetching(true);

      const apiResponse = await deleteFavouriteCityApi(
        token,
        favouriteCity.favouriteCityId
      );
      if (apiResponse.status === 1) {
        loadOnDelete(0);
      }
      setIsFetching(false);
    }
  };

  return (
    <div className="flex flex-col gap-y-3">
      {/* card top */}
      <div className="flex w-full flex-row">
        {/* city name, country code */}
        <div className="flex flex-row w-full py-2 text-3xl font-bold">
          {favouriteCity.city.name}, {favouriteCity.city.country.countryCode}
        </div>
        {/* {#DeleteFavouriteCityButton Section} */}
        {loadOnDelete && (
          <div
            className="flex flex-col justify-center"
            onClick={deleteFavouriteCity}
          >
            <span className="transition ease-out duration-300 hover:bg-gray-50 bg-gray-100 h-9 px-2 py-2 text-center rounded-full text-gray-100 cursor-pointer">
              <svg
                className="h-5 w-5 text-gray-500"
                fill="none"
                viewBox="0 0 24 24"
              >
                <path
                  fill="currentColor"
                  d="M7 21q-.825 0-1.413-.588T5 19V6H4V4h5V3h6v1h5v2h-1v13q0 .825-.588 1.413T17 21H7ZM17 6H7v13h10V6ZM9 17h2V8H9v9Zm4 0h2V8h-2v9ZM7 6v13V6Z"
                />
              </svg>
            </span>
          </div>
        )}
      </div>
      {/* latitude */}
      <div className="flex items-center gap-x-2">
        {/* latitude icon */}
        <div className="text-[20px]">
          <TbWorldLatitude />
        </div>
        <div className="text-lg">
          {/* Latitude */}
          Latitude <span className="ml-2">{favouriteCity.city.latitude} </span>
        </div>
      </div>
      {/* longitude */}
      <div className="flex items-center gap-x-2">
        {/* longitude icon */}
        <div className="text-[20px]">
          <TbWorldLongitude />
        </div>
        <div className="text-lg">
          {/* Longitude */}
          Longitude <span className="ml-2">{favouriteCity.city.longitude}</span>
        </div>
      </div>
      {/* date */}
      <div className="text-lg">Last Updated: {favouriteCity.createdOn}</div>
    </div>
  );
};

export default DisplayFavouriteCity;
