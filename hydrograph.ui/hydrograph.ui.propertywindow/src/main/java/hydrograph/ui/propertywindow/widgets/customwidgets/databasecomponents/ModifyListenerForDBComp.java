/*******************************************************************************
 * Copyright 2017 Capital One Services, LLC and Bitwise, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package hydrograph.ui.propertywindow.widgets.customwidgets.databasecomponents;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.propertywindow.messages.Messages;

/**
 * ModifyListenerForDBComp validate numeric fields and compare the fields
 * @author Bitwise
 *
 */
public class ModifyListenerForDBComp implements ModifyListener{
	private Text lowerBoundText;
	private Text upperBoundText;
	private ControlDecoration lowerBoundTextDecorator;
	private ControlDecoration upperBoundTextDecorator;
	
	
	public ModifyListenerForDBComp(Text upperBoundText, Text lowerBoundText, 
			ControlDecoration lowerBoundTextDecorator, ControlDecoration upperBoundTextDecorator) {
		this.lowerBoundText = lowerBoundText;
		this.upperBoundText = upperBoundText;
		this.lowerBoundTextDecorator = lowerBoundTextDecorator;
		this.upperBoundTextDecorator = upperBoundTextDecorator;
	}

	@Override
	public void modifyText(ModifyEvent event) {
		if(event.getSource() == lowerBoundText){
			validate(lowerBoundText, upperBoundText, 
					lowerBoundTextDecorator, upperBoundTextDecorator, lowerBoundText.getText(), upperBoundText.getText());
		}else{
			validate(upperBoundText, lowerBoundText, 
					upperBoundTextDecorator, lowerBoundTextDecorator, lowerBoundText.getText(), upperBoundText.getText());
		}
	}

	private void validate(Text text1, Text text2, 
			ControlDecoration text1Decorator, ControlDecoration text2Decorator, String lowerValue, String upperValue) {
		if(validateNumericField(text1.getText())){
			text1Decorator.hide();
			if(StringUtils.isNotBlank(text2.getText()) && validateNumericField(text2.getText())){
				if(compareBigIntegerValue(upperValue, lowerValue) == -1){
					text1Decorator.show();
					text1Decorator.setDescriptionText(Messages.UPPER_LOWER_BOUND_ERROR);
				}else{
					text1Decorator.hide();
					text2Decorator.hide();
				}
			}
		}
		else{
			text1Decorator.show();
			text1.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
			text1Decorator.setDescriptionText(Messages.DB_NUMERIC_PARAMETERZIATION_ERROR);
			validateFieldWithParameter(text1.getText(), text1Decorator);
			validateFieldWithParameter(text2.getText(), text2Decorator);
		}
	}

	/**
	 * The Function will compare bigInteger values
	 * @param value1
	 * @param value2
	 * @return
	 */
	private int compareBigIntegerValue(String value1, String value2){
		BigInteger int1= BigInteger.valueOf(Long.parseLong(value1));
		BigInteger int2 = BigInteger.valueOf(Long.parseLong(value2));
		
		return int1.compareTo(int2);
	}
	
	/**
	 * The Function used to validate parameter field 
	 * @param text
	 * @param txtDecorator
	 */
	private void validateFieldWithParameter(String text, ControlDecoration txtDecorator){
		if(StringUtils.isNotBlank(text)){
			if(ParameterUtil.isParameter(text) || validateNumericField(text)){
				txtDecorator.hide();
			}else{
				txtDecorator.show();
				txtDecorator.setDescriptionText(Messages.DB_NUMERIC_PARAMETERZIATION_ERROR);
				
			}
		}
	}
	
	/**
	 * The Function used to validate the field & field should be positive integer
	 * @param text
	 * @param text1Decorator
	 * @return
	 */
	private boolean validateNumericField(String text){
		if(StringUtils.isNotBlank(text)){
			Matcher matchs = Pattern.compile(Constants.NUMERIC_REGEX).matcher(text);
			if (matchs.matches()) {
				return true;
			}
		}
		return false;
	}
}
