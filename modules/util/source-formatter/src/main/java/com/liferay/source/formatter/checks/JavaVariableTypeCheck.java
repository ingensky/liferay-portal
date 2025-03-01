/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.source.formatter.checks;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.parser.JavaVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JavaVariableTypeCheck extends BaseJavaTermCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, JavaTerm javaTerm,
		String fileContent) {

		JavaClass javaClass = (JavaClass)javaTerm;

		String classContent = javaClass.getContent();

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			if (childJavaTerm.isJavaVariable()) {
				classContent = _checkFieldType(
					absolutePath, javaClass, classContent,
					(JavaVariable)childJavaTerm);
			}
		}

		return classContent;
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS};
	}

	private String _checkFieldType(
		String absolutePath, JavaClass javaClass, String classContent,
		JavaVariable javaVariable) {

		if (javaVariable.isPublic()) {
			return classContent;
		}

		String fieldType = _getFieldType(javaVariable);
		boolean isFinal = _containsNonaccessModifier(javaVariable, "final");

		if (!isFinal) {
			classContent = _formatDefaultValue(
				classContent, javaVariable, fieldType);
		}

		if (!javaVariable.isPrivate()) {
			return classContent;
		}

		if (isFinal) {
			JavaClass parentJavaClass = javaClass.getParentJavaClass();

			if ((parentJavaClass == null) && !javaVariable.isStatic() &&
				(_isImmutableField(fieldType, absolutePath) ||
				 fieldType.matches("Pattern(\\[\\])*") ||
				 (fieldType.equals("Log") &&
				  !isExcludedPath(_STATIC_LOG_EXCLUDES, absolutePath)))) {

				classContent = _formatStaticableFieldType(
					classContent, javaVariable.getContent(), fieldType);
			}
		}
		else if (!_containsNonaccessModifier(javaVariable, "volatile")) {
			classContent = _formatFinalableFieldType(
				classContent, javaClass, javaVariable, fieldType);
		}

		return classContent;
	}

	private boolean _containsNonaccessModifier(
		JavaVariable javaVariable, String modifier) {

		Pattern pattern = Pattern.compile(
			javaVariable.getAccessModifier() +
				" (((final|static|synchronized|transient|volatile)(\n| ))*)");

		Matcher matcher = pattern.matcher(javaVariable.getContent());

		if (matcher.find()) {
			String nonAccessModifiers = matcher.group(1);

			if (nonAccessModifiers.contains(modifier)) {
				return true;
			}
		}

		return false;
	}

	private String _formatDefaultValue(
		String classContent, JavaVariable javaVariable, String fieldType) {

		String defaultValue = null;

		if (StringUtil.isLowerCase(fieldType)) {
			Map<String, String> defaultPrimitiveValues =
				_getDefaultPrimitiveValues();

			defaultValue = defaultPrimitiveValues.get(fieldType);
		}
		else {
			defaultValue = StringPool.NULL;
		}

		Pattern isDefaultValuePattern = Pattern.compile(
			" =\\s+" + defaultValue + ";(\\s+)$");

		Matcher matcher = isDefaultValuePattern.matcher(
			javaVariable.getContent());

		if (matcher.find()) {
			return StringUtil.replace(
				classContent, javaVariable.getContent(),
				matcher.replaceFirst(";$1"));
		}

		return classContent;
	}

	private String _formatFinalableFieldType(
		String classContent, JavaClass javaClass, JavaVariable javaVariable,
		String fieldType) {

		for (String annotation : _getAnnotationsExclusions()) {
			if (javaVariable.hasAnnotation(annotation)) {
				return classContent;
			}
		}

		JavaClass parentJavaClass = javaClass;

		while (true) {
			if (parentJavaClass.getParentJavaClass() == null) {
				break;
			}

			parentJavaClass = parentJavaClass.getParentJavaClass();
		}

		List<JavaTerm> allChildJavaTerms = _getAllChildJavaTerms(
			parentJavaClass);

		StringBundler sb = new StringBundler(6);

		sb.append("(((\\+\\+( ?))|(--( ?)))");
		sb.append(javaVariable.getName());
		sb.append(")|((\\b|\\.)");
		sb.append(javaVariable.getName());
		sb.append("((( )((=)|(\\+=)|(-=)|(\\*=)|(/=)|(%=)))");
		sb.append("|(\\+\\+)|(--)|(( )((\\|=)|(&=)|(^=)))))");

		Pattern pattern = Pattern.compile(sb.toString());

		if (!_isFinalableField(javaClass, pattern, allChildJavaTerms)) {
			return classContent;
		}

		String javaVariableContent = javaVariable.getContent();

		String newJavaVariableContent = StringUtil.replaceFirst(
			javaVariableContent, fieldType, "final " + fieldType);

		return StringUtil.replace(
			classContent, javaVariableContent, newJavaVariableContent);
	}

	private String _formatStaticableFieldType(
		String classContent, String javaVariableContent, String fieldType) {

		if (!javaVariableContent.contains(StringPool.EQUAL) ||
			(fieldType.endsWith("[]") &&
			 (javaVariableContent.contains(" new ") ||
			  javaVariableContent.contains("\tnew ")))) {

			return classContent;
		}

		String newJavaVariableContent = StringUtil.replaceFirst(
			javaVariableContent, "private final", "private static final");

		return StringUtil.replace(
			classContent, javaVariableContent, newJavaVariableContent);
	}

	private List<JavaTerm> _getAllChildJavaTerms(JavaClass javaClass) {
		List<JavaTerm> childJavaTerms = new ArrayList<>();

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			childJavaTerms.add(childJavaTerm);

			if (childJavaTerm.isJavaClass()) {
				JavaClass childJavaClass = (JavaClass)childJavaTerm;

				childJavaTerms.addAll(_getAllChildJavaTerms(childJavaClass));
			}
		}

		return childJavaTerms;
	}

	private synchronized List<String> _getAnnotationsExclusions() {
		if (_annotationsExclusions == null) {
			_annotationsExclusions = ListUtil.fromArray(
				"ArquillianResource", "Autowired", "BeanReference", "Captor",
				"Context", "Inject", "Mock", "Parameter", "Reference",
				"ServiceReference", "SuppressWarnings", "Value");
		}

		return _annotationsExclusions;
	}

	private synchronized Map<String, String> _getDefaultPrimitiveValues() {
		if (_defaultPrimitiveValues == null) {
			_defaultPrimitiveValues = MapUtil.fromArray(
				new String[] {
					"boolean", "false", "char", "'\\\\0'", "byte", "0",
					"double", "0\\.0", "float", "0\\.0", "int", "0", "long",
					"0", "short", "0"
				});
		}

		return _defaultPrimitiveValues;
	}

	private String _getFieldType(JavaVariable javaVariable) {
		StringBundler sb = new StringBundler(4);

		sb.append(javaVariable.getAccessModifier());
		sb.append(" (((final|static|synchronized|transient|volatile)(\n| ))*)");
		sb.append("([\\s\\S]*?)");
		sb.append(javaVariable.getName());

		Pattern pattern = Pattern.compile(sb.toString());

		Matcher matcher = pattern.matcher(javaVariable.getContent());

		if (matcher.find()) {
			return StringUtil.trim(matcher.group(5));
		}

		return null;
	}

	private boolean _isFinalableField(
		JavaClass javaClass, Pattern pattern,
		List<JavaTerm> allChildJavaTerms) {

		int assignmentCount = 0;

		for (JavaTerm childJavaTerm : allChildJavaTerms) {
			String content = childJavaTerm.getContent();

			Matcher matcher = pattern.matcher(content);

			boolean found = matcher.find();

			if (found) {
				assignmentCount++;
			}

			if (childJavaTerm.isJavaConstructor()) {
				JavaClass constructorClass = childJavaTerm.getParentJavaClass();

				String constructorClassName = constructorClass.getName();

				if (constructorClassName.equals(javaClass.getName())) {
					if (!found) {
						return false;
					}
				}
				else if (found) {
					return false;
				}
			}
			else if (childJavaTerm.isJavaMethod()) {
				if (found) {
					return false;
				}
			}
			else if (childJavaTerm.isJavaVariable()) {
				if (found && content.contains("{\n\n")) {
					return false;
				}
			}
		}

		if (assignmentCount == 0) {
			return false;
		}

		return true;
	}

	private boolean _isImmutableField(String fieldType, String absolutePath) {
		List<String> immutableFieldTypes = getAttributeValues(
			_IMMUTABLE_FIELD_TYPES_KEY, absolutePath);

		for (String immutableFieldType : immutableFieldTypes) {
			if (fieldType.equals(immutableFieldType) ||
				fieldType.startsWith(immutableFieldType + "[]")) {

				return true;
			}
		}

		return false;
	}

	private static final String _IMMUTABLE_FIELD_TYPES_KEY =
		"immutableFieldTypes";

	private static final String _STATIC_LOG_EXCLUDES = "static.log.excludes";

	private List<String> _annotationsExclusions;
	private Map<String, String> _defaultPrimitiveValues;

}