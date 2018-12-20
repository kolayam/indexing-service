package eu.nimble.indexing.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.nimble.indexing.repository.model.catalogue.AdditionalProperty;
import eu.nimble.indexing.repository.model.catalogue.ItemType;
import eu.nimble.indexing.repository.model.catalogue.PartyType;
import eu.nimble.indexing.repository.model.owl.Clazz;
import eu.nimble.indexing.repository.model.owl.Property;
import eu.nimble.indexing.service.CatalogueService;
import eu.nimble.indexing.service.ClassService;
import eu.nimble.indexing.service.ManufacturerService;
import eu.nimble.indexing.service.PropertyService;

@RestController
public class IndexController {
	
	@Autowired
	private PropertyService properties;
	
	@Autowired
	private ClassService classes;
	
	@Autowired
	private ManufacturerService manufacturers;
	
	@Autowired
	private CatalogueService items;

	@GetMapping("/class")
	public ResponseEntity<Clazz> getClass(    		
			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestParam String uri) {
		Clazz c = classes.getClass(uri);
		return ResponseEntity.ok(c);
	}
	@GetMapping("/classes")
	public ResponseEntity<List<Clazz>> getClasses(    		
			@RequestHeader(value = "Authorization") String bearerToken, 
			@RequestParam String property) {
		List<Clazz> result = classes.getClasses(property);
		return ResponseEntity.ok(result);
	}
	@DeleteMapping("/class")
	public ResponseEntity<Boolean> removeClass(    		
			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestParam String uri) {
		classes.removeClass(uri);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@GetMapping("/manufacturer")
	public ResponseEntity<PartyType> getManufacturer(    		
			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestParam String uri) {
		if ( uri.equals("null") ) {
			PartyType p = new PartyType();
			p.setId("uri");
			p.setName("name");
			p.setOrigin("origin");
			p.setCertificateType(Collections.singletonList("certificateType"));
			p.setPpapComplianceLevel("ppapComlianceLevel");
			p.setPpapDocumentType("ppapDocumentType");
			p.setTrustDeliveryPackaging(0.0d);
			p.setTrustFullfillmentOfTerms(0.0d);
			p.setTrustNumberOfTransactions(0.0d);
			p.setTrustRating(0.0d);
			p.setTrustScore(0.0d);
			p.setTrustSellerCommunication(0.0d);
			p.setTrustTradingVolume(0.0d);
			return ResponseEntity.ok(p);
		}
		PartyType m = manufacturers.getManufacturerParty(uri);
		return ResponseEntity.ok(m);
	}
	@GetMapping("/manufacturers")
	public ResponseEntity<List<PartyType>> getManufacturers(    		
			@RequestHeader(value = "Authorization") String bearerToken, 
			@RequestParam String property) {
		// TODO: check query options
		List<PartyType> result = manufacturers.getManufacturerParties(null);
		return ResponseEntity.ok(result);
	}
	@DeleteMapping("/manufacturer")
	public ResponseEntity<Boolean> removeManufacturer(    		
			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestParam String uri) {
		manufacturers.removeRemoveManufacturerParty(uri);
		return ResponseEntity.ok(Boolean.TRUE);
	}
	@PostMapping("/manufacturer")
	public ResponseEntity<Boolean> setManufacturer(		
			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestBody PartyType party) {
		manufacturers.setManufacturerParty(party);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@GetMapping("/property")
	public ResponseEntity<Property> getProperty(    		
			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestParam String uri) {
		Property prop = properties.getProperty(uri);
		return ResponseEntity.ok(prop);
	}
	@GetMapping("/properties")
	public ResponseEntity<List<Property>> getProperties(    		
			@RequestHeader(value = "Authorization") String bearerToken, 
			@RequestParam(name="product") String productType) {
		List<Property> prop = properties.getProperties(productType);
		return ResponseEntity.ok(prop);
	}
	@DeleteMapping("/property")
	public ResponseEntity<Boolean> removeProperty(    		
			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestParam String uri) {
		properties.removeProperty(uri);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@PostMapping("/property")
	public ResponseEntity<Boolean> setProperty(
			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestBody Property prop
    		) {
		properties.setProperty(prop);
		return ResponseEntity.ok(Boolean.TRUE);
	}
	

	@GetMapping("/item")
	public ResponseEntity<ItemType> getItem(    		
			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestParam(defaultValue="null") String uri) {
		if ( uri.equals("null")) {
			ItemType item = new ItemType();
			item.setUri("uri");
			item.addName("en", "English Name");
			item.addName("es", "Espana");
			item.addDescription("en", "English desc");
			item.setCatalogueId("cat_id");
			item.setEmissionStandard("emission");
			item.setFreeOfCharge(false);
			item.addPrice("EUR", 100.00);
			item.addPrice("USD", 110.00);
			item.addPackageAmounts("Palette(s)", asList(30.0, 15.0));
			item.setCatalogueId("Euro");
			item.setManufacturerId("manu_001");
			item.addDeliveryTime("Week(s)", 2.0);
			item.addDeliveryTime("Day(s)", 14.0);
			AdditionalProperty add = new AdditionalProperty();
			add.setId("uri-prop-01");
			// 
			add.addName("en", "Property Name");
			add.addName("es", "Prop nom");
			// 
			add.addValue("Month(s)", 3.0);
			add.addValue("Day(s)", 60.0);
			//
			add.setValueQualifier("quantity");
			// 
			item.getAdditionalProperty().add(add);
			return ResponseEntity.ok(item);
		}
		ItemType prop = items.getItem(uri);
		return ResponseEntity.ok(prop);
	}
	private <T> List<T> asList(@SuppressWarnings("unchecked") T ...ts ) {
		List<T> set = new ArrayList<T>();
		for ( T t : ts) {
			set.add(t);
		}
		return set;
	}
	@DeleteMapping("/item")
	public ResponseEntity<Boolean> removeItem(    		
			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestParam String uri) {
		items.removeItem(uri);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@PostMapping("/item")
	public ResponseEntity<Boolean> setItem(
			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestBody ItemType prop
    		) {
		items.setItem(prop);
		return ResponseEntity.ok(Boolean.TRUE);
	}
			

}
