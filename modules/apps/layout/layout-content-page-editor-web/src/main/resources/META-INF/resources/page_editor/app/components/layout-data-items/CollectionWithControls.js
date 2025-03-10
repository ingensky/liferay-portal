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

import React from 'react';

import {
	LayoutDataPropTypes,
	getLayoutDataItemPropTypes,
} from '../../../prop-types/index';
import {LAYOUT_DATA_FLOATING_TOOLBAR_BUTTONS} from '../../config/constants/layoutDataFloatingToolbarButtons';
import selectCanUpdateLayoutContent from '../../selectors/selectCanUpdateLayoutContent';
import {useSelector} from '../../store/index';
import Topper from '../Topper';
import FloatingToolbar from '../floating-toolbar/FloatingToolbar';
import Collection from './Collection';

const CollectionWithControls = React.forwardRef(
	({children, item, layoutData}, ref) => {
		const canUpdateLayoutContent = useSelector(
			selectCanUpdateLayoutContent
		);

		const buttons = [];

		buttons.push(
			LAYOUT_DATA_FLOATING_TOOLBAR_BUTTONS.collectionConfiguration
		);

		const content = (
			<>
				<Collection item={item} ref={ref}>
					{children}
				</Collection>

				<FloatingToolbar buttons={buttons} item={item} itemRef={ref} />
			</>
		);

		return canUpdateLayoutContent ? (
			<Topper item={item} itemRef={ref} layoutData={layoutData}>
				{() => content}
			</Topper>
		) : (
			content
		);
	}
);

CollectionWithControls.propTypes = {
	item: getLayoutDataItemPropTypes().isRequired,
	layoutData: LayoutDataPropTypes.isRequired,
};

export default CollectionWithControls;
