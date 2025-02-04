from rdflib import Graph, URIRef, RDF,RDFS, Literal
from rdflib.namespace import Namespace
from collections import defaultdict
from typing import Optional

# Define namespaces
NS = Namespace("http://www.nimble-project.org/catalogue#")
QUANTITY_TYPE = "QuantityType"
CODE_TYPE = "CodeType"
UNIT_TYPE = "UnitType"
LIST_TYPE = "ListType"
UNIT_LIST = "UnitList"
CODE_LIST = "CodeList"
HAS_CODE = "hasCode"
HAS_UNIT = "hasUnit"
HAS_CODE_LIST = "hasCodeList"
HAS_UNIT_LIST = "hasUnitList"
CODE = "code"
UNIT_CODE = "unitCode"
IS_VISIBLE = "isVisible"
IS_REQUIRED = "isRequired"
ID = "id"
QUANTITY_PROPERTY_TYPE = "QuantityProperty"
CODE_PROPERTY_TYPE = "CodeProperty"
FILE_PROPERTY_TYPE = "FileProperty"

class ValueQualifier:
    QUANTITY = "QUANTITY"
    TEXT = "TEXT"
    FILE = "FILE"
    NUMBER = "NUMBER"
    BOOLEAN = "BOOLEAN"
    STRING = "STRING"

class NIMBLEOntology:
    def __init__(self, graph):
        self.nimble_model = graph
        self.CODE_LIST_PROPS = {URIRef(NS + HAS_CODE), URIRef(NS + CODE)}
        self.UNIT_LIST_PROPS = {URIRef(NS + HAS_CODE), URIRef(NS + CODE), URIRef(NS + HAS_UNIT)}
        self.QUANTITY_PROPERTY_PROPS = {
            URIRef(NS + HAS_CODE), URIRef(NS + HAS_CODE_LIST), URIRef(NS + HAS_UNIT),
            URIRef(NS + HAS_UNIT_LIST), URIRef(NS + CODE), URIRef(NS + UNIT_CODE)
        }
        self.CODE_PROPERTY_PROPS = {
            URIRef(NS + HAS_CODE), URIRef(NS + HAS_CODE_LIST), URIRef(NS + CODE)
        }

    def is_quantity_property(self, prop):
        return self.check_property(prop, QUANTITY_PROPERTY_TYPE)

    def check_property(self, prop, property_type):
        property_uri = URIRef(NS + property_type)
        # print(f"pppp{prop} cccc{property_uri}")
        return prop == property_uri or (prop, RDFS.subPropertyOf, property_uri) in self.nimble_model

    def is_code_property(self, prop):
        return self.check_property(prop, CODE_PROPERTY_TYPE)
    
    def is_unit_type(self, prop):
        return self.check_property(prop, UNIT_TYPE)

    def is_file_property(self, prop):
        return self.check_property(prop, FILE_PROPERTY_TYPE)

    def is_code_list(self, resource):
        return self.check_class(resource, CODE_LIST)

    def check_class(self, resource, class_type):
        class_uri = URIRef(NS + class_type)
        return (resource, RDF.type, class_uri) in self.nimble_model

    def is_list_type(self, resource):
        return self.is_unit_list(resource) or self.is_code_list(resource)

    def is_unit_list(self, resource):
        return self.check_class(resource, UNIT_LIST)

    def is_code_type(self, resource):
        return self.check_class(resource, CODE_TYPE)

    def get_properties(self, resource_uri):
        resource = URIRef(resource_uri)
        if self.is_code_list(resource):
            return self.CODE_LIST_PROPS
        elif self.is_unit_list(resource):
            return self.UNIT_LIST_PROPS
        elif self.is_code_type(resource):
            return self.CODE_PROPERTY_PROPS
        elif self.is_quantity_property(resource):
            return self.QUANTITY_PROPERTY_PROPS
        else:
            return set()

    def list_nimble_statements(self, resource):
        statements = set()
        for prop in self.get_properties(resource):
            for stmt in self.nimble_model.subjects(predicate=prop):
                statements.add((stmt, prop, resource))
        return statements

    def get_property_value(self, resource, property_name, default=None):
        property_uri = URIRef(NS + property_name)
        for stmt in self.nimble_model.subject_objects(predicate=property_uri):
            if stmt[0] == resource:
                return stmt[1]
        return default

    def has_code(self, resource, default=None):
        return self.get_property_value(resource, HAS_CODE, default) or self.get_property_value(resource, CODE, default)

    def is_visible(self, resource, default=False):
        return self.get_property_value(resource, IS_VISIBLE, default)

    def is_required(self, resource, default=False):
        return self.get_property_value(resource, IS_REQUIRED, default)

    def list_id(self, resource, default=None):
        return self.get_property_value(resource, ID, default)



    def get_value_qualifier(self,prop,g) -> Optional[str]:
        if self.is_quantity_property(prop):
            return ValueQualifier.QUANTITY
        if NIMBLEOntology.is_code_property(self,prop):
            return ValueQualifier.TEXT
        if NIMBLEOntology.is_file_property(self,prop):
            return ValueQualifier.FILE

        return self.from_range(g.value(prop, RDFS.range))


    def from_range(self,range_resource: Optional[str]) -> Optional[str]:
        if range_resource:
            if range_resource.startswith("http://www.w3.org/2001/XMLSchema#"):
                local_name = range_resource.split("#")[-1]
                return self.from_xsd_local_name(local_name)
            elif self.is_unit_type(range_resource):
                return ValueQualifier.QUANTITY
            elif self.is_code_type(range_resource):
                return ValueQualifier.TEXT
        return None


    def from_xsd_local_name(self,local_name: str) -> str:
        if local_name in ["float", "double", "decimal", "int"]:
            return ValueQualifier.NUMBER
        elif local_name == "boolean":
            return ValueQualifier.BOOLEAN
        elif local_name in ["string", "normalizedString"]:
            return ValueQualifier.STRING
        return ValueQualifier.STRING
    
    # def process_coded_types(pt, qualifier, resource):
    #     # Process all relevant nimble statements
    #     code_set = set()
    #     # Pre-set the code_set with any existing codes
    #     code_set.update(pt.get_code_list())
    #     code_list_uri = None

    #     nimble_iter = NIMBLEOntology.list_nimble_statements(resource)
    #     for stmt in nimble_iter:
    #         if stmt.get_object().is_literal():
    #             # Keep the literal as a possible code
    #             code_set.add(stmt.get_object().as_literal().get_string())
    #         elif stmt.get_object().is_resource():
    #             n_res = stmt.get_object().as_resource()
    #             # In case it is a list
    #             if NIMBLEOntology.is_list_type(n_res):
    #                 # Keep the URI of the list ID... check for the nimble:id element
    #                 code_list_uri = NIMBLEOntology.list_id(n_res, n_res.get_uri())
    #                 # Collect the codes from the list
    #                 code_set.update(process_coded_list(n_res))
    #             else:
    #                 # Process the coded type along with the property as list identifier
    #                 # Thus, keep the property URI as list ID
    #                 code_list_uri = resource.get_uri()
    #                 code_set.add(process_coded_item(resource, n_res))

    #     # Store the code list
    #     pt.get_code_list().update(code_set)
    #     # Store the list URI - helpful to obtain the list of codes
    #     pt.set_code_list_id(code_list_uri)


    # def process_coded_list(list_resource):
    #     codes = set()
    #     nimble_iter = NIMBLEOntology.list_nimble_statements(list_resource)
    #     for stmt in nimble_iter:
    #         if stmt.get_object().is_literal():
    #             # Keep the literal as a possible code
    #             codes.add(stmt.get_object().as_literal().get_string())
    #         elif stmt.get_object().is_resource():
    #             n_res = stmt.get_object().as_resource()
    #             # Process the nimble-list item and add the returned code
    #             codes.add(process_coded_item(list_resource, n_res))
    #     return codes    


    # def process_coded_item(list_resource, item):
    #     coded_type = coded_repository.find_by_id(item.get_uri()).or_else(CodedType())
    #     coded_type.set_uri(item.get_uri())
    #     coded_type.set_name_space(item.get_name_space())
    #     coded_type.set_local_name(item.get_local_name())
        
    #     # Process all the labels
    #     process_labels(coded_type, item)
        
    #     # Check for the list ID and the value
    #     coded_type.set_list_id(NIMBLEOntology.list_id(list_resource, list_resource.get_uri()))
        
    #     # Find the nimble:hasCode (use localName as default)
    #     coded_type.set_code(NIMBLEOntology.has_code(item, item.get_local_name()))
        
    #     # Store the coded item
    #     coded_repository.save(coded_type)
        
    #     # Return the code
    #     return coded_type.get_code()


# Usage example

# Create an empty RDF graph (this would normally be loaded from a file)
g = Graph()

# Add some data to the graph (in real scenarios, this would come from the ontology file)
g.add((URIRef(NS + "exampleResource"), RDF.type, URIRef(NS + CODE_LIST)))
g.add((URIRef(NS + "exampleResource"), URIRef(NS + HAS_CODE), Literal("123")))

# Initialize the NIMBLEOntology with the RDF graph
ontology = NIMBLEOntology(g)

# Example checks
resource = URIRef(NS + "exampleResource")
print(ontology.is_code_list(resource))  # True
print(ontology.has_code(resource))  # 123
print(ontology.list_nimble_statements(resource))  # { (exampleResource, hasCode, 123) }
