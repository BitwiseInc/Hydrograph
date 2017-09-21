
package hydrograph.engine.jaxb.straightpulltypes;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the hydrograph.engine.jaxb.straightpulltypes package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: hydrograph.engine.jaxb.straightpulltypes
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RemoveDups }
     * 
     */
    public RemoveDups createRemoveDups() {
        return new RemoveDups();
    }

    /**
     * Create an instance of {@link Limit }
     * 
     */
    public Limit createLimit() {
        return new Limit();
    }

    /**
     * Create an instance of {@link Dummy }
     * 
     */
    public Dummy createDummy() {
        return new Dummy();
    }

    /**
     * Create an instance of {@link UnionAll }
     * 
     */
    public UnionAll createUnionAll() {
        return new UnionAll();
    }

    /**
     * Create an instance of {@link Clone }
     * 
     */
    public Clone createClone() {
        return new Clone();
    }

    /**
     * Create an instance of {@link Sort }
     * 
     */
    public Sort createSort() {
        return new Sort();
    }

    /**
     * Create an instance of {@link RemoveDups.Keep }
     * 
     */
    public RemoveDups.Keep createRemoveDupsKeep() {
        return new RemoveDups.Keep();
    }

    /**
     * Create an instance of {@link Limit.MaxRecords }
     * 
     */
    public Limit.MaxRecords createLimitMaxRecords() {
        return new Limit.MaxRecords();
    }

    /**
     * Create an instance of {@link Dummy.Properties }
     * 
     */
    public Dummy.Properties createDummyProperties() {
        return new Dummy.Properties();
    }

}
