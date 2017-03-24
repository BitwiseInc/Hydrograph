package hydrograph.engine.transformation.standardfunctions;

import java.util.Arrays;

/**
 * The Class SpecialFunctions.
 *
 * @author Bitwise
 *
 */

public class SpecialFunctions {

    /**
     * Returns value corresponding to {@code inputValue} present in {@code keyValues}
     *
     * @param inputValue input string
     * @param keyValues variable length array with key,value pair and default value
     *                      to be used for mapping to {@code inputValue}
     * @return mapped value for {@code inputValue}
     * if {@code keyValues} is null return null
     * if {@code keyValues} does not contain default throw exception
     */
    public static String decode(String inputValue,String ... keyValues) throws Exception{

        if(keyValues==null)
            return null;
        int keyValuesSize = keyValues.length;
        if(keyValuesSize%2==0)
            throw new Exception("keyValues are even in number, default value is required");

        int inputValuePos = Arrays.asList(keyValues).indexOf(inputValue);

        return (inputValuePos<0) ? keyValues[keyValuesSize-1] : keyValues[inputValuePos+1];
    }
}
