package org.springframework.data.simpledb.core;

import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.DeleteDomainRequest;
import com.amazonaws.services.simpledb.model.ListDomainsRequest;
import java.util.List;

public class SimpleDBDomainOperations {

    //TODO
    private AmazonSimpleDB sdb;

    public void dropDomain(String domainName) {
        sdb.deleteDomain(new DeleteDomainRequest(domainName));
    }

    public List<String> fetchDomainNames() {
        return sdb.listDomains().getDomainNames();
    }

    public void createDomain(String domainName) {
        sdb.createDomain(new CreateDomainRequest(domainName));
    }

    public void createDomainsIfNotExist(String domainName) {
        boolean found = false;
        for(String domain : fetchDomainNames()) {
            if(domain.equals(domainName)) {
                found = true;
                break;
            }
        }
        if(!found) {
            sdb.createDomain(new CreateDomainRequest(domainName));
        }
    }
}
