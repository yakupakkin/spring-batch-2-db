package com.example.project.service;

import org.springframework.batch.item.ItemProcessor;

import com.example.project.pojo.Car;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class CarItemProcessor implements ItemProcessor<Car, Car> {


	@Override
	public Car process(final Car car) throws Exception {

		Car transformedCar = new Car();
		transformedCar.setBrand(car.getBrand().toLowerCase());
		transformedCar.setModel(car.getModel().toLowerCase());
		transformedCar.setColor(car.getColor().toLowerCase());
		transformedCar.setId(car.getId());
		// log.info("Converting ( {} ) into ( {} )", car, transformedCar);

		return transformedCar;
	}
}
