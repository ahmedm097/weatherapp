package com.bptn.weatherapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bptn.weatherapp.domain.FavouriteCityResponse;
import com.bptn.weatherapp.service.FavouriteCityService;

@CrossOrigin
@RestController
@RequestMapping("/favouritecity")
public class FavouriteCityController {

	final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	FavouriteCityService favouriteCityService;

	@PostMapping("/{cityId}")
	public void createFavouriteCity(@PathVariable int cityId) {
		logger.debug("Creating favourite city for cityId: {}", cityId);
		this.favouriteCityService.createFavouriteCity(cityId);
	}

	@GetMapping
	public FavouriteCityResponse getFavouriteCities() {
		logger.debug("Getting favourite cities");
		return this.favouriteCityService.getFavouriteCities();
	}

	@DeleteMapping("/{favouriteCityId}")
	public void deleteFavouriteCity(@PathVariable int favouriteCityId) {
		this.favouriteCityService.deleteFavouriteCity(favouriteCityId);
	}

}
