from rdflib import XSD, Graph, RDF, RDFS, OWL, URIRef, Literal, Namespace
from rdflib.namespace import split_uri
import pysolr
import rdflib
solr_url = 'http://159.65.211.184:8983/solr/'
# solr_url = 'http://localhost:8983/solr/'
solr_prop = pysolr.Solr(solr_url+"props")
solr_class = pysolr.Solr(solr_url+"class")

from NIMBLEOntology import NIMBLEOntology
NIMBLE = Namespace("http://www.nimble-project.org/catalogue#")
from rdflib.plugins.sparql import prepareQuery

import re

def get_super_classes(cls, direct=False):
    sup = set()

    # This function helps to find superclasses recursively if direct=False
    def find_super_classes(class_uri, direct):
        for super_class in g.objects(class_uri, RDFS.subClassOf):
            if isinstance(super_class, URIRef):  # Only consider URI resources
                sup.add(str(super_class))  # Add the superclass
                if not direct:
                    # If we're looking for transitive superclasses, keep going deeper
                    find_super_classes(super_class, direct)
    
    # Start finding superclasses from the given class
    find_super_classes(cls, direct)
    
    return list(sup)

def get_sub_classes(cls, direct=False):
    sub = set()

    # This function helps to find subclasses recursively if direct=False
    def find_sub_classes(class_uri, direct):
        for sub_class in g.subjects(RDFS.subClassOf, class_uri):
            if isinstance(sub_class, URIRef):
                sub.add(str(sub_class))  # Add direct subclass
                if not direct:
                    # If we're looking for transitive subclasses, keep going deeper
                    find_sub_classes(sub_class, direct)
    
    # Start finding subclasses from the given class
    find_sub_classes(cls, direct)
    
    return list(sub)

# def get_sub_classes(cls, direct=False):
#     sub = set()
    
#     # Direct or all subclasses depending on the 'direct' flag
#     for sub_class in cls.transitive_subclasses(direct=direct):
#         # Only include URI resources
#         if isinstance(sub_class, URIRef):
#             sub.add(str(sub_class))
    
#     return sub

def get_properties(ont_class, model):
    properties = set()
    class_name = ont_class
    sparql_query = """
    PREFIX ex: <http://www.aidimme.es/FurnitureSectorOntology.owl#>
    PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
    PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
    PREFIX owl: <http://www.w3.org/2002/07/owl#>
    SELECT ?property ?domain WHERE {
        {
            <""" + class_name + """> rdfs:subClassOf* ?domain .
            ?property rdfs:domain ?domain .
        }
        UNION
        {
            ?property rdfs:domain ?unionClass .
            ?unionClass owl:unionOf/rdf:rest*/rdf:first ?domain .
            <""" + class_name + """> rdfs:subClassOf* ?domain .
        }
    }
    """
    
    # Prepare the SPARQL query
    query = prepareQuery(sparql_query)
    
    # Execute the query on the model
    for row in model.query(query):
        properties.add(str(row.property))
    
    return properties

def dynamic_field_part(qualifier):
    if not qualifier:
        return "undefined"
    else:
        # Convert to a format that will be easier to work with
        dynamic_field_part = ''.join([f'_{c.lower()}' if c.isupper() else c for c in qualifier]).lstrip('_')
        
        # Remove non-alphanumeric characters and spaces
        dynamic_field_part = re.sub(r'[^a-zA-Z0-9_ ]', '', dynamic_field_part)
        
        # Replace spaces with underscores
        dynamic_field_part = dynamic_field_part.replace(' ', '_')

        # Split the string into words and make the first word lowercase, rest capitalized
        dynamic_field_part = dynamic_field_part.split('_')

        # Convert the first word to lowercase and the rest to proper camel case
        dynamic_field_part = dynamic_field_part[0].lower() + ''.join(word.capitalize() for word in dynamic_field_part[1:])

        return dynamic_field_part



# Function to create dynamic fields based on language
def create_dynamic_fields(doc, label_node):
    dynamic_fields = {}
    idxField = [str(label_node).split("#")[1],dynamic_field_part(str(s))]
    # Extract labels and comments from RDF
    labels = g.objects(subject=label_node, predicate=RDFS.label)
    comments = g.objects(subject=label_node, predicate=RDFS.comment)

    languages = []

    # Combine labels and comments into dynamic fields (based on languages)
    for label in labels:
        lang = label.language  # Get the language of the label
        languages.append(lang)
        label_value = str(label)  # Get the label value as a string
        
        # Generate dynamic fields based on the language
        dynamic_fields[f'{lang}_label'] = label_value
        dynamic_fields[f'{lang}_txt'] = label_value
        dynamic_fields[f'{lang}_text_'] = label_value
        dynamic_fields[f'{lang}_labels'] = label_value
        dynamic_fields[f'{lang}_lowercaseLabel'] = label_value.lower()
        idxField.append(dynamic_field_part(label_value))

    # We can also add the comment as a dynamic field, if needed
    for comment in comments:
        lang = comment.language  # Get the language of the comment
        comment_value = str(comment)  # Get the comment value as a string
        dynamic_fields[f'{lang}_comment'] = comment_value
        dynamic_fields[f'{lang}_comments'] = comment_value

    labels = g.objects(subject=label_node, predicate=RDFS.label)
    comments = g.objects(subject=label_node, predicate=RDFS.comment)
    # Add a combined "allLabels" field, for example, concatenating the labels
    all_labels_combined = " | ".join([str(label) for label in labels])
    dynamic_fields['allLabels'] = all_labels_combined

    all_comments_combined = " | ".join([str(comment) for comment in comments])
    dynamic_fields['allComments'] = all_comments_combined
    dynamic_fields['languages'] = languages


    dynamic_fields['idxField'] = idxField

    return dynamic_fields


def check_property(graph, property_uri, check_subproperty, check_type):
    # Check for rdfs:subPropertyOf relationship
    subproperty_check = any(graph.objects(property_uri, RDFS.subPropertyOf, check_subproperty))
    # print(f"property{property_uri} sub pro{check_subproperty} subproperty_check{subproperty_check}")
    # Check for specific rdf:type
    type_check = check_type in list(graph.objects(property_uri, RDF.type, RDF.Property))
    # print(f"property{property_uri} sub pro{check_type} type_check{type_check}")

    return subproperty_check | type_check

def has_direct_super_property(graph, property_uri):
    return any(graph.objects(property_uri, RDFS.subPropertyOf))

def get_super_properties(graph, property_uri):
    return list(graph.objects(property_uri, RDFS.subPropertyOf))

# Function to check if a domain is a class
def is_class(uri):
    return (uri, RDF.type, OWL.Class) in g or (uri, RDF.type, RDFS.Class) in g

def boolean_value_of(value):
    return str(value).strip().lower() == 'true'

from urllib.parse import urlparse

def is_valid_uri(uri):
    try:
        parsed = urlparse(uri)
        # A valid URI must have a scheme and a network location or path
        return bool(parsed.scheme) and (bool(parsed.netloc) or bool(parsed.path))
    except Exception:
        return False

# Function to extract namespace and local name
def get_namespace_and_localname(uri):
    try:
        namespace, localname = split_uri(uri)
        return namespace, localname
    except ValueError:  # In case the URI is not well-formed
        return None, None

# Function to get rdf:type(s) of a property
def get_rdf_types(property_uri):
    types = [str(rdf_type) for rdf_type in g.objects(URIRef(property_uri), RDF.type)]
    return types

# Function to get explicit rdf:type(s) of an object property
def get_explicit_rdf_types(property_uri):
    explicit_types = [
        str(rdf_type) for rdf_type in g.objects(URIRef(property_uri), RDF.type)
    ]
    return explicit_types

# Function to check if a property is explicitly an owl:FunctionalProperty
def is_functional_property(property_uri):
    # Check if rdf:type is explicitly owl:FunctionalProperty
    return (
        URIRef("http://www.w3.org/2002/07/owl#FunctionalProperty")
        in g.objects(URIRef(property_uri), RDF.type)
    )

# Path to your OWL file
owl_file = "/home/sword/Downloads/eclass_514en.owl"
# owl_file = "/home/sword/Downloads/FurnitureSectorTaxonomy-v2.4.8-1.owl"

# Load the OWL file into an RDFlib graph
g = Graph()
g.bind("rdfs", RDFS)
g.bind("xsd", XSD)
g.parse(owl_file, format="xml")  # Change format to "turtle" if your file is in Turtle format


ontology = NIMBLEOntology(g)
data = []
# Extract Object Properties
# print("\nObject Properties:")
i = 0
for s in g.subjects(RDF.type, OWL.ObjectProperty):
    i += 1
    print(f"{i} {s}")
    # print(f"Subclasses of {check_property(g,s,NIMBLE.QuantityProperty, NIMBLE.QuantityProperty)}")
    # print(f"{s}is quatity{ontology.get_value_qualifier(URIRef(s),g)}")
    valueQualifier = ontology.get_value_qualifier(URIRef(s),g)
    # namespace, localname = get_namespace_and_localname(str(s)) 
    label = g.value(s, RDFS.label)
    domain = g.value(s, RDFS.domain)
    # localName = str(s).replace(RDF._NS,"")
    # nameSpace = RDF._NS
    range = g.value(s, RDFS.range)
    visible = g.value(s,NIMBLE.isVisible,default= "True")
    required = g.value(s,NIMBLE.isRequired,default="True")
    label = g.value(s, RDFS.label)
    comment = g.value(s,RDFS.comment)
    subclasses=[]
    propType = ""
    if len(get_explicit_rdf_types(s))>1:
        propType = get_explicit_rdf_types(s)[1] 
    else:
        propType = get_explicit_rdf_types(s)[0]
    # print(f"Subclasses of {is_valid_uri(domain)}")
    if is_valid_uri(domain):
        subclasses = [str(sub) for sub in g.subjects(RDFS.subClassOf, URIRef(domain))]
        # print(f"Subclasses of {domain}: {subclasses}")
    # print(f"Property URI: {s}, preditate{g.value(s,NIMBLE.isVisible)} , Label: {label} is domain class{is_class(domain)}")
    document = {
        "id": str(s),
        # "label": str(label) if isinstance(label, Literal) else None,
        "used_in": subclasses,
        "localName" : str(s).split("#")[1],
        "nameSpace" : str(s).split("#")[0] + "#",
        "range" : g.value(s, RDFS.range),
        "isVisible" : visible,
        "isRequired" : required,
        # "label" : g.value(s, RDFS.label),
        # "comment" : g.value(s,RDFS.comment),
        "propType":propType.split("#")[1],
        "valueQualifier": valueQualifier,
        "isFacet": True,
    }
    

    # Generate dynamic fields for this class
    dynamic_fields = create_dynamic_fields(None, s)
    # print(f"dynamic_fields {dynamic_fields}")

    document.update(dynamic_fields)
    solr_prop.add(document)
    solr_prop.commit()

# Extract Functional Properties
print("\nFunctional Properties:")
for s in g.subjects(RDF.type, OWL.FunctionalProperty):
    label = g.value(s, RDFS.label)


for prop in g.subjects(RDF.type, OWL.DatatypeProperty):  # For Datatype Properties
    domain = g.value(prop, RDFS.domain)
   


