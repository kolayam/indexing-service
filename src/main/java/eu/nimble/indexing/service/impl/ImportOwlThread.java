package eu.nimble.indexing.service.impl;

import eu.nimble.indexing.service.OntologyService;

import java.util.List;

public class ImportOwlThread extends Thread {
    private OntologyService onto;
    private String mimeType;
    private List<String> nameSpaces;
    private String ontoStr;

    public ImportOwlThread(OntologyService onto, String mimeType, List<String> nameSpaces, String ontoStr) {
        this.onto=onto;
        this.mimeType=mimeType;
        this.nameSpaces=nameSpaces;
        this.ontoStr=ontoStr;
    }

    @Override
    public void run() {
        System.out.println("========start upload onto");
        this.onto.upload(mimeType, this.nameSpaces, this.ontoStr);
    }
}
