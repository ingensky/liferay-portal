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

import ClayLabel from '@clayui/label';
import React from 'react';
import {withRouter} from 'react-router-dom';

import Link from '../components/Link.es';

export default withRouter(({tags = [], match: {params: {sectionTitle}}}) => (
	<ul className="c-mb-0 d-flex flex-wrap list-unstyled stretched-link-layer">
		{tags.map(tag => (
			<li key={tag.taxonomyCategoryId}>
				<ClayLabel displayType="secondary">
					<Link
						className="questions-disabled-link"
						key={tag.taxonomyCategoryId}
						to={`/questions/${
							sectionTitle ? sectionTitle + '/' : ''
						}tag/${tag.taxonomyCategoryId}`}
					>
						{tag.taxonomyCategoryName}
					</Link>
				</ClayLabel>
			</li>
		))}
	</ul>
));
