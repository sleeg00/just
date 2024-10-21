package com.example.just.Repository;

import com.example.just.Redis.Fcm;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FcmRepository extends CrudRepository<Fcm, Long> {
}
