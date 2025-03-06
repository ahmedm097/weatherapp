import React, { useState, useEffect } from "react";
import { BsEye, BsWater, BsThermometer, BsWind } from "react-icons/bs";
import { TbWorldLatitude, TbWorldLongitude } from "react-icons/tb";
import {
  WiCelsius,
  WiSunrise,
  WiSunset,
  WiWindDeg,
  WiBarometer,
  WiDegrees,
} from "react-icons/wi";

import TokenExpirationPage from "../TokenExpirationPage/TokenExpirationPage";
import {
  getFavouriteCitiesApi,
  createFavouriteCityApi,
} from "../../util/ApiUtil";
import toast from "react-hot-toast";

const DisplayWeatherData = ({ apiResponse, token }) => {
  const [result, setResult] = useState([]);
  const [data, setData] = useState(null);
  const [isFavourite, setIsFavourite] = useState(false);
  const [tokenExpired, setTokenExpired] = useState(false);

  const getMyFavouriteCities = async () => {
    const response = await getFavouriteCitiesApi(token);
    if (response.status === 1 && response.payLoad.favouriteCities.length > 0) {
      setResult(response.payLoad.favouriteCities);
      setData(true);
      checkFavouriteCity();
    } else if (
      response.status === 1 &&
      response.payLoad.favouriteCities.length === 0
    ) {
      setData(false);
    } else if (
      response.status === 0 &&
      response.payLoad === "Token has Expired"
    ) {
      setTokenExpired(true);
    } else {
      toast(response.payLoad);
    }
  };

  useEffect(() => {
    getMyFavouriteCities();
  }, [isFavourite]);

  const checkFavouriteCity = () => {
    if (result.find((x) => x.city.cityId === apiResponse.city.cityId)) {
      setIsFavourite(true);
      return;
    }
  };

  const addFavouriteCity = async () => {
    if (!isFavourite) {
      const response = await createFavouriteCityApi(
        token,
        apiResponse.city.cityId
      );

      if (response.status === 1) {
        setIsFavourite(true);
        toast(
          `${apiResponse.city.name} has been added to your favourite. You can now view it on your favourite cities page!`
        );
      } else if (
        response.status === 0 &&
        response.payLoad === "Token has Expired"
      ) {
        setTokenExpired(true);
      } else {
        toast(response.payLoad);
      }
    } else {
      toast(
        `${apiResponse.city.name} is already on the list of Favourite Cities!`
      );
    }
  };

  // if (tokenExpired) {
  //   return <TokenExpirationPage />;
  // }
  //getting the icon from the weather response
  let iconCode = apiResponse.icon;

  //generating image url with the icon code
  let iconUrl = "http://openweathermap.org/img/w/" + iconCode + ".png";

  return (
    <div>
      {/* card top */}
      <div className="flex justify-between">
        <div className="flex items-center gap-x-5">
          {/* icon */}
          <div className="text-[87px]">
            <img src={iconUrl} />
          </div>
          <div>
            {/* city name, country code */}
            <div className="text-2xl font-semibold">
              {apiResponse.city.name}, {apiResponse.city.country.countryCode}
            </div>
            {/* date */}
            <div>Last Updated: {apiResponse.updatedOn}</div>
          </div>
        </div>
        <div
          className="flex flex-col justify-center"
          onClick={addFavouriteCity}
        >
          <span className="">
            <svg
              width="120px"
              height="120px"
              viewBox="0 0 24 24"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
              <g id="SVGRepo_bgCarrier" stroke-width="0"></g>
              <g
                id="SVGRepo_tracerCarrier"
                stroke-linecap="round"
                stroke-linejoin="round"
              ></g>
              <g id="SVGRepo_iconCarrier">
                {" "}
                <path
                  d="M11.5245 3.46353C11.6741 3.00287 12.3259 3.00287 12.4755 3.46353L14.1329 8.56434C14.1998 8.77035 14.3918 8.90983 14.6084 8.90983H19.9717C20.4561 8.90983 20.6575 9.52964 20.2656 9.81434L15.9266 12.9668C15.7514 13.0941 15.678 13.3198 15.745 13.5258L17.4023 18.6266C17.552 19.0873 17.0248 19.4704 16.6329 19.1857L12.2939 16.0332C12.1186 15.9059 11.8814 15.9059 11.7061 16.0332L7.3671 19.1857C6.97524 19.4704 6.448 19.0873 6.59768 18.6266L8.25503 13.5258C8.32197 13.3198 8.24864 13.0941 8.07339 12.9668L3.73438 9.81434C3.34253 9.52964 3.54392 8.90983 4.02828 8.90983H9.39159C9.6082 8.90983 9.80018 8.77035 9.86712 8.56434L11.5245 3.46353Z"
                  fill={` ${isFavourite ? "yellow" : "white"}`}
                ></path>{" "}
              </g>
            </svg>
          </span>
        </div>
      </div>

      {/* card body */}
      <div className="my-20">
        <div className="flex justify-center items-center">
          {/* temp */}
          <div className="text-[144px] leading-none font-light">
            {parseInt(apiResponse.temp)}
          </div>
          {/* celsius icon */}
          <div className="text-6xl">
            <WiCelsius />
          </div>
        </div>
        {/* weather description */}
        <div className="capitalize text-center">
          {apiResponse.description}
          <br />
          {/* weather cloudiness */}
          Cloudiness {apiResponse.cloudsAll} %
        </div>
      </div>

      {/* card bottom */}
      <div className="max-w-[378px] mx-auto flex flex-col gap-y-6">
        <div className="flex justify-between">
          <div className="flex items-center gap-x-2">
            {/* latitude icon */}
            <div className="text-[20px]">
              <TbWorldLatitude />
            </div>
            <div>
              {/* Latitude */}
              Latitude{" "}
              <span className="ml-2">{apiResponse.city.latitude} </span>
            </div>
          </div>
          <div className="flex items-center gap-x-2">
            {/* longitude icon */}
            <div className="text-[20px]">
              <TbWorldLongitude />
            </div>
            <div className="flex">
              {/* Longitude */}
              Longitude{" "}
              <span className="ml-2">{apiResponse.city.longitude}</span>
            </div>
          </div>
        </div>
        <div className="flex justify-between">
          <div className="flex items-center gap-x-2">
            {/* Visibility icon */}
            <div className="text-[20px]">
              <BsEye />
            </div>
            <div>
              {/* Visibility */}
              Visibility{" "}
              <span className="ml-2">{apiResponse.visibility / 1000} km</span>
            </div>
          </div>
          <div className="flex items-center gap-x-2">
            {/* icon */}
            <div className="text-[20px]">
              <BsThermometer />
            </div>
            <div className="flex">
              {/* Feels Like */}
              Feels like
              <div className="flex ml-2 text-3xl">
                {parseInt(apiResponse.feelsLike)}
                <WiCelsius />
              </div>
            </div>
          </div>
        </div>
        <div className="flex justify-between">
          <div className="flex items-center gap-x-2">
            {/* icon */}
            <div className="text-[20px]">
              <BsWater />
            </div>
            <div>
              {/* Humidity */}
              Humidity
              <span className="ml-2">{apiResponse.humidity} %</span>
            </div>
          </div>
          <div className="flex items-center gap-x-2">
            {/* icon */}
            <div className="text-[20px]">
              <BsWind />
            </div>
            <div>
              {/* Wind Speed*/}
              Wind Speed{" "}
              <span className="ml-2">{apiResponse.windSpeed} m/s</span>
            </div>
          </div>
        </div>
        <div className="flex justify-between">
          <div className="flex items-center gap-x-2">
            {/* icon */}
            <div className="text-[20px]">
              <BsThermometer />
            </div>
            <div className="flex">
              {/* Min Temp */}
              Min Temp
              <div className="flex ml-2 text-3xl">
                {parseInt(apiResponse.tempMin)}
                <WiCelsius />
              </div>
            </div>
          </div>
          <div className="flex items-center gap-x-2">
            {/* icon */}
            <div className="text-[20px]">
              <BsThermometer />
            </div>
            <div className="flex">
              {/* Max Temp */}
              Max Temp
              <div className="flex ml-2 text-3xl">
                {parseInt(apiResponse.tempMax)}
                <WiCelsius />
              </div>
            </div>
          </div>
        </div>
        <div className="flex justify-between">
          <div className="flex items-center gap-x-2">
            {/* icon */}
            <div className="text-[20px]">
              <WiSunrise />
            </div>
            <div className="flex">
              {/* Sunrise*/}
              Sunrise
              <div className="flex ml-2 text-1xl">
                {new Date(apiResponse.sunrise).toLocaleTimeString()}
              </div>
            </div>
          </div>
          <div className="flex items-center gap-x-2">
            {/* icon */}
            <div className="text-[20px]">
              <WiSunset />
            </div>
            <div className="flex">
              {/* Sunset */}
              Sunset
              <div className="flex ml-2 text-1xl">
                {new Date(apiResponse.sunset).toLocaleTimeString()}
              </div>
            </div>
          </div>
        </div>
        <div className="flex justify-between">
          <div className="flex items-center gap-x-2">
            {/* icon */}
            <div className="text-[20px]">
              <WiWindDeg />
            </div>
            <div className="flex">
              {/* Wind direction */}
              Wind Direction
              <div className="flex ml-2 text-1xl">
                {apiResponse.windDirection}
                <div className="text-3xl">
                  <WiDegrees />
                </div>
              </div>
            </div>
          </div>
          <div className="flex items-center gap-x-2">
            {/* icon */}
            <div className="text-[20px]">
              <WiBarometer />
            </div>
            <div className="flex">
              {/* Pressure */}
              Pressure
              <div className="flex ml-2 text-1xl">
                {apiResponse.pressure} hPa
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DisplayWeatherData;
