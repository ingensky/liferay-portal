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

import classNames from 'classnames';
import {useIsMounted} from 'frontend-js-react-web';
import {debounce} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useEffect, useState} from 'react';

import {updateFragmentEntryLinkContent} from '../../actions/index';
import {BACKGROUND_IMAGE_FRAGMENT_ENTRY_PROCESSOR} from '../../config/constants/backgroundImageFragmentEntryProcessor';
import {EDITABLE_FLOATING_TOOLBAR_BUTTONS} from '../../config/constants/editableFloatingToolbarButtons';
import {EDITABLE_FRAGMENT_ENTRY_PROCESSOR} from '../../config/constants/editableFragmentEntryProcessor';
import Processors from '../../processors/index';
import selectCanUpdateLayoutContent from '../../selectors/selectCanUpdateLayoutContent';
import selectPrefixedSegmentsExperienceId from '../../selectors/selectPrefixedSegmentsExperienceId';
import selectSegmentsExperienceId from '../../selectors/selectSegmentsExperienceId';
import FragmentService from '../../services/FragmentService';
import {useDispatch, useSelector} from '../../store/index';
import {useGetFieldValue} from '../ControlsIdConverterContext';
import UnsafeHTML from '../UnsafeHTML';
import {
	useEditableProcessorUniqueId,
	useSetEditableProcessorUniqueId,
} from './EditableProcessorContext';
import FragmentContentDecoration from './FragmentContentDecoration';
import FragmentContentFloatingToolbar from './FragmentContentFloatingToolbar';
import FragmentContentInteractionsFilter from './FragmentContentInteractionsFilter';
import FragmentContentProcessor from './FragmentContentProcessor';
import getAllEditables from './getAllEditables';
import getEditableElementId from './getEditableElementId';
import getEditableUniqueId from './getEditableUniqueId';
import resolveEditableValue from './resolveEditableValue';

const FragmentContent = React.forwardRef(
	({fragmentEntryLinkId, itemId}, ref) => {
		const dispatch = useDispatch();
		const element = ref.current;
		const isMounted = useIsMounted();
		const editableProcessorUniqueId = useEditableProcessorUniqueId();
		const setEditableProcessorUniqueId = useSetEditableProcessorUniqueId();
		const canUpdateLayoutContent = useSelector(
			selectCanUpdateLayoutContent
		);

		const getFieldValue = useGetFieldValue();

		const [editableElements, setEditableElements] = useState([]);

		const updateEditableElements = (parent = element) => {
			setEditableElements(parent ? getAllEditables(parent) : []);
		};

		const languageId = useSelector(state => state.languageId);

		const prefixedSegmentsExperienceId = useSelector(
			selectPrefixedSegmentsExperienceId
		);
		const segmentsExperienceId = useSelector(selectSegmentsExperienceId);

		const defaultContent = useSelector(state =>
			state.fragmentEntryLinks[fragmentEntryLinkId]
				? state.fragmentEntryLinks[fragmentEntryLinkId].content
				: ''
		);

		const editableValues = useSelector(state =>
			state.fragmentEntryLinks[fragmentEntryLinkId]
				? state.fragmentEntryLinks[fragmentEntryLinkId].editableValues
				: {}
		);

		const [content, setContent] = useState(defaultContent);

		useEffect(() => {
			FragmentService.renderFragmentEntryLinkContent({
				fragmentEntryLinkId,
				onNetworkStatus: dispatch,
				segmentsExperienceId,
			}).then(({content}) =>
				dispatch(
					updateFragmentEntryLinkContent({
						content,
						fragmentEntryLinkId,
					})
				)
			);
		}, [dispatch, fragmentEntryLinkId, segmentsExperienceId]);

		useEffect(() => {
			let element = document.createElement('div');
			element.innerHTML = defaultContent;

			const updateContent = debounce(() => {
				if (isMounted() && element) {
					setContent(element.innerHTML);
				}
			}, 50);

			if (!editableProcessorUniqueId) {
				Array.from(
					element.querySelectorAll('[data-lfr-background-image-id]')
				).map(editable => {
					resolveEditableValue(
						editableValues,
						editable.dataset.lfrBackgroundImageId,
						BACKGROUND_IMAGE_FRAGMENT_ENTRY_PROCESSOR,
						languageId,
						prefixedSegmentsExperienceId,
						getFieldValue
					).then(([value, _editableConfig]) => {
						const processor = Processors['background-image'];

						processor.render(editable, value);
						updateContent();
					});
				});

				Array.from(element.querySelectorAll('lfr-editable')).forEach(
					editable => {
						editable.classList.add('page-editor__editable');

						resolveEditableValue(
							editableValues,
							editable.getAttribute('id'),
							EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
							languageId,
							prefixedSegmentsExperienceId,
							getFieldValue
						).then(([value, editableConfig]) => {
							const processor =
								Processors[editable.getAttribute('type')] ||
								Processors.fallback;

							processor.render(editable, value, editableConfig);
							updateContent();
						});
					}
				);
				updateContent();
			}

			return () => {
				element = null;
			};
		}, [
			defaultContent,
			editableProcessorUniqueId,
			editableValues,
			getFieldValue,
			isMounted,
			languageId,
			prefixedSegmentsExperienceId,
		]);

		const onFloatingToolbarButtonClick = (buttonId, editableId) => {
			if (buttonId === EDITABLE_FLOATING_TOOLBAR_BUTTONS.edit.id) {
				setEditableProcessorUniqueId(
					getEditableUniqueId(fragmentEntryLinkId, editableId)
				);
			}
		};

		return (
			<>
				<FragmentContentInteractionsFilter
					editableElements={editableElements}
					fragmentEntryLinkId={fragmentEntryLinkId}
					itemId={itemId}
				>
					<UnsafeHTML
						className={classNames('page-editor__fragment-content', {
							'page-editor__fragment-content--portlet-topper-hidden': !canUpdateLayoutContent,
						})}
						contentRef={ref}
						markup={content}
						onRender={updateEditableElements}
					/>
				</FragmentContentInteractionsFilter>

				{canUpdateLayoutContent && (
					<FragmentContentFloatingToolbar
						editableElements={editableElements}
						fragmentEntryLinkId={fragmentEntryLinkId}
						onButtonClick={onFloatingToolbarButtonClick}
					/>
				)}

				<FragmentContentProcessor
					element={element}
					fragmentEntryLinkId={fragmentEntryLinkId}
				/>

				{editableElements.map(editableElement => (
					<FragmentContentDecoration
						editableElement={editableElement}
						element={element}
						fragmentEntryLinkId={fragmentEntryLinkId}
						itemId={itemId}
						key={getEditableElementId(editableElement)}
					/>
				))}
			</>
		);
	}
);

FragmentContent.propTypes = {
	fragmentEntryLinkId: PropTypes.string.isRequired,
	itemId: PropTypes.string.isRequired,
};

export default FragmentContent;
