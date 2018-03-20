package com.example.demo.common.utils;

import org.springframework.hateoas.Link;

import javax.xml.bind.annotation.XmlAnyAttribute;

public class ExtendedLink extends Link {
    @XmlAnyAttribute
    String method;

    public ExtendedLink(Link link,String method)
    {
        super(link.getHref(), link.getRel());
        this.method = method;
    }
}
