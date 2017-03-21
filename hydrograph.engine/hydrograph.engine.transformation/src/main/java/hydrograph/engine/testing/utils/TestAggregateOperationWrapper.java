package hydrograph.engine.testing.utils;

import hydrograph.engine.transformation.userfunctions.base.AggregateTransformBase;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
/**
 * The Class TestAggregateOperationWrapper.
 *
 * @author Bitwise
 */
public class TestAggregateOperationWrapper {

	public static Object[] callAggregateOperation(AggregateTransformBase aggr, Properties props,
			ArrayList<String> inputFieldsName, ArrayList<String> outputFieldsName, ArrayList<String> keyFieldsName, Object[][] inputGroup) {
		
		List<TestReusableRow> inputReusableRowList = new ArrayList<>();
		for(Object[] data:inputGroup){
			TestReusableRow reusableRow=new TestReusableRow(new LinkedHashSet<>(inputFieldsName));
			reusableRow.setRow(data);
			inputReusableRowList.add(reusableRow);
		}
		
		TestReusableRow outputReusableRow=new TestReusableRow(new LinkedHashSet<>(outputFieldsName));
		outputReusableRow.setRow(new Object[outputFieldsName.size()]);
		
		aggr.prepare(props, inputFieldsName, outputFieldsName, keyFieldsName);
		
		aggregate(aggr, inputReusableRowList, outputReusableRow);
		
		aggr.cleanup();
		
		Object[] outputDataRow=new Object[outputFieldsName.size()];
		
		for(int i=0;i<outputFieldsName.size();i++){
			outputDataRow[i]=outputReusableRow.getField(i);
		}
		
		return outputDataRow;
	}

	private static void aggregate(AggregateTransformBase aggr, List<TestReusableRow> inputReusableRowList,
			TestReusableRow outputReusableRow) {
		for(int i=0;i<inputReusableRowList.size();i++){
			aggr.aggregate(inputReusableRowList.get(i));
		}
		
		aggr.onCompleteGroup(outputReusableRow);
	}
	
	
}
