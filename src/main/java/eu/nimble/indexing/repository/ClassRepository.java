package eu.nimble.indexing.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

import eu.nimble.indexing.repository.model.owl.ClassType;

@Repository
public interface ClassRepository  extends SolrCrudRepository<ClassType, String>{

	
	@Query(fields= {"*"})
	List<ClassType> findByUri(String uri);
	@Query(fields= {"*"})
	List<ClassType> findByProperties(String properties);
	/**
	 * Remove all properties of the provided namespace
	 * @param namespace
	 * @return
	 */
	long deleteByNameSpace(String namespace);
	/**
	 * Retrieve all classes based on their id (uri)
	 * @param uri
	 * @return
	 */
	List<ClassType> findByUriIn(Set<String> uri);
	/**
	 * 
	 * @param namespace
	 * @param localNames
	 * @return
	 */
	List<ClassType> findByNameSpaceAndLocalNameIn(String namespace, Set<String> localNames);
}


