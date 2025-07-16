package com.example.candy.repositories;

import com.example.candy.models.Order;
import com.example.candy.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    
    // Make sure that the Specification-based queries are properly handling pagination
    // when using JpaSpecificationExecutor.findAll(Specification, Pageable)
    
    List<Order> findByUserOrderByOrderDateDesc(User user);
    
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items oi LEFT JOIN FETCH oi.book WHERE o.user = :user ORDER BY o.orderDate DESC")
    List<Order> findByUserWithItemsAndBooks(@Param("user") User user);
    
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items oi LEFT JOIN FETCH oi.book WHERE o.id = :id")
    Optional<Order> findByIdWithItemsAndBooks(@Param("id") Long id);
    
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items oi LEFT JOIN FETCH oi.book WHERE o.id = :id AND o.user = :user")
    Optional<Order> findByIdAndUserWithItemsAndBooks(@Param("id") Long id, @Param("user") User user);
}
