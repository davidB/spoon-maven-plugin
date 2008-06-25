package net.sf.alchim.spoon.contrib.maven;


/**
 * Wrapper class for the systemPropery argument type.
 *
 * @author <a href="mailto:romain.rouvoy@gmail.com">Romain Rouvoy</a>
 */
public class Property {
    private String key, value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}