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

package com.liferay.poshi.runner;

import com.liferay.poshi.runner.logger.PoshiLogger;
import com.liferay.poshi.runner.logger.SummaryLogger;
import com.liferay.poshi.runner.selenium.LiferaySeleniumUtil;
import com.liferay.poshi.runner.selenium.SeleniumUtil;
import com.liferay.poshi.runner.util.PropsValues;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Element;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.UnreachableBrowserException;

/**
 * @author Brian Wing Shun Chan
 * @author Michael Hashimoto
 * @author Karen Dang
 * @author Leslie Wong
 */
@RunWith(Parameterized.class)
public class PoshiRunner {

	@Parameterized.Parameters(name = "{0}")
	public static List<String> getList() throws Exception {
		List<String> namespacedClassCommandNames = new ArrayList<>();

		List<String> testNames = Arrays.asList(
			PropsValues.TEST_NAME.split("\\s*,\\s*"));

		PoshiRunnerContext.readFiles(_getTestClassFileIncludes(testNames));

		for (String testName : testNames) {
			PoshiRunnerValidation.validate(testName);

			String namespace =
				PoshiRunnerGetterUtil.
					getNamespaceFromNamespacedClassCommandName(testName);

			if (testName.contains("#")) {
				String classCommandName =
					PoshiRunnerGetterUtil.
						getClassCommandNameFromNamespacedClassCommandName(
							testName);

				namespacedClassCommandNames.add(
					namespace + "." + classCommandName);
			}
			else {
				String className =
					PoshiRunnerGetterUtil.
						getClassNameFromNamespacedClassCommandName(testName);

				Element rootElement = PoshiRunnerContext.getTestCaseRootElement(
					className, namespace);

				List<Element> commandElements = rootElement.elements("command");

				for (Element commandElement : commandElements) {
					namespacedClassCommandNames.add(
						namespace + "." + className + "#" +
							commandElement.attributeValue("name"));
				}
			}
		}

		return namespacedClassCommandNames;
	}

	public PoshiRunner(String namespacedClassCommandName) throws Exception {
		_testNamespacedClassCommandName = namespacedClassCommandName;

		_testNamespacedClassName =
			PoshiRunnerGetterUtil.
				getNamespacedClassNameFromNamespacedClassCommandName(
					_testNamespacedClassCommandName);

		_poshiLogger = new PoshiLogger(namespacedClassCommandName);

		_poshiRunnerExecutor = new PoshiRunnerExecutor(_poshiLogger);
	}

	@Before
	public void setUp() throws Exception {
		System.out.println();
		System.out.println("###");
		System.out.println("### " + _testNamespacedClassCommandName);
		System.out.println("###");
		System.out.println();

		PoshiRunnerContext.setTestCaseNamespacedClassCommandName(
			_testNamespacedClassCommandName);

		PoshiRunnerVariablesUtil.clear();

		try {
			SummaryLogger.startRunning();

			SeleniumUtil.startSelenium();

			_runSetUp();
		}
		catch (WebDriverException webDriverException) {
			webDriverException.printStackTrace();

			throw webDriverException;
		}
		catch (Exception exception) {
			LiferaySeleniumUtil.printJavaProcessStacktrace();

			PoshiRunnerStackTraceUtil.printStackTrace(exception.getMessage());

			PoshiRunnerStackTraceUtil.emptyStackTrace();

			exception.printStackTrace();

			throw exception;
		}
	}

	@After
	public void tearDown() throws Exception {
		LiferaySeleniumUtil.writePoshiWarnings();

		SummaryLogger.createSummaryReport();

		try {
			if (!PropsValues.TEST_SKIP_TEAR_DOWN) {
				_runTearDown();
			}
		}
		catch (Exception exception) {
			PoshiRunnerStackTraceUtil.printStackTrace(exception.getMessage());

			PoshiRunnerStackTraceUtil.emptyStackTrace();
		}
		finally {
			SummaryLogger.stopRunning();

			_poshiLogger.createPoshiReport();

			SeleniumUtil.stopSelenium();
		}
	}

	@Test
	public void test() throws Exception {
		try {
			_runCommand();

			LiferaySeleniumUtil.assertNoPoshiWarnings();
		}
		catch (Exception exception) {
			LiferaySeleniumUtil.printJavaProcessStacktrace();

			PoshiRunnerStackTraceUtil.printStackTrace(exception.getMessage());

			PoshiRunnerStackTraceUtil.emptyStackTrace();

			exception.printStackTrace();

			throw exception;
		}
	}

	@Rule
	public RetryTestRule retryTestRule = new RetryTestRule();

	private static String[] _getTestClassFileIncludes(List<String> testNames) {
		Set<String> testClassFileGlobsSet = new HashSet<>();

		for (String testName : testNames) {
			String testClassName =
				PoshiRunnerGetterUtil.
					getClassNameFromNamespacedClassCommandName(testName);

			testClassFileGlobsSet.add("**/" + testClassName + ".prose");
			testClassFileGlobsSet.add("**/" + testClassName + ".testcase");
		}

		return testClassFileGlobsSet.toArray(new String[0]);
	}

	private void _runCommand() throws Exception {
		_poshiLogger.logNamespacedClassCommandName(
			_testNamespacedClassCommandName);

		_runNamespacedClassCommandName(_testNamespacedClassCommandName);
	}

	private void _runNamespacedClassCommandName(
			String namespacedClassCommandName)
		throws Exception {

		String namespace =
			PoshiRunnerGetterUtil.getNamespaceFromNamespacedClassCommandName(
				namespacedClassCommandName);

		String classCommandName =
			PoshiRunnerGetterUtil.
				getClassCommandNameFromNamespacedClassCommandName(
					namespacedClassCommandName);

		Element commandElement = PoshiRunnerContext.getTestCaseCommandElement(
			classCommandName, namespace);

		if (commandElement != null) {
			PoshiRunnerStackTraceUtil.startStackTrace(
				namespacedClassCommandName, "test-case");

			_poshiLogger.updateStatus(commandElement, "pending");

			_poshiRunnerExecutor.runTestCaseCommandElement(
				commandElement, namespacedClassCommandName);

			_poshiLogger.updateStatus(commandElement, "pass");

			PoshiRunnerStackTraceUtil.emptyStackTrace();
		}
	}

	private void _runSetUp() throws Exception {
		_poshiLogger.logNamespacedClassCommandName(
			_testNamespacedClassName + "#set-up");

		SummaryLogger.startMajorSteps();

		_runNamespacedClassCommandName(_testNamespacedClassName + "#set-up");
	}

	private void _runTearDown() throws Exception {
		_poshiLogger.logNamespacedClassCommandName(
			_testNamespacedClassName + "#tear-down");

		SummaryLogger.startMajorSteps();

		_runNamespacedClassCommandName(_testNamespacedClassName + "#tear-down");
	}

	private final PoshiLogger _poshiLogger;
	private final PoshiRunnerExecutor _poshiRunnerExecutor;
	private final String _testNamespacedClassCommandName;
	private final String _testNamespacedClassName;

	private class RetryTestRule implements TestRule {

		public Statement apply(Statement statement, Description description) {
			return new RetryStatement(statement);
		}

		public class RetryStatement extends Statement {

			public RetryStatement(Statement statement) {
				_statement = statement;
			}

			@Override
			public void evaluate() throws Throwable {
				for (int i = 0; i <= _MAX_RETRY_COUNT; i++) {
					try {
						_statement.evaluate();

						return;
					}
					catch (Throwable t) {
						if ((i == _MAX_RETRY_COUNT) ||
							!_isValidRetryThrowable(t)) {

							throw t;
						}
					}
				}
			}

			private String _getShortMessage(Throwable throwable) {
				String message = throwable.getMessage();

				if (throwable instanceof WebDriverException) {
					int index = message.indexOf("Build info:");

					message = message.substring(0, index);

					message = message.trim();
				}

				return message;
			}

			private boolean _isValidRetryThrowable(Throwable throwable) {
				List<Throwable> throwables = null;

				if (throwable instanceof MultipleFailureException) {
					MultipleFailureException multipleFailureException =
						(MultipleFailureException)throwable;

					throwables = multipleFailureException.getFailures();
				}
				else {
					throwables = Arrays.asList(throwable);
				}

				for (Throwable validRetryThrowable : _validRetryThrowables) {
					Class<?> validRetryThrowableClass =
						validRetryThrowable.getClass();
					String validRetryThrowableShortMessage = _getShortMessage(
						validRetryThrowable);

					for (Throwable t : throwables) {
						if (validRetryThrowableClass.equals(t.getClass())) {
							if ((validRetryThrowableShortMessage == null) ||
								validRetryThrowableShortMessage.isEmpty()) {

								return true;
							}

							if (validRetryThrowableShortMessage.equals(
									_getShortMessage(t))) {

								return true;
							}
						}
					}
				}

				return false;
			}

			private static final int _MAX_RETRY_COUNT = 2;

			private final Statement _statement;
			private final Throwable[] _validRetryThrowables = {
				new TimeoutException(), new UnreachableBrowserException(null),
				new WebDriverException(
					"Timed out waiting 45 seconds for Firefox to start."),
				new WebDriverException(
					"unknown error: unable to discover open pages")
			};

		}

	}

}