package com.slavacom.testtask.repository;

import com.slavacom.testtask.entity.Subscription;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByName(String name);

    @Query("""
                SELECT s FROM Subscription s
                LEFT JOIN s.users u
                GROUP BY s
                ORDER BY COUNT(u) DESC
            """)
    List<Subscription> findTopSubscriptions(Pageable pageable);

    boolean existsByName(String name);

}
