package com.bptn.weatherapp.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bptn.weatherapp.domain.FavouriteCityResponse;
import com.bptn.weatherapp.exception.domain.CityNotFoundException;
import com.bptn.weatherapp.exception.domain.FavouriteCityExistsException;
import com.bptn.weatherapp.exception.domain.FavouriteCityMaxReachedException;
import com.bptn.weatherapp.exception.domain.FavouriteCityNotFoundException;
import com.bptn.weatherapp.exception.domain.FavouriteCityNotUserException;
import com.bptn.weatherapp.exception.domain.UserNotFoundException;
import com.bptn.weatherapp.jpa.City;
import com.bptn.weatherapp.jpa.FavouriteCity;
import com.bptn.weatherapp.jpa.User;
import com.bptn.weatherapp.provider.ResourceProvider;
import com.bptn.weatherapp.repository.CityRepository;
import com.bptn.weatherapp.repository.FavouriteCityRepository;
import com.bptn.weatherapp.repository.UserRepository;

@Service
public class FavouriteCityService {

	final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	FavouriteCityRepository favouriteCityRepository;

	@Autowired
	ResourceProvider resourceProvider;

	@Autowired
	UserRepository userRepository;

	@Autowired
	CityRepository cityRepository;

	public void createFavouriteCity(int cityId) {

		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		User user = this.userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException(String.format("Username doesn't exist, %s", username)));

		City city = this.cityRepository.findById(cityId).get();

		if (city == null) {
			throw new CityNotFoundException(String.format("City doesn't exist, %s", cityId));
		}

		Optional<FavouriteCity> optFavouriteCity = this.favouriteCityRepository.findByUserAndCity(user, city);

		if (optFavouriteCity.isPresent()) {
			throw new FavouriteCityExistsException(String.format("Favourite city already exists for %s", city));
		}

		if (this.favouriteCityRepository.countByUser(user) >= resourceProvider.getMaxFavouriteCities()) {
			throw new FavouriteCityMaxReachedException(
					String.format("Exceeds maxiumum favourite city for %s", username));
		}

		FavouriteCity favouriteCity = new FavouriteCity();

		favouriteCity.setCity(city);
		favouriteCity.setUser(user);
		favouriteCity.setCreatedOn(Timestamp.from(Instant.now()));
		this.favouriteCityRepository.save(favouriteCity);

	}

	public FavouriteCityResponse getFavouriteCities() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		User user = this.userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException(String.format("Username doesn't exist, %s", username)));

		List<FavouriteCity> listOfFavouriteCity = this.favouriteCityRepository.findFavouriteCitiesByUser(user);

		long maxNumOfFavouriteCity = this.resourceProvider.getMaxFavouriteCities();

		return new FavouriteCityResponse(listOfFavouriteCity, maxNumOfFavouriteCity);
	}

	public void deleteFavouriteCity(int favouriteCityId) {

		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		User user = this.userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException(String.format("Username doesn't exist, %s", username)));

		FavouriteCity favouriteCity = this.favouriteCityRepository.findById(favouriteCityId).get();

		if (favouriteCity == null) {
			throw new FavouriteCityNotFoundException(String.format("Favourite city not found for %s", username));
		}

		if (this.favouriteCityRepository.findFavouriteCitiesByUser(user).contains(favouriteCity)) {
			throw new FavouriteCityNotUserException(String.format("Favourite city doesnt belong to %s", username));
		} else {
			this.favouriteCityRepository.delete(favouriteCity);
		}

	}
}
