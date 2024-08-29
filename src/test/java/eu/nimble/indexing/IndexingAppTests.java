package eu.nimble.indexing;

import eu.nimble.indexing.service.OntologyService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IndexingAppTests {
	@Autowired
	private OntologyService service;

	@Test
	public void contextLoads() {
		this.service.upload(null, null, null);
	}

}

