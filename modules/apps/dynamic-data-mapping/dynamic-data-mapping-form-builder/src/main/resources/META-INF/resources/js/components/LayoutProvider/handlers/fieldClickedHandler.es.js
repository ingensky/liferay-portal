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

import {FormSupport, PagesVisitor} from 'dynamic-data-mapping-form-renderer';

import {localizeField} from '../../../util/fieldSupport.es';

const handleFieldClicked = (props, state, event) => {
	const {fieldName} = event;
	const {pages} = state;

	const fieldProperties = FormSupport.findFieldByName(pages, fieldName);
	const {settingsContext} = fieldProperties;
	const visitor = new PagesVisitor(settingsContext.pages);

	const focusedField = {
		...fieldProperties,
		settingsContext: {
			...settingsContext,
			pages: visitor.mapFields(field => {
				const {fieldName} = field;
				const {defaultLanguageId, editingLanguageId} = props;

				if (fieldName === 'validation') {
					field = {
						...field,
						validation: {
							...field.validation,
							fieldName: fieldProperties.fieldName,
						},
					};
				}

				return localizeField(
					field,
					defaultLanguageId,
					editingLanguageId
				);
			}),
		},
	};

	return {
		focusedField,
		previousFocusedField: focusedField,
	};
};

export default handleFieldClicked;
