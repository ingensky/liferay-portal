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

package com.liferay.petra.sql.dsl.spi;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.ast.ASTNodeListener;
import com.liferay.petra.sql.dsl.base.BaseTable;
import com.liferay.petra.sql.dsl.expression.Alias;
import com.liferay.petra.sql.dsl.expression.ColumnAlias;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.expression.step.WhenThenStep;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.petra.sql.dsl.query.HavingStep;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.petra.sql.dsl.query.LimitStep;
import com.liferay.petra.sql.dsl.query.OrderByStep;
import com.liferay.petra.sql.dsl.query.sort.OrderByExpression;
import com.liferay.petra.sql.dsl.query.sort.OrderByInfo;
import com.liferay.petra.sql.dsl.spi.ast.BaseASTNode;
import com.liferay.petra.sql.dsl.spi.ast.DefaultASTNodeListener;
import com.liferay.petra.sql.dsl.spi.expression.AggregateExpression;
import com.liferay.petra.sql.dsl.spi.expression.DSLFunction;
import com.liferay.petra.sql.dsl.spi.expression.DSLFunctionType;
import com.liferay.petra.sql.dsl.spi.expression.DefaultAlias;
import com.liferay.petra.sql.dsl.spi.expression.DefaultColumnAlias;
import com.liferay.petra.sql.dsl.spi.expression.DefaultPredicate;
import com.liferay.petra.sql.dsl.spi.expression.NullExpression;
import com.liferay.petra.sql.dsl.spi.expression.Operand;
import com.liferay.petra.sql.dsl.spi.expression.Scalar;
import com.liferay.petra.sql.dsl.spi.expression.ScalarList;
import com.liferay.petra.sql.dsl.spi.expression.step.CaseWhenThen;
import com.liferay.petra.sql.dsl.spi.expression.step.ElseEnd;
import com.liferay.petra.sql.dsl.spi.expression.step.WhenThen;
import com.liferay.petra.sql.dsl.spi.query.From;
import com.liferay.petra.sql.dsl.spi.query.GroupBy;
import com.liferay.petra.sql.dsl.spi.query.Having;
import com.liferay.petra.sql.dsl.spi.query.Join;
import com.liferay.petra.sql.dsl.spi.query.JoinType;
import com.liferay.petra.sql.dsl.spi.query.Limit;
import com.liferay.petra.sql.dsl.spi.query.OrderBy;
import com.liferay.petra.sql.dsl.spi.query.QueryExpression;
import com.liferay.petra.sql.dsl.spi.query.QueryTable;
import com.liferay.petra.sql.dsl.spi.query.Select;
import com.liferay.petra.sql.dsl.spi.query.SetOperation;
import com.liferay.petra.sql.dsl.spi.query.SetOperationType;
import com.liferay.petra.sql.dsl.spi.query.Where;
import com.liferay.petra.sql.dsl.spi.query.sort.DefaultOrderByExpression;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;

import java.sql.Clob;
import java.sql.Types;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Preston Crary
 */
public class SQLDSLTest {

	@ClassRule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		new CodeCoverageAssertor() {

			@Override
			public void appendAssertClasses(List<Class<?>> assertClasses) {
				assertClasses.clear();

				assertClasses.add(AggregateExpression.class);
				assertClasses.add(BaseASTNode.class);
				assertClasses.add(BaseTable.class);
				assertClasses.add(CaseWhenThen.class);
				assertClasses.add(DefaultAlias.class);
				assertClasses.add(DefaultASTNodeListener.class);
				assertClasses.add(DefaultColumn.class);
				assertClasses.add(DefaultColumnAlias.class);
				assertClasses.add(DefaultOrderByExpression.class);
				assertClasses.add(DefaultPredicate.class);
				assertClasses.add(DSLFunction.class);
				assertClasses.add(DSLFunctionType.class);
				assertClasses.add(DSLFunctionFactoryUtil.class);
				assertClasses.add(DSLQueryFactoryUtil.class);
				assertClasses.add(ElseEnd.class);
				assertClasses.add(From.class);
				assertClasses.add(GroupBy.class);
				assertClasses.add(Having.class);
				assertClasses.add(Join.class);
				assertClasses.add(JoinType.class);
				assertClasses.add(Limit.class);
				assertClasses.add(NullExpression.class);
				assertClasses.add(Operand.class);
				assertClasses.add(OrderBy.class);
				assertClasses.add(QueryExpression.class);
				assertClasses.add(QueryTable.class);
				assertClasses.add(Scalar.class);
				assertClasses.add(ScalarList.class);
				assertClasses.add(Select.class);
				assertClasses.add(SetOperation.class);
				assertClasses.add(SetOperationType.class);
				assertClasses.add(WhenThen.class);
				assertClasses.add(Where.class);
			}

		};

	@Test
	public void testAggregateExpression() {
		Expression<Long> countExpression = DSLFunctionFactoryUtil.countDistinct(
			ReferenceExampleTable.INSTANCE.name);

		AggregateExpression<Long> countAggregateExpression =
			(AggregateExpression<Long>)countExpression;

		Assert.assertTrue(
			countAggregateExpression.toString(),
			countAggregateExpression.isDistinct());
		Assert.assertSame(
			ReferenceExampleTable.INSTANCE.name,
			countAggregateExpression.getExpression());
		Assert.assertEquals("count", countAggregateExpression.getName());
	}

	@Test
	public void testAlias() {
		String name = "alias";

		Alias<String> alias = ReferenceExampleTable.INSTANCE.name.as(name);

		Assert.assertSame(name, alias.getName());
	}

	@Test
	public void testASTNodeListenerOrder() {
		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
		).from(
			MainExampleTable.INSTANCE
		);

		StringBundler sb = new StringBundler();

		dslQuery.toSQL(
			sb::append,
			astNode -> {
				if (astNode instanceof Select) {
					Assert.assertEquals("", sb.toString());
				}
				else if (astNode instanceof From) {
					Assert.assertEquals("select * ", sb.toString());
				}
			});

		Assert.assertEquals("select * from MainExample", sb.toString());
	}

	@Test
	public void testBaseASTNode() {
		FromStep fromStep = DSLQueryFactoryUtil.select();

		From from = new From(fromStep, MainExampleTable.INSTANCE);

		Assert.assertSame(fromStep, from.getChild());

		OrderBy orderBy = new OrderBy(
			from,
			new OrderByExpression[] {
				MainExampleTable.INSTANCE.mainExampleId.ascending()
			});

		Assert.assertSame(from, orderBy.getChild());

		Assert.assertEquals(
			"select * from MainExample order by MainExample.mainExampleId asc",
			orderBy.toString());

		JoinStep joinStep = from.innerJoinON(
			ReferenceExampleTable.INSTANCE,
			ReferenceExampleTable.INSTANCE.mainExampleId.eq(
				MainExampleTable.INSTANCE.mainExampleId));

		OrderBy orderBy2 = orderBy.withNewChild(joinStep);

		Assert.assertNotSame(orderBy, orderBy2);

		Assert.assertEquals(
			"select * from MainExample inner join ReferenceExample on " +
				"ReferenceExample.mainExampleId = MainExample.mainExampleId " +
					"order by MainExample.mainExampleId asc",
			orderBy2.toString());

		CloneNotSupportedException cloneNotSupportedException =
			new CloneNotSupportedException();

		try {
			BaseASTNode baseASTNode = new BaseASTNode() {

				@Override
				protected Object clone() throws CloneNotSupportedException {
					throw cloneNotSupportedException;
				}

				@Override
				protected void doToSQL(
					Consumer<String> consumer,
					ASTNodeListener astNodeListener) {
				}

			};

			baseASTNode.withNewChild(null);

			Assert.fail();
		}
		catch (RuntimeException runtimeException) {
			Throwable cause = runtimeException.getCause();

			Assert.assertSame(cloneNotSupportedException, cause);
		}
	}

	@Test
	public void testCaseSelect() {
		Alias<String> numberAlias = DSLFunctionFactoryUtil.caseWhenThen(
			MainExampleTable.INSTANCE.mainExampleId.eq(1L), new Scalar<>("one")
		).whenThen(
			MainExampleTable.INSTANCE.mainExampleId.eq(2L), "two"
		).whenThen(
			MainExampleTable.INSTANCE.mainExampleId.eq(3L), "three"
		).elseEnd(
			"unknown"
		).as(
			"number"
		);

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			numberAlias
		).from(
			MainExampleTable.INSTANCE
		).where(
			numberAlias.neq("unknown")
		);

		DefaultASTNodeListener defaultASTNodeListener =
			new DefaultASTNodeListener();

		Assert.assertEquals(
			StringBundler.concat(
				"select case when MainExample.mainExampleId = ? then ? when ",
				"MainExample.mainExampleId = ? then ? when MainExample.",
				"mainExampleId = ? then ? else ? end number from MainExample ",
				"where number != ?"),
			dslQuery.toSQL(defaultASTNodeListener));

		Assert.assertEquals(
			Arrays.asList(
				1L, "one", 2L, "two", 3L, "three", "unknown", "unknown"),
			defaultASTNodeListener.getScalarValues());
	}

	@Test
	public void testCaseWhenThen() {
		Predicate predicate = MainExampleTable.INSTANCE.mainExampleId.eq(1L);

		Scalar<String> scalar = new Scalar<>("one");

		CaseWhenThen<String> caseWhenThen = new CaseWhenThen<>(
			predicate, scalar);

		Assert.assertSame(predicate, caseWhenThen.getPredicate());
		Assert.assertSame(scalar, caseWhenThen.getThenExpression());
	}

	@Test
	public void testColumn() {
		MainExampleTable aliasMainExampleTable = MainExampleTable.INSTANCE.as(
			"alias");

		Assert.assertEquals(
			MainExampleTable.INSTANCE.mainExampleId,
			MainExampleTable.INSTANCE.mainExampleId);
		Assert.assertEquals(
			MainExampleTable.INSTANCE.mainExampleId,
			aliasMainExampleTable.mainExampleId);
		Assert.assertEquals(
			MainExampleTable.INSTANCE.mainExampleId.hashCode(),
			aliasMainExampleTable.mainExampleId.hashCode());
		Assert.assertNotEquals(
			MainExampleTable.INSTANCE.mainExampleId,
			MainExampleTable.INSTANCE.name);
		Assert.assertNotEquals(
			MainExampleTable.INSTANCE.mainExampleId,
			ReferenceExampleTable.INSTANCE.mainExampleId);
		Assert.assertNotEquals(
			MainExampleTable.INSTANCE.mainExampleId, MainExampleTable.INSTANCE);

		Assert.assertSame(
			aliasMainExampleTable,
			aliasMainExampleTable.mainExampleId.getTable());

		Assert.assertFalse(
			MainExampleTable.INSTANCE.mainExampleId.isNullAllowed());
		Assert.assertFalse(aliasMainExampleTable.mainExampleId.isNullAllowed());
		Assert.assertTrue(MainExampleTable.INSTANCE.name.isNullAllowed());
		Assert.assertTrue(aliasMainExampleTable.name.isNullAllowed());

		Assert.assertTrue(
			MainExampleTable.INSTANCE.mainExampleId.isPrimaryKey());
		Assert.assertTrue(aliasMainExampleTable.mainExampleId.isPrimaryKey());
		Assert.assertFalse(MainExampleTable.INSTANCE.name.isPrimaryKey());
		Assert.assertFalse(aliasMainExampleTable.name.isPrimaryKey());
	}

	@Test
	public void testConstructors() {
		new DSLFunctionFactoryUtil();
		new DSLQueryFactoryUtil();
	}

	@Test
	public void testDerivedTable() {
		ReferenceExampleTable aliasReferenceExampleTable =
			ReferenceExampleTable.INSTANCE.as("referenceExample");

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
		).from(
			MainExampleTable.INSTANCE
		).leftJoinOn(
			DSLQueryFactoryUtil.select(
				ReferenceExampleTable.INSTANCE.mainExampleId,
				ReferenceExampleTable.INSTANCE.name
			).from(
				ReferenceExampleTable.INSTANCE
			).groupBy(
				ReferenceExampleTable.INSTANCE.mainExampleId,
				ReferenceExampleTable.INSTANCE.name
			).having(
				DSLFunctionFactoryUtil.count(
					ReferenceExampleTable.INSTANCE.name
				).gt(
					3L
				)
			).as(
				aliasReferenceExampleTable.getName()
			),
			aliasReferenceExampleTable.mainExampleId.eq(
				MainExampleTable.INSTANCE.mainExampleId)
		).orderBy(
			ReferenceExampleTable.INSTANCE.name.ascending()
		);

		DefaultASTNodeListener defaultASTNodeListener =
			new DefaultASTNodeListener();

		Assert.assertEquals(
			StringBundler.concat(
				"select * from MainExample left join (select ",
				"ReferenceExample.mainExampleId, ReferenceExample.name from ",
				"ReferenceExample group by ReferenceExample.mainExampleId, ",
				"ReferenceExample.name having count(ReferenceExample.name) > ",
				"?) referenceExample on referenceExample.mainExampleId = ",
				"MainExample.mainExampleId order by ReferenceExample.name asc"),
			dslQuery.toSQL(defaultASTNodeListener));

		Assert.assertArrayEquals(
			new String[] {"MainExample", "ReferenceExample"},
			defaultASTNodeListener.getTableNames());
	}

	@Test
	public void testElseEnd() {
		WhenThenStep<String> whenThenStep = DSLFunctionFactoryUtil.caseWhenThen(
			MainExampleTable.INSTANCE.mainExampleId.eq(
				ReferenceExampleTable.INSTANCE.mainExampleId),
			"equals");

		String scalarValue = "not equals";

		ElseEnd<String> elseEnd = new ElseEnd<>(
			whenThenStep, new Scalar<>(scalarValue));

		Scalar<String> scalar = (Scalar<String>)elseEnd.getElseExpression();

		Assert.assertSame(scalarValue, scalar.getValue());
	}

	@Test
	public void testFrom() {
		From from = new From(
			DSLQueryFactoryUtil.count(), MainExampleTable.INSTANCE);

		Assert.assertSame(MainExampleTable.INSTANCE, from.getTable());
	}

	@Test
	public void testFunction() {
		Expression<?>[] expressions = new Expression<?>[] {
			MainExampleTable.INSTANCE.mainExampleId,
			MainExampleTable.INSTANCE.flag
		};

		DSLFunction<Long> dslFunction = new DSLFunction<>(
			DSLFunctionType.BITWISE_AND, expressions);

		Assert.assertSame(
			DSLFunctionType.BITWISE_AND, dslFunction.getDslFunctionType());

		Assert.assertSame(expressions, dslFunction.getExpressions());

		try {
			new DSLFunction<>(DSLFunctionType.BITWISE_AND);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertSame(
				IllegalArgumentException.class, exception.getClass());

			Assert.assertEquals("Expressions is empty", exception.getMessage());
		}
	}

	@Test
	public void testFunctions() {
		Assert.assertEquals(
			"MainExample.mainExampleId + ReferenceExample.referenceExampleId",
			String.valueOf(
				DSLFunctionFactoryUtil.add(
					MainExampleTable.INSTANCE.mainExampleId,
					ReferenceExampleTable.INSTANCE.referenceExampleId)));
		Assert.assertEquals(
			"MainExample.mainExampleId + ?",
			String.valueOf(
				DSLFunctionFactoryUtil.add(
					MainExampleTable.INSTANCE.mainExampleId, 2L)));
		Assert.assertEquals(
			"avg(MainExample.mainExampleId)",
			String.valueOf(
				DSLFunctionFactoryUtil.avg(
					MainExampleTable.INSTANCE.mainExampleId)));
		Assert.assertEquals(
			"BITAND(MainExample.mainExampleId, " +
				"ReferenceExample.referenceExampleId)",
			String.valueOf(
				DSLFunctionFactoryUtil.bitAnd(
					MainExampleTable.INSTANCE.mainExampleId,
					ReferenceExampleTable.INSTANCE.referenceExampleId)));
		Assert.assertEquals(
			"BITAND(MainExample.mainExampleId, ?)",
			String.valueOf(
				DSLFunctionFactoryUtil.bitAnd(
					MainExampleTable.INSTANCE.mainExampleId, 2L)));
		Assert.assertEquals(
			"CAST_CLOB_TEXT(MainExample.description)",
			String.valueOf(
				DSLFunctionFactoryUtil.castClobText(
					MainExampleTable.INSTANCE.description)));
		Assert.assertEquals(
			"CAST_LONG(MainExample.name)",
			String.valueOf(
				DSLFunctionFactoryUtil.castLong(
					MainExampleTable.INSTANCE.name)));
		Assert.assertEquals(
			"CAST_TEXT(MainExample.mainExampleId)",
			String.valueOf(
				DSLFunctionFactoryUtil.castText(
					MainExampleTable.INSTANCE.mainExampleId)));
		Assert.assertEquals(
			"CONCAT(MainExample.name, ?, ReferenceExample.name)",
			String.valueOf(
				DSLFunctionFactoryUtil.concat(
					MainExampleTable.INSTANCE.name,
					new Scalar<>("__delimiter__"),
					ReferenceExampleTable.INSTANCE.name)));
		Assert.assertEquals(
			"count(MainExample.name)",
			String.valueOf(
				DSLFunctionFactoryUtil.count(MainExampleTable.INSTANCE.name)));
		Assert.assertEquals(
			"LOWER(MainExample.name)",
			String.valueOf(
				DSLFunctionFactoryUtil.lower(MainExampleTable.INSTANCE.name)));
		Assert.assertEquals(
			"MainExample.mainExampleId / ?",
			String.valueOf(
				DSLFunctionFactoryUtil.divide(
					MainExampleTable.INSTANCE.mainExampleId, 2L)));
		Assert.assertEquals(
			"MainExample.mainExampleId / ReferenceExample.referenceExampleId",
			String.valueOf(
				DSLFunctionFactoryUtil.divide(
					MainExampleTable.INSTANCE.mainExampleId,
					ReferenceExampleTable.INSTANCE.referenceExampleId)));
		Assert.assertEquals(
			"max(MainExample.mainExampleId)",
			String.valueOf(
				DSLFunctionFactoryUtil.max(
					MainExampleTable.INSTANCE.mainExampleId)));
		Assert.assertEquals(
			"min(MainExample.mainExampleId)",
			String.valueOf(
				DSLFunctionFactoryUtil.min(
					MainExampleTable.INSTANCE.mainExampleId)));
		Assert.assertEquals(
			"MainExample.mainExampleId * ?",
			String.valueOf(
				DSLFunctionFactoryUtil.multiply(
					MainExampleTable.INSTANCE.mainExampleId, 2L)));
		Assert.assertEquals(
			"MainExample.mainExampleId * ReferenceExample.referenceExampleId",
			String.valueOf(
				DSLFunctionFactoryUtil.multiply(
					MainExampleTable.INSTANCE.mainExampleId,
					ReferenceExampleTable.INSTANCE.referenceExampleId)));
		Assert.assertEquals(
			"MainExample.mainExampleId - ?",
			String.valueOf(
				DSLFunctionFactoryUtil.subtract(
					MainExampleTable.INSTANCE.mainExampleId, 2L)));
		Assert.assertEquals(
			"MainExample.mainExampleId - ReferenceExample.referenceExampleId",
			String.valueOf(
				DSLFunctionFactoryUtil.subtract(
					MainExampleTable.INSTANCE.mainExampleId,
					ReferenceExampleTable.INSTANCE.referenceExampleId)));
		Assert.assertEquals(
			"sum(MainExample.mainExampleId)",
			String.valueOf(
				DSLFunctionFactoryUtil.sum(
					MainExampleTable.INSTANCE.mainExampleId)));
	}

	@Test
	public void testGroupBy() {
		From from = new From(
			DSLQueryFactoryUtil.select(
				MainExampleTable.INSTANCE.mainExampleId,
				MainExampleTable.INSTANCE.name),
			MainExampleTable.INSTANCE);

		GroupBy groupBy = new GroupBy(from, MainExampleTable.INSTANCE.name);

		Expression<?>[] expressions = groupBy.getExpressions();

		Assert.assertEquals(
			Arrays.toString(expressions), 1, expressions.length);

		Assert.assertSame(MainExampleTable.INSTANCE.name, expressions[0]);

		DSLQuery dslQuery = groupBy.limit(0, 20);

		Assert.assertEquals(
			"select MainExample.mainExampleId, MainExample.name from " +
				"MainExample group by MainExample.name ",
			dslQuery.toString());

		try {
			from.groupBy();

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertSame(
				IllegalArgumentException.class, exception.getClass());

			Assert.assertEquals("Expressions is empty", exception.getMessage());
		}
	}

	@Test
	public void testHaving() {
		HavingStep havingStep = DSLQueryFactoryUtil.select(
			MainExampleTable.INSTANCE.mainExampleId
		).from(
			MainExampleTable.INSTANCE
		).groupBy(
			MainExampleTable.INSTANCE.name
		);

		OrderByStep orderByStep = havingStep.having(null);

		Assert.assertEquals(
			"select MainExample.mainExampleId from MainExample group by " +
				"MainExample.name",
			orderByStep.toString());

		Predicate predicate = DSLFunctionFactoryUtil.count(
			MainExampleTable.INSTANCE.name
		).gt(
			10L
		);

		Having having = new Having(havingStep, predicate);

		Assert.assertEquals(
			"select MainExample.mainExampleId from MainExample group by " +
				"MainExample.name having count(MainExample.name) > ?",
			having.toString());

		Assert.assertSame(predicate, having.getPredicate());
	}

	@Test
	public void testJoin() {
		Assert.assertEquals("inner", JoinType.INNER.toString());
		Assert.assertEquals("left", JoinType.LEFT.toString());

		Predicate onPredicate = ReferenceExampleTable.INSTANCE.mainExampleId.eq(
			MainExampleTable.INSTANCE.mainExampleId);

		Join join = new Join(
			DSLQueryFactoryUtil.countDistinct(
				MainExampleTable.INSTANCE.name
			).from(
				MainExampleTable.INSTANCE
			),
			JoinType.INNER, ReferenceExampleTable.INSTANCE, onPredicate);

		Assert.assertSame(ReferenceExampleTable.INSTANCE, join.getTable());
		Assert.assertSame(JoinType.INNER, join.getJoinType());
		Assert.assertSame(onPredicate, join.getOnPredicate());
	}

	@Test
	public void testJoinCount() {
		DSLQuery dslQuery = DSLQueryFactoryUtil.countDistinct(
			MainExampleTable.INSTANCE.name
		).from(
			MainExampleTable.INSTANCE
		).leftJoinOn(
			MainExampleTable.INSTANCE, null
		).innerJoinON(
			ReferenceExampleTable.INSTANCE,
			ReferenceExampleTable.INSTANCE.mainExampleId.eq(
				MainExampleTable.INSTANCE.mainExampleId)
		).innerJoinON(
			ReferenceExampleTable.INSTANCE, null
		).where(
			MainExampleTable.INSTANCE.name.neq("")
		);

		Assert.assertEquals(
			StringBundler.concat(
				"select count(distinct MainExample.name) COUNT_VALUE from ",
				"MainExample inner join ReferenceExample on ",
				"ReferenceExample.mainExampleId = MainExample.mainExampleId ",
				"where MainExample.name != ?"),
			dslQuery.toString());
	}

	@Test
	public void testLeftJoin() {
		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			MainExampleTable.INSTANCE.mainExampleId
		).from(
			MainExampleTable.INSTANCE
		).leftJoinOn(
			ReferenceExampleTable.INSTANCE,
			ReferenceExampleTable.INSTANCE.mainExampleId.eq(
				MainExampleTable.INSTANCE.mainExampleId)
		).where(
			ReferenceExampleTable.INSTANCE.mainExampleId.isNull()
		).orderBy(
			MainExampleTable.INSTANCE.flag.descending(),
			MainExampleTable.INSTANCE.name.ascending()
		);

		Assert.assertEquals(
			StringBundler.concat(
				"select MainExample.mainExampleId from MainExample left join ",
				"ReferenceExample on ReferenceExample.mainExampleId = ",
				"MainExample.mainExampleId where ",
				"ReferenceExample.mainExampleId is NULL order by ",
				"MainExample.flag desc, MainExample.name asc"),
			dslQuery.toString());
	}

	@Test
	public void testOperands() {
		Assert.assertEquals("and", Operand.AND.toString());

		Assert.assertEquals("=", Operand.EQUAL.toString());

		Assert.assertEquals(">", Operand.GREATER_THAN.toString());

		Assert.assertEquals(">=", Operand.GREATER_THAN_OR_EQUAL.toString());

		Assert.assertEquals("in", Operand.IN.toString());

		Assert.assertEquals("is", Operand.IS.toString());

		Assert.assertEquals("is not", Operand.IS_NOT.toString());

		Assert.assertEquals("<", Operand.LESS_THAN.toString());

		Assert.assertEquals("<=", Operand.LESS_THAN_OR_EQUAL.toString());

		Assert.assertEquals("like", Operand.LIKE.toString());

		Assert.assertEquals("!=", Operand.NOT_EQUAL.toString());

		Assert.assertEquals("not in", Operand.NOT_IN.toString());

		Assert.assertEquals("not like", Operand.NOT_LIKE.toString());

		Assert.assertEquals("or", Operand.OR.toString());
	}

	@Test
	public void testOrderBy() {
		JoinStep joinStep = DSLQueryFactoryUtil.select(
			MainExampleTable.INSTANCE.name
		).from(
			MainExampleTable.INSTANCE
		);

		Assert.assertEquals(
			"select MainExample.name from MainExample", joinStep.toString());

		LimitStep limitStep = joinStep.orderBy();

		Assert.assertEquals(
			"select MainExample.name from MainExample", limitStep.toString());

		limitStep = joinStep.orderBy((OrderByExpression[])null);

		Assert.assertEquals(
			"select MainExample.name from MainExample", limitStep.toString());

		limitStep = joinStep.orderBy(orderByStep -> null);

		Assert.assertEquals(
			"select MainExample.name from MainExample", limitStep.toString());

		limitStep = joinStep.orderBy(
			orderByStep -> orderByStep.orderBy(
				MainExampleTable.INSTANCE.name.ascending()));

		Assert.assertEquals(
			"select MainExample.name from MainExample order by " +
				"MainExample.name asc",
			limitStep.toString());

		try {
			new OrderBy(joinStep, new OrderByExpression[0]);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertSame(
				IllegalArgumentException.class, exception.getClass());

			Assert.assertEquals(
				"Order by expressions is empty", exception.getMessage());
		}

		OrderByExpression orderByExpression =
			MainExampleTable.INSTANCE.name.ascending();

		OrderByExpression[] orderByExpressions = {orderByExpression};

		OrderBy orderBy = new OrderBy(joinStep, orderByExpressions);

		Assert.assertSame(orderByExpressions, orderBy.getOrderByExpressions());

		Assert.assertEquals(
			MainExampleTable.INSTANCE.name, orderByExpression.getExpression());
		Assert.assertTrue(
			orderByExpression.toString(), orderByExpression.isAscending());

		Assert.assertEquals(
			"select MainExample.name from MainExample order by " +
				"MainExample.name asc",
			orderBy.toString());
	}

	@Test
	public void testOrderByOrderByInfo() {
		OrderByStep orderByStep = DSLQueryFactoryUtil.select(
			MainExampleTable.INSTANCE.name
		).from(
			MainExampleTable.INSTANCE
		);

		DSLQuery dslQuery = orderByStep.orderBy(
			MainExampleTable.INSTANCE, null);

		Assert.assertEquals(
			"select MainExample.name from MainExample", dslQuery.toString());

		OrderByInfo orderByInfo = new TestOrderByInfo(
			MainExampleTable.INSTANCE.flag.getName());

		dslQuery = orderByStep.orderBy(MainExampleTable.INSTANCE, orderByInfo);

		Assert.assertEquals(
			"select MainExample.name from MainExample order by " +
				"MainExample.flag asc",
			dslQuery.toString());

		dslQuery = orderByStep.orderBy(
			ReferenceExampleTable.INSTANCE, orderByInfo);

		Assert.assertEquals(
			"select MainExample.name from MainExample", dslQuery.toString());
	}

	@Test
	public void testOrderByOrderByInfoWithAlias() {
		ColumnAlias<MainExampleTable, String> columnAlias =
			MainExampleTable.INSTANCE.name.as("columnAlias");

		OrderByStep orderByStep = DSLQueryFactoryUtil.select(
			columnAlias
		).from(
			columnAlias.getTable()
		);

		DSLQuery dslQuery = orderByStep.orderBy(columnAlias.getTable(), null);

		Assert.assertEquals(
			"select MainExample.name columnAlias from MainExample",
			dslQuery.toString());

		OrderByInfo orderByInfo = new TestOrderByInfo(
			columnAlias.getName(),
			ReferenceExampleTable.INSTANCE.referenceExampleId.getName());

		dslQuery = orderByStep.orderBy(columnAlias.getTable(), orderByInfo);

		Assert.assertEquals(
			"select MainExample.name columnAlias from MainExample order by " +
				"columnAlias asc",
			dslQuery.toString());
	}

	@Test
	public void testPredicateParentheses() {
		Predicate leftPredicate = MainExampleTable.INSTANCE.mainExampleId.gte(
			1L);

		Predicate rightPredicate = MainExampleTable.INSTANCE.name.eq(
			"test"
		).or(
			MainExampleTable.INSTANCE.name.eq((String)null)
		).withParentheses();

		DefaultPredicate defaultPredicate = new DefaultPredicate(
			leftPredicate, Operand.AND, rightPredicate);

		Assert.assertSame(leftPredicate, defaultPredicate.getLeftExpression());
		Assert.assertSame(Operand.AND, defaultPredicate.getOperand());
		Assert.assertSame(
			rightPredicate, defaultPredicate.getRightExpression());

		Assert.assertFalse(defaultPredicate.isWrapParentheses());

		Assert.assertSame(rightPredicate, rightPredicate.withParentheses());

		DSLQuery dslQuery = DSLQueryFactoryUtil.count(
		).from(
			MainExampleTable.INSTANCE
		).where(
			defaultPredicate
		);

		DefaultASTNodeListener defaultASTNodeListener =
			new DefaultASTNodeListener();

		Assert.assertEquals(
			StringBundler.concat(
				"select count(*) COUNT_VALUE from MainExample where ",
				"MainExample.mainExampleId >= ? and (MainExample.name = ? or ",
				"MainExample.name = ?)"),
			dslQuery.toSQL(defaultASTNodeListener));

		Assert.assertEquals(
			Arrays.asList(1L, "test", null),
			defaultASTNodeListener.getScalarValues());
	}

	@Test
	public void testQueryTable() {
		Table<?> table1 = DSLQueryFactoryUtil.select(
			new Scalar<>(1)
		).as(
			"alias"
		);

		Assert.assertEquals("(select ?) alias", table1.toString());

		Assert.assertEquals(System.identityHashCode(table1), table1.hashCode());

		Table<?> table2 = DSLQueryFactoryUtil.select(
			new Scalar<>(2)
		).as(
			"alias"
		);

		Assert.assertEquals(table1, table1);

		Assert.assertNotEquals(table1, table2);
	}

	@Test
	public void testScalarList() {
		Long[] longs = {0L, 1L, 2L};

		ScalarList<Long> scalarList = new ScalarList<>(longs);

		Assert.assertSame(longs, scalarList.getValues());

		try {
			new ScalarList<>(new String[0]);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertSame(
				IllegalArgumentException.class, exception.getClass());

			Assert.assertEquals("Values is empty", exception.getMessage());
		}
	}

	@Test
	public void testSelect() {
		List<Expression<?>> expressions = Arrays.asList(
			MainExampleTable.INSTANCE.mainExampleId,
			MainExampleTable.INSTANCE.flag);

		Select select = new Select(true, expressions);

		Assert.assertTrue(select.isDistinct());
		Assert.assertSame(expressions, select.getExpressions());
	}

	@Test
	public void testSelect1() {
		DSLQuery dslQuery = DSLQueryFactoryUtil.select(new Scalar<>(1));

		Assert.assertEquals("select ?", dslQuery.toString());
	}

	@Test
	public void testSelectDistinctWhereInWithAlias() {
		MainExampleTable aliasMainExampleTable = MainExampleTable.INSTANCE.as(
			"mainTable");

		JoinStep joinStep = DSLQueryFactoryUtil.selectDistinct(
			aliasMainExampleTable.name
		).from(
			aliasMainExampleTable
		);

		DSLQuery dslQuery = joinStep.where(
			aliasMainExampleTable.flag.in(new Integer[] {1, 2}));

		Assert.assertEquals(
			"select distinct mainTable.name from MainExample mainTable where " +
				"mainTable.flag in (?, ?)",
			dslQuery.toString());

		dslQuery = joinStep.where(
			aliasMainExampleTable.mainExampleId.in(new Long[] {1L, 2L, null})
		).orderBy(
			aliasMainExampleTable.name.ascending()
		);

		DefaultASTNodeListener defaultASTNodeListener =
			new DefaultASTNodeListener();

		Assert.assertEquals(
			StringBundler.concat(
				"select distinct mainTable.name from MainExample mainTable ",
				"where mainTable.mainExampleId in (?, ?, ?) order by ",
				"mainTable.name asc"),
			dslQuery.toSQL(defaultASTNodeListener));

		Assert.assertEquals(
			Arrays.asList(1L, 2L, null),
			defaultASTNodeListener.getScalarValues());

		String[] strings = {"1", "2", "3"};

		dslQuery = joinStep.where(
			DSLFunctionFactoryUtil.castText(
				aliasMainExampleTable.mainExampleId
			).in(
				strings
			)
		).orderBy(
			aliasMainExampleTable.name.ascending()
		);

		defaultASTNodeListener = new DefaultASTNodeListener();

		Assert.assertEquals(
			StringBundler.concat(
				"select distinct mainTable.name from MainExample mainTable ",
				"where CAST_TEXT(mainTable.mainExampleId) in (?, ?, ?) order ",
				"by mainTable.name asc"),
			dslQuery.toSQL(defaultASTNodeListener));

		Assert.assertEquals(
			Arrays.asList(strings), defaultASTNodeListener.getScalarValues());
	}

	@Test
	public void testSelectTable() {
		FromStep fromStep = DSLQueryFactoryUtil.select(
			MainExampleTable.INSTANCE);

		Assert.assertEquals(
			"select MainExample.description, MainExample.flag, " +
				"MainExample.mainExampleId, MainExample.name",
			fromStep.toString());

		fromStep = DSLQueryFactoryUtil.selectDistinct(
			MainExampleTable.INSTANCE);

		Assert.assertEquals(
			"select distinct MainExample.description, MainExample.flag, " +
				"MainExample.mainExampleId, MainExample.name",
			fromStep.toString());
	}

	@Test
	public void testSelfJoin() {
		MainExampleTable aliasMainExampleTable = MainExampleTable.INSTANCE.as(
			"tempMainExample");

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			MainExampleTable.INSTANCE.mainExampleId,
			MainExampleTable.INSTANCE.name
		).from(
			MainExampleTable.INSTANCE
		).leftJoinOn(
			aliasMainExampleTable,
			MainExampleTable.INSTANCE.mainExampleId.lt(
				aliasMainExampleTable.mainExampleId)
		).where(
			aliasMainExampleTable.mainExampleId.isNull()
		);

		Assert.assertEquals(
			StringBundler.concat(
				"select MainExample.mainExampleId, MainExample.name from ",
				"MainExample left join MainExample tempMainExample on ",
				"MainExample.mainExampleId < tempMainExample.mainExampleId ",
				"where tempMainExample.mainExampleId is NULL"),
			dslQuery.toString());
	}

	@Test
	public void testSimpleCount() {
		DSLQuery dslQuery = DSLQueryFactoryUtil.count(
		).from(
			MainExampleTable.INSTANCE
		);

		Assert.assertEquals(
			"select count(*) COUNT_VALUE from MainExample",
			dslQuery.toString());
	}

	@Test
	public void testSimpleSelect() {
		FromStep fromStep = DSLQueryFactoryUtil.select();

		Assert.assertEquals("select *", fromStep.toString());

		JoinStep joinStep = fromStep.from(MainExampleTable.INSTANCE);

		Assert.assertEquals("select * from MainExample", joinStep.toString());

		GroupByStep groupByStep = joinStep.where(() -> null);

		Assert.assertEquals(
			"select * from MainExample", groupByStep.toString());

		Predicate predicate = MainExampleTable.INSTANCE.name.eq("test");

		Assert.assertEquals("MainExample.name = ?", predicate.toString());

		predicate = predicate.and(
			MainExampleTable.INSTANCE.mainExampleId.gt(0L));

		Assert.assertEquals(
			"MainExample.name = ? and MainExample.mainExampleId > ?",
			predicate.toString());

		predicate = predicate.and(null);

		Assert.assertEquals(
			"MainExample.name = ? and MainExample.mainExampleId > ?",
			predicate.toString());

		predicate = predicate.or(null);

		Assert.assertEquals(
			"MainExample.name = ? and MainExample.mainExampleId > ?",
			predicate.toString());

		Where where = new Where(joinStep, predicate);

		Assert.assertSame(predicate, where.getPredicate());

		Assert.assertEquals(
			"select * from MainExample where MainExample.name = ? and " +
				"MainExample.mainExampleId > ?",
			where.toString());

		OrderByExpression orderByExpression =
			MainExampleTable.INSTANCE.mainExampleId.descending();

		Assert.assertEquals(
			"MainExample.mainExampleId desc", orderByExpression.toString());

		OrderBy orderBy = new OrderBy(
			where, new OrderByExpression[] {orderByExpression});

		DefaultASTNodeListener defaultASTNodeListener =
			new DefaultASTNodeListener();

		Assert.assertEquals(
			StringBundler.concat(
				"select * from MainExample where MainExample.name = ? and ",
				"MainExample.mainExampleId > ? order by ",
				"MainExample.mainExampleId desc"),
			orderBy.toSQL(defaultASTNodeListener));

		String[] tableNames = defaultASTNodeListener.getTableNames();

		Assert.assertArrayEquals(
			new String[] {MainExampleTable.INSTANCE.getTableName()},
			tableNames);

		Assert.assertEquals(
			Arrays.asList("test", 0L),
			defaultASTNodeListener.getScalarValues());

		Assert.assertEquals(-1, defaultASTNodeListener.getStart());
		Assert.assertEquals(-1, defaultASTNodeListener.getEnd());

		Limit limit = new Limit(orderBy, -1, -1);

		defaultASTNodeListener = new DefaultASTNodeListener();

		Assert.assertEquals(
			StringBundler.concat(
				"select * from MainExample where MainExample.name = ? and ",
				"MainExample.mainExampleId > ? order by ",
				"MainExample.mainExampleId desc "),
			limit.toSQL(defaultASTNodeListener));

		Assert.assertEquals(-1, defaultASTNodeListener.getStart());
		Assert.assertEquals(-1, defaultASTNodeListener.getEnd());

		limit = new Limit(orderBy, 10, 30);

		defaultASTNodeListener = new DefaultASTNodeListener();

		Assert.assertEquals(
			StringBundler.concat(
				"select * from MainExample where MainExample.name = ? and ",
				"MainExample.mainExampleId > ? order by ",
				"MainExample.mainExampleId desc "),
			limit.toSQL(defaultASTNodeListener));

		Assert.assertEquals(10, defaultASTNodeListener.getStart());
		Assert.assertEquals(30, defaultASTNodeListener.getEnd());
	}

	@Test
	public void testSubqueryCount() {
		DSLQuery dslQuery = DSLQueryFactoryUtil.count(
		).from(
			MainExampleTable.INSTANCE
		).where(
			MainExampleTable.INSTANCE.mainExampleId.in(
				DSLQueryFactoryUtil.select(
					ReferenceExampleTable.INSTANCE.mainExampleId
				).from(
					ReferenceExampleTable.INSTANCE
				).where(
					() -> ReferenceExampleTable.INSTANCE.name.eq("test")
				))
		);

		Assert.assertEquals(
			StringBundler.concat(
				"select count(*) COUNT_VALUE from MainExample where ",
				"MainExample.mainExampleId in (select ",
				"ReferenceExample.mainExampleId from ReferenceExample where ",
				"ReferenceExample.name = ?)"),
			dslQuery.toString());
	}

	@Test
	public void testTable() {
		Assert.assertEquals(
			"MainExample", MainExampleTable.INSTANCE.toString());

		MainExampleTable aliasMainExampleTable = MainExampleTable.INSTANCE.as(
			"alias");

		Assert.assertNotSame(MainExampleTable.INSTANCE, aliasMainExampleTable);

		Assert.assertEquals(
			MainExampleTable.INSTANCE, MainExampleTable.INSTANCE);
		Assert.assertEquals(MainExampleTable.INSTANCE, aliasMainExampleTable);
		Assert.assertNotEquals(
			MainExampleTable.INSTANCE, MainExampleTable.INSTANCE.name);
		Assert.assertNotEquals(
			MainExampleTable.INSTANCE, ReferenceExampleTable.INSTANCE);

		Assert.assertEquals(
			MainExampleTable.INSTANCE.hashCode(),
			aliasMainExampleTable.hashCode());

		Assert.assertSame(
			MainExampleTable.INSTANCE.name,
			MainExampleTable.INSTANCE.getColumn(
				MainExampleTable.INSTANCE.name.getName(),
				MainExampleTable.INSTANCE.name.getJavaType()));
		Assert.assertNull(
			MainExampleTable.INSTANCE.getColumn(
				MainExampleTable.INSTANCE.name.getName(), Long.class));

		ColumnAlias<MainExampleTable, String> nameColumnAlias =
			aliasMainExampleTable.name.as("nameColumnAlias");

		Assert.assertEquals(
			"MainExample alias", aliasMainExampleTable.toString());

		Assert.assertNotSame(
			MainExampleTable.INSTANCE.name, nameColumnAlias.getExpression());

		aliasMainExampleTable = nameColumnAlias.getTable();

		Assert.assertEquals("alias", aliasMainExampleTable.getName());

		Column<MainExampleTable, String> column =
			(Column<MainExampleTable, String>)nameColumnAlias.getExpression();

		Assert.assertSame(column.getTable(), nameColumnAlias.getTable());

		Assert.assertEquals(
			MainExampleTable.INSTANCE.name,
			aliasMainExampleTable.getColumn(
				nameColumnAlias.getName(), column.getJavaType()));

		Assert.assertNull(
			MainExampleTable.INSTANCE.getColumn(nameColumnAlias.getName()));
		Assert.assertNull(
			MainExampleTable.INSTANCE.getColumn(
				nameColumnAlias.getName(), column.getJavaType()));

		Collection<Column<MainExampleTable, ?>> columns =
			MainExampleTable.INSTANCE.getColumns();

		Assert.assertEquals(columns.toString(), 4, columns.size());

		Assert.assertTrue(
			columns.contains(MainExampleTable.INSTANCE.mainExampleId));
		Assert.assertTrue(columns.contains(MainExampleTable.INSTANCE.name));
		Assert.assertTrue(
			columns.contains(MainExampleTable.INSTANCE.description));
		Assert.assertTrue(columns.contains(MainExampleTable.INSTANCE.flag));

		try {
			columns.remove(MainExampleTable.INSTANCE.mainExampleId);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertSame(
				UnsupportedOperationException.class, exception.getClass());
		}
	}

	@Test
	public void testUnionSelect() {
		Assert.assertEquals("union", SetOperationType.UNION.toString());

		Assert.assertEquals("union all", SetOperationType.UNION_ALL.toString());

		DSLQuery dslQuery1 = DSLQueryFactoryUtil.select(
			MainExampleTable.INSTANCE.name.as("name")
		).from(
			MainExampleTable.INSTANCE
		);

		DSLQuery dslQuery2 = DSLQueryFactoryUtil.select(
			ReferenceExampleTable.INSTANCE.name.as("name")
		).from(
			ReferenceExampleTable.INSTANCE
		);

		SetOperation setOperation = (SetOperation)dslQuery1.union(dslQuery2);

		Assert.assertSame(dslQuery1, setOperation.getLeftDSLQuery());

		Assert.assertSame(
			SetOperationType.UNION, setOperation.getSetOperationType());

		Assert.assertSame(dslQuery2, setOperation.getRightDSLQuery());

		Assert.assertEquals(
			"select MainExample.name name from MainExample union select " +
				"ReferenceExample.name name from ReferenceExample",
			setOperation.toString());

		setOperation = (SetOperation)dslQuery1.unionAll(
			DSLQueryFactoryUtil.select(
				ReferenceExampleTable.INSTANCE.name.as("name")
			).from(
				ReferenceExampleTable.INSTANCE
			));

		Assert.assertSame(
			SetOperationType.UNION_ALL, setOperation.getSetOperationType());

		String sql =
			"select MainExample.name name from MainExample union all select " +
				"ReferenceExample.name name from ReferenceExample";

		Assert.assertEquals(sql, setOperation.toString());

		setOperation = (SetOperation)setOperation.union(setOperation);

		Assert.assertEquals(
			StringBundler.concat(sql, " union ", sql), setOperation.toString());
	}

	@Test
	public void testWhenThen() {
		Predicate predicate = MainExampleTable.INSTANCE.mainExampleId.eq(2L);

		Scalar<String> scalar = new Scalar<>("two");

		WhenThen<String> whenThen = new WhenThen<>(
			DSLFunctionFactoryUtil.caseWhenThen(
				MainExampleTable.INSTANCE.mainExampleId.eq(1L), "one"),
			predicate, scalar);

		Assert.assertSame(predicate, whenThen.getPredicate());

		Assert.assertSame(scalar, whenThen.getThenExpression());
	}

	private static class MainExampleTable extends BaseTable<MainExampleTable> {

		public static final MainExampleTable INSTANCE = new MainExampleTable();

		public final Column<MainExampleTable, Clob> description = createColumn(
			"description", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
		public final Column<MainExampleTable, Integer> flag = createColumn(
			"flag", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
		public final Column<MainExampleTable, Long> mainExampleId =
			createColumn(
				"mainExampleId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
		public final Column<MainExampleTable, String> name = createColumn(
			"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

		private MainExampleTable() {
			super("MainExample", MainExampleTable::new);
		}

	}

	private static class ReferenceExampleTable
		extends BaseTable<ReferenceExampleTable> {

		public static final ReferenceExampleTable INSTANCE =
			new ReferenceExampleTable();

		public final Column<ReferenceExampleTable, Long> mainExampleId =
			createColumn(
				"mainExampleId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
		public final Column<ReferenceExampleTable, String> name = createColumn(
			"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
		public final Column<ReferenceExampleTable, Long> referenceExampleId =
			createColumn(
				"referenceExampleId", Long.class, Types.BIGINT,
				Column.FLAG_DEFAULT);

		private ReferenceExampleTable() {
			super("ReferenceExample", ReferenceExampleTable::new);
		}

	}

	private static class TestOrderByInfo implements OrderByInfo {

		@Override
		public String[] getOrderByFields() {
			return _orderByFields;
		}

		@Override
		public boolean isAscending(String field) {
			return true;
		}

		private TestOrderByInfo(String... orderByFields) {
			_orderByFields = orderByFields;
		}

		private final String[] _orderByFields;

	}

}