
package hydrograph.engine.jaxb.ofxml;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the hydrograph.engine.jaxb.ofxml package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: hydrograph.engine.jaxb.ofxml
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TypeXmlRecord }
     * 
     */
    public TypeXmlRecord createTypeXmlRecord() {
        return new TypeXmlRecord();
    }

    /**
     * Create an instance of {@link TypeXmlField }
     * 
     */
    public TypeXmlField createTypeXmlField() {
        return new TypeXmlField();
    }

    /**
     * Create an instance of {@link TypeOutputFileXmlBase }
     * 
     */
    public TypeOutputFileXmlBase createTypeOutputFileXmlBase() {
        return new TypeOutputFileXmlBase();
    }

    /**
     * Create an instance of {@link TypeOutputXmlInSocket }
     * 
     */
    public TypeOutputXmlInSocket createTypeOutputXmlInSocket() {
        return new TypeOutputXmlInSocket();
    }

}
