package com.mistatistic.webhookbot.services;

import com.mistatistic.webhookbot.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
