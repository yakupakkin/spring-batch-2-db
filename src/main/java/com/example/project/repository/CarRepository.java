package com.example.project.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.project.pojo.Car;

public interface CarRepository extends CrudRepository<Car, String> {

}
