package com.example.project.service;

import com.example.project.pojo.Car;
import org.springframework.batch.item.ItemProcessor;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class CarItemProcessor implements ItemProcessor<Car, Car> {


	@Override
	public Car process(final Car car) throws Exception {
		String brand = car.getBrand().toLowerCase();
		String origin = car.getModel().toLowerCase();
		String color = car.getColor().toLowerCase();

		Car transformedCar = new Car(brand, origin, color);
		// log.info("Converting ( {} ) into ( {} )", car, transformedCar);

		return transformedCar;
	}
}
