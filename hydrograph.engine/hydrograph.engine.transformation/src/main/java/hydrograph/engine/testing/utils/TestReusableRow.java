package hydrograph.engine.testing.utils;

import hydrograph.engine.transformation.userfunctions.base.ReusableRow;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
/**
 * The Class TestReusableRow.
 *
 * @author Bitwise
 */
public class TestReusableRow extends ReusableRow implements Serializable, Cloneable{

	private Object[] inputRow;
	private Map<String,Integer> fieldMap;
	
	public TestReusableRow(LinkedHashSet<String> fields) {
		super(fields);
		fieldMap=new HashMap<>();
		int index=0;
		for(String field:fields){
			fieldMap.put(field, index++);
		}
	}
	
	public TestReusableRow (TestReusableRow testReusableRow) {
		super(new LinkedHashSet<String>(testReusableRow.getFieldNames()));
		int index=0;
		fieldMap=new HashMap<>();
		for(String field:testReusableRow.getFieldNames()){
			fieldMap.put(field, index++);
		}
		
		inputRow = new  Object[testReusableRow.inputRow.length];
		
		for(int i=0; i< testReusableRow.inputRow.length; i++) {
			inputRow[i] = testReusableRow.inputRow[i];
		}
		
	}
	
	public void setRow(Object[] row){
		inputRow=row;
	}

	@Override
	protected Comparable getFieldInternal(int index) {
		return (Comparable) inputRow[index];
	}

	@Override
	protected Comparable getFieldInternal(String fieldName) {
		return (Comparable) inputRow[fieldMap.get(fieldName)];
	}

	@Override
	protected void setFieldInternal(int index, Comparable value) {
		inputRow[index]=value;
	}

	@Override
	protected void setFieldInternal(String fieldName, Comparable value) {
		inputRow[fieldMap.get(fieldName)]=value;
	}
	
	@Override
	public Object clone() {
	    try {
	        return super.clone();
	    } catch (Exception e) {
	        // either handle the exception or throw it
	        return null;
	    }
	}

}
