package hydrograph.engine.testing.utils;

import hydrograph.engine.transformation.userfunctions.base.TransformBase;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
/**
 * The Class TestTransformOperationWrapper.
 *
 * @author Bitwise
 */
public class TestTransformOperationWrapper {

	public static Object[][] callTransformOperation(TransformBase trans, Properties props,
			ArrayList<String> inputFieldsName, ArrayList<String> outputFieldsName, Object[][] inputData) {
		
		List<TestReusableRow> inputReusableRowList = new ArrayList<>();
		for(Object[] data:inputData){
			TestReusableRow reusableRow=new TestReusableRow(new LinkedHashSet<>(inputFieldsName));
			reusableRow.setRow(data);
			inputReusableRowList.add(reusableRow);
		}
		List<TestReusableRow> outputReusableRowList = new ArrayList<>();
		for(int i=0;i<inputData.length;i++){
			TestReusableRow reusableRow=new TestReusableRow(new LinkedHashSet<>(outputFieldsName));
			reusableRow.setRow(new Object[outputFieldsName.size()]);
			outputReusableRowList.add(reusableRow);
		}
		
		trans.prepare(props, inputFieldsName, outputFieldsName);
		
		transform(trans, inputReusableRowList, outputReusableRowList);
		
		trans.cleanup();
		
		Object[][] outputData=new Object[outputReusableRowList.size()][outputFieldsName.size()];
		for(int j=0; j<outputReusableRowList.size();j++ )
		for(int i=0;i<outputFieldsName.size();i++){
			outputData[j][i]=outputReusableRowList.get(j).getField(i);
		}
		
		return outputData;
	}

	private static void transform(TransformBase trans, List<TestReusableRow> inputReusableRowList,
			List<TestReusableRow> outputReusableRowList) {
		for(int i=0;i<inputReusableRowList.size();i++){
			trans.transform(inputReusableRowList.get(i), outputReusableRowList.get(i));
		}
	}
	
	
}
