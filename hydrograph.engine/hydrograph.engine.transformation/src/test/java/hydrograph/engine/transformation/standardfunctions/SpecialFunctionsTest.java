package hydrograph.engine.transformation.standardfunctions;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by shivarajn on 3/15/2017.
 */
public class SpecialFunctionsTest {

    @Test
    public void itShouldValidateDecode() {

        String outputCase1 = null, outputCase2 = null, outputCase3 = null, outputCase4 = null, outputCase5 = null;
        try {
            outputCase1 = SpecialFunctions.decode("red", "red", "Abort", "green", "Success", "Status Unknown");
            outputCase2 = SpecialFunctions.decode("purple", "red", "Abort", "green", "Success", "Status Unknown");
            outputCase3 = SpecialFunctions.decode(null, "red", "Abort", "green", "Success", "Status Unknown");
            outputCase4 = SpecialFunctions.decode("red", null);
            outputCase5 = SpecialFunctions.decode("yellow", "red", "Abort", "green", "Success", "yellow", "In Progress", "Status Unknown");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        Assert.assertEquals("Abort", outputCase1);
        Assert.assertEquals("Status Unknown", outputCase2);
        Assert.assertEquals("Status Unknown", outputCase3);
        Assert.assertNull(outputCase4);
        Assert.assertEquals("In Progress", outputCase5);
    }
}
