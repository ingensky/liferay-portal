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

package com.liferay.portal.workflow.kaleo.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link KaleoActionLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoActionLocalService
 * @generated
 */
public class KaleoActionLocalServiceWrapper
	implements KaleoActionLocalService,
			   ServiceWrapper<KaleoActionLocalService> {

	public KaleoActionLocalServiceWrapper(
		KaleoActionLocalService kaleoActionLocalService) {

		_kaleoActionLocalService = kaleoActionLocalService;
	}

	/**
	 * Adds the kaleo action to the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoAction the kaleo action
	 * @return the kaleo action that was added
	 */
	@Override
	public com.liferay.portal.workflow.kaleo.model.KaleoAction addKaleoAction(
		com.liferay.portal.workflow.kaleo.model.KaleoAction kaleoAction) {

		return _kaleoActionLocalService.addKaleoAction(kaleoAction);
	}

	@Override
	public com.liferay.portal.workflow.kaleo.model.KaleoAction addKaleoAction(
			String kaleoClassName, long kaleoClassPK, long kaleoDefinitionId,
			long kaleoDefinitionVersionId, String kaleoNodeName,
			com.liferay.portal.workflow.kaleo.definition.Action action,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoActionLocalService.addKaleoAction(
			kaleoClassName, kaleoClassPK, kaleoDefinitionId,
			kaleoDefinitionVersionId, kaleoNodeName, action, serviceContext);
	}

	/**
	 * Creates a new kaleo action with the primary key. Does not add the kaleo action to the database.
	 *
	 * @param kaleoActionId the primary key for the new kaleo action
	 * @return the new kaleo action
	 */
	@Override
	public com.liferay.portal.workflow.kaleo.model.KaleoAction
		createKaleoAction(long kaleoActionId) {

		return _kaleoActionLocalService.createKaleoAction(kaleoActionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoActionLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public void deleteCompanyKaleoActions(long companyId) {
		_kaleoActionLocalService.deleteCompanyKaleoActions(companyId);
	}

	/**
	 * Deletes the kaleo action from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoAction the kaleo action
	 * @return the kaleo action that was removed
	 */
	@Override
	public com.liferay.portal.workflow.kaleo.model.KaleoAction
		deleteKaleoAction(
			com.liferay.portal.workflow.kaleo.model.KaleoAction kaleoAction) {

		return _kaleoActionLocalService.deleteKaleoAction(kaleoAction);
	}

	/**
	 * Deletes the kaleo action with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoActionId the primary key of the kaleo action
	 * @return the kaleo action that was removed
	 * @throws PortalException if a kaleo action with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.workflow.kaleo.model.KaleoAction
			deleteKaleoAction(long kaleoActionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoActionLocalService.deleteKaleoAction(kaleoActionId);
	}

	@Override
	public void deleteKaleoDefinitionVersionKaleoActions(
		long kaleoDefinitionVersionId) {

		_kaleoActionLocalService.deleteKaleoDefinitionVersionKaleoActions(
			kaleoDefinitionVersionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoActionLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _kaleoActionLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _kaleoActionLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoActionModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _kaleoActionLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoActionModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _kaleoActionLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _kaleoActionLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _kaleoActionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.workflow.kaleo.model.KaleoAction fetchKaleoAction(
		long kaleoActionId) {

		return _kaleoActionLocalService.fetchKaleoAction(kaleoActionId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _kaleoActionLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _kaleoActionLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the kaleo action with the primary key.
	 *
	 * @param kaleoActionId the primary key of the kaleo action
	 * @return the kaleo action
	 * @throws PortalException if a kaleo action with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.workflow.kaleo.model.KaleoAction getKaleoAction(
			long kaleoActionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoActionLocalService.getKaleoAction(kaleoActionId);
	}

	/**
	 * Returns a range of all the kaleo actions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoActionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kaleo actions
	 * @param end the upper bound of the range of kaleo actions (not inclusive)
	 * @return the range of kaleo actions
	 */
	@Override
	public java.util.List<com.liferay.portal.workflow.kaleo.model.KaleoAction>
		getKaleoActions(int start, int end) {

		return _kaleoActionLocalService.getKaleoActions(start, end);
	}

	@Override
	public java.util.List<com.liferay.portal.workflow.kaleo.model.KaleoAction>
		getKaleoActions(
			long companyId, String kaleoClassName, long kaleoClassPK) {

		return _kaleoActionLocalService.getKaleoActions(
			companyId, kaleoClassName, kaleoClassPK);
	}

	@Override
	public java.util.List<com.liferay.portal.workflow.kaleo.model.KaleoAction>
		getKaleoActions(
			long companyId, String kaleoClassName, long kaleoClassPK,
			String executionType) {

		return _kaleoActionLocalService.getKaleoActions(
			companyId, kaleoClassName, kaleoClassPK, executionType);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #getKaleoActions(long, String, long)}
	 */
	@Deprecated
	@Override
	public java.util.List<com.liferay.portal.workflow.kaleo.model.KaleoAction>
		getKaleoActions(String kaleoClassName, long kaleoClassPK) {

		return _kaleoActionLocalService.getKaleoActions(
			kaleoClassName, kaleoClassPK);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #getKaleoActions(long, String, long, String)}
	 */
	@Deprecated
	@Override
	public java.util.List<com.liferay.portal.workflow.kaleo.model.KaleoAction>
		getKaleoActions(
			String kaleoClassName, long kaleoClassPK, String executionType) {

		return _kaleoActionLocalService.getKaleoActions(
			kaleoClassName, kaleoClassPK, executionType);
	}

	/**
	 * Returns the number of kaleo actions.
	 *
	 * @return the number of kaleo actions
	 */
	@Override
	public int getKaleoActionsCount() {
		return _kaleoActionLocalService.getKaleoActionsCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _kaleoActionLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoActionLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the kaleo action in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoAction the kaleo action
	 * @return the kaleo action that was updated
	 */
	@Override
	public com.liferay.portal.workflow.kaleo.model.KaleoAction
		updateKaleoAction(
			com.liferay.portal.workflow.kaleo.model.KaleoAction kaleoAction) {

		return _kaleoActionLocalService.updateKaleoAction(kaleoAction);
	}

	@Override
	public KaleoActionLocalService getWrappedService() {
		return _kaleoActionLocalService;
	}

	@Override
	public void setWrappedService(
		KaleoActionLocalService kaleoActionLocalService) {

		_kaleoActionLocalService = kaleoActionLocalService;
	}

	private KaleoActionLocalService _kaleoActionLocalService;

}