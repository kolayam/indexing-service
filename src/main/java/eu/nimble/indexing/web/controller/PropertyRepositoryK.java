package eu.nimble.indexing.web.controller;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface PropertyRepositoryK extends JpaRepository<PropertyK,String> {

    @Query("select s.url from PropertyK s where s.product like %:uri%")
    Set<String> findByProductContaining(String uri);

}
