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

import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import React, {useEffect} from 'react';
import {useDrag} from 'react-dnd';
import {getEmptyImage} from 'react-dnd-html5-backend';

import {useSelectItem} from '../../../app/components/Controls';
import {LAYOUT_DATA_ITEM_TYPES} from '../../../app/config/constants/layoutDataItemTypes';
import {useDispatch, useSelector} from '../../../app/store/index';
import addItem from '../../../app/thunks/addItem';
import Collapse from '../../../common/components/Collapse';

const CollectionDisplayCard = () => {
	const dispatch = useDispatch();
	const store = useSelector(state => state);
	const selectItem = useSelectItem();

	const [, drag, preview] = useDrag({
		end(_item, monitor) {
			const result = monitor.getDropResult();

			if (!result) {
				return;
			}

			const {parentId, position} = result;

			if (parentId) {
				dispatch(
					addItem({
						itemType: LAYOUT_DATA_ITEM_TYPES.collection,
						parentItemId: parentId,
						position,
						selectItem,
						store,
					})
				);
			}
		},
		item: {
			name: Liferay.Language.get('collection-display'),
			type: LAYOUT_DATA_ITEM_TYPES.collection,
		},
	});

	useEffect(() => {
		preview(getEmptyImage(), {captureDraggingState: true});
	}, [preview]);

	return (
		<div
			className={classNames(
				'page-editor__fragments__fragment-card',
				'card',
				'card-interactive',
				'card-interactive-secondary',
				'selector-button',
				'overflow-hidden'
			)}
			ref={drag}
		>
			<div className="page-editor__fragments__fragment-card-no-preview">
				<ClayIcon symbol="list" />
			</div>

			<div className="card-body">
				<div className="card-row">
					<div className="autofit-col autofit-col-expand autofit-row-center">
						<div className="card-title text-truncate">
							{Liferay.Language.get('collection-display')}
						</div>
					</div>
				</div>
			</div>
		</div>
	);
};

export default function CollectionDisplay() {
	return (
		<>
			<Collapse
				label={Liferay.Language.get('collection-display')}
				open={false}
			>
				<div className="d-flex flex-wrap justify-content-between">
					<CollectionDisplayCard />
				</div>
			</Collapse>
		</>
	);
}
