package eu.nimble.indexing.web.controller;

import org.hibernate.annotations.GenericGenerator;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;


@Entity
public class PropertyK {
    @Id
    @GenericGenerator(name = "id-generator", strategy = "uuid")
    @GeneratedValue(generator = "id-generator")
    private String id;

    @ElementCollection
    @JoinTable(name = "prop_product")
    private Set<String> products;

    @Column(columnDefinition = "TEXT")
    private String product;

    @Column(columnDefinition = "TEXT")
    private String url;

    public PropertyK(Collection<String> products, String url) {
        this.products = products.stream().collect(Collectors.toSet());
        this.url = url;
    }

    public PropertyK(String product, String url) {
        this.product = product;
        this.url = url;
    }

    public PropertyK() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<String> getProducts() {
        return products;
    }

    public void setProducts(Set<String> product) {
        this.products = product;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }
}
