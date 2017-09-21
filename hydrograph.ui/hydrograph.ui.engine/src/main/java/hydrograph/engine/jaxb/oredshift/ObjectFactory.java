
package hydrograph.engine.jaxb.oredshift;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the hydrograph.engine.jaxb.oredshift package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: hydrograph.engine.jaxb.oredshift
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TypePrimaryKeys }
     * 
     */
    public TypePrimaryKeys createTypePrimaryKeys() {
        return new TypePrimaryKeys();
    }

    /**
     * Create an instance of {@link TypeOutputRedshiftInSocket }
     * 
     */
    public TypeOutputRedshiftInSocket createTypeOutputRedshiftInSocket() {
        return new TypeOutputRedshiftInSocket();
    }

    /**
     * Create an instance of {@link TypeOutputRedshiftBase }
     * 
     */
    public TypeOutputRedshiftBase createTypeOutputRedshiftBase() {
        return new TypeOutputRedshiftBase();
    }

    /**
     * Create an instance of {@link TypeRedshiftRecord }
     * 
     */
    public TypeRedshiftRecord createTypeRedshiftRecord() {
        return new TypeRedshiftRecord();
    }

    /**
     * Create an instance of {@link TypeRedshiftField }
     * 
     */
    public TypeRedshiftField createTypeRedshiftField() {
        return new TypeRedshiftField();
    }

    /**
     * Create an instance of {@link TypeLoadChoice }
     * 
     */
    public TypeLoadChoice createTypeLoadChoice() {
        return new TypeLoadChoice();
    }

    /**
     * Create an instance of {@link TypeUpdateKeys }
     * 
     */
    public TypeUpdateKeys createTypeUpdateKeys() {
        return new TypeUpdateKeys();
    }

}
