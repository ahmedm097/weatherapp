package com.bptn.weatherapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bptn.weatherapp.jpa.City;
import com.bptn.weatherapp.jpa.FavouriteCity;
import com.bptn.weatherapp.jpa.User;

public interface FavouriteCityRepository extends JpaRepository<FavouriteCity, Integer> {

	List<FavouriteCity> findFavouriteCitiesByUser(User user);

	Optional<FavouriteCity> findByUserAndCity(User user, City city);

	long countByUser(User user);
}
