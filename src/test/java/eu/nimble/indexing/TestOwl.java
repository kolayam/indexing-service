//package eu.nimble.indexing;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import eu.nimble.indexing.service.impl.OntologyServiceImpl;
//import eu.nimble.service.model.solr.owl.ClassType;
//import eu.nimble.service.model.solr.owl.PropertyType;
//import org.apache.jena.ontology.OntClass;
//import org.apache.jena.ontology.OntModel;
//import org.apache.jena.ontology.OntModelSpec;
//import org.apache.jena.ontology.OntProperty;
//import org.apache.jena.query.Dataset;
//import org.apache.jena.rdf.model.ModelFactory;
//import org.apache.jena.riot.Lang;
//import org.apache.jena.riot.RDFDataMgr;
//import org.apache.jena.riot.RDFParser;
//import org.apache.jena.riot.system.StreamRDF;
//import org.apache.jena.riot.system.StreamRDFBase;
//import org.apache.jena.tdb.TDBFactory;
//
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//public class TestOwl {
//    public static void main(String[] args) throws FileNotFoundException {
//        try {
//            OntologyServiceImpl service = new OntologyServiceImpl();
//            // Create an InputStream for the RDF file
////            InputStream inputStream = new FileInputStream("eclass_514en.owl");
//            InputStream inputStream = new FileInputStream("FurnitureSectorTaxonomy-v2.4-org.owl");
//
//
//            // Create a custom StreamRDF handler to process each triple or quad
//            StreamRDF streamRDFHandler = new StreamRDFBase() {
//                @Override
//                public void triple(org.apache.jena.graph.Triple triple) {
//                    // Handle each triple as it is parsed
//                    System.out.println("Triple: " + triple);
//                }
//
//                @Override
//                public void quad(org.apache.jena.sparql.core.Quad quad) {
//                    // Handle each quad as it is parsed (if working with quads)
//                    System.out.println("Quad: " + quad);
//                }
//            };
//
//            // Parse the RDF data using the streaming handler
////            RDFParser.create()
////                    .source(inputStream)
////                    .lang(Lang.RDFXML) // Specify the RDF language (e.g., RDF/XML, Turtle)
////                    .parse(streamRDFHandler);
//
////            OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF);
//
////            RDFDataMgr.read(ontModel, inputStream, Lang.RDFNULL);
//
////            RDFParser.create()
////                    .source(inputStream)
////                    .lang(Lang.RDFXML) // Specify the RDF language (e.g., RDF/XML, Turtle)
////                    .parse(ontModel);
//
//
//            String directory = ".";
//            Dataset dataset = TDBFactory.createDataset(directory);
//
//// Access the OntModel from the dataset
//            OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, dataset.getDefaultModel());
//
//// Load the OWL file as before
//            ontModel.read(inputStream, null, "RDF/XML");
//            List<String> nameSpaces = new ArrayList<>();
////            nameSpaces.add("http://www.ebusiness-unibw.org/ontologies/eclass/5.1.4/#");
//            nameSpaces.add("http://www.aidimme.es/FurnitureSectorOntology.owl#");
//            nameSpaces.add("http://www.nimble-project.org/catalogue#");
//            List<PropertyType> indexedProp = new ArrayList<>();
//            /*
//             * Process all ontology properties, index them and fill
//             * the list of indexedProp
//             */
//            Iterator<OntProperty> properties = ontModel.listAllOntProperties();
//            while (properties.hasNext()) {
//                OntProperty p = properties.next();
//                // restrict import to namespace list provided
//                if (nameSpaces.isEmpty() || nameSpaces.contains(p.getNameSpace())) {
//                    if (!p.isOntLanguageTerm()) {
//                        PropertyType prop = service.processProperty(ontModel, p);
////                        try {
//////                            System.out.println("=============" + new ObjectMapper().writeValueAsString(prop));
////                        } catch (JsonProcessingException e) {
////                            throw new RuntimeException(e);
////                        }
//                        if (prop != null) {
////                            propRepo.save(prop);
//                            indexedProp.add(prop);
//                        }
//                    }
//                }
//            }
//
//            try {
//                System.out.println("=============" + new ObjectMapper().writeValueAsString(indexedProp));
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException(e);
//            }
//
//            /*
//             * process all ontology classes, index them and map all
//             * properties applicable to the class
//             */
//            List<ClassType> classTypes = new ArrayList<>();
//            Iterator<OntClass> classes = ontModel.listClasses();
//            while (classes.hasNext()) {
//                OntClass c = classes.next();
//                // restrict import to namespace list provided
//                if (nameSpaces.isEmpty() || nameSpaces.contains(c.getNameSpace())) {
//
//                    if (!c.isOntLanguageTerm()) {
//                        ClassType clazz = service.processClazz(ontModel, c, indexedProp);
//                        if (clazz != null) {
////                            classRepository.save(clazz);
//                            classTypes.add(clazz);
//                        }
//                    }
//                }
//            }
//
//            try {
//                System.out.println("=============" + new ObjectMapper().writeValueAsString(classTypes));
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException(e);
//            }
//        } finally {
//
//        }
//    }
//
//}
