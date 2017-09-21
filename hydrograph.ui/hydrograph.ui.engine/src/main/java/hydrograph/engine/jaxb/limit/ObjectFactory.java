
package hydrograph.engine.jaxb.limit;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the hydrograph.engine.jaxb.limit package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: hydrograph.engine.jaxb.limit
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TypeOutSocket }
     * 
     */
    public TypeOutSocket createTypeOutSocket() {
        return new TypeOutSocket();
    }

    /**
     * Create an instance of {@link TypeLimitInSocket }
     * 
     */
    public TypeLimitInSocket createTypeLimitInSocket() {
        return new TypeLimitInSocket();
    }

    /**
     * Create an instance of {@link TypeLimitBase }
     * 
     */
    public TypeLimitBase createTypeLimitBase() {
        return new TypeLimitBase();
    }

    /**
     * Create an instance of {@link TypeLimitOutSocket }
     * 
     */
    public TypeLimitOutSocket createTypeLimitOutSocket() {
        return new TypeLimitOutSocket();
    }

    /**
     * Create an instance of {@link TypeOutSocketAsInSocketIn0 }
     * 
     */
    public TypeOutSocketAsInSocketIn0 createTypeOutSocketAsInSocketIn0() {
        return new TypeOutSocketAsInSocketIn0();
    }

}
