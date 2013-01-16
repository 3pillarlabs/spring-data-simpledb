package org.springframework.data.simpledb.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SimpleDbTemplate implements SimpleDbOperations, ApplicationContextAware{

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
