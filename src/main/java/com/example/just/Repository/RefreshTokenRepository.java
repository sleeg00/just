package com.example.just.Repository;

import com.example.just.Dao.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    @Query("SELECT r.refreshToken FROM RefreshToken r WHERE r.refreshToken = :token")
    String findByRefreshToken(@Param("token") String token);
}
