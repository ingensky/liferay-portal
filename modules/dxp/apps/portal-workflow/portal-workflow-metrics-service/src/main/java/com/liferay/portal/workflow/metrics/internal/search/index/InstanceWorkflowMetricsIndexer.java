/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.portal.workflow.metrics.internal.search.index;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;

import java.text.ParseException;

import java.time.Duration;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Inácio Nery
 */
@Component(immediate = true, service = InstanceWorkflowMetricsIndexer.class)
public class InstanceWorkflowMetricsIndexer extends BaseWorkflowMetricsIndexer {

	public Document createDocument(KaleoInstance kaleoInstance) {
		Document document = new DocumentImpl();

		document.addUID(
			"WorkflowMetricsInstance",
			digest(
				kaleoInstance.getCompanyId(),
				kaleoInstance.getKaleoDefinitionVersionId(),
				kaleoInstance.getKaleoInstanceId()));

		document.addLocalizedKeyword(
			"assetTitle", _createAssetTitleLocalizationMap(kaleoInstance),
			false, true);
		document.addLocalizedKeyword(
			"assetType", _createAssetTypeLocalizationMap(kaleoInstance), false,
			true);
		document.addKeyword("className", kaleoInstance.getClassName());
		document.addKeyword("classPK", kaleoInstance.getClassPK());
		document.addKeyword("companyId", kaleoInstance.getCompanyId());
		document.addKeyword("completed", kaleoInstance.isCompleted());

		Date completionDate = kaleoInstance.getCompletionDate();

		if (kaleoInstance.isCompleted()) {
			document.addDateSortable("completionDate", completionDate);
		}

		Date createDate = kaleoInstance.getCreateDate();

		document.addDateSortable("createDate", createDate);

		document.addKeyword("deleted", false);

		if (kaleoInstance.isCompleted()) {
			Duration duration = Duration.between(
				createDate.toInstant(), completionDate.toInstant());

			document.addNumber("duration", duration.toMillis());
		}

		document.addKeyword("instanceId", kaleoInstance.getKaleoInstanceId());
		document.addDateSortable(
			"modifiedDate", kaleoInstance.getModifiedDate());
		document.addKeyword("processId", kaleoInstance.getKaleoDefinitionId());
		document.addKeyword("userId", kaleoInstance.getUserId());
		document.addKeyword("userName", kaleoInstance.getUserName());
		document.addKeyword(
			"version",
			StringBundler.concat(
				kaleoInstance.getKaleoDefinitionVersion(), CharPool.PERIOD, 0));

		return document;
	}

	@Override
	public void deleteDocument(Document document) {
		super.deleteDocument(document);

		_slaInstanceResultWorkflowMetricsIndexer.deleteDocuments(
			GetterUtil.getLong(document.get("companyId")),
			GetterUtil.getLong(document.get("instanceId")));

		_slaTaskResultWorkflowMetricsIndexer.deleteDocuments(
			GetterUtil.getLong(document.get("companyId")),
			GetterUtil.getLong(document.get("instanceId")));
	}

	@Override
	public String getIndexName() {
		return "workflow-metrics-instances";
	}

	@Override
	public String getIndexType() {
		return "WorkflowMetricsInstanceType";
	}

	@Override
	public void reindex(long companyId) throws PortalException {
		ActionableDynamicQuery actionableDynamicQuery =
			kaleoInstanceLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property companyIdProperty = PropertyFactoryUtil.forName(
					"companyId");

				dynamicQuery.add(companyIdProperty.eq(companyId));
			});
		actionableDynamicQuery.setPerformActionMethod(
			(KaleoInstance kaleoInstance) ->
				workflowMetricsPortalExecutor.execute(
					() -> addDocument(createDocument(kaleoInstance))));

		actionableDynamicQuery.performActions();
	}

	@Override
	public void updateDocument(Document document) {
		super.updateDocument(document);

		if (!GetterUtil.getBoolean(document.get("completed"))) {
			return;
		}

		BooleanQuery booleanQuery = queries.booleanQuery();

		booleanQuery.addMustQueryClauses(
			queries.term(
				"companyId", GetterUtil.getLong(document.get("companyId"))),
			queries.term(
				"instanceId", GetterUtil.getLong(document.get("instanceId"))));

		_slaInstanceResultWorkflowMetricsIndexer.updateDocuments(
			documentImpl -> new DocumentImpl() {
				{
					try {
						addDateSortable(
							"completionDate",
							document.getDate("completionDate"));
					}
					catch (ParseException parseException) {
						if (_log.isWarnEnabled()) {
							_log.warn(parseException, parseException);
						}
					}

					addKeyword("instanceCompleted", Boolean.TRUE);
					addKeyword(Field.UID, documentImpl.getString(Field.UID));
				}
			},
			booleanQuery);

		_slaTaskResultWorkflowMetricsIndexer.updateDocuments(
			documentImpl -> new DocumentImpl() {
				{
					addKeyword("instanceCompleted", Boolean.TRUE);
					addKeyword(Field.UID, documentImpl.getString(Field.UID));
				}
			},
			booleanQuery);

		_tokenWorkflowMetricsIndexer.updateDocuments(
			documentImpl -> new DocumentImpl() {
				{
					addKeyword("instanceCompleted", Boolean.TRUE);
					addKeyword(Field.UID, documentImpl.getString(Field.UID));
				}
			},
			booleanQuery);
	}

	private Map<Locale, String> _createAssetTitleLocalizationMap(
		KaleoInstance kaleoInstance) {

		try {
			AssetRenderer<?> assetRenderer = _getAssetRenderer(
				kaleoInstance.getClassName(), kaleoInstance.getClassPK());

			if (assetRenderer != null) {
				AssetEntry assetEntry = assetEntryLocalService.getEntry(
					assetRenderer.getClassName(), assetRenderer.getClassPK());

				return LocalizationUtil.populateLocalizationMap(
					assetEntry.getTitleMap(), assetEntry.getDefaultLanguageId(),
					assetEntry.getGroupId());
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException, portalException);
			}
		}

		WorkflowHandler<?> workflowHandler =
			WorkflowHandlerRegistryUtil.getWorkflowHandler(
				kaleoInstance.getClassName());

		Map<Locale, String> localizationMap = new HashMap<>();

		for (Locale availableLocale :
				LanguageUtil.getAvailableLocales(kaleoInstance.getGroupId())) {

			localizationMap.put(
				availableLocale,
				workflowHandler.getTitle(
					kaleoInstance.getClassPK(), availableLocale));
		}

		return localizationMap;
	}

	private Map<Locale, String> _createAssetTypeLocalizationMap(
		KaleoInstance kaleoInstance) {

		Map<Locale, String> localizationMap = new HashMap<>();

		for (Locale availableLocale :
				LanguageUtil.getAvailableLocales(kaleoInstance.getGroupId())) {

			localizationMap.put(
				availableLocale,
				ResourceActionsUtil.getModelResource(
					availableLocale, kaleoInstance.getClassName()));
		}

		return localizationMap;
	}

	private AssetRenderer<?> _getAssetRenderer(String className, long classPK)
		throws PortalException {

		AssetRendererFactory<?> assetRendererFactory = _getAssetRendererFactory(
			className);

		if (assetRendererFactory != null) {
			return assetRendererFactory.getAssetRenderer(classPK);
		}

		return null;
	}

	private AssetRendererFactory<?> _getAssetRendererFactory(String className) {
		return AssetRendererFactoryRegistryUtil.
			getAssetRendererFactoryByClassName(className);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		InstanceWorkflowMetricsIndexer.class);

	@Reference
	private SLAInstanceResultWorkflowMetricsIndexer
		_slaInstanceResultWorkflowMetricsIndexer;

	@Reference
	private SLATaskResultWorkflowMetricsIndexer
		_slaTaskResultWorkflowMetricsIndexer;

	@Reference
	private TokenWorkflowMetricsIndexer _tokenWorkflowMetricsIndexer;

}