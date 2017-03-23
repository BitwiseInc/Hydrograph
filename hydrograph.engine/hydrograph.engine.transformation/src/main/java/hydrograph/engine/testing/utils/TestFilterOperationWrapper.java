package hydrograph.engine.testing.utils;

import hydrograph.engine.transformation.userfunctions.base.FilterBase;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;

/**
 * The Class TestFilterOperationWrapper.
 *
 * @author Bitwise
 */
public class TestFilterOperationWrapper {


    public static Object[] callFilterOperation(FilterBase filter, Properties props,
                                              ArrayList<String> inputFieldsName, Object[][] inputData) {

        List<TestReusableRow> inputReusableRowList = new ArrayList<>();
        for (Object[] data : inputData) {
            TestReusableRow reusableRow = new TestReusableRow(new LinkedHashSet<>(inputFieldsName));
            reusableRow.setRow(data);
            inputReusableRowList.add(reusableRow);
        }

        filter.prepare(props, inputFieldsName);

        Object [] outputData=new Object[inputReusableRowList.size()];

        for (int i = 0; i < inputReusableRowList.size(); i++) {
           outputData[i]= filter.isRemove(inputReusableRowList.get(i));
        }


        filter.cleanup();


        return outputData;
    }

}
