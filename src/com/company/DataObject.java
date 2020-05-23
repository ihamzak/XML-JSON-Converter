package com.company;

public abstract class DataObject {
    protected static final Character ATTRIBUTE_CHAR = '@';
    protected static final Character ELEMENT_CHAR = '#';

    public abstract String getXMLString();

    public abstract String getJSONString();

    public abstract void parse();

    public abstract String getConvertedResult();
}