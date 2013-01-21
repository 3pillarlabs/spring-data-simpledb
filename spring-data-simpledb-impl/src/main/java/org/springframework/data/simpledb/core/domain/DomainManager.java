package org.springframework.data.simpledb.core.domain;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.DeleteDomainRequest;
import com.amazonaws.services.simpledb.model.ListDomainsRequest;
import com.amazonaws.services.simpledb.model.ListDomainsResult;
import org.springframework.util.Assert;

import java.util.List;

public class DomainManager {

    private final AmazonSimpleDB sdb;
    private final DomainManagementPolicy policy;


    public DomainManager(AmazonSimpleDB sdb, String domainUpdatePolicy) {
        Assert.notNull(sdb);
        this.sdb = sdb;

        if(domainUpdatePolicy == null){
            domainUpdatePolicy = "NONE";
        }
        policy = DomainManagementPolicy.valueOf(domainUpdatePolicy);

    }


    public void manageDomain(String domainName){
        if(policy == DomainManagementPolicy.NONE){
            return;
        } else if(policy == DomainManagementPolicy.UPDATE) {
            createDomain(domainName);
        } else if (policy == DomainManagementPolicy.DROP_CREATE){
            dropDomain(domainName);
            createDomain(domainName);
        }

    }

    private void dropDomain(String domainName) {
        DeleteDomainRequest request = new DeleteDomainRequest(domainName);
        sdb.deleteDomain(request);
    }

    private void createDomain(String domainName) {
        CreateDomainRequest request = new CreateDomainRequest(domainName);
        sdb.createDomain(request);
    }

    public boolean exists(String domainName){
        ListDomainsResult listDomainsResult = sdb.listDomains(new ListDomainsRequest());
        List<String> domainNames = listDomainsResult.getDomainNames();
        return  domainNames.contains(domainName);
    }


}
