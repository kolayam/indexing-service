package eu.nimble.indexing;

import eu.nimble.indexing.service.OntologyService;
import eu.nimble.indexing.service.impl.OntologyServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableJpaRepositories
public class IndexingAppTests {
	@Autowired
	private OntologyService service;

	@Test
	public void contextLoads() {
		Path filePath = Paths.get("/Users/fansword/Downloads/eclass_514en.owl");

		try {
			byte[] fileBytes = Files.readAllBytes(filePath);
			String content = new String(fileBytes);
			this.service.upload("application/rdf+xml", Collections.singletonList("http://www.ebusiness-unibw.org/ontologies/eclass/5.1.4/#"), content);

			System.out.println(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

