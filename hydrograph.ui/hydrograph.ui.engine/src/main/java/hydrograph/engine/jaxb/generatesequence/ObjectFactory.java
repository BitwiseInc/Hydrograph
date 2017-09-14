
package hydrograph.engine.jaxb.generatesequence;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the hydrograph.engine.jaxb.generatesequence package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: hydrograph.engine.jaxb.generatesequence
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TypeOperation }
     * 
     */
    public TypeOperation createTypeOperation() {
        return new TypeOperation();
    }

    /**
     * Create an instance of {@link TypeOutSocket }
     * 
     */
    public TypeOutSocket createTypeOutSocket() {
        return new TypeOutSocket();
    }

    /**
     * Create an instance of {@link TypePassthroughInputField }
     * 
     */
    public TypePassthroughInputField createTypePassthroughInputField() {
        return new TypePassthroughInputField();
    }

    /**
     * Create an instance of {@link TypeOperationOutputField }
     * 
     */
    public TypeOperationOutputField createTypeOperationOutputField() {
        return new TypeOperationOutputField();
    }

    /**
     * Create an instance of {@link TypeNameField }
     * 
     */
    public TypeNameField createTypeNameField() {
        return new TypeNameField();
    }

}
