
package hydrograph.engine.jaxb.operationstypes;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the hydrograph.engine.jaxb.operationstypes package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: hydrograph.engine.jaxb.operationstypes
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PartitionByExpression }
     * 
     */
    public PartitionByExpression createPartitionByExpression() {
        return new PartitionByExpression();
    }

    /**
     * Create an instance of {@link Lookup }
     * 
     */
    public Lookup createLookup() {
        return new Lookup();
    }

    /**
     * Create an instance of {@link Subjob }
     * 
     */
    public Subjob createSubjob() {
        return new Subjob();
    }

    /**
     * Create an instance of {@link Filter }
     * 
     */
    public Filter createFilter() {
        return new Filter();
    }

    /**
     * Create an instance of {@link Cumulate }
     * 
     */
    public Cumulate createCumulate() {
        return new Cumulate();
    }

    /**
     * Create an instance of {@link Transform }
     * 
     */
    public Transform createTransform() {
        return new Transform();
    }

    /**
     * Create an instance of {@link GenerateSequence }
     * 
     */
    public GenerateSequence createGenerateSequence() {
        return new GenerateSequence();
    }

    /**
     * Create an instance of {@link Groupcombine }
     * 
     */
    public Groupcombine createGroupcombine() {
        return new Groupcombine();
    }

    /**
     * Create an instance of {@link Normalize }
     * 
     */
    public Normalize createNormalize() {
        return new Normalize();
    }

    /**
     * Create an instance of {@link Join }
     * 
     */
    public Join createJoin() {
        return new Join();
    }

    /**
     * Create an instance of {@link Executiontracking }
     * 
     */
    public Executiontracking createExecutiontracking() {
        return new Executiontracking();
    }

    /**
     * Create an instance of {@link Aggregate }
     * 
     */
    public Aggregate createAggregate() {
        return new Aggregate();
    }

    /**
     * Create an instance of {@link PartitionByExpression.NoOfPartitions }
     * 
     */
    public PartitionByExpression.NoOfPartitions createPartitionByExpressionNoOfPartitions() {
        return new PartitionByExpression.NoOfPartitions();
    }

    /**
     * Create an instance of {@link Lookup.Match }
     * 
     */
    public Lookup.Match createLookupMatch() {
        return new Lookup.Match();
    }

    /**
     * Create an instance of {@link Subjob.Path }
     * 
     */
    public Subjob.Path createSubjobPath() {
        return new Subjob.Path();
    }

}
