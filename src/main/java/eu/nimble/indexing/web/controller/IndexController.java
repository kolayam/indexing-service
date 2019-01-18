package eu.nimble.indexing.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.nimble.indexing.repository.model.ItemUtils;
import eu.nimble.indexing.repository.model.PartyTypeUtils;
import eu.nimble.indexing.repository.model.catalogue.ItemType;
import eu.nimble.indexing.repository.model.catalogue.PartyType;
import eu.nimble.indexing.repository.model.owl.ClassType;
import eu.nimble.indexing.repository.model.owl.PropertyType;
import eu.nimble.indexing.service.CatalogueService;
import eu.nimble.indexing.service.ClassService;
import eu.nimble.indexing.service.PartyTypeService;
import eu.nimble.indexing.service.PropertyService;

@RestController
public class IndexController {
	
	@Autowired
	private PropertyService properties;
	
	@Autowired
	private ClassService classes;
	
	@Autowired
	private PartyTypeService partyService;
	
	@Autowired
	private CatalogueService items;

	@GetMapping("/class")
	public ResponseEntity<ClassType> getClass(    		
//			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestParam String uri) {
		ClassType c = classes.getClass(uri);
		return ResponseEntity.ok(c);
	}
	@GetMapping("/classes")
	public ResponseEntity<List<ClassType>> getClasses(    		
//			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam(required=false) Set<String> uri,
			@RequestParam(required=false) String nameSpace,
			@RequestParam(required=false) Set<String> localName,
			@RequestParam(required=false) String property) {
		if ( property != null) {
			List<ClassType> result = classes.getClassesForProperty(property);
			return ResponseEntity.ok(result);
		}
		if ( uri!=null && !uri.isEmpty()) {
			List<ClassType> result = classes.getClasses(uri);
			return ResponseEntity.ok(result);
			
		}
		if ( nameSpace !=null && localName!=null && !localName.isEmpty()) {
			List<ClassType> result = classes.getClasses(uri);
			return ResponseEntity.ok(result);
		}
		return ResponseEntity.ok(new ArrayList<>());
	}
	@DeleteMapping("/class")
	public ResponseEntity<Boolean> removeClass(    		
//			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestParam String uri) {
		classes.removeClass(uri);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@GetMapping("/party")
	public ResponseEntity<PartyType> getParty(    		
//			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestParam(defaultValue="null") String uri) {
		if ( uri.equals("null") ) {
			return ResponseEntity.ok(PartyTypeUtils.template());
		}
		PartyType m = partyService.getPartyType(uri);
		return ResponseEntity.ok(m);
	}
	@GetMapping("/parties")
	public ResponseEntity<List<PartyType>> getParties(    		
//			@RequestHeader(value = "Authorization") String bearerToken, 
			@RequestParam String property) {
		// TODO: check query options
		List<PartyType> result = partyService.getPartyTypes(null);
		return ResponseEntity.ok(result);
	}
	@DeleteMapping("/party")
	public ResponseEntity<Boolean> removeManufacturer(    		
//			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestParam String uri) {
		partyService.removePartyType(uri);
		return ResponseEntity.ok(Boolean.TRUE);
	}
	@PostMapping("/party")
	public ResponseEntity<Boolean> setManufacturer(		
//			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestBody PartyType party) {
		partyService.setPartyType(party);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@GetMapping("/property")
	public ResponseEntity<PropertyType> getProperty(    		
//			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestParam String uri) {
		PropertyType prop = properties.getProperty(uri);
		return ResponseEntity.ok(prop);
	}
	@GetMapping("/properties")
	public ResponseEntity<List<PropertyType>> getProperties(    		
//			@RequestHeader(value = "Authorization") String bearerToken, 
			@RequestParam(name="uri", required=false) Set<String> uri,
			@RequestParam(name="product", required=false) String productType,
			@RequestParam(name="nameSpace", required=false) String nameSpace,
			@RequestParam(name="localName", required=false) Set<String> localNames,
			@RequestParam(name="idxName", required=false) Set<String> idxName) {
		if ( uri!=null && !uri.isEmpty()) {
			List<PropertyType> prop = properties.getPropertiesByUri(uri);
			return ResponseEntity.ok(prop);
		}
		if ( productType != null) {
			List<PropertyType> prop = properties.getProperties(productType);
			return ResponseEntity.ok(prop);
		}
		if ( idxName!=null) {
			List<PropertyType> prop = properties.getPropertiesByIndexName(idxName);
			return ResponseEntity.ok(prop);
		}
		if ( nameSpace!=null && localNames != null && !localNames.isEmpty()) {
			List<PropertyType> prop = properties.getPropertiesByName(nameSpace, localNames);
			return ResponseEntity.ok(prop);
			
		}
		return ResponseEntity.ok(new ArrayList<>());
	}
	@DeleteMapping("/property")
	public ResponseEntity<Boolean> removeProperty(    		
//			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestParam String uri) {
		properties.removeProperty(uri);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@PostMapping("/property")
	public ResponseEntity<Boolean> setProperty(
//			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestBody PropertyType prop
    		) {
		properties.setProperty(prop);
		return ResponseEntity.ok(Boolean.TRUE);
	}
	

	@GetMapping("/item")
	public ResponseEntity<ItemType> getItem(    		
//			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestParam(defaultValue="null") String uri) {
		if ( uri.equals("null")) {
			return ResponseEntity.ok(ItemUtils.template());
		}
		ItemType prop = items.getItem(uri);
		return ResponseEntity.ok(prop);
	}

	@DeleteMapping("/item")
	public ResponseEntity<Boolean> removeItem(    		
//			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestParam String uri) {
		items.removeItem(uri);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@PostMapping("/item")
	public ResponseEntity<Boolean> setItem(
//			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestBody ItemType prop
    		) {
		items.setItem(prop);
		return ResponseEntity.ok(Boolean.TRUE);
	}

}
