package com.example.gateway_dynamic_route.repository;


import com.example.gateway_dynamic_route.model.pojo.Router;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRepository extends JpaRepository<Router,String> {
}
