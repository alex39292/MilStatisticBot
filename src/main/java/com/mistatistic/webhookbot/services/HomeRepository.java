package com.mistatistic.webhookbot.services;

import com.mistatistic.webhookbot.models.HomeDB;
import org.springframework.data.repository.CrudRepository;

public interface HomeRepository extends CrudRepository<HomeDB, Integer> {
}
