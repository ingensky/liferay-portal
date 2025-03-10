/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import React, {useCallback, useContext, useMemo, useState} from 'react';

import EmptyState from '../../../../shared/components/empty-state/EmptyState.es';
import RetryButton from '../../../../shared/components/list/RetryButton.es';
import LoadingState from '../../../../shared/components/loading/LoadingState.es';
import PromisesResolver from '../../../../shared/components/promises-resolver/PromisesResolver.es';
import {useToaster} from '../../../../shared/components/toaster/hooks/useToaster.es';
import {useFetch} from '../../../../shared/hooks/useFetch.es';
import {usePost} from '../../../../shared/hooks/usePost.es';
import {InstanceListContext} from '../../InstanceListPageProvider.es';
import {ModalContext} from '../ModalProvider.es';
import UpdateDueDateStep from './UpdateDueDateStep.es';

const ErrorView = ({onClick}) => {
	return (
		<EmptyState
			actionButton={<RetryButton onClick={onClick} />}
			className="border-0 pb-5 pt-5 sheet"
			hideAnimation={true}
			message={Liferay.Language.get(
				'there-was-a-problem-retrieving-data-please-try-reloading-the-page'
			)}
			messageClassName="small"
			type="error"
		/>
	);
};

const LoadingView = () => {
	return <LoadingState className="border-0 pb-6 pt-7" />;
};

const SingleUpdateDueDateModal = () => {
	const [errorToast, setErrorToast] = useState(false);
	const [retry, setRetry] = useState(0);
	const [sendingPost, setSendingPost] = useState(false);

	const toaster = useToaster();

	const {
		setUpdateDueDate,
		setVisibleModal,
		updateDueDate,
		visibleModal,
	} = useContext(ModalContext);
	const {selectedInstance, setSelectedItem, setSelectedItems} = useContext(
		InstanceListContext
	);

	const {comment, dueDate} = updateDueDate;

	const {observer, onClose} = useModal({
		onClose: () => {
			setSelectedItem({});
			setSelectedItems([]);
			setVisibleModal('');
			setUpdateDueDate({
				comment: undefined,
				dueDate: undefined,
			});
		},
	});

	const {data, fetchData} = useFetch({
		admin: true,
		params: {completed: false, page: 1, pageSize: 1},
		url: `/workflow-instances/${selectedInstance.id}/workflow-tasks`,
	});

	const {dateDue, id: taskId} = useMemo(
		() => (data.items && data.items[0] ? data.items[0] : {}),
		[data]
	);

	const {postData} = usePost({
		admin: true,
		body: {comment, dueDate},
		url: `/workflow-tasks/${taskId}/update-due-date`,
	});

	const handleDone = useCallback(() => {
		if (dueDate) {
			setSendingPost(true);
			setErrorToast(false);

			postData()
				.then(() => {
					onClose();
					toaster.success(
						Liferay.Language.get(
							'the-due-date-for-this-task-has-been-updated'
						)
					);
					setSendingPost(false);
					setErrorToast(false);
				})
				.catch(() => {
					setErrorToast(
						`${Liferay.Language.get(
							'your-request-has-failed'
						)} ${Liferay.Language.get('select-done-to-retry')}`
					);
					setSendingPost(false);
				});
		}
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [postData]);

	const promises = useMemo(() => {
		setErrorToast(false);

		if (selectedInstance.id && visibleModal === 'updateDueDate') {
			return [
				fetchData().catch(err => {
					setErrorToast(
						Liferay.Language.get('your-request-has-failed')
					);

					return Promise.reject(err);
				}),
			];
		}
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [fetchData, retry, visibleModal]);

	return (
		<>
			<PromisesResolver promises={promises}>
				{visibleModal === 'updateDueDate' && (
					<ClayModal
						data-testid="updateDueDateModal"
						observer={observer}
						size="md"
					>
						<ClayModal.Header>
							{Liferay.Language.get('update-task-due-date')}
						</ClayModal.Header>

						{errorToast && (
							<ClayAlert
								className="mb-0"
								data-testid="alertError"
								displayType="danger"
								title={Liferay.Language.get('error')}
							>
								{errorToast}
							</ClayAlert>
						)}

						<PromisesResolver.Pending>
							<LoadingView />
						</PromisesResolver.Pending>

						<PromisesResolver.Rejected>
							<ErrorView
								onClick={() => {
									setRetry(retry => retry + 1);
								}}
							/>
						</PromisesResolver.Rejected>

						<PromisesResolver.Resolved>
							<UpdateDueDateStep dueDate={dateDue} />
						</PromisesResolver.Resolved>

						<ClayModal.Footer
							last={
								<>
									<ClayButton
										className="mr-3"
										data-testid="cancelButton"
										disabled={sendingPost}
										displayType="secondary"
										onClick={onClose}
									>
										{Liferay.Language.get('cancel')}
									</ClayButton>

									<ClayButton
										data-testid="doneButton"
										disabled={sendingPost || !dueDate}
										onClick={handleDone}
									>
										{Liferay.Language.get('done')}
									</ClayButton>
								</>
							}
						/>
					</ClayModal>
				)}
			</PromisesResolver>
		</>
	);
};

export {SingleUpdateDueDateModal};
