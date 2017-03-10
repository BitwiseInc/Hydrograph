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
package hydrograph.ui.expression.editor.datastructure;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.BinaryType;
import org.eclipse.jdt.internal.core.SourceMethod;
import org.eclipse.jdt.internal.core.SourceType;
import org.slf4j.Logger;

import hydrograph.ui.expression.editor.Constants;
import hydrograph.ui.logging.factory.LogFactory;

@SuppressWarnings("restriction")
public class ClassDetails implements Comparable<ClassDetails> {

	private boolean isUserDefined;
	private String displayName;
	private String packageName=Constants.EMPTY_STRING;
	private String jarName=Constants.EMPTY_STRING;
	private String cName;
	private String javaDoc;
	private List<MethodDetails> methodList = new ArrayList<MethodDetails>();
	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(ClassDetails.class);

	public ClassDetails(IClassFile classFile, String jarFileName, String packageName, boolean isUserDefined) {
		LOGGER.debug("Extracting methods from "+classFile.getElementName());
	
		try {
			this.javaDoc=getJavaDoc(classFile);	
			intialize(classFile,jarFileName,packageName, isUserDefined);
			for (IJavaElement iJavaElement : classFile.getChildren()) {
				if (iJavaElement instanceof IType) {
					IType iType = (IType) iJavaElement;
					for (IMethod iMethod : iType.getMethods()) {
						addMethodsToClass(iMethod);
					}
				}
			}
		} catch (JavaModelException e) {
			LOGGER.error("Error occurred while fetching methods from class"+cName);
		}
	}

	private String getJavaDoc(IClassFile classFile) throws JavaModelException {
		BinaryType binaryType=(BinaryType)classFile.getType();
		if(binaryType.getSource() !=null && binaryType.getJavadocRange()!=null){
		String javaDoc=Constants.EMPTY_STRING;
			javaDoc = StringUtils.substring(binaryType.getSource().toString(), 0, binaryType.getJavadocRange().getLength());
		javaDoc = StringUtils.replaceEachRepeatedly(javaDoc, new String[] { "/*", "*/", "*" }, new String[] {
				Constants.EMPTY_STRING, Constants.EMPTY_STRING, Constants.EMPTY_STRING });
		}
		return javaDoc;
	}

	public ClassDetails(SourceType javaClassFile, String jarFileName, String packageName, boolean isUserDefined) {
		Logger LOGGER = LogFactory.INSTANCE.getLogger(ClassDetails.class);
		LOGGER.debug("Extracting methods from " + cName);

		try {
			this.javaDoc=getJavaDoc(javaClassFile);			
			intialize(javaClassFile, jarFileName, packageName, isUserDefined);
			for (IJavaElement iJavaElement : javaClassFile.getChildren()) {
				if (iJavaElement instanceof SourceMethod) {
					addMethodsToClass((IMethod) iJavaElement);
				}
			}
		} catch (JavaModelException e) {
			LOGGER.error("Error occurred while fetching methods from class" + cName);
		}
	}

	private String getJavaDoc(SourceType javaClassFile) throws JavaModelException {
		StringBuffer source = new StringBuffer(javaClassFile.getSource());
		try {
			String javaDoc = StringUtils.substring(source.toString(), 0, javaClassFile.getJavadocRange().getLength());
			javaDoc = StringUtils.replaceEachRepeatedly(javaDoc, new String[] { "/*", "*/", "*" },
					new String[] { Constants.EMPTY_STRING, Constants.EMPTY_STRING, Constants.EMPTY_STRING });
		} catch (Exception exception) {
			LOGGER.warn("Failed to build java-doc for :{}", javaClassFile);
		}
		return javaDoc;
	}
	
	private void addMethodsToClass(IMethod iMethod) throws JavaModelException {
		if (iMethod.isConstructor() || iMethod.isMainMethod() || isMethodDepricated(iMethod)) {
			return;
		} else {
			if (Flags.isPublic(iMethod.getFlags()) && Flags.isStatic(iMethod.getFlags())) {
				if (StringUtils.isBlank(iMethod.getSource())) {
					methodList.add(new MethodDetails(iMethod,cName, false));
				} else
					methodList.add(new MethodDetails(iMethod,cName, true));
			}
		}
	}
	
	private void intialize(IClassFile classFile, String jarFileName, String packageName, boolean isUserDefined) {
		this.cName = StringUtils.removeEndIgnoreCase(classFile.getElementName(), Constants.CLASS_EXTENSION);
		displayName=cName;
		if(StringUtils.isNotBlank(jarFileName)){
			jarName=jarFileName;
		}
		if(StringUtils.isNotBlank(packageName)){
			this.packageName=packageName;
		}
		if(StringUtils.isBlank(javaDoc)){
			javaDoc=Constants.EMPTY_STRING;
		}
		if(isUserDefined){
			isUserDefined=true;
			displayName=cName+Constants.USER_DEFINED_SUFFIX;
			updateJavaDoc(jarFileName, packageName);
		}
	}
	
	private void intialize(SourceType javaClassFile, String jarFileName, String packageName, boolean isUserDefined) {
		this.cName=javaClassFile.getElementName();
		displayName=cName;
		if(StringUtils.isNotBlank(jarFileName)){
			jarName=jarFileName;
		}
		if(StringUtils.isNotBlank(packageName)){
			this.packageName=packageName;
		}
		if(StringUtils.isBlank(javaDoc)){
			javaDoc=Constants.EMPTY_STRING;
		}
		if(isUserDefined){
			isUserDefined=true;
			displayName=cName+Constants.SRC_CLASS_SUFFIX;
			updateJavaDocForSorceClass(jarFileName, packageName);
		}
	}

	private void updateJavaDocForSorceClass(String jarFileName, String packageName) {
		StringBuffer buffer=new StringBuffer();
		buffer.append(Constants.PACKAGE_NAME_HEADER+packageName);
		buffer.append(Constants.HTML_NEW_LINE_TAG);
		buffer.append(Constants.HTML_NEW_LINE_TAG);
		javaDoc=buffer.toString()+"\n"+javaDoc;
	}

	private void updateJavaDoc(String jarFileName, String packageName) {
		StringBuffer buffer=new StringBuffer();
		buffer.append(Constants.JAR_FILE_NAME_HEADER+jarFileName);
		buffer.append(Constants.HTML_NEW_LINE_TAG);
		buffer.append(Constants.PACKAGE_NAME_HEADER+packageName);
		buffer.append(Constants.HTML_NEW_LINE_TAG);
		buffer.append(Constants.HTML_NEW_LINE_TAG);
		javaDoc=buffer.toString()+"\n"+javaDoc;
	}

	private boolean isMethodDepricated(IMethod iMethod) throws JavaModelException {
		for (IAnnotation annotation : iMethod.getAnnotations()) {
			if (StringUtils.equals(annotation.getElementName() , Deprecated.class.getCanonicalName())) {
				return true;
			}
		}
		return false;
	}

	public String getDisplayName(){
		return displayName;
	}
	
	public String getJavaDoc() {
		return javaDoc;
	}
	
	public String getJarName() {
		return jarName;
	}
	
	public String getPackageName() {
		return packageName;
	}

	public String getcName() {
		return cName;
	}

	public List<MethodDetails> getMethodList() {
		return new ArrayList<MethodDetails>(methodList);
	}

	public boolean isUserDefined() {
		return isUserDefined;
	}

	@Override
	public int compareTo(ClassDetails o) {
		
		return this.cName.compareToIgnoreCase(((ClassDetails)o).cName);
	}
}
