
package hydrograph.engine.jaxb.oteradata;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the hydrograph.engine.jaxb.oteradata package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: hydrograph.engine.jaxb.oteradata
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
     * Create an instance of {@link DatabaseType }
     * 
     */
    public DatabaseType createDatabaseType() {
        return new DatabaseType();
    }

    /**
     * Create an instance of {@link TypeOutputTeradataOutSocket }
     * 
     */
    public TypeOutputTeradataOutSocket createTypeOutputTeradataOutSocket() {
        return new TypeOutputTeradataOutSocket();
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

    /**
     * Create an instance of {@link TypeTeradataRecord }
     * 
     */
    public TypeTeradataRecord createTypeTeradataRecord() {
        return new TypeTeradataRecord();
    }

    /**
     * Create an instance of {@link TypeOutputTeradataBase }
     * 
     */
    public TypeOutputTeradataBase createTypeOutputTeradataBase() {
        return new TypeOutputTeradataBase();
    }

    /**
     * Create an instance of {@link TypeTeradataField }
     * 
     */
    public TypeTeradataField createTypeTeradataField() {
        return new TypeTeradataField();
    }

}
