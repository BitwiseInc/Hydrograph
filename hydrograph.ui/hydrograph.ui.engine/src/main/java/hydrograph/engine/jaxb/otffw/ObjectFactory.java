
package hydrograph.engine.jaxb.otffw;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the hydrograph.engine.jaxb.otffw package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: hydrograph.engine.jaxb.otffw
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TypeOutputFixedwidthInSocket }
     * 
     */
    public TypeOutputFixedwidthInSocket createTypeOutputFixedwidthInSocket() {
        return new TypeOutputFixedwidthInSocket();
    }

    /**
     * Create an instance of {@link TypeFixedWidthBase }
     * 
     */
    public TypeFixedWidthBase createTypeFixedWidthBase() {
        return new TypeFixedWidthBase();
    }

    /**
     * Create an instance of {@link TypeFixedwidthField }
     * 
     */
    public TypeFixedwidthField createTypeFixedwidthField() {
        return new TypeFixedwidthField();
    }

    /**
     * Create an instance of {@link TypeFixedwidthRecord }
     * 
     */
    public TypeFixedwidthRecord createTypeFixedwidthRecord() {
        return new TypeFixedwidthRecord();
    }

}
