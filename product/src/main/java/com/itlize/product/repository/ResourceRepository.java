package com.itlize.product.repository;

import com.itlize.product.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ResourceRepository extends JpaRepository<Resource,Integer> {
}