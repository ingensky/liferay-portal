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

package com.liferay.knowledge.base.service.impl;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetLink;
import com.liferay.asset.kernel.model.AssetLinkConstants;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.knowledge.base.configuration.KBGroupServiceConfiguration;
import com.liferay.knowledge.base.constants.AdminActivityKeys;
import com.liferay.knowledge.base.constants.KBArticleConstants;
import com.liferay.knowledge.base.constants.KBConstants;
import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.exception.KBArticleContentException;
import com.liferay.knowledge.base.exception.KBArticleParentException;
import com.liferay.knowledge.base.exception.KBArticlePriorityException;
import com.liferay.knowledge.base.exception.KBArticleSourceURLException;
import com.liferay.knowledge.base.exception.KBArticleStatusException;
import com.liferay.knowledge.base.exception.KBArticleTitleException;
import com.liferay.knowledge.base.exception.KBArticleUrlTitleException;
import com.liferay.knowledge.base.exception.NoSuchArticleException;
import com.liferay.knowledge.base.internal.importer.KBArchiveFactory;
import com.liferay.knowledge.base.internal.importer.KBArticleImporter;
import com.liferay.knowledge.base.internal.util.AdminSubscriptionSenderFactory;
import com.liferay.knowledge.base.internal.util.KBArticleDiffUtil;
import com.liferay.knowledge.base.internal.util.KBArticleLocalSiblingNavigationHelper;
import com.liferay.knowledge.base.internal.util.KBCommentUtil;
import com.liferay.knowledge.base.internal.util.KBSectionEscapeUtil;
import com.liferay.knowledge.base.internal.util.KnowledgeBaseConstants;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.base.KBArticleLocalServiceBaseImpl;
import com.liferay.knowledge.base.util.KnowledgeBaseUtil;
import com.liferay.knowledge.base.util.comparator.KBArticlePriorityComparator;
import com.liferay.knowledge.base.util.comparator.KBArticleVersionComparator;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.dao.orm.Conjunction;
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.Disjunction;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Junction;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.systemevent.SystemEventHierarchyEntryThreadLocal;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SubscriptionSender;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.view.count.ViewCountManager;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;
import com.liferay.subscription.model.Subscription;
import com.liferay.subscription.service.SubscriptionLocalService;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Peter Shin
 * @author Brian Wing Shun Chan
 * @author Edward Han
 */
@Component(
	property = "model.class.name=com.liferay.knowledge.base.model.KBArticle",
	service = AopService.class
)
public class KBArticleLocalServiceImpl extends KBArticleLocalServiceBaseImpl {

	@Override
	public FileEntry addAttachment(
			long userId, long resourcePrimKey, String fileName,
			InputStream inputStream, String mimeType)
		throws PortalException {

		KBArticle kbArticle = kbArticleLocalService.getLatestKBArticle(
			resourcePrimKey, WorkflowConstants.STATUS_ANY);

		return _portletFileRepository.addPortletFileEntry(
			kbArticle.getGroupId(), userId, KBArticle.class.getName(),
			kbArticle.getClassPK(), KBConstants.SERVICE_NAME,
			kbArticle.getAttachmentsFolderId(), inputStream, fileName, mimeType,
			false);
	}

	@Override
	public KBArticle addKBArticle(
			long userId, long parentResourceClassNameId,
			long parentResourcePrimKey, String title, String urlTitle,
			String content, String description, String sourceURL,
			String[] sections, String[] selectedFileNames,
			ServiceContext serviceContext)
		throws PortalException {

		// KB article

		User user = userLocalService.getUser(userId);

		long groupId = serviceContext.getScopeGroupId();
		urlTitle = normalizeUrlTitle(urlTitle);
		double priority = getPriority(groupId, parentResourcePrimKey);

		validate(title, content, sourceURL);
		validateParent(parentResourceClassNameId, parentResourcePrimKey);

		long kbFolderId = KnowledgeBaseUtil.getKBFolderId(
			parentResourceClassNameId, parentResourcePrimKey);

		urlTitle = StringUtil.toLowerCase(urlTitle);

		validateUrlTitle(groupId, kbFolderId, urlTitle);

		long kbArticleId = counterLocalService.increment();

		long resourcePrimKey = counterLocalService.increment();

		long rootResourcePrimKey = getRootResourcePrimKey(
			resourcePrimKey, parentResourceClassNameId, parentResourcePrimKey);

		KBArticle kbArticle = kbArticlePersistence.create(kbArticleId);

		kbArticle.setUuid(serviceContext.getUuid());
		kbArticle.setResourcePrimKey(resourcePrimKey);
		kbArticle.setGroupId(groupId);
		kbArticle.setCompanyId(user.getCompanyId());
		kbArticle.setUserId(user.getUserId());
		kbArticle.setUserName(user.getFullName());
		kbArticle.setRootResourcePrimKey(rootResourcePrimKey);
		kbArticle.setParentResourceClassNameId(parentResourceClassNameId);
		kbArticle.setParentResourcePrimKey(parentResourcePrimKey);
		kbArticle.setKbFolderId(kbFolderId);
		kbArticle.setVersion(KBArticleConstants.DEFAULT_VERSION);
		kbArticle.setTitle(title);
		kbArticle.setUrlTitle(
			getUniqueUrlTitle(
				groupId, kbFolderId, kbArticleId, title, urlTitle));
		kbArticle.setContent(content);
		kbArticle.setDescription(description);
		kbArticle.setPriority(priority);
		kbArticle.setSections(
			StringUtil.merge(KBSectionEscapeUtil.escapeSections(sections)));
		kbArticle.setLatest(true);
		kbArticle.setMain(false);
		kbArticle.setSourceURL(sourceURL);
		kbArticle.setStatus(WorkflowConstants.STATUS_DRAFT);
		kbArticle.setExpandoBridgeAttributes(serviceContext);

		kbArticle = kbArticlePersistence.update(kbArticle);

		// Resources

		if (serviceContext.isAddGroupPermissions() ||
			serviceContext.isAddGuestPermissions()) {

			addKBArticleResources(
				kbArticle, serviceContext.isAddGroupPermissions(),
				serviceContext.isAddGuestPermissions());
		}
		else {
			addKBArticleResources(
				kbArticle, serviceContext.getModelPermissions());
		}

		// Asset

		updateKBArticleAsset(
			userId, kbArticle, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds());

		// Attachments

		addKBArticleAttachments(userId, kbArticle, selectedFileNames);

		// Workflow

		return WorkflowHandlerRegistryUtil.startWorkflowInstance(
			user.getCompanyId(), groupId, userId, KBArticle.class.getName(),
			resourcePrimKey, kbArticle, serviceContext, Collections.emptyMap());
	}

	@Override
	public void addKBArticleResources(
			KBArticle kbArticle, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		resourceLocalService.addResources(
			kbArticle.getCompanyId(), kbArticle.getGroupId(),
			kbArticle.getUserId(), KBArticle.class.getName(),
			kbArticle.getResourcePrimKey(), false, addGroupPermissions,
			addGuestPermissions);
	}

	@Override
	public void addKBArticleResources(
			KBArticle kbArticle, ModelPermissions modelPermissions)
		throws PortalException {

		resourceLocalService.addModelResources(
			kbArticle.getCompanyId(), kbArticle.getGroupId(),
			kbArticle.getUserId(), KBArticle.class.getName(),
			kbArticle.getResourcePrimKey(), modelPermissions);
	}

	@Override
	public void addKBArticleResources(
			long kbArticleId, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		KBArticle kbArticle = kbArticlePersistence.findByPrimaryKey(
			kbArticleId);

		addKBArticleResources(
			kbArticle, addGroupPermissions, addGuestPermissions);
	}

	@Override
	public int addKBArticlesMarkdown(
			long userId, long groupId, long parentKbFolderId, String fileName,
			boolean prioritizeByNumericalPrefix, InputStream inputStream,
			ServiceContext serviceContext)
		throws PortalException {

		boolean workflowEnabled = WorkflowThreadLocal.isEnabled();

		try {
			WorkflowThreadLocal.setEnabled(false);

			KBArticleImporter kbArticleImporter = new KBArticleImporter(
				_kbArchiveFactory, this, _portal, _dlURLHelper);

			return kbArticleImporter.processZipFile(
				userId, groupId, parentKbFolderId, prioritizeByNumericalPrefix,
				inputStream, serviceContext);
		}
		finally {
			WorkflowThreadLocal.setEnabled(workflowEnabled);
		}
	}

	@Override
	public void addTempAttachment(
			long groupId, long userId, String fileName, String tempFolderName,
			InputStream inputStream, String mimeType)
		throws PortalException {

		TempFileEntryUtil.addTempFileEntry(
			groupId, userId, tempFolderName, fileName, inputStream, mimeType);
	}

	@Override
	public void deleteGroupKBArticles(long groupId) throws PortalException {

		// KB articles

		deleteKBArticles(groupId, KBFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		// Subscriptions

		Group group = groupLocalService.getGroup(groupId);

		List<Subscription> subscriptions =
			_subscriptionLocalService.getSubscriptions(
				group.getCompanyId(), KBArticle.class.getName(), groupId);

		for (Subscription subscription : subscriptions) {
			unsubscribeGroupKBArticles(subscription.getUserId(), groupId);
		}
	}

	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public KBArticle deleteKBArticle(KBArticle kbArticle)
		throws PortalException {

		// Child KB articles

		deleteKBArticles(
			kbArticle.getGroupId(), kbArticle.getResourcePrimKey());

		// Resources

		resourceLocalService.deleteResource(
			kbArticle.getCompanyId(), KBArticle.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, kbArticle.getResourcePrimKey());

		// KB articles

		kbArticlePersistence.removeByResourcePrimKey(
			kbArticle.getResourcePrimKey());

		// KB comments

		KBCommentUtil.deleteKBComments(
			KBArticle.class.getName(), classNameLocalService,
			kbArticle.getResourcePrimKey(), kbCommentPersistence);

		// Asset

		deleteAssets(kbArticle);

		// Expando

		expandoRowLocalService.deleteRows(kbArticle.getKbArticleId());

		// Ratings

		ratingsStatsLocalService.deleteStats(
			KBArticle.class.getName(), kbArticle.getResourcePrimKey());

		// Social

		socialActivityLocalService.deleteActivities(
			KBArticle.class.getName(), kbArticle.getResourcePrimKey());

		// Indexer

		Indexer<KBArticle> indexer = _indexerRegistry.getIndexer(
			KBArticle.class);

		indexer.delete(kbArticle);

		// Attachments

		_portletFileRepository.deletePortletFolder(
			kbArticle.getAttachmentsFolderId());

		// Subscriptions

		deleteSubscriptions(kbArticle);

		// View count

		_viewCountManager.deleteViewCount(
			kbArticle.getCompanyId(),
			classNameLocalService.getClassNameId(KBArticle.class),
			kbArticle.getPrimaryKey());

		// Workflow

		workflowInstanceLinkLocalService.deleteWorkflowInstanceLinks(
			kbArticle.getCompanyId(), kbArticle.getGroupId(),
			KBArticle.class.getName(), kbArticle.getResourcePrimKey());

		return kbArticle;
	}

	@Override
	public KBArticle deleteKBArticle(long resourcePrimKey)
		throws PortalException {

		KBArticle kbArticle = getLatestKBArticle(
			resourcePrimKey, WorkflowConstants.STATUS_ANY);

		return kbArticleLocalService.deleteKBArticle(kbArticle);
	}

	@Override
	public void deleteKBArticles(long groupId, long parentResourcePrimKey)
		throws PortalException {

		List<KBArticle> childKBArticles = getKBArticles(
			groupId, parentResourcePrimKey, WorkflowConstants.STATUS_ANY,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		for (KBArticle childKBArticle : childKBArticles) {
			kbArticleLocalService.deleteKBArticle(childKBArticle);
		}
	}

	@Override
	public void deleteKBArticles(long[] resourcePrimKeys)
		throws PortalException {

		List<KBArticle> kbArticles = getKBArticles(
			resourcePrimKeys, WorkflowConstants.STATUS_ANY, null);

		for (KBArticle kbArticle : kbArticles) {
			kbArticleLocalService.deleteKBArticle(kbArticle);
		}
	}

	@Override
	public void deleteTempAttachment(
			long groupId, long userId, String fileName, String tempFolderName)
		throws PortalException {

		TempFileEntryUtil.deleteTempFileEntry(
			groupId, userId, tempFolderName, fileName);
	}

	@Override
	public KBArticle fetchFirstChildKBArticle(
		long groupId, long parentResourcePrimKey) {

		return kbArticlePersistence.fetchByG_P_L_First(
			groupId, parentResourcePrimKey, true,
			new KBArticlePriorityComparator(true));
	}

	@Override
	public KBArticle fetchKBArticle(
		long resourcePrimKey, long groupId, int version) {

		return kbArticlePersistence.fetchByR_G_V(
			resourcePrimKey, groupId, version);
	}

	@Override
	public KBArticle fetchKBArticleByUrlTitle(
		long groupId, long kbFolderId, String urlTitle) {

		urlTitle = StringUtil.replaceFirst(
			urlTitle, CharPool.SLASH, StringPool.BLANK);

		KBArticle kbArticle = fetchLatestKBArticleByUrlTitle(
			groupId, kbFolderId, urlTitle, WorkflowConstants.STATUS_APPROVED);

		if (kbArticle == null) {
			kbArticle = fetchLatestKBArticleByUrlTitle(
				groupId, kbFolderId, urlTitle,
				WorkflowConstants.STATUS_PENDING);
		}

		return kbArticle;
	}

	@Override
	public KBArticle fetchKBArticleByUrlTitle(
		long groupId, String kbFolderUrlTitle, String urlTitle) {

		urlTitle = StringUtil.replaceFirst(
			urlTitle, CharPool.SLASH, StringPool.BLANK);

		List<KBArticle> kbArticles = kbArticleFinder.findByUrlTitle(
			groupId, kbFolderUrlTitle, urlTitle, _STATUSES, 0, 1);

		if (kbArticles.isEmpty()) {
			return null;
		}

		return kbArticles.get(0);
	}

	@Override
	public KBArticle fetchLatestKBArticle(long resourcePrimKey, int status) {
		if (status == WorkflowConstants.STATUS_ANY) {
			return kbArticlePersistence.fetchByResourcePrimKey_First(
				resourcePrimKey, new KBArticleVersionComparator());
		}

		return kbArticlePersistence.fetchByR_S_First(
			resourcePrimKey, status, new KBArticleVersionComparator());
	}

	@Override
	public KBArticle fetchLatestKBArticle(long resourcePrimKey, long groupId) {
		return kbArticlePersistence.fetchByR_G_L_First(
			resourcePrimKey, groupId, true, null);
	}

	@Override
	public KBArticle fetchLatestKBArticleByUrlTitle(
		long groupId, long kbFolderId, String urlTitle, int status) {

		urlTitle = StringUtil.replaceFirst(
			urlTitle, CharPool.SLASH, StringPool.BLANK);

		List<KBArticle> kbArticles = null;

		OrderByComparator<KBArticle> orderByComparator =
			new KBArticleVersionComparator();

		if (status == WorkflowConstants.STATUS_ANY) {
			kbArticles = kbArticlePersistence.findByG_KBFI_UT(
				groupId, kbFolderId, urlTitle, 0, 1, orderByComparator);
		}
		else {
			kbArticles = kbArticlePersistence.findByG_KBFI_UT_ST(
				groupId, kbFolderId, urlTitle, status, 0, 1, orderByComparator);
		}

		if (kbArticles.isEmpty()) {
			return null;
		}

		return kbArticles.get(0);
	}

	@Override
	public List<KBArticle> getAllDescendantKBArticles(
		long resourcePrimKey, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getAllDescendantKBArticles(
			resourcePrimKey, status, orderByComparator, false);
	}

	@Override
	public List<KBArticle> getCompanyKBArticles(
		long companyId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return kbArticlePersistence.findByC_L(
				companyId, true, start, end, orderByComparator);
		}
		else if (status == WorkflowConstants.STATUS_APPROVED) {
			return kbArticlePersistence.findByC_M(
				companyId, true, start, end, orderByComparator);
		}

		return kbArticlePersistence.findByC_S(
			companyId, status, start, end, orderByComparator);
	}

	@Override
	public int getCompanyKBArticlesCount(long companyId, int status) {
		if (status == WorkflowConstants.STATUS_ANY) {
			return kbArticlePersistence.countByC_L(companyId, true);
		}
		else if (status == WorkflowConstants.STATUS_APPROVED) {
			return kbArticlePersistence.countByC_M(companyId, true);
		}

		return kbArticlePersistence.countByC_S(companyId, status);
	}

	@Override
	public List<KBArticle> getGroupKBArticles(
		long groupId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return kbArticlePersistence.findByG_L(
				groupId, true, start, end, orderByComparator);
		}
		else if (status == WorkflowConstants.STATUS_APPROVED) {
			return kbArticlePersistence.findByG_M(
				groupId, true, start, end, orderByComparator);
		}

		return kbArticlePersistence.findByG_S(
			groupId, status, start, end, orderByComparator);
	}

	@Override
	public int getGroupKBArticlesCount(long groupId, int status) {
		if (status == WorkflowConstants.STATUS_ANY) {
			return kbArticlePersistence.countByG_L(groupId, true);
		}
		else if (status == WorkflowConstants.STATUS_APPROVED) {
			return kbArticlePersistence.countByG_M(groupId, true);
		}

		return kbArticlePersistence.countByG_S(groupId, status);
	}

	@Override
	public KBArticle getKBArticle(long resourcePrimKey, int version)
		throws PortalException {

		return kbArticlePersistence.findByR_V(resourcePrimKey, version);
	}

	@Override
	public List<KBArticle> getKBArticleAndAllDescendantKBArticles(
		long resourcePrimKey, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getAllDescendantKBArticles(
			resourcePrimKey, status, orderByComparator, true);
	}

	@Override
	public KBArticle getKBArticleByUrlTitle(
			long groupId, long kbFolderId, String urlTitle)
		throws PortalException {

		urlTitle = StringUtil.replaceFirst(
			urlTitle, CharPool.SLASH, StringPool.BLANK);

		// Get the latest KB article that is approved, if none are approved, get
		// the latest unapproved KB article

		KBArticle kbArticle = fetchKBArticleByUrlTitle(
			groupId, kbFolderId, urlTitle);

		if (kbArticle == null) {
			throw new NoSuchArticleException(
				StringBundler.concat(
					"No KBArticle exists with the key {groupId=", groupId,
					", kbFolderId=", kbFolderId, ", urlTitle=", urlTitle, "}"));
		}

		return kbArticle;
	}

	@Override
	public KBArticle getKBArticleByUrlTitle(
			long groupId, String kbFolderUrlTitle, String urlTitle)
		throws PortalException {

		urlTitle = StringUtil.replaceFirst(
			urlTitle, CharPool.SLASH, StringPool.BLANK);

		KBArticle kbArticle = fetchKBArticleByUrlTitle(
			groupId, kbFolderUrlTitle, urlTitle);

		if (kbArticle == null) {
			throw new NoSuchArticleException(
				StringBundler.concat(
					"No KBArticle with the key {groupId=", groupId,
					", urlTitle=", urlTitle,
					"} found in a folder with URL title ", kbFolderUrlTitle));
		}

		return kbArticle;
	}

	@Override
	public List<KBArticle> getKBArticles(
		long groupId, long parentResourcePrimKey, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return kbArticlePersistence.findByG_P_L(
				groupId, parentResourcePrimKey, true, start, end,
				orderByComparator);
		}
		else if (status == WorkflowConstants.STATUS_APPROVED) {
			return kbArticlePersistence.findByG_P_M(
				groupId, parentResourcePrimKey, true, start, end,
				orderByComparator);
		}

		return kbArticlePersistence.findByG_P_S(
			groupId, parentResourcePrimKey, status, start, end,
			orderByComparator);
	}

	@Override
	public List<KBArticle> getKBArticles(
		long[] resourcePrimKeys, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		List<KBArticle> kbArticles = new ArrayList<>();

		Long[][] params = {ArrayUtil.toArray(resourcePrimKeys)};

		while ((params = KnowledgeBaseUtil.getParams(params[0])) != null) {
			List<KBArticle> curKBArticles = null;

			if (status == WorkflowConstants.STATUS_ANY) {
				curKBArticles = kbArticlePersistence.findByR_L(
					ArrayUtil.toArray(params[1]), true);
			}
			else if (status == WorkflowConstants.STATUS_APPROVED) {
				curKBArticles = kbArticlePersistence.findByR_M(
					ArrayUtil.toArray(params[1]), true);
			}
			else {
				curKBArticles = kbArticlePersistence.findByR_S(
					ArrayUtil.toArray(params[1]), status);
			}

			kbArticles.addAll(curKBArticles);
		}

		if (orderByComparator != null) {
			kbArticles = ListUtil.sort(kbArticles, orderByComparator);
		}
		else {
			kbArticles = KnowledgeBaseUtil.sort(resourcePrimKeys, kbArticles);
		}

		return Collections.unmodifiableList(kbArticles);
	}

	@Override
	public int getKBArticlesCount(
		long groupId, long parentResourcePrimKey, int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return kbArticlePersistence.countByG_P_L(
				groupId, parentResourcePrimKey, true);
		}
		else if (status == WorkflowConstants.STATUS_APPROVED) {
			return kbArticlePersistence.countByG_P_M(
				groupId, parentResourcePrimKey, true);
		}

		return kbArticlePersistence.countByG_P_S(
			groupId, parentResourcePrimKey, status);
	}

	@Override
	public List<KBArticle> getKBArticleVersions(
		long resourcePrimKey, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return kbArticlePersistence.findByResourcePrimKey(
				resourcePrimKey, start, end, orderByComparator);
		}

		return kbArticlePersistence.findByR_S(
			resourcePrimKey, status, start, end, orderByComparator);
	}

	@Override
	public int getKBArticleVersionsCount(long resourcePrimKey, int status) {
		if (status == WorkflowConstants.STATUS_ANY) {
			return kbArticlePersistence.countByResourcePrimKey(resourcePrimKey);
		}

		return kbArticlePersistence.countByR_S(resourcePrimKey, status);
	}

	@Override
	public List<KBArticle> getKBFolderKBArticles(
		long groupId, long kbFolderId) {

		return kbArticlePersistence.findByG_KBFI_L(groupId, kbFolderId, true);
	}

	@Override
	public int getKBFolderKBArticlesCount(
		long groupId, long kbFolderId, int status) {

		return kbArticlePersistence.countByG_KBFI_S(
			groupId, kbFolderId, status);
	}

	@Override
	public KBArticle getLatestKBArticle(long resourcePrimKey, int status)
		throws PortalException {

		if (status == WorkflowConstants.STATUS_ANY) {
			return kbArticlePersistence.findByResourcePrimKey_First(
				resourcePrimKey, new KBArticleVersionComparator());
		}

		return kbArticlePersistence.findByR_S_First(
			resourcePrimKey, status, new KBArticleVersionComparator());
	}

	@Override
	public KBArticle getLatestKBArticleByUrlTitle(
			long groupId, long kbFolderId, String urlTitle, int status)
		throws PortalException {

		urlTitle = StringUtil.replaceFirst(
			urlTitle, CharPool.SLASH, StringPool.BLANK);

		KBArticle latestKBArticle = fetchLatestKBArticleByUrlTitle(
			groupId, kbFolderId, urlTitle, status);

		if (latestKBArticle == null) {
			throw new NoSuchArticleException(
				StringBundler.concat(
					"No KBArticle exists with the key {groupId=", groupId,
					", kbFolderId=", kbFolderId, ", urlTitle=", urlTitle,
					", status=", status, "}"));
		}

		return latestKBArticle;
	}

	@Override
	public KBArticle[] getPreviousAndNextKBArticles(long kbArticleId)
		throws PortalException {

		KBArticleLocalSiblingNavigationHelper
			kbArticleLocalSiblingNavigationHelper =
				new KBArticleLocalSiblingNavigationHelper(kbArticlePersistence);

		return kbArticleLocalSiblingNavigationHelper.
			getPreviousAndNextKBArticles(kbArticleId);
	}

	@Override
	public List<KBArticle> getSectionsKBArticles(
		long groupId, String[] sections, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		String[] array = KBSectionEscapeUtil.escapeSections(sections);

		for (int i = 0; i < array.length; i++) {
			array[i] = StringUtil.quote(array[i], StringPool.PERCENT);
		}

		if (status == WorkflowConstants.STATUS_ANY) {
			return kbArticlePersistence.findByG_S_L(
				groupId, array, true, start, end, orderByComparator);
		}
		else if (status == WorkflowConstants.STATUS_APPROVED) {
			return kbArticlePersistence.findByG_S_M(
				groupId, array, true, start, end, orderByComparator);
		}

		return kbArticlePersistence.findByG_S_S(
			groupId, array, status, start, end, orderByComparator);
	}

	@Override
	public int getSectionsKBArticlesCount(
		long groupId, String[] sections, int status) {

		String[] array = KBSectionEscapeUtil.escapeSections(sections);

		for (int i = 0; i < array.length; i++) {
			array[i] = StringUtil.quote(array[i], StringPool.PERCENT);
		}

		if (status == WorkflowConstants.STATUS_ANY) {
			return kbArticlePersistence.countByG_S_L(groupId, array, true);
		}
		else if (status == WorkflowConstants.STATUS_APPROVED) {
			return kbArticlePersistence.countByG_S_M(groupId, array, true);
		}

		return kbArticlePersistence.countByG_S_S(groupId, array, status);
	}

	@Override
	public String[] getTempAttachmentNames(
			long groupId, long userId, String tempFolderName)
		throws PortalException {

		return TempFileEntryUtil.getTempFileNames(
			groupId, userId, tempFolderName);
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public void incrementViewCount(
			long userId, long resourcePrimKey, int increment)
		throws PortalException {

		KBArticle kbArticle = getLatestKBArticle(
			resourcePrimKey, WorkflowConstants.STATUS_ANY);
		long classNameId = classNameLocalService.getClassNameId(
			KBArticle.class);

		_viewCountManager.incrementViewCount(
			kbArticle.getCompanyId(), classNameId, kbArticle.getPrimaryKey(),
			increment);

		if (kbArticle.isApproved() || kbArticle.isFirstVersion()) {
			return;
		}

		kbArticle = getLatestKBArticle(
			resourcePrimKey, WorkflowConstants.STATUS_APPROVED);

		_viewCountManager.incrementViewCount(
			kbArticle.getCompanyId(), classNameId, kbArticle.getPrimaryKey(),
			increment);
	}

	@Override
	public void moveKBArticle(
			long userId, long resourcePrimKey, long parentResourceClassNameId,
			long parentResourcePrimKey, double priority)
		throws PortalException {

		KBArticle kbArticle = getLatestKBArticle(
			resourcePrimKey, WorkflowConstants.STATUS_ANY);

		if (kbArticle.getResourcePrimKey() == parentResourcePrimKey) {
			return;
		}

		validateParent(
			kbArticle, parentResourceClassNameId, parentResourcePrimKey);
		validateParentStatus(
			parentResourceClassNameId, parentResourcePrimKey,
			kbArticle.getStatus());

		validate(priority);

		updatePermissionFields(
			resourcePrimKey, parentResourceClassNameId, parentResourcePrimKey);

		long kbFolderClassNameId = classNameLocalService.getClassNameId(
			KBFolderConstants.getClassName());

		long kbFolderId = KBFolderConstants.DEFAULT_PARENT_FOLDER_ID;

		if (parentResourceClassNameId == kbFolderClassNameId) {
			kbFolderId = parentResourcePrimKey;
		}
		else {
			KBArticle parentKBArticle = getLatestKBArticle(
				parentResourcePrimKey, WorkflowConstants.STATUS_ANY);

			kbFolderId = parentKBArticle.getKbFolderId();
		}

		List<KBArticle> kbArticles = getKBArticleVersions(
			resourcePrimKey, WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, new KBArticleVersionComparator());

		for (KBArticle curKBArticle : kbArticles) {
			curKBArticle.setParentResourceClassNameId(
				parentResourceClassNameId);
			curKBArticle.setParentResourcePrimKey(parentResourcePrimKey);

			curKBArticle.setKbFolderId(kbFolderId);
			curKBArticle.setPriority(priority);

			kbArticlePersistence.update(curKBArticle);
		}

		if (kbArticle.getKbFolderId() != kbFolderId) {
			List<KBArticle> descendantKBArticles = getAllDescendantKBArticles(
				resourcePrimKey, WorkflowConstants.STATUS_ANY, null);

			for (KBArticle curKBArticle : descendantKBArticles) {
				List<KBArticle> kbArticleVersions = getKBArticleVersions(
					curKBArticle.getResourcePrimKey(),
					WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, new KBArticleVersionComparator());

				for (KBArticle kbArticleVersion : kbArticleVersions) {
					kbArticleVersion.setKbFolderId(kbFolderId);

					kbArticlePersistence.update(kbArticleVersion);
				}
			}
		}

		// Social

		KBArticle latestKBArticle = getLatestKBArticle(
			resourcePrimKey, WorkflowConstants.STATUS_ANY);

		JSONObject extraDataJSONObject = JSONUtil.put(
			"title", latestKBArticle.getTitle());

		if (latestKBArticle.isApproved() || !latestKBArticle.isFirstVersion()) {
			socialActivityLocalService.addActivity(
				userId, latestKBArticle.getGroupId(), KBArticle.class.getName(),
				resourcePrimKey, AdminActivityKeys.MOVE_KB_ARTICLE,
				extraDataJSONObject.toString(), 0);
		}
	}

	@Override
	public KBArticle revertKBArticle(
			long userId, long resourcePrimKey, int version,
			ServiceContext serviceContext)
		throws PortalException {

		KBArticle kbArticle = kbArticleLocalService.getKBArticle(
			resourcePrimKey, version);

		ExpandoBridge expandoBridge = kbArticle.getExpandoBridge();

		serviceContext.setExpandoBridgeAttributes(
			expandoBridge.getAttributes());

		return updateKBArticle(
			userId, resourcePrimKey, kbArticle.getTitle(),
			kbArticle.getContent(), kbArticle.getDescription(),
			kbArticle.getSourceURL(), StringUtil.split(kbArticle.getSections()),
			null, null, serviceContext);
	}

	@Override
	public List<KBArticle> search(
		long groupId, String title, String content, int status, Date startDate,
		Date endDate, boolean andOperator, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		DynamicQuery dynamicQuery = buildDynamicQuery(
			groupId, title, content, status, startDate, endDate, andOperator);

		return dynamicQuery(dynamicQuery, start, end, orderByComparator);
	}

	@Override
	public void subscribeGroupKBArticles(long userId, long groupId)
		throws PortalException {

		_subscriptionLocalService.addSubscription(
			userId, groupId, KBArticle.class.getName(), groupId);
	}

	@Override
	public void subscribeKBArticle(
			long userId, long groupId, long resourcePrimKey)
		throws PortalException {

		_subscriptionLocalService.addSubscription(
			userId, groupId, KBArticle.class.getName(), resourcePrimKey);
	}

	@Override
	public void unsubscribeGroupKBArticles(long userId, long groupId)
		throws PortalException {

		_subscriptionLocalService.deleteSubscription(
			userId, KBArticle.class.getName(), groupId);
	}

	@Override
	public void unsubscribeKBArticle(long userId, long resourcePrimKey)
		throws PortalException {

		_subscriptionLocalService.deleteSubscription(
			userId, KBArticle.class.getName(), resourcePrimKey);
	}

	@Override
	public KBArticle updateKBArticle(
			long userId, long resourcePrimKey, String title, String content,
			String description, String sourceURL, String[] sections,
			String[] selectedFileNames, long[] removeFileEntryIds,
			ServiceContext serviceContext)
		throws PortalException {

		// KB article

		User user = userLocalService.getUser(userId);

		validate(title, content, sourceURL);

		KBArticle oldKBArticle = getLatestKBArticle(
			resourcePrimKey, WorkflowConstants.STATUS_ANY);

		int oldVersion = oldKBArticle.getVersion();

		KBArticle kbArticle = null;

		if (oldKBArticle.isApproved()) {
			long kbArticleId = counterLocalService.increment();

			kbArticle = kbArticlePersistence.create(kbArticleId);

			kbArticle.setUuid(serviceContext.getUuid());
			kbArticle.setResourcePrimKey(oldKBArticle.getResourcePrimKey());
			kbArticle.setGroupId(oldKBArticle.getGroupId());
			kbArticle.setCompanyId(user.getCompanyId());
			kbArticle.setUserId(user.getUserId());
			kbArticle.setUserName(user.getFullName());
			kbArticle.setCreateDate(oldKBArticle.getCreateDate());
			kbArticle.setRootResourcePrimKey(
				oldKBArticle.getRootResourcePrimKey());
			kbArticle.setParentResourceClassNameId(
				oldKBArticle.getParentResourceClassNameId());
			kbArticle.setParentResourcePrimKey(
				oldKBArticle.getParentResourcePrimKey());
			kbArticle.setKbFolderId(oldKBArticle.getKbFolderId());
			kbArticle.setVersion(oldVersion + 1);
			kbArticle.setUrlTitle(oldKBArticle.getUrlTitle());
			kbArticle.setPriority(oldKBArticle.getPriority());

			_viewCountManager.incrementViewCount(
				kbArticle.getCompanyId(),
				classNameLocalService.getClassNameId(KBArticle.class),
				kbArticle.getPrimaryKey(), (int)oldKBArticle.getViewCount());
		}
		else {
			kbArticle = oldKBArticle;
		}

		if (oldKBArticle.isPending()) {
			kbArticle.setStatus(WorkflowConstants.STATUS_PENDING);
		}
		else {
			kbArticle.setStatus(WorkflowConstants.STATUS_DRAFT);
		}

		kbArticle.setTitle(title);
		kbArticle.setContent(content);
		kbArticle.setDescription(description);
		kbArticle.setSections(
			StringUtil.merge(KBSectionEscapeUtil.escapeSections(sections)));
		kbArticle.setLatest(true);
		kbArticle.setMain(false);
		kbArticle.setSourceURL(sourceURL);
		kbArticle.setExpandoBridgeAttributes(serviceContext);

		kbArticle = kbArticlePersistence.update(kbArticle);

		if (oldKBArticle.isApproved()) {
			oldKBArticle.setLatest(false);

			kbArticlePersistence.update(oldKBArticle);
		}

		// Asset

		updateKBArticleAsset(
			userId, kbArticle, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds());

		// Attachments

		addKBArticleAttachments(userId, kbArticle, selectedFileNames);

		removeKBArticleAttachments(removeFileEntryIds);

		// Workflow

		WorkflowHandlerRegistryUtil.startWorkflowInstance(
			user.getCompanyId(), kbArticle.getGroupId(), userId,
			KBArticle.class.getName(), resourcePrimKey, kbArticle,
			serviceContext);

		return kbArticle;
	}

	@Override
	public void updateKBArticleAsset(
			long userId, KBArticle kbArticle, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds)
		throws PortalException {

		boolean visible = false;

		if (kbArticle.isApproved()) {
			visible = true;
		}

		String summary = HtmlUtil.extractText(
			StringUtil.shorten(kbArticle.getContent(), 500));

		AssetEntry assetEntry = assetEntryLocalService.updateEntry(
			userId, kbArticle.getGroupId(), kbArticle.getCreateDate(),
			kbArticle.getModifiedDate(), KBArticle.class.getName(),
			kbArticle.getClassPK(), kbArticle.getUuid(), 0, assetCategoryIds,
			assetTagNames, true, visible, null, null, null, null,
			ContentTypes.TEXT_HTML, kbArticle.getTitle(),
			kbArticle.getDescription(), summary, null, null, 0, 0, null);

		assetLinkLocalService.updateLinks(
			userId, assetEntry.getEntryId(), assetLinkEntryIds,
			AssetLinkConstants.TYPE_RELATED);
	}

	@Override
	public void updateKBArticleResources(
			KBArticle kbArticle, String[] groupPermissions,
			String[] guestPermissions)
		throws PortalException {

		resourceLocalService.updateResources(
			kbArticle.getCompanyId(), kbArticle.getGroupId(),
			KBArticle.class.getName(), kbArticle.getResourcePrimKey(),
			groupPermissions, guestPermissions);
	}

	@Override
	public void updateKBArticlesPriorities(
			Map<Long, Double> resourcePrimKeyToPriorityMap)
		throws PortalException {

		for (double priority : resourcePrimKeyToPriorityMap.values()) {
			validate(priority);
		}

		long[] resourcePrimKeys = StringUtil.split(
			StringUtil.merge(resourcePrimKeyToPriorityMap.keySet()), 0L);

		List<KBArticle> kbArticles = getKBArticles(
			resourcePrimKeys, WorkflowConstants.STATUS_ANY, null);

		for (KBArticle kbArticle : kbArticles) {
			double priority = resourcePrimKeyToPriorityMap.get(
				kbArticle.getResourcePrimKey());

			updatePriority(kbArticle.getResourcePrimKey(), priority);
		}
	}

	@Override
	public void updatePriority(long resourcePrimKey, double priority) {
		List<KBArticle> kbArticleVersions = getKBArticleVersions(
			resourcePrimKey, WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		for (KBArticle kbArticle : kbArticleVersions) {
			kbArticle.setPriority(priority);

			kbArticlePersistence.update(kbArticle);
		}
	}

	@Override
	public KBArticle updateStatus(
			long userId, long resourcePrimKey, int status,
			ServiceContext serviceContext)
		throws PortalException {

		// KB article

		User user = userLocalService.getUser(userId);
		boolean main = false;
		Date now = new Date();

		if (status == WorkflowConstants.STATUS_APPROVED) {
			main = true;
		}

		KBArticle kbArticle = getLatestKBArticle(
			resourcePrimKey, WorkflowConstants.STATUS_ANY);

		validateParentStatus(
			kbArticle.getParentResourceClassNameId(),
			kbArticle.getParentResourcePrimKey(), status);

		kbArticle.setMain(main);
		kbArticle.setStatus(status);
		kbArticle.setStatusByUserId(user.getUserId());
		kbArticle.setStatusByUserName(user.getFullName());
		kbArticle.setStatusDate(serviceContext.getModifiedDate(now));

		kbArticle = kbArticlePersistence.update(kbArticle);

		if (status != WorkflowConstants.STATUS_APPROVED) {
			return kbArticle;
		}

		if (!kbArticle.isFirstVersion()) {
			KBArticle oldKBArticle = kbArticlePersistence.findByR_V(
				resourcePrimKey, kbArticle.getVersion() - 1);

			oldKBArticle.setMain(false);

			kbArticlePersistence.update(oldKBArticle);
		}

		// Asset

		AssetEntry assetEntry = assetEntryLocalService.getEntry(
			KBArticle.class.getName(), kbArticle.getKbArticleId());

		List<AssetLink> assetLinks = assetLinkLocalService.getDirectLinks(
			assetEntry.getEntryId(), AssetLinkConstants.TYPE_RELATED);

		long[] assetLinkEntryIds = StringUtil.split(
			ListUtil.toString(assetLinks, AssetLink.ENTRY_ID2_ACCESSOR), 0L);

		updateKBArticleAsset(
			userId, kbArticle, assetEntry.getCategoryIds(),
			assetEntry.getTagNames(), assetLinkEntryIds);

		SystemEventHierarchyEntryThreadLocal.push(KBArticle.class);

		try {
			assetEntryLocalService.deleteEntry(
				KBArticle.class.getName(), kbArticle.getKbArticleId());
		}
		finally {
			SystemEventHierarchyEntryThreadLocal.pop(KBArticle.class);
		}

		assetEntryLocalService.updateVisible(
			KBArticle.class.getName(), kbArticle.getResourcePrimKey(), true);

		// Social

		JSONObject extraDataJSONObject = JSONUtil.put(
			"title", kbArticle.getTitle());

		if (!kbArticle.isFirstVersion()) {
			socialActivityLocalService.addActivity(
				userId, kbArticle.getGroupId(), KBArticle.class.getName(),
				resourcePrimKey, AdminActivityKeys.UPDATE_KB_ARTICLE,
				extraDataJSONObject.toString(), 0);
		}
		else {
			socialActivityLocalService.addActivity(
				userId, kbArticle.getGroupId(), KBArticle.class.getName(),
				resourcePrimKey, AdminActivityKeys.ADD_KB_ARTICLE,
				extraDataJSONObject.toString(), 0);
		}

		// Indexer

		Indexer<KBArticle> indexer = _indexerRegistry.getIndexer(
			KBArticle.class);

		indexer.reindex(kbArticle);

		// Subscriptions

		notifySubscribers(userId, kbArticle, serviceContext);

		return kbArticle;
	}

	protected void addKBArticleAttachment(
			long userId, long groupId, long resourcePrimKey,
			String selectedFileName)
		throws PortalException {

		FileEntry tempFileEntry = TempFileEntryUtil.getTempFileEntry(
			groupId, userId, KnowledgeBaseConstants.TEMP_FOLDER_NAME,
			selectedFileName);

		InputStream inputStream = tempFileEntry.getContentStream();

		addAttachment(
			userId, resourcePrimKey, selectedFileName, inputStream,
			tempFileEntry.getMimeType());

		if (tempFileEntry != null) {
			TempFileEntryUtil.deleteTempFileEntry(
				tempFileEntry.getFileEntryId());
		}
	}

	protected void addKBArticleAttachments(
			long userId, KBArticle kbArticle, String[] selectedFileNames)
		throws PortalException {

		if (ArrayUtil.isEmpty(selectedFileNames)) {
			return;
		}

		for (String selectedFileName : selectedFileNames) {
			addKBArticleAttachment(
				userId, kbArticle.getGroupId(), kbArticle.getResourcePrimKey(),
				selectedFileName);
		}
	}

	protected DynamicQuery buildDynamicQuery(
		long groupId, String title, String content, int status, Date startDate,
		Date endDate, boolean andOperator) {

		Junction junction = null;

		if (andOperator) {
			junction = RestrictionsFactoryUtil.conjunction();
		}
		else {
			junction = RestrictionsFactoryUtil.disjunction();
		}

		Map<String, String> terms = new HashMap<>();

		if (Validator.isNotNull(title)) {
			terms.put("title", title);
		}

		if (Validator.isNotNull(content)) {
			terms.put("content", content);
		}

		for (Map.Entry<String, String> entry : terms.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			Disjunction disjunction = RestrictionsFactoryUtil.disjunction();

			for (String keyword : KnowledgeBaseUtil.splitKeywords(value)) {
				Criterion criterion = RestrictionsFactoryUtil.ilike(
					key, StringUtil.quote(keyword, StringPool.PERCENT));

				disjunction.add(criterion);
			}

			junction.add(disjunction);
		}

		if (status != WorkflowConstants.STATUS_ANY) {
			Property property = PropertyFactoryUtil.forName("status");

			junction.add(property.eq(status));
		}

		if ((endDate != null) && (startDate != null)) {
			Disjunction disjunction = RestrictionsFactoryUtil.disjunction();

			String[] propertyNames = {"createDate", "modifiedDate"};

			for (String propertyName : propertyNames) {
				Property property = PropertyFactoryUtil.forName(propertyName);

				Conjunction conjunction = RestrictionsFactoryUtil.conjunction();

				conjunction.add(property.gt(startDate));
				conjunction.add(property.lt(endDate));

				disjunction.add(conjunction);
			}

			junction.add(disjunction);
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			KBArticle.class, getClassLoader());

		if (status == WorkflowConstants.STATUS_ANY) {
			Property property = PropertyFactoryUtil.forName("latest");

			dynamicQuery.add(property.eq(Boolean.TRUE));
		}
		else if (status == WorkflowConstants.STATUS_APPROVED) {
			Property property = PropertyFactoryUtil.forName("main");

			dynamicQuery.add(property.eq(Boolean.TRUE));
		}

		if (groupId > 0) {
			Property property = PropertyFactoryUtil.forName("groupId");

			dynamicQuery.add(property.eq(groupId));
		}

		return dynamicQuery.add(junction);
	}

	protected void deleteAssets(KBArticle kbArticle) throws PortalException {
		assetEntryLocalService.deleteEntry(
			KBArticle.class.getName(), kbArticle.getClassPK());

		if (!kbArticle.isApproved() && !kbArticle.isFirstVersion()) {
			assetEntryLocalService.deleteEntry(
				KBArticle.class.getName(), kbArticle.getResourcePrimKey());
		}
	}

	protected void deleteSubscriptions(KBArticle kbArticle)
		throws PortalException {

		List<Subscription> subscriptions =
			_subscriptionLocalService.getSubscriptions(
				kbArticle.getCompanyId(), KBArticle.class.getName(),
				kbArticle.getResourcePrimKey());

		for (Subscription subscription : subscriptions) {
			unsubscribeKBArticle(
				subscription.getUserId(), subscription.getClassPK());
		}
	}

	protected void getAllDescendantKBArticles(
		List<KBArticle> kbArticles, long resourcePrimKey, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		List<KBArticle> curKBArticles = null;

		if (status == WorkflowConstants.STATUS_ANY) {
			curKBArticles = kbArticlePersistence.findByP_L(
				resourcePrimKey, true, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				orderByComparator);
		}
		else if (status == WorkflowConstants.STATUS_APPROVED) {
			curKBArticles = kbArticlePersistence.findByP_M(
				resourcePrimKey, true, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				orderByComparator);
		}
		else {
			curKBArticles = kbArticlePersistence.findByP_S(
				resourcePrimKey, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				orderByComparator);
		}

		for (KBArticle curKBArticle : curKBArticles) {
			kbArticles.add(curKBArticle);

			getAllDescendantKBArticles(
				kbArticles, curKBArticle.getResourcePrimKey(), status,
				orderByComparator);
		}
	}

	protected List<KBArticle> getAllDescendantKBArticles(
		long resourcePrimKey, int status,
		OrderByComparator<KBArticle> orderByComparator,
		boolean includeParentArticle) {

		List<KBArticle> kbArticles = null;

		if (includeParentArticle) {
			kbArticles = getKBArticles(
				new long[] {resourcePrimKey}, status, null);

			kbArticles = ListUtil.copy(kbArticles);
		}
		else {
			kbArticles = new ArrayList<>();
		}

		getAllDescendantKBArticles(
			kbArticles, resourcePrimKey, status, orderByComparator);

		return Collections.unmodifiableList(kbArticles);
	}

	protected Map<String, String> getEmailKBArticleDiffs(KBArticle kbArticle) {
		Map<String, String> emailKBArticleDiffs = new HashMap<>();

		for (String param : new String[] {"content", "title"}) {
			String value = BeanPropertiesUtil.getString(kbArticle, param);

			try {
				value = KBArticleDiffUtil.getKBArticleDiff(
					version -> getKBArticle(
						kbArticle.getResourcePrimKey(), version),
					kbArticle.getVersion() - 1, kbArticle.getVersion(), param);
			}
			catch (Exception exception) {
				_log.error(exception, exception);
			}

			emailKBArticleDiffs.put(param, value);
		}

		return emailKBArticleDiffs;
	}

	protected KBGroupServiceConfiguration getKBGroupServiceConfiguration(
			long groupId)
		throws ConfigurationException {

		return _configurationProvider.getConfiguration(
			KBGroupServiceConfiguration.class,
			new GroupServiceSettingsLocator(groupId, KBConstants.SERVICE_NAME));
	}

	protected double getPriority(long groupId, long parentResourcePrimKey)
		throws PortalException {

		KBGroupServiceConfiguration kbGroupServiceConfiguration =
			getKBGroupServiceConfiguration(groupId);

		if (!kbGroupServiceConfiguration.articleIncrementPriorityEnabled()) {
			return KBArticleConstants.DEFAULT_VERSION;
		}

		List<KBArticle> kbArticles = getKBArticles(
			groupId, parentResourcePrimKey, WorkflowConstants.STATUS_ANY, 0, 1,
			new KBArticlePriorityComparator());

		if (kbArticles.isEmpty()) {
			return KBArticleConstants.DEFAULT_PRIORITY;
		}

		KBArticle kbArticle = kbArticles.get(0);

		return Math.floor(kbArticle.getPriority()) + 1;
	}

	protected long getRootResourcePrimKey(
			long resourcePrimKey, long parentResourceClassNameId,
			long parentResourcePrimKey)
		throws PortalException {

		if (parentResourcePrimKey ==
				KBFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

			return resourcePrimKey;
		}

		long classNameId = classNameLocalService.getClassNameId(
			KBArticleConstants.getClassName());

		if (parentResourceClassNameId == classNameId) {
			KBArticle kbArticle = getLatestKBArticle(
				parentResourcePrimKey, WorkflowConstants.STATUS_ANY);

			return kbArticle.getRootResourcePrimKey();
		}

		return resourcePrimKey;
	}

	protected String getUniqueUrlTitle(
			long groupId, long kbFolderId, long kbArticleId, String title)
		throws PortalException {

		String urlTitle = KnowledgeBaseUtil.getUrlTitle(kbArticleId, title);

		String uniqueUrlTitle = urlTitle;

		if (kbFolderId == KBFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			int kbArticlesCount = kbArticlePersistence.countByG_KBFI_UT_ST(
				groupId, kbFolderId, uniqueUrlTitle, _STATUSES);

			for (int i = 1; kbArticlesCount > 0; i++) {
				uniqueUrlTitle = getUniqueUrlTitle(urlTitle, i);

				kbArticlesCount = kbArticlePersistence.countByG_KBFI_UT_ST(
					groupId, kbFolderId, uniqueUrlTitle, _STATUSES);
			}

			return uniqueUrlTitle;
		}

		KBFolder kbFolder = kbFolderPersistence.findByPrimaryKey(kbFolderId);

		int kbArticlesCount = kbArticleFinder.countByUrlTitle(
			groupId, kbFolder.getUrlTitle(), uniqueUrlTitle, _STATUSES);

		for (int i = 1; kbArticlesCount > 0; i++) {
			uniqueUrlTitle = getUniqueUrlTitle(urlTitle, i);

			kbArticlesCount = kbArticleFinder.countByUrlTitle(
				groupId, kbFolder.getUrlTitle(), uniqueUrlTitle, _STATUSES);
		}

		return uniqueUrlTitle;
	}

	protected String getUniqueUrlTitle(
			long groupId, long kbFolderId, long kbArticleId, String title,
			String urlTitle)
		throws PortalException {

		if (Validator.isNull(urlTitle)) {
			return getUniqueUrlTitle(groupId, kbFolderId, kbArticleId, title);
		}

		return urlTitle.substring(1);
	}

	protected String getUniqueUrlTitle(String urlTitle, int suffix) {
		String uniqueUrlTitle = urlTitle + StringPool.DASH + suffix;

		int maxLength = ModelHintsUtil.getMaxLength(
			KBArticle.class.getName(), "urlTitle");

		return StringUtil.shorten(
			uniqueUrlTitle, maxLength, StringPool.DASH + suffix);
	}

	protected String normalizeUrlTitle(String urlTitle) {
		if (Validator.isNull(urlTitle)) {
			return null;
		}

		if (StringUtil.startsWith(urlTitle, CharPool.SLASH)) {
			return urlTitle;
		}

		return StringPool.SLASH + urlTitle;
	}

	protected void notifySubscribers(
			long userId, KBArticle kbArticle, ServiceContext serviceContext)
		throws PortalException {

		if (Validator.isNull(serviceContext.getLayoutFullURL())) {
			return;
		}

		KBGroupServiceConfiguration kbGroupServiceConfiguration =
			getKBGroupServiceConfiguration(kbArticle.getGroupId());

		if (serviceContext.isCommandAdd() &&
			!kbGroupServiceConfiguration.emailKBArticleAddedEnabled()) {

			return;
		}

		if (serviceContext.isCommandUpdate() &&
			!kbGroupServiceConfiguration.emailKBArticleUpdatedEnabled()) {

			return;
		}

		String fromName = kbGroupServiceConfiguration.emailFromName();
		String fromAddress = kbGroupServiceConfiguration.emailFromAddress();

		String kbArticleContent = StringUtil.replace(
			kbArticle.getContent(), new String[] {"href=\"/", "src=\"/"},
			new String[] {
				"href=\"" + serviceContext.getPortalURL() + "/",
				"src=\"" + serviceContext.getPortalURL() + "/"
			});

		Map<String, String> kbArticleDiffs = getEmailKBArticleDiffs(kbArticle);

		for (Map.Entry<String, String> entry : kbArticleDiffs.entrySet()) {
			String value = StringUtil.replace(
				entry.getValue(), new String[] {"href=\"/", "src=\"/"},
				new String[] {
					"href=\"" + serviceContext.getPortalURL() + "/",
					"src=\"" + serviceContext.getPortalURL() + "/"
				});

			kbArticleDiffs.put(entry.getKey(), value);
		}

		String subject = null;
		String body = null;

		if (serviceContext.isCommandAdd()) {
			subject = kbGroupServiceConfiguration.emailKBArticleAddedSubject();
			body = kbGroupServiceConfiguration.emailKBArticleAddedBody();
		}
		else {
			subject =
				kbGroupServiceConfiguration.emailKBArticleUpdatedSubject();
			body = kbGroupServiceConfiguration.emailKBArticleUpdatedBody();
		}

		SubscriptionSender subscriptionSender =
			AdminSubscriptionSenderFactory.createSubscriptionSender(
				kbArticle, serviceContext);

		subscriptionSender.setBody(body);
		subscriptionSender.setCompanyId(kbArticle.getCompanyId());
		subscriptionSender.setContextAttribute(
			"[$ARTICLE_CONTENT$]", kbArticleContent, false);
		subscriptionSender.setContextAttribute(
			"[$ARTICLE_CONTENT_DIFF$]", kbArticleDiffs.get("content"), false);
		subscriptionSender.setContextAttribute(
			"[$ARTICLE_TITLE$]", kbArticle.getTitle(), false);
		subscriptionSender.setContextAttribute(
			"[$ARTICLE_TITLE_DIFF$]", kbArticleDiffs.get("title"), false);
		subscriptionSender.setContextCreatorUserPrefix("ARTICLE");
		subscriptionSender.setCreatorUserId(kbArticle.getUserId());
		subscriptionSender.setCurrentUserId(userId);
		subscriptionSender.setFrom(fromAddress, fromName);
		subscriptionSender.setHtmlFormat(true);
		subscriptionSender.setMailId("kb_article", kbArticle.getKbArticleId());
		subscriptionSender.setPortletId(serviceContext.getPortletId());
		subscriptionSender.setReplyToAddress(fromAddress);
		subscriptionSender.setScopeGroupId(kbArticle.getGroupId());
		subscriptionSender.setSubject(subject);

		subscriptionSender.addPersistedSubscribers(
			KBArticle.class.getName(), kbArticle.getGroupId());
		subscriptionSender.addPersistedSubscribers(
			KBArticle.class.getName(), kbArticle.getResourcePrimKey());

		while (!kbArticle.isRoot() &&
			   (kbArticle.getClassNameId() ==
				   kbArticle.getParentResourceClassNameId())) {

			kbArticle = getLatestKBArticle(
				kbArticle.getParentResourcePrimKey(),
				WorkflowConstants.STATUS_APPROVED);

			subscriptionSender.addPersistedSubscribers(
				KBArticle.class.getName(), kbArticle.getResourcePrimKey());
		}

		subscriptionSender.flushNotificationsAsync();
	}

	protected void removeKBArticleAttachments(long[] removeFileEntryIds)
		throws PortalException {

		if (ArrayUtil.isEmpty(removeFileEntryIds)) {
			return;
		}

		for (long removeFileEntryId : removeFileEntryIds) {
			_portletFileRepository.deletePortletFileEntry(removeFileEntryId);
		}
	}

	protected void updatePermissionFields(
			long resourcePrimKey, long parentResourceClassNameId,
			long parentResourcePrimKey)
		throws PortalException {

		KBArticle kbArticle = getLatestKBArticle(
			resourcePrimKey, WorkflowConstants.STATUS_ANY);

		if (kbArticle.getParentResourcePrimKey() == parentResourcePrimKey) {
			return;
		}

		long rootResourcePrimKey = getRootResourcePrimKey(
			resourcePrimKey, parentResourceClassNameId, parentResourcePrimKey);

		if (kbArticle.getRootResourcePrimKey() == rootResourcePrimKey) {
			return;
		}

		// Sync database

		List<KBArticle> kbArticles1 = getKBArticleAndAllDescendantKBArticles(
			resourcePrimKey, WorkflowConstants.STATUS_ANY, null);

		for (KBArticle kbArticle1 : kbArticles1) {
			List<KBArticle> kbArticles2 = getKBArticleVersions(
				kbArticle1.getResourcePrimKey(), WorkflowConstants.STATUS_ANY,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			for (KBArticle kbArticle2 : kbArticles2) {
				kbArticle2.setRootResourcePrimKey(rootResourcePrimKey);

				kbArticlePersistence.update(kbArticle2);
			}
		}

		// Sync indexed permission fields

		_indexWriterHelper.updatePermissionFields(
			KBArticle.class.getName(), String.valueOf(resourcePrimKey));
	}

	protected void validate(double priority) throws PortalException {
		if (priority <= 0) {
			throw new KBArticlePriorityException(
				"Invalid priority " + priority);
		}
	}

	protected void validate(String title, String content, String sourceURL)
		throws PortalException {

		if (Validator.isNull(title)) {
			throw new KBArticleTitleException("Title is null");
		}

		if (Validator.isNull(content)) {
			throw new KBArticleContentException("Content is null");
		}

		validateSourceURL(sourceURL);
	}

	protected void validateParent(
			KBArticle kbArticle, long parentResourceClassNameId,
			long parentResourcePrimKey)
		throws PortalException {

		validateParent(parentResourceClassNameId, parentResourcePrimKey);

		long kbArticleClassNameId = classNameLocalService.getClassNameId(
			KBArticleConstants.getClassName());

		if (parentResourceClassNameId == kbArticleClassNameId) {
			KBArticle parentKBArticle = getLatestKBArticle(
				parentResourcePrimKey, WorkflowConstants.STATUS_ANY);

			List<Long> ancestorResourcePrimaryKeys =
				parentKBArticle.getAncestorResourcePrimaryKeys();

			if (ancestorResourcePrimaryKeys.contains(
					kbArticle.getResourcePrimKey())) {

				throw new KBArticleParentException(
					String.format(
						"Cannot move KBArticle %s inside its descendant " +
							"KBArticle %s",
						kbArticle.getResourcePrimKey(),
						parentKBArticle.getResourcePrimKey()));
			}
		}
	}

	protected void validateParent(
			long resourceClassNameId, long resourcePrimKey)
		throws PortalException {

		long kbArticleClassNameId = classNameLocalService.getClassNameId(
			KBArticleConstants.getClassName());
		long kbFolderClassNameId = classNameLocalService.getClassNameId(
			KBFolderConstants.getClassName());

		if ((resourceClassNameId != kbArticleClassNameId) &&
			(resourceClassNameId != kbFolderClassNameId)) {

			throw new KBArticleParentException(
				String.format(
					"Invalid parent with resource class name ID %s and " +
						"resource primary key %s",
					resourceClassNameId, resourcePrimKey));
		}
	}

	protected void validateParentStatus(
			long parentResourceClassNameId, long parentResourcePrimKey,
			int status)
		throws PortalException {

		long kbFolderClassNameId = classNameLocalService.getClassNameId(
			KBFolder.class);

		if (parentResourceClassNameId == kbFolderClassNameId) {
			return;
		}

		KBArticle kbArticle = fetchLatestKBArticle(
			parentResourcePrimKey, WorkflowConstants.STATUS_APPROVED);

		if ((kbArticle == null) &&
			(status == WorkflowConstants.STATUS_APPROVED)) {

			throw new KBArticleStatusException();
		}
	}

	protected void validateSourceURL(String sourceURL) throws PortalException {
		if (Validator.isNull(sourceURL)) {
			return;
		}

		if (!Validator.isUrl(sourceURL)) {
			throw new KBArticleSourceURLException(sourceURL);
		}
	}

	protected void validateUrlTitle(
			long groupId, long kbFolderId, String urlTitle)
		throws PortalException {

		if (Validator.isNull(urlTitle)) {
			return;
		}

		if (!KnowledgeBaseUtil.isValidUrlTitle(urlTitle)) {
			throw new KBArticleUrlTitleException.
				MustNotContainInvalidCharacters(urlTitle);
		}

		int urlTitleMaxSize = ModelHintsUtil.getMaxLength(
			KBArticle.class.getName(), "urlTitle");

		if (urlTitle.length() > (urlTitleMaxSize + 1)) {
			throw new KBArticleUrlTitleException.MustNotExceedMaximumSize(
				urlTitle, urlTitleMaxSize);
		}

		Collection<KBArticle> kbArticles = kbArticlePersistence.findByG_KBFI_UT(
			groupId, kbFolderId, urlTitle.substring(1));

		if (!kbArticles.isEmpty()) {
			throw new KBArticleUrlTitleException.MustNotBeDuplicate(urlTitle);
		}
	}

	private static final int[] _STATUSES = {
		WorkflowConstants.STATUS_APPROVED, WorkflowConstants.STATUS_PENDING
	};

	private static final Log _log = LogFactoryUtil.getLog(
		KBArticleLocalServiceImpl.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private IndexerRegistry _indexerRegistry;

	@Reference
	private IndexWriterHelper _indexWriterHelper;

	@Reference
	private KBArchiveFactory _kbArchiveFactory;

	@Reference
	private Portal _portal;

	@Reference
	private PortletFileRepository _portletFileRepository;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

	@Reference
	private ViewCountManager _viewCountManager;

}