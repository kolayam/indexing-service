package eu.nimble.indexing.web.controller;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyRepositoryK extends JpaRepository<PropertyK,String> {
    List<PropertyK> findByProduct(String product);
}
