package com.bptn.weatherapp.domain;

import java.util.List;

import com.bptn.weatherapp.jpa.FavouriteCity;

public class FavouriteCityResponse {

	List<FavouriteCity> favouriteCities;
	long maxFavouriteCities;

	public FavouriteCityResponse(List<FavouriteCity> favouriteCities, long maxFavouriteCities) {
		this.favouriteCities = favouriteCities;
		this.maxFavouriteCities = maxFavouriteCities;
	}

	public List<FavouriteCity> getFavouriteCities() {
		return favouriteCities;
	}

	public long getMaxFavouriteCities() {
		return maxFavouriteCities;
	}

}
