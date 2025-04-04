package com.seamlesstrackers.safarsathi.repository;

import com.seamlesstrackers.safarsathi.entity.Rahasathi;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface Rahasathirepo extends MongoRepository<Rahasathi, String> {
    Rahasathi findByBusNo(String busNo);
}
