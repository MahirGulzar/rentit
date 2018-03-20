package com.example.demo.common.utils;

import org.springframework.hateoas.Link;
import org.springframework.http.HttpMethod;

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("ALL")
@XmlType(name = "_xlink", namespace = Link.ATOM_NAMESPACE)
public class ExtendedLink extends Link {
    private HttpMethod method;

    protected ExtendedLink(){}

    public ExtendedLink(Link link, HttpMethod method){
        super(link.getHref(),link.getRel());
        this.method = method;
    }

    public HttpMethod getMethod(){
        return method;
    }

}
