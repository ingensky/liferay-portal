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

package com.liferay.layout.page.template.headless.delivery.dto.v1_0;

import com.liferay.headless.delivery.dto.v1_0.MasterPage;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;

/**
 * @author Rubén Pulido
 */
public class MasterPageConverterUtil {

	public static MasterPage toMasterPage(
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

		return new MasterPage() {
			{
				name = layoutPageTemplateEntry.getName();
			}
		};
	}

}