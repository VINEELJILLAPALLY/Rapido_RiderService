package com.alpha.rider.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alpha.rider.entity.Rider;

@Repository
public interface RiderRepository extends JpaRepository<Rider, Integer>{

}
