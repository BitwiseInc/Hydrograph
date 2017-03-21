package hydrograph.engine.testing.utils;

import hydrograph.engine.transformation.userfunctions.base.NormalizeTransformBase;
import hydrograph.engine.transformation.userfunctions.base.OutputDispatcher;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
/**
 * The Class TestNormalizeOperationWrapper.
 *
 * @author Bitwise
 */
public class TestNormalizeOperationWrapper {

	public static Object[][] callNormalizeOperation(NormalizeTransformBase norm, Properties props,
			ArrayList<String> inputFieldsName, ArrayList<String> outputFieldsName, Object[][] inputData) {
		
		List<TestReusableRow> inputReusableRowList = new ArrayList<>();
		for(Object[] data:inputData){
			TestReusableRow reusableRow=new TestReusableRow(new LinkedHashSet<>(inputFieldsName));
			reusableRow.setRow(data);
			inputReusableRowList.add(reusableRow);
		}
		
		TestReusableRow outputReusableRow=new TestReusableRow(new LinkedHashSet<>(outputFieldsName));
		outputReusableRow.setRow(new Object[outputFieldsName.size()]);
		
		norm.prepare(props);		
		
		List<TestReusableRow> outputReusableRowList = normalize(norm, inputReusableRowList, outputReusableRow, outputFieldsName);
		
		Object[][] outputData=new Object[outputReusableRowList.size()][outputFieldsName.size()];
		for(int j=0; j<outputReusableRowList.size();j++ )
		for(int i=0;i<outputFieldsName.size();i++){
			outputData[j][i]=outputReusableRowList.get(j).getField(i);
		}
		
		return outputData;
	}

	private static List<TestReusableRow>  normalize(NormalizeTransformBase norm, List<TestReusableRow> inputReusableRowList,
			TestReusableRow outputReusableRow, ArrayList<String> outputFieldsName) {
		
		List<TestReusableRow> outputList = new ArrayList<TestReusableRow>();
		NormalizeOutputCollector outputDispatcher = new NormalizeOutputCollector(outputReusableRow, outputFieldsName);
		
		
		for(int i=0;i<inputReusableRowList.size();i++){
			outputDispatcher.initialize();
			norm.Normalize(inputReusableRowList.get(i), outputReusableRow, outputDispatcher);
			outputList.addAll(outputDispatcher.getOutRows());
		}
		norm.cleanup();		
		return outputList;
	}

}

class NormalizeOutputCollector implements OutputDispatcher {
	
	List<TestReusableRow> outputReusableRowList;// = new ArrayList<TestReusableRow>();
	TestReusableRow outputReusableRow;
	ArrayList<String> outputFieldsName;
	
	NormalizeOutputCollector(TestReusableRow x,ArrayList<String> y) {
		outputReusableRow = x;
		outputFieldsName=y;
	}
	
	public void initialize() {
		outputReusableRowList = new ArrayList<TestReusableRow>();
	}
	
	public List<TestReusableRow> getOutRows() {
		return outputReusableRowList;
	}
	
	public void sendOutput() {
/*		for(int i=0; i<outputReusableRowList.size();i++) {
			System.out.println("1-->" + outputReusableRowList.get(i));
			System.out.println("2-->" + outputReusableRowList.get(i).hashCode());
		}
		
		System.out.println("3-->" + outputReusableRow.hashCode());
*/		
		TestReusableRow temp = new TestReusableRow(outputReusableRow);
//		System.out.println("4-->" + temp.hashCode());
		outputReusableRowList.add(temp);
		
/*		for(int i=0; i<outputReusableRowList.size();i++) {
			System.out.println("5-->" + outputReusableRowList.get(i));
			System.out.println("6-->" + outputReusableRowList.get(i).hashCode());
		}
		
		System.out.println("");*/

	}
	
}	
