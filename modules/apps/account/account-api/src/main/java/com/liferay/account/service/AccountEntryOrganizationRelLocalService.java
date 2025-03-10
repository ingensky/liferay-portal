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

package com.liferay.account.service;

import com.liferay.account.model.AccountEntryOrganizationRel;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for AccountEntryOrganizationRel. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see AccountEntryOrganizationRelLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface AccountEntryOrganizationRelLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link AccountEntryOrganizationRelLocalServiceUtil} to access the account entry organization rel local service. Add custom service methods to <code>com.liferay.account.service.impl.AccountEntryOrganizationRelLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */

	/**
	 * Adds the account entry organization rel to the database. Also notifies the appropriate model listeners.
	 *
	 * @param accountEntryOrganizationRel the account entry organization rel
	 * @return the account entry organization rel that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public AccountEntryOrganizationRel addAccountEntryOrganizationRel(
		AccountEntryOrganizationRel accountEntryOrganizationRel);

	public AccountEntryOrganizationRel addAccountEntryOrganizationRel(
			long accountEntryId, long organizationId)
		throws PortalException;

	public void addAccountEntryOrganizationRels(
			long accountEntryId, long[] organizationIds)
		throws PortalException;

	/**
	 * Creates a new account entry organization rel with the primary key. Does not add the account entry organization rel to the database.
	 *
	 * @param accountEntryOrganizationRelId the primary key for the new account entry organization rel
	 * @return the new account entry organization rel
	 */
	@Transactional(enabled = false)
	public AccountEntryOrganizationRel createAccountEntryOrganizationRel(
		long accountEntryOrganizationRelId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the account entry organization rel from the database. Also notifies the appropriate model listeners.
	 *
	 * @param accountEntryOrganizationRel the account entry organization rel
	 * @return the account entry organization rel that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public AccountEntryOrganizationRel deleteAccountEntryOrganizationRel(
		AccountEntryOrganizationRel accountEntryOrganizationRel);

	/**
	 * Deletes the account entry organization rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param accountEntryOrganizationRelId the primary key of the account entry organization rel
	 * @return the account entry organization rel that was removed
	 * @throws PortalException if a account entry organization rel with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public AccountEntryOrganizationRel deleteAccountEntryOrganizationRel(
			long accountEntryOrganizationRelId)
		throws PortalException;

	public void deleteAccountEntryOrganizationRel(
			long accountEntryId, long organizationId)
		throws PortalException;

	public void deleteAccountEntryOrganizationRels(
			long accountEntryId, long[] organizationIds)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountEntryOrganizationRelModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountEntryOrganizationRelModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AccountEntryOrganizationRel fetchAccountEntryOrganizationRel(
		long accountEntryOrganizationRelId);

	/**
	 * Returns the account entry organization rel with the primary key.
	 *
	 * @param accountEntryOrganizationRelId the primary key of the account entry organization rel
	 * @return the account entry organization rel
	 * @throws PortalException if a account entry organization rel with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AccountEntryOrganizationRel getAccountEntryOrganizationRel(
			long accountEntryOrganizationRelId)
		throws PortalException;

	/**
	 * Returns a range of all the account entry organization rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountEntryOrganizationRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of account entry organization rels
	 * @param end the upper bound of the range of account entry organization rels (not inclusive)
	 * @return the range of account entry organization rels
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AccountEntryOrganizationRel> getAccountEntryOrganizationRels(
		int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AccountEntryOrganizationRel> getAccountEntryOrganizationRels(
		long accountEntryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AccountEntryOrganizationRel>
		getAccountEntryOrganizationRelsByOrganizationId(long organizationId);

	/**
	 * Returns the number of account entry organization rels.
	 *
	 * @return the number of account entry organization rels
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAccountEntryOrganizationRelsCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAccountEntryOrganizationRelsCount(long accountEntryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasAccountEntryOrganizationRel(
		long accountEntryId, long organizationId);

	/**
	 * Creates an AccountEntryOrganizationRel for each given organizationId,
	 * unless it already exists, and removes existing
	 * AccountEntryOrganizationRels if their organizationId is not present in
	 * the given organizationIds.
	 *
	 * @param accountEntryId
	 * @param organizationIds
	 * @throws PortalException
	 * @review
	 */
	public void setAccountEntryOrganizationRels(
			long accountEntryId, long[] organizationIds)
		throws PortalException;

	/**
	 * Updates the account entry organization rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * @param accountEntryOrganizationRel the account entry organization rel
	 * @return the account entry organization rel that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public AccountEntryOrganizationRel updateAccountEntryOrganizationRel(
		AccountEntryOrganizationRel accountEntryOrganizationRel);

}