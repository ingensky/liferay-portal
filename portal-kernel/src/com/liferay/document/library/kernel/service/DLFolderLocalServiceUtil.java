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

package com.liferay.document.library.kernel.service;

import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;

/**
 * Provides the local service utility for DLFolder. This utility wraps
 * <code>com.liferay.portlet.documentlibrary.service.impl.DLFolderLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see DLFolderLocalService
 * @generated
 */
public class DLFolderLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portlet.documentlibrary.service.impl.DLFolderLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static void addDLFileEntryTypeDLFolder(
		long fileEntryTypeId,
		com.liferay.document.library.kernel.model.DLFolder dlFolder) {

		getService().addDLFileEntryTypeDLFolder(fileEntryTypeId, dlFolder);
	}

	public static void addDLFileEntryTypeDLFolder(
		long fileEntryTypeId, long folderId) {

		getService().addDLFileEntryTypeDLFolder(fileEntryTypeId, folderId);
	}

	public static void addDLFileEntryTypeDLFolders(
		long fileEntryTypeId,
		java.util.List<com.liferay.document.library.kernel.model.DLFolder>
			dlFolders) {

		getService().addDLFileEntryTypeDLFolders(fileEntryTypeId, dlFolders);
	}

	public static void addDLFileEntryTypeDLFolders(
		long fileEntryTypeId, long[] folderIds) {

		getService().addDLFileEntryTypeDLFolders(fileEntryTypeId, folderIds);
	}

	/**
	 * Adds the document library folder to the database. Also notifies the appropriate model listeners.
	 *
	 * @param dlFolder the document library folder
	 * @return the document library folder that was added
	 */
	public static com.liferay.document.library.kernel.model.DLFolder
		addDLFolder(
			com.liferay.document.library.kernel.model.DLFolder dlFolder) {

		return getService().addDLFolder(dlFolder);
	}

	public static com.liferay.document.library.kernel.model.DLFolder addFolder(
			long userId, long groupId, long repositoryId, boolean mountPoint,
			long parentFolderId, String name, String description,
			boolean hidden,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().addFolder(
			userId, groupId, repositoryId, mountPoint, parentFolderId, name,
			description, hidden, serviceContext);
	}

	public static void clearDLFileEntryTypeDLFolders(long fileEntryTypeId) {
		getService().clearDLFileEntryTypeDLFolders(fileEntryTypeId);
	}

	/**
	 * Creates a new document library folder with the primary key. Does not add the document library folder to the database.
	 *
	 * @param folderId the primary key for the new document library folder
	 * @return the new document library folder
	 */
	public static com.liferay.document.library.kernel.model.DLFolder
		createDLFolder(long folderId) {

		return getService().createDLFolder(folderId);
	}

	/**
	 * @throws PortalException
	 */
	public static com.liferay.portal.kernel.model.PersistedModel
			createPersistedModel(java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteAllByGroup(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().deleteAllByGroup(groupId);
	}

	public static void deleteAllByRepository(long repositoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().deleteAllByRepository(repositoryId);
	}

	public static void deleteDLFileEntryTypeDLFolder(
		long fileEntryTypeId,
		com.liferay.document.library.kernel.model.DLFolder dlFolder) {

		getService().deleteDLFileEntryTypeDLFolder(fileEntryTypeId, dlFolder);
	}

	public static void deleteDLFileEntryTypeDLFolder(
		long fileEntryTypeId, long folderId) {

		getService().deleteDLFileEntryTypeDLFolder(fileEntryTypeId, folderId);
	}

	public static void deleteDLFileEntryTypeDLFolders(
		long fileEntryTypeId,
		java.util.List<com.liferay.document.library.kernel.model.DLFolder>
			dlFolders) {

		getService().deleteDLFileEntryTypeDLFolders(fileEntryTypeId, dlFolders);
	}

	public static void deleteDLFileEntryTypeDLFolders(
		long fileEntryTypeId, long[] folderIds) {

		getService().deleteDLFileEntryTypeDLFolders(fileEntryTypeId, folderIds);
	}

	/**
	 * Deletes the document library folder from the database. Also notifies the appropriate model listeners.
	 *
	 * @param dlFolder the document library folder
	 * @return the document library folder that was removed
	 */
	public static com.liferay.document.library.kernel.model.DLFolder
		deleteDLFolder(
			com.liferay.document.library.kernel.model.DLFolder dlFolder) {

		return getService().deleteDLFolder(dlFolder);
	}

	/**
	 * Deletes the document library folder with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param folderId the primary key of the document library folder
	 * @return the document library folder that was removed
	 * @throws PortalException if a document library folder with the primary key could not be found
	 */
	public static com.liferay.document.library.kernel.model.DLFolder
			deleteDLFolder(long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().deleteDLFolder(folderId);
	}

	public static com.liferay.document.library.kernel.model.DLFolder
			deleteFolder(
				com.liferay.document.library.kernel.model.DLFolder dlFolder)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().deleteFolder(dlFolder);
	}

	public static com.liferay.document.library.kernel.model.DLFolder
			deleteFolder(
				com.liferay.document.library.kernel.model.DLFolder dlFolder,
				boolean includeTrashedEntries)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().deleteFolder(dlFolder, includeTrashedEntries);
	}

	public static com.liferay.document.library.kernel.model.DLFolder
			deleteFolder(long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().deleteFolder(folderId);
	}

	public static com.liferay.document.library.kernel.model.DLFolder
			deleteFolder(long folderId, boolean includeTrashedEntries)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().deleteFolder(folderId, includeTrashedEntries);
	}

	public static com.liferay.document.library.kernel.model.DLFolder
			deleteFolder(
				long userId, long folderId, boolean includeTrashedEntries)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().deleteFolder(
			userId, folderId, includeTrashedEntries);
	}

	/**
	 * @throws PortalException
	 */
	public static com.liferay.portal.kernel.model.PersistedModel
			deletePersistedModel(
				com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static com.liferay.portal.kernel.dao.orm.DynamicQuery
		dynamicQuery() {

		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return getService().dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static com.liferay.document.library.kernel.model.DLFolder
		fetchDLFolder(long folderId) {

		return getService().fetchDLFolder(folderId);
	}

	/**
	 * Returns the document library folder matching the UUID and group.
	 *
	 * @param uuid the document library folder's UUID
	 * @param groupId the primary key of the group
	 * @return the matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	public static com.liferay.document.library.kernel.model.DLFolder
		fetchDLFolderByUuidAndGroupId(String uuid, long groupId) {

		return getService().fetchDLFolderByUuidAndGroupId(uuid, groupId);
	}

	public static com.liferay.document.library.kernel.model.DLFolder
		fetchFolder(long folderId) {

		return getService().fetchFolder(folderId);
	}

	public static com.liferay.document.library.kernel.model.DLFolder
		fetchFolder(long groupId, long parentFolderId, String name) {

		return getService().fetchFolder(groupId, parentFolderId, name);
	}

	public static com.liferay.document.library.kernel.model.DLFolder
		fetchFolder(String uuid, long groupId) {

		return getService().fetchFolder(uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFolder> getCompanyFolders(
			long companyId, int start, int end) {

		return getService().getCompanyFolders(companyId, start, end);
	}

	public static int getCompanyFoldersCount(long companyId) {
		return getService().getCompanyFoldersCount(companyId);
	}

	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFolder>
			getDLFileEntryTypeDLFolders(long fileEntryTypeId) {

		return getService().getDLFileEntryTypeDLFolders(fileEntryTypeId);
	}

	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFolder>
			getDLFileEntryTypeDLFolders(
				long fileEntryTypeId, int start, int end) {

		return getService().getDLFileEntryTypeDLFolders(
			fileEntryTypeId, start, end);
	}

	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFolder>
			getDLFileEntryTypeDLFolders(
				long fileEntryTypeId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.document.library.kernel.model.DLFolder>
						orderByComparator) {

		return getService().getDLFileEntryTypeDLFolders(
			fileEntryTypeId, start, end, orderByComparator);
	}

	public static int getDLFileEntryTypeDLFoldersCount(long fileEntryTypeId) {
		return getService().getDLFileEntryTypeDLFoldersCount(fileEntryTypeId);
	}

	/**
	 * Returns the fileEntryTypeIds of the document library file entry types associated with the document library folder.
	 *
	 * @param folderId the folderId of the document library folder
	 * @return long[] the fileEntryTypeIds of document library file entry types associated with the document library folder
	 */
	public static long[] getDLFileEntryTypePrimaryKeys(long folderId) {
		return getService().getDLFileEntryTypePrimaryKeys(folderId);
	}

	/**
	 * Returns the document library folder with the primary key.
	 *
	 * @param folderId the primary key of the document library folder
	 * @return the document library folder
	 * @throws PortalException if a document library folder with the primary key could not be found
	 */
	public static com.liferay.document.library.kernel.model.DLFolder
			getDLFolder(long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getDLFolder(folderId);
	}

	/**
	 * Returns the document library folder matching the UUID and group.
	 *
	 * @param uuid the document library folder's UUID
	 * @param groupId the primary key of the group
	 * @return the matching document library folder
	 * @throws PortalException if a matching document library folder could not be found
	 */
	public static com.liferay.document.library.kernel.model.DLFolder
			getDLFolderByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getDLFolderByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the document library folders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of document library folders
	 */
	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFolder> getDLFolders(
			int start, int end) {

		return getService().getDLFolders(start, end);
	}

	/**
	 * Returns all the document library folders matching the UUID and company.
	 *
	 * @param uuid the UUID of the document library folders
	 * @param companyId the primary key of the company
	 * @return the matching document library folders, or an empty list if no matches were found
	 */
	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFolder>
			getDLFoldersByUuidAndCompanyId(String uuid, long companyId) {

		return getService().getDLFoldersByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of document library folders matching the UUID and company.
	 *
	 * @param uuid the UUID of the document library folders
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching document library folders, or an empty list if no matches were found
	 */
	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFolder>
			getDLFoldersByUuidAndCompanyId(
				String uuid, long companyId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.document.library.kernel.model.DLFolder>
						orderByComparator) {

		return getService().getDLFoldersByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of document library folders.
	 *
	 * @return the number of document library folders
	 */
	public static int getDLFoldersCount() {
		return getService().getDLFoldersCount();
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static java.util.List<Object> getFileEntriesAndFileShortcuts(
		long groupId, long folderId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition) {

		return getService().getFileEntriesAndFileShortcuts(
			groupId, folderId, queryDefinition);
	}

	public static int getFileEntriesAndFileShortcutsCount(
		long groupId, long folderId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition) {

		return getService().getFileEntriesAndFileShortcutsCount(
			groupId, folderId, queryDefinition);
	}

	public static com.liferay.document.library.kernel.model.DLFolder getFolder(
			long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFolder(folderId);
	}

	public static com.liferay.document.library.kernel.model.DLFolder getFolder(
			long groupId, long parentFolderId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFolder(groupId, parentFolderId, name);
	}

	public static long getFolderId(long companyId, long folderId) {
		return getService().getFolderId(companyId, folderId);
	}

	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFolder> getFolders(
			long groupId, long parentFolderId) {

		return getService().getFolders(groupId, parentFolderId);
	}

	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFolder> getFolders(
			long groupId, long parentFolderId, boolean includeMountfolders) {

		return getService().getFolders(
			groupId, parentFolderId, includeMountfolders);
	}

	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFolder> getFolders(
			long groupId, long parentFolderId, boolean includeMountfolders,
			int status, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.document.library.kernel.model.DLFolder> obc) {

		return getService().getFolders(
			groupId, parentFolderId, includeMountfolders, status, start, end,
			obc);
	}

	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFolder> getFolders(
			long groupId, long parentFolderId, boolean includeMountfolders,
			int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.document.library.kernel.model.DLFolder> obc) {

		return getService().getFolders(
			groupId, parentFolderId, includeMountfolders, start, end, obc);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #getFolders(long, long, boolean, int, int,
	 OrderByComparator)}
	 */
	@Deprecated
	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFolder> getFolders(
			long groupId, long parentFolderId, int status,
			boolean includeMountfolders, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.document.library.kernel.model.DLFolder> obc) {

		return getService().getFolders(
			groupId, parentFolderId, status, includeMountfolders, start, end,
			obc);
	}

	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFolder> getFolders(
			long groupId, long parentFolderId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.document.library.kernel.model.DLFolder> obc) {

		return getService().getFolders(
			groupId, parentFolderId, start, end, obc);
	}

	public static java.util.List<Object>
		getFoldersAndFileEntriesAndFileShortcuts(
			long groupId, long folderId, String[] mimeTypes,
			boolean includeMountFolders,
			com.liferay.portal.kernel.dao.orm.QueryDefinition<?>
				queryDefinition) {

		return getService().getFoldersAndFileEntriesAndFileShortcuts(
			groupId, folderId, mimeTypes, includeMountFolders, queryDefinition);
	}

	public static int getFoldersAndFileEntriesAndFileShortcutsCount(
		long groupId, long folderId, String[] mimeTypes,
		boolean includeMountFolders,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition) {

		return getService().getFoldersAndFileEntriesAndFileShortcutsCount(
			groupId, folderId, mimeTypes, includeMountFolders, queryDefinition);
	}

	public static int getFoldersCount(long groupId, long parentFolderId) {
		return getService().getFoldersCount(groupId, parentFolderId);
	}

	public static int getFoldersCount(
		long groupId, long parentFolderId, boolean includeMountfolders) {

		return getService().getFoldersCount(
			groupId, parentFolderId, includeMountfolders);
	}

	public static int getFoldersCount(
		long groupId, long parentFolderId, boolean includeMountfolders,
		int status) {

		return getService().getFoldersCount(
			groupId, parentFolderId, includeMountfolders, status);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #getFoldersCount(long, long, boolean, int)}
	 */
	@Deprecated
	public static int getFoldersCount(
		long groupId, long parentFolderId, int status,
		boolean includeMountfolders) {

		return getService().getFoldersCount(
			groupId, parentFolderId, status, includeMountfolders);
	}

	public static java.util.List<Long> getGroupFolderIds(
		long groupId, long parentFolderId) {

		return getService().getGroupFolderIds(groupId, parentFolderId);
	}

	public static void getGroupSubfolderIds(
		java.util.List<Long> folderIds, long groupId, long folderId) {

		getService().getGroupSubfolderIds(folderIds, groupId, folderId);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static com.liferay.document.library.kernel.model.DLFolder
			getMountFolder(long repositoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getMountFolder(repositoryId);
	}

	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFolder> getMountFolders(
			long groupId, long parentFolderId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.document.library.kernel.model.DLFolder> obc) {

		return getService().getMountFolders(
			groupId, parentFolderId, start, end, obc);
	}

	public static int getMountFoldersCount(long groupId, long parentFolderId) {
		return getService().getMountFoldersCount(groupId, parentFolderId);
	}

	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFolder>
			getNoAssetFolders() {

		return getService().getNoAssetFolders();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static com.liferay.portal.kernel.model.PersistedModel
			getPersistedModel(java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static java.util.List<Long> getRepositoryFolderIds(
		long repositoryId, long parentFolderId) {

		return getService().getRepositoryFolderIds(
			repositoryId, parentFolderId);
	}

	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFolder>
			getRepositoryFolders(long repositoryId, int start, int end) {

		return getService().getRepositoryFolders(repositoryId, start, end);
	}

	public static int getRepositoryFoldersCount(long repositoryId) {
		return getService().getRepositoryFoldersCount(repositoryId);
	}

	public static void getRepositorySubfolderIds(
		java.util.List<Long> folderIds, long repositoryId, long folderId) {

		getService().getRepositorySubfolderIds(
			folderIds, repositoryId, folderId);
	}

	public static String getUniqueFolderName(
		String uuid, long groupId, long parentFolderId, String name,
		int count) {

		return getService().getUniqueFolderName(
			uuid, groupId, parentFolderId, name, count);
	}

	public static boolean hasDLFileEntryTypeDLFolder(
		long fileEntryTypeId, long folderId) {

		return getService().hasDLFileEntryTypeDLFolder(
			fileEntryTypeId, folderId);
	}

	public static boolean hasDLFileEntryTypeDLFolders(long fileEntryTypeId) {
		return getService().hasDLFileEntryTypeDLFolders(fileEntryTypeId);
	}

	public static boolean hasFolderLock(long userId, long folderId) {
		return getService().hasFolderLock(userId, folderId);
	}

	public static boolean hasInheritableLock(long folderId) {
		return getService().hasInheritableLock(folderId);
	}

	public static com.liferay.portal.kernel.lock.Lock lockFolder(
			long userId, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().lockFolder(userId, folderId);
	}

	public static com.liferay.portal.kernel.lock.Lock lockFolder(
			long userId, long folderId, String owner, boolean inheritable,
			long expirationTime)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().lockFolder(
			userId, folderId, owner, inheritable, expirationTime);
	}

	public static com.liferay.document.library.kernel.model.DLFolder moveFolder(
			long userId, long folderId, long parentFolderId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().moveFolder(
			userId, folderId, parentFolderId, serviceContext);
	}

	public static void rebuildTree(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().rebuildTree(companyId);
	}

	public static void rebuildTree(
			long companyId, long parentFolderId, String parentTreePath,
			boolean reindex)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().rebuildTree(
			companyId, parentFolderId, parentTreePath, reindex);
	}

	public static void setDLFileEntryTypeDLFolders(
		long fileEntryTypeId, long[] folderIds) {

		getService().setDLFileEntryTypeDLFolders(fileEntryTypeId, folderIds);
	}

	public static void unlockFolder(
			long groupId, long parentFolderId, String name, String lockUuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().unlockFolder(groupId, parentFolderId, name, lockUuid);
	}

	public static void unlockFolder(long folderId, String lockUuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().unlockFolder(folderId, lockUuid);
	}

	/**
	 * Updates the document library folder in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * @param dlFolder the document library folder
	 * @return the document library folder that was updated
	 */
	public static com.liferay.document.library.kernel.model.DLFolder
		updateDLFolder(
			com.liferay.document.library.kernel.model.DLFolder dlFolder) {

		return getService().updateDLFolder(dlFolder);
	}

	public static com.liferay.document.library.kernel.model.DLFolder
			updateFolder(
				long folderId, long parentFolderId, String name,
				String description, long defaultFileEntryTypeId,
				java.util.List<Long> fileEntryTypeIds, int restrictionType,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().updateFolder(
			folderId, parentFolderId, name, description, defaultFileEntryTypeId,
			fileEntryTypeIds, restrictionType, serviceContext);
	}

	public static com.liferay.document.library.kernel.model.DLFolder
			updateFolder(
				long folderId, String name, String description,
				long defaultFileEntryTypeId,
				java.util.List<Long> fileEntryTypeIds, int restrictionType,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().updateFolder(
			folderId, name, description, defaultFileEntryTypeId,
			fileEntryTypeIds, restrictionType, serviceContext);
	}

	public static com.liferay.document.library.kernel.model.DLFolder
			updateFolderAndFileEntryTypes(
				long userId, long folderId, long parentFolderId, String name,
				String description, long defaultFileEntryTypeId,
				java.util.List<Long> fileEntryTypeIds, int restrictionType,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().updateFolderAndFileEntryTypes(
			userId, folderId, parentFolderId, name, description,
			defaultFileEntryTypeId, fileEntryTypeIds, restrictionType,
			serviceContext);
	}

	public static void updateLastPostDate(
			long folderId, java.util.Date lastPostDate)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().updateLastPostDate(folderId, lastPostDate);
	}

	public static com.liferay.document.library.kernel.model.DLFolder
			updateStatus(
				long userId, long folderId, int status,
				java.util.Map<String, java.io.Serializable> workflowContext,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().updateStatus(
			userId, folderId, status, workflowContext, serviceContext);
	}

	public static boolean verifyInheritableLock(long folderId, String lockUuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().verifyInheritableLock(folderId, lockUuid);
	}

	public static DLFolderLocalService getService() {
		if (_service == null) {
			_service = (DLFolderLocalService)PortalBeanLocatorUtil.locate(
				DLFolderLocalService.class.getName());
		}

		return _service;
	}

	private static DLFolderLocalService _service;

}