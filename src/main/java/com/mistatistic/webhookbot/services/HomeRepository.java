package com.mistatistic.webhookbot.services;

import com.mistatistic.webhookbot.models.Home;
import org.springframework.data.repository.CrudRepository;

public interface HomeRepository extends CrudRepository<Home, Integer> {
}
