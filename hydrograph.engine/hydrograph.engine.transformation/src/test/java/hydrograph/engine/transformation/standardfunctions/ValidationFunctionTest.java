package hydrograph.engine.transformation.standardfunctions;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by shivarajn on 3/16/2017.
 */
public class ValidationFunctionTest {

    //@Test
    /*public void itShouldValidateIsError(){
        Integer one = 1, zero = 0;

        Assert.assertEquals(one, ValidationFunctions.isError("1","/","0"));
        Assert.assertEquals(zero, ValidationFunctions.isError("1","+","1"));
        Assert.assertEquals(zero, ValidationFunctions.isError("(","10.4","+","0",")","/","5"));
        Assert.assertEquals(one, ValidationFunctions.isError("10","+","0sda"));
        Assert.assertEquals(one, ValidationFunctions.isError(5,"/",0));
        Assert.assertEquals(zero, ValidationFunctions.isError("10","+",new BigDecimal(12.887)));
    }*/

    @Test
    public void itShouldValidateIsError(){
        Integer one = 1, zero = 0;

        Assert.assertEquals(one, ValidationFunctions.isError("1 / 0"));
        Assert.assertEquals(zero, ValidationFunctions.isError("1 + 1"));
        Assert.assertEquals(zero, ValidationFunctions.isError("( 10.4 + 0 ) / 5"));
        Assert.assertEquals(one, ValidationFunctions.isError("10 + 0sda"));
    }

    @Test
    public void itShouldValidateCheckValidity(){

        Integer one = 1, zero = 0;
        Short nullShort = null;
        Integer nullInteger = null;
        Long nullLong = null;
        Float nullFloat = null;
        Double nullDouble = null;
        BigDecimal nullBigDecimal = null;
        String data1 = null;
        Date nullDate = null;

        Assert.assertEquals(one, ValidationFunctions.checkValidity(new Short((short)5)));
        Assert.assertEquals(zero, ValidationFunctions.checkValidity(nullShort));
        Assert.assertEquals(one, ValidationFunctions.checkValidity(new Integer(5)));
        Assert.assertEquals(zero, ValidationFunctions.checkValidity(nullInteger));
        Assert.assertEquals(one, ValidationFunctions.checkValidity(new Long(5)));
        Assert.assertEquals(zero, ValidationFunctions.checkValidity(nullLong));
        Assert.assertEquals(one, ValidationFunctions.checkValidity(new Float(5.12)));
        Assert.assertEquals(zero, ValidationFunctions.checkValidity(nullFloat));
        Assert.assertEquals(one, ValidationFunctions.checkValidity(new Double(512.53425)));
        Assert.assertEquals(zero, ValidationFunctions.checkValidity(nullDouble));
        Assert.assertEquals(one, ValidationFunctions.checkValidity(new BigDecimal(5.12414)));
        Assert.assertEquals(zero, ValidationFunctions.checkValidity(nullBigDecimal));

        Assert.assertEquals(one, ValidationFunctions.checkValidity("asdads"));
        Assert.assertEquals(zero, ValidationFunctions.checkValidity("   "));
        Assert.assertEquals(zero, ValidationFunctions.checkValidity(data1));

        Assert.assertEquals(zero, ValidationFunctions.checkValidity(nullDate));
        Assert.assertEquals(one, ValidationFunctions.checkValidity(new Date(1212341241L)));

    }
}
