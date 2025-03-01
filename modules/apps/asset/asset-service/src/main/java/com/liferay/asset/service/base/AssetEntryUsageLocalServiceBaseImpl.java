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

package com.liferay.asset.service.base;

import com.liferay.asset.model.AssetEntryUsage;
import com.liferay.asset.service.AssetEntryUsageLocalService;
import com.liferay.asset.service.persistence.AssetEntryUsageFinder;
import com.liferay.asset.service.persistence.AssetEntryUsagePersistence;
import com.liferay.exportimport.kernel.lar.ExportImportHelperUtil;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.SqlUpdate;
import com.liferay.portal.kernel.dao.jdbc.SqlUpdateFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DefaultActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiService;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalServiceImpl;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PortalUtil;

import java.io.Serializable;

import java.util.List;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Reference;

/**
 * Provides the base implementation for the asset entry usage local service.
 *
 * <p>
 * This implementation exists only as a container for the default service methods generated by ServiceBuilder. All custom service methods should be put in {@link com.liferay.asset.service.impl.AssetEntryUsageLocalServiceImpl}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see com.liferay.asset.service.impl.AssetEntryUsageLocalServiceImpl
 * @deprecated As of Mueller (7.2.x), replaced by {@link
 com.liferay.layout.service.impl.LayoutClassedModelUsageLocalServiceImpl}
 * @generated
 */
@Deprecated
public abstract class AssetEntryUsageLocalServiceBaseImpl
	extends BaseLocalServiceImpl
	implements AopService, AssetEntryUsageLocalService,
			   IdentifiableOSGiService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Use <code>AssetEntryUsageLocalService</code> via injection or a <code>org.osgi.util.tracker.ServiceTracker</code> or use <code>com.liferay.asset.service.AssetEntryUsageLocalServiceUtil</code>.
	 */

	/**
	 * Adds the asset entry usage to the database. Also notifies the appropriate model listeners.
	 *
	 * @param assetEntryUsage the asset entry usage
	 * @return the asset entry usage that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public AssetEntryUsage addAssetEntryUsage(AssetEntryUsage assetEntryUsage) {
		assetEntryUsage.setNew(true);

		return assetEntryUsagePersistence.update(assetEntryUsage);
	}

	/**
	 * Creates a new asset entry usage with the primary key. Does not add the asset entry usage to the database.
	 *
	 * @param assetEntryUsageId the primary key for the new asset entry usage
	 * @return the new asset entry usage
	 */
	@Override
	@Transactional(enabled = false)
	public AssetEntryUsage createAssetEntryUsage(long assetEntryUsageId) {
		return assetEntryUsagePersistence.create(assetEntryUsageId);
	}

	/**
	 * Deletes the asset entry usage with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param assetEntryUsageId the primary key of the asset entry usage
	 * @return the asset entry usage that was removed
	 * @throws PortalException if a asset entry usage with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	@Override
	public AssetEntryUsage deleteAssetEntryUsage(long assetEntryUsageId)
		throws PortalException {

		return assetEntryUsagePersistence.remove(assetEntryUsageId);
	}

	/**
	 * Deletes the asset entry usage from the database. Also notifies the appropriate model listeners.
	 *
	 * @param assetEntryUsage the asset entry usage
	 * @return the asset entry usage that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	@Override
	public AssetEntryUsage deleteAssetEntryUsage(
		AssetEntryUsage assetEntryUsage) {

		return assetEntryUsagePersistence.remove(assetEntryUsage);
	}

	@Override
	public DynamicQuery dynamicQuery() {
		Class<?> clazz = getClass();

		return DynamicQueryFactoryUtil.forClass(
			AssetEntryUsage.class, clazz.getClassLoader());
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return assetEntryUsagePersistence.findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.model.impl.AssetEntryUsageModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Override
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return assetEntryUsagePersistence.findWithDynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.model.impl.AssetEntryUsageModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Override
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return assetEntryUsagePersistence.findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return assetEntryUsagePersistence.countWithDynamicQuery(dynamicQuery);
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
		DynamicQuery dynamicQuery, Projection projection) {

		return assetEntryUsagePersistence.countWithDynamicQuery(
			dynamicQuery, projection);
	}

	@Override
	public AssetEntryUsage fetchAssetEntryUsage(long assetEntryUsageId) {
		return assetEntryUsagePersistence.fetchByPrimaryKey(assetEntryUsageId);
	}

	/**
	 * Returns the asset entry usage matching the UUID and group.
	 *
	 * @param uuid the asset entry usage's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset entry usage, or <code>null</code> if a matching asset entry usage could not be found
	 */
	@Override
	public AssetEntryUsage fetchAssetEntryUsageByUuidAndGroupId(
		String uuid, long groupId) {

		return assetEntryUsagePersistence.fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the asset entry usage with the primary key.
	 *
	 * @param assetEntryUsageId the primary key of the asset entry usage
	 * @return the asset entry usage
	 * @throws PortalException if a asset entry usage with the primary key could not be found
	 */
	@Override
	public AssetEntryUsage getAssetEntryUsage(long assetEntryUsageId)
		throws PortalException {

		return assetEntryUsagePersistence.findByPrimaryKey(assetEntryUsageId);
	}

	@Override
	public ActionableDynamicQuery getActionableDynamicQuery() {
		ActionableDynamicQuery actionableDynamicQuery =
			new DefaultActionableDynamicQuery();

		actionableDynamicQuery.setBaseLocalService(assetEntryUsageLocalService);
		actionableDynamicQuery.setClassLoader(getClassLoader());
		actionableDynamicQuery.setModelClass(AssetEntryUsage.class);

		actionableDynamicQuery.setPrimaryKeyPropertyName("assetEntryUsageId");

		return actionableDynamicQuery;
	}

	@Override
	public IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			new IndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setBaseLocalService(
			assetEntryUsageLocalService);
		indexableActionableDynamicQuery.setClassLoader(getClassLoader());
		indexableActionableDynamicQuery.setModelClass(AssetEntryUsage.class);

		indexableActionableDynamicQuery.setPrimaryKeyPropertyName(
			"assetEntryUsageId");

		return indexableActionableDynamicQuery;
	}

	protected void initActionableDynamicQuery(
		ActionableDynamicQuery actionableDynamicQuery) {

		actionableDynamicQuery.setBaseLocalService(assetEntryUsageLocalService);
		actionableDynamicQuery.setClassLoader(getClassLoader());
		actionableDynamicQuery.setModelClass(AssetEntryUsage.class);

		actionableDynamicQuery.setPrimaryKeyPropertyName("assetEntryUsageId");
	}

	@Override
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		final PortletDataContext portletDataContext) {

		final ExportActionableDynamicQuery exportActionableDynamicQuery =
			new ExportActionableDynamicQuery() {

				@Override
				public long performCount() throws PortalException {
					ManifestSummary manifestSummary =
						portletDataContext.getManifestSummary();

					StagedModelType stagedModelType = getStagedModelType();

					long modelAdditionCount = super.performCount();

					manifestSummary.addModelAdditionCount(
						stagedModelType, modelAdditionCount);

					long modelDeletionCount =
						ExportImportHelperUtil.getModelDeletionCount(
							portletDataContext, stagedModelType);

					manifestSummary.addModelDeletionCount(
						stagedModelType, modelDeletionCount);

					return modelAdditionCount;
				}

			};

		initActionableDynamicQuery(exportActionableDynamicQuery);

		exportActionableDynamicQuery.setAddCriteriaMethod(
			new ActionableDynamicQuery.AddCriteriaMethod() {

				@Override
				public void addCriteria(DynamicQuery dynamicQuery) {
					portletDataContext.addDateRangeCriteria(
						dynamicQuery, "modifiedDate");
				}

			});

		exportActionableDynamicQuery.setCompanyId(
			portletDataContext.getCompanyId());

		exportActionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<AssetEntryUsage>() {

				@Override
				public void performAction(AssetEntryUsage assetEntryUsage)
					throws PortalException {

					StagedModelDataHandlerUtil.exportStagedModel(
						portletDataContext, assetEntryUsage);
				}

			});
		exportActionableDynamicQuery.setStagedModelType(
			new StagedModelType(
				PortalUtil.getClassNameId(AssetEntryUsage.class.getName())));

		return exportActionableDynamicQuery;
	}

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return assetEntryUsagePersistence.create(
			((Long)primaryKeyObj).longValue());
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException {

		return assetEntryUsageLocalService.deleteAssetEntryUsage(
			(AssetEntryUsage)persistedModel);
	}

	public BasePersistence<AssetEntryUsage> getBasePersistence() {
		return assetEntryUsagePersistence;
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return assetEntryUsagePersistence.findByPrimaryKey(primaryKeyObj);
	}

	/**
	 * Returns all the asset entry usages matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset entry usages
	 * @param companyId the primary key of the company
	 * @return the matching asset entry usages, or an empty list if no matches were found
	 */
	@Override
	public List<AssetEntryUsage> getAssetEntryUsagesByUuidAndCompanyId(
		String uuid, long companyId) {

		return assetEntryUsagePersistence.findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of asset entry usages matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset entry usages
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of asset entry usages
	 * @param end the upper bound of the range of asset entry usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching asset entry usages, or an empty list if no matches were found
	 */
	@Override
	public List<AssetEntryUsage> getAssetEntryUsagesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetEntryUsage> orderByComparator) {

		return assetEntryUsagePersistence.findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the asset entry usage matching the UUID and group.
	 *
	 * @param uuid the asset entry usage's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset entry usage
	 * @throws PortalException if a matching asset entry usage could not be found
	 */
	@Override
	public AssetEntryUsage getAssetEntryUsageByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return assetEntryUsagePersistence.findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns a range of all the asset entry usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.model.impl.AssetEntryUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset entry usages
	 * @param end the upper bound of the range of asset entry usages (not inclusive)
	 * @return the range of asset entry usages
	 */
	@Override
	public List<AssetEntryUsage> getAssetEntryUsages(int start, int end) {
		return assetEntryUsagePersistence.findAll(start, end);
	}

	/**
	 * Returns the number of asset entry usages.
	 *
	 * @return the number of asset entry usages
	 */
	@Override
	public int getAssetEntryUsagesCount() {
		return assetEntryUsagePersistence.countAll();
	}

	/**
	 * Updates the asset entry usage in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * @param assetEntryUsage the asset entry usage
	 * @return the asset entry usage that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public AssetEntryUsage updateAssetEntryUsage(
		AssetEntryUsage assetEntryUsage) {

		return assetEntryUsagePersistence.update(assetEntryUsage);
	}

	@Override
	public Class<?>[] getAopInterfaces() {
		return new Class<?>[] {
			AssetEntryUsageLocalService.class, IdentifiableOSGiService.class,
			CTService.class, PersistedModelLocalService.class
		};
	}

	@Override
	public void setAopProxy(Object aopProxy) {
		assetEntryUsageLocalService = (AssetEntryUsageLocalService)aopProxy;
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return AssetEntryUsageLocalService.class.getName();
	}

	@Override
	public CTPersistence<AssetEntryUsage> getCTPersistence() {
		return assetEntryUsagePersistence;
	}

	@Override
	public Class<AssetEntryUsage> getModelClass() {
		return AssetEntryUsage.class;
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<AssetEntryUsage>, R, E>
				updateUnsafeFunction)
		throws E {

		return updateUnsafeFunction.apply(assetEntryUsagePersistence);
	}

	protected String getModelClassName() {
		return AssetEntryUsage.class.getName();
	}

	/**
	 * Performs a SQL query.
	 *
	 * @param sql the sql query
	 */
	protected void runSQL(String sql) {
		try {
			DataSource dataSource = assetEntryUsagePersistence.getDataSource();

			DB db = DBManagerUtil.getDB();

			sql = db.buildSQL(sql);
			sql = PortalUtil.transformSQL(sql);

			SqlUpdate sqlUpdate = SqlUpdateFactoryUtil.getSqlUpdate(
				dataSource, sql);

			sqlUpdate.update();
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	protected AssetEntryUsageLocalService assetEntryUsageLocalService;

	@Reference
	protected AssetEntryUsagePersistence assetEntryUsagePersistence;

	@Reference
	protected AssetEntryUsageFinder assetEntryUsageFinder;

	@Reference
	protected com.liferay.counter.kernel.service.CounterLocalService
		counterLocalService;

	@Reference
	protected com.liferay.portal.kernel.service.LayoutLocalService
		layoutLocalService;

}