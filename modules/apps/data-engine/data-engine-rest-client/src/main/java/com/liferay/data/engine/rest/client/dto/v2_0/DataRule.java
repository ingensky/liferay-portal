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

package com.liferay.data.engine.rest.client.dto.v2_0;

import com.liferay.data.engine.rest.client.function.UnsafeSupplier;
import com.liferay.data.engine.rest.client.serdes.v2_0.DataRuleSerDes;

import java.util.Objects;

import javax.annotation.Generated;

/**
 * @author Jeyvison Nascimento
 * @generated
 */
@Generated("")
public class DataRule implements Cloneable {

	public Object[] getActions() {
		return actions;
	}

	public void setActions(Object[] actions) {
		this.actions = actions;
	}

	public void setActions(
		UnsafeSupplier<Object[], Exception> actionsUnsafeSupplier) {

		try {
			actions = actionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object[] actions;

	public Object[] getConditions() {
		return conditions;
	}

	public void setConditions(Object[] conditions) {
		this.conditions = conditions;
	}

	public void setConditions(
		UnsafeSupplier<Object[], Exception> conditionsUnsafeSupplier) {

		try {
			conditions = conditionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object[] conditions;

	public String getLogicalOperator() {
		return logicalOperator;
	}

	public void setLogicalOperator(String logicalOperator) {
		this.logicalOperator = logicalOperator;
	}

	public void setLogicalOperator(
		UnsafeSupplier<String, Exception> logicalOperatorUnsafeSupplier) {

		try {
			logicalOperator = logicalOperatorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String logicalOperator;

	@Override
	public DataRule clone() throws CloneNotSupportedException {
		return (DataRule)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DataRule)) {
			return false;
		}

		DataRule dataRule = (DataRule)object;

		return Objects.equals(toString(), dataRule.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return DataRuleSerDes.toJSON(this);
	}

}