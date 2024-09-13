package eu.nimble.indexing.web.controller;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
public class PropertyK {
    @Id
    @GenericGenerator(name = "id-generator", strategy = "uuid")
    @GeneratedValue(generator = "id-generator")
    private String id;

    private String product;

    private String url;

    public PropertyK(String product, String url) {
        this.product = product;
        this.url = url;
    }

    public PropertyK(String id, String product, String url) {
        this.id = id;
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

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
