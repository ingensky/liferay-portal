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

package com.liferay.layout.admin.web.internal.display.context;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyServiceUtil;
import com.liferay.exportimport.kernel.staging.LayoutStagingUtil;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.layout.admin.web.internal.configuration.LayoutConverterConfiguration;
import com.liferay.layout.admin.web.internal.configuration.LayoutEditorTypeConfiguration;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryServiceUtil;
import com.liferay.layout.page.template.util.comparator.LayoutPageTemplateCollectionNameComparator;
import com.liferay.layout.util.LayoutCopyHelper;
import com.liferay.layout.util.comparator.LayoutCreateDateComparator;
import com.liferay.layout.util.template.LayoutConverter;
import com.liferay.layout.util.template.LayoutConverterRegistry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.model.LayoutType;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetBranchLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.util.LayoutDescription;
import com.liferay.portal.util.LayoutListUtil;
import com.liferay.portal.util.LayoutTypeControllerTracker;
import com.liferay.portal.util.RobotsUtil;
import com.liferay.portlet.layoutsadmin.display.context.GroupDisplayContextHelper;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalServiceUtil;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.taglib.security.PermissionsURLTag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.portlet.ActionRequest;
import javax.portlet.PortletURL;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class LayoutsAdminDisplayContext {

	public LayoutsAdminDisplayContext(
		LayoutConverterConfiguration layoutConverterConfiguration,
		LayoutConverterRegistry layoutConverterRegistry,
		LayoutCopyHelper layoutCopyHelper,
		LayoutEditorTypeConfiguration layoutEditorTypeConfiguration,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		StagingGroupHelper stagingGroupHelper) {

		_layoutConverterConfiguration = layoutConverterConfiguration;
		_layoutConverterRegistry = layoutConverterRegistry;
		_layoutCopyHelper = layoutCopyHelper;
		_layoutEditorTypeConfiguration = layoutEditorTypeConfiguration;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_stagingGroupHelper = stagingGroupHelper;

		httpServletRequest = PortalUtil.getHttpServletRequest(
			_liferayPortletRequest);

		_groupDisplayContextHelper = new GroupDisplayContextHelper(
			httpServletRequest);

		themeDisplay = (ThemeDisplay)_liferayPortletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getAddLayoutDropdownItems() {
		return new DropdownItemList() {
			{
				if (isShowPublicPages()) {
					add(
						dropdownItem -> {
							dropdownItem.setHref(
								getSelectLayoutPageTemplateEntryURL(false));
							dropdownItem.setLabel(
								LanguageUtil.get(
									httpServletRequest, "public-page"));
						});
				}

				add(
					dropdownItem -> {
						dropdownItem.setHref(
							getSelectLayoutPageTemplateEntryURL(true));
						dropdownItem.setLabel(
							LanguageUtil.get(
								httpServletRequest, "private-page"));
					});
			}
		};
	}

	public String getAddLayoutURL() {
		PortletURL portletURL = _liferayPortletResponse.createActionURL();

		portletURL.setParameter(
			"mvcPath", "/select_layout_page_template_entry.jsp");
		portletURL.setParameter("backURL", _getBackURL());
		portletURL.setParameter("portletResource", getPortletResource());
		portletURL.setParameter("groupId", String.valueOf(getGroupId()));
		portletURL.setParameter(
			"liveGroupId", String.valueOf(getLiveGroupId()));
		portletURL.setParameter(
			"stagingGroupId", String.valueOf(getStagingGroupId()));
		portletURL.setParameter(
			"parentLayoutId", String.valueOf(getParentLayoutId()));
		portletURL.setParameter(
			"privateLayout", String.valueOf(isPrivateLayout()));
		portletURL.setParameter("explicitCreation", Boolean.TRUE.toString());

		String type = ParamUtil.getString(httpServletRequest, "type");

		if (Validator.isNotNull(type)) {
			portletURL.setParameter("type", type);
		}

		long layoutPageTemplateEntryId = ParamUtil.getLong(
			httpServletRequest, "layoutPageTemplateEntryId");

		portletURL.setParameter(
			"layoutPageTemplateEntryId",
			String.valueOf(layoutPageTemplateEntryId));

		long masterLayoutPlid = ParamUtil.getLong(
			httpServletRequest, "masterLayoutPlid");

		portletURL.setParameter(
			"masterLayoutPlid", String.valueOf(masterLayoutPlid));

		if (layoutPageTemplateEntryId > 0) {
			portletURL.setParameter(
				ActionRequest.ACTION_NAME, "/layout/add_content_layout");
		}
		else {
			portletURL.setParameter(
				ActionRequest.ACTION_NAME, "/layout/add_simple_layout");
		}

		return portletURL.toString();
	}

	public String getAutoSiteNavigationMenuNames() {
		List<SiteNavigationMenu> siteNavigationMenus =
			SiteNavigationMenuLocalServiceUtil.getAutoSiteNavigationMenus(
				themeDisplay.getScopeGroupId());

		return ListUtil.toString(
			siteNavigationMenus, SiteNavigationMenu.NAME_ACCESSOR,
			StringPool.COMMA_AND_SPACE);
	}

	public List<SiteNavigationMenu> getAutoSiteNavigationMenus() {
		return SiteNavigationMenuLocalServiceUtil.getAutoSiteNavigationMenus(
			themeDisplay.getScopeGroupId());
	}

	public JSONArray getBreadcrumbEntriesJSONArray() throws PortalException {
		boolean privatePages = isPrivateLayout();

		Layout selLayout = getSelLayout();

		if (selLayout != null) {
			privatePages = selLayout.isPrivateLayout();
		}

		JSONObject breadcrumbEntryJSONObject = JSONUtil.put(
			"title", LanguageUtil.get(httpServletRequest, "pages"));

		PortletURL portletURL = _liferayPortletResponse.createRenderURL();

		portletURL.setParameter("tabs1", getTabs1());
		portletURL.setParameter(
			"selPlid", String.valueOf(LayoutConstants.DEFAULT_PLID));

		String displayStyle = getDisplayStyle();

		if (Validator.isNotNull(displayStyle)) {
			portletURL.setParameter("displayStyle", displayStyle);
		}

		portletURL.setParameter("firstColumn", Boolean.TRUE.toString());

		breadcrumbEntryJSONObject.put("url", portletURL.toString());

		JSONArray breadcrumbEntriesJSONArray = JSONUtil.put(
			breadcrumbEntryJSONObject);

		if (isFirstColumn()) {
			return breadcrumbEntriesJSONArray;
		}

		breadcrumbEntriesJSONArray.put(
			_getBreadcrumbEntryJSONObject(
				LayoutConstants.DEFAULT_PLID, privatePages,
				getTitle(privatePages)));

		if ((getSelPlid() == LayoutConstants.DEFAULT_PLID) ||
			(selLayout == null)) {

			return breadcrumbEntriesJSONArray;
		}

		List<Layout> layouts = selLayout.getAncestors();

		Collections.reverse(layouts);

		for (Layout layout : layouts) {
			breadcrumbEntriesJSONArray.put(
				_getBreadcrumbEntryJSONObject(
					layout.getPlid(), layout.isPrivateLayout(),
					layout.getName(themeDisplay.getLocale())));
		}

		breadcrumbEntriesJSONArray.put(
			_getBreadcrumbEntryJSONObject(
				selLayout.getPlid(), selLayout.isPrivateLayout(),
				selLayout.getName(themeDisplay.getLocale())));

		return breadcrumbEntriesJSONArray;
	}

	public String getConfigureLayoutURL(Layout layout) {
		PortletURL configureLayoutURL =
			_liferayPortletResponse.createRenderURL();

		configureLayoutURL.setParameter(
			"mvcRenderCommandName", "/layout/edit_layout");
		configureLayoutURL.setParameter(
			"redirect", themeDisplay.getURLCurrent());
		configureLayoutURL.setParameter(
			"backURL", themeDisplay.getURLCurrent());

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		configureLayoutURL.setParameter(
			"portletResource", portletDisplay.getId());

		configureLayoutURL.setParameter(
			"groupId", String.valueOf(layout.getGroupId()));
		configureLayoutURL.setParameter(
			"selPlid", String.valueOf(layout.getPlid()));
		configureLayoutURL.setParameter(
			"privateLayout", String.valueOf(layout.isPrivateLayout()));

		return configureLayoutURL.toString();
	}

	public String getConvertLayoutURL(Layout layout) {
		PortletURL convertLayoutURL = _liferayPortletResponse.createActionURL();

		convertLayoutURL.setParameter(
			ActionRequest.ACTION_NAME, "/layout/convert_layout");
		convertLayoutURL.setParameter("redirect", themeDisplay.getURLCurrent());
		convertLayoutURL.setParameter(
			"selPlid", String.valueOf(layout.getPlid()));

		return convertLayoutURL.toString();
	}

	public String getCopyLayoutRenderURL(Layout layout) throws Exception {
		PortletURL copyLayoutRenderURL =
			_liferayPortletResponse.createActionURL();

		copyLayoutRenderURL.setParameter(
			"mvcRenderCommandName", "/layout/add_layout");
		copyLayoutRenderURL.setParameter(
			"sourcePlid", String.valueOf(layout.getPlid()));
		copyLayoutRenderURL.setParameter(
			"privateLayout", String.valueOf(isPrivateLayout()));
		copyLayoutRenderURL.setWindowState(LiferayWindowState.POP_UP);

		return copyLayoutRenderURL.toString();
	}

	public String getCopyLayoutURL(long sourcePlid) {
		PortletURL copyLayoutURL = _liferayPortletResponse.createActionURL();

		copyLayoutURL.setParameter(
			ActionRequest.ACTION_NAME, "/layout/copy_layout");
		copyLayoutURL.setParameter("sourcePlid", String.valueOf(sourcePlid));
		copyLayoutURL.setParameter("groupId", String.valueOf(getGroupId()));
		copyLayoutURL.setParameter(
			"liveGroupId", String.valueOf(getLiveGroupId()));
		copyLayoutURL.setParameter(
			"stagingGroupId", String.valueOf(getStagingGroupId()));
		copyLayoutURL.setParameter(
			"privateLayout", String.valueOf(isPrivateLayout()));
		copyLayoutURL.setParameter(
			"explicitCreation", String.valueOf(Boolean.TRUE));

		return copyLayoutURL.toString();
	}

	public String getDeleteLayoutURL(Layout layout) throws PortalException {
		PortletURL deleteLayoutURL = _liferayPortletResponse.createActionURL();

		deleteLayoutURL.setParameter(
			ActionRequest.ACTION_NAME, "/layout/delete_layout");

		PortletURL redirectURL = _liferayPortletResponse.createRenderURL();

		redirectURL.setParameter(
			"selPlid", String.valueOf(layout.getParentPlid()));
		redirectURL.setParameter(
			"layoutSetBranchId", String.valueOf(getActiveLayoutSetBranchId()));

		deleteLayoutURL.setParameter("redirect", redirectURL.toString());

		deleteLayoutURL.setParameter(
			"selPlid", String.valueOf(layout.getPlid()));
		deleteLayoutURL.setParameter(
			"layoutSetBranchId", String.valueOf(getActiveLayoutSetBranchId()));

		return deleteLayoutURL.toString();
	}

	public String getDisplayStyle() {
		if (Validator.isNotNull(_displayStyle)) {
			return _displayStyle;
		}

		_displayStyle = ParamUtil.getString(
			httpServletRequest, "displayStyle", "miller-columns");

		return _displayStyle;
	}

	public String getEditLayoutURL(Layout layout) throws Exception {
		if (layout.isTypeContent()) {
			return _getDraftLayoutURL(layout);
		}

		if (!Objects.equals(layout.getType(), LayoutConstants.TYPE_PORTLET)) {
			return StringPool.BLANK;
		}

		Layout draftLayout = LayoutLocalServiceUtil.fetchLayout(
			PortalUtil.getClassNameId(Layout.class), layout.getPlid());

		if (draftLayout == null) {
			return StringPool.BLANK;
		}

		return _getDraftLayoutURL(layout);
	}

	public String getFirstColumnConfigureLayoutURL(boolean privatePages) {
		PortletURL editLayoutSetURL = _liferayPortletResponse.createRenderURL();

		editLayoutSetURL.setParameter(
			"mvcRenderCommandName", "/layout/edit_layout_set");
		editLayoutSetURL.setParameter("redirect", themeDisplay.getURLCurrent());
		editLayoutSetURL.setParameter("backURL", themeDisplay.getURLCurrent());
		editLayoutSetURL.setParameter(
			"groupId", String.valueOf(themeDisplay.getScopeGroupId()));
		editLayoutSetURL.setParameter(
			"privateLayout", String.valueOf(privatePages));

		return editLayoutSetURL.toString();
	}

	public SearchContainer getFirstColumnLayoutsSearchContainer() {
		if (_layoutsSearchContainer != null) {
			return _layoutsSearchContainer;
		}

		SearchContainer layoutsSearchContainer = new SearchContainer(
			_liferayPortletRequest, getPortletURL(), null, StringPool.BLANK);

		List<String> results = new ArrayList<>();

		if (isShowPublicPages()) {
			results.add("public-pages");
		}

		results.add("private-pages");

		layoutsSearchContainer.setTotal(results.size());
		layoutsSearchContainer.setResults(results);

		_layoutsSearchContainer = layoutsSearchContainer;

		return _layoutsSearchContainer;
	}

	public long getFirstLayoutPageTemplateCollectionId() {
		LayoutPageTemplateCollectionNameComparator
			layoutPageTemplateCollectionNameComparator =
				new LayoutPageTemplateCollectionNameComparator(true);

		List<LayoutPageTemplateCollection> layoutPageTemplateCollections =
			LayoutPageTemplateCollectionLocalServiceUtil.
				getLayoutPageTemplateCollections(
					getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					layoutPageTemplateCollectionNameComparator);

		if (layoutPageTemplateCollections.isEmpty()) {
			return 0;
		}

		for (LayoutPageTemplateCollection layoutPageTemplateCollection :
				layoutPageTemplateCollections) {

			int layoutPageTemplateEntriesCount =
				LayoutPageTemplateEntryServiceUtil.
					getLayoutPageTemplateEntriesCount(
						themeDisplay.getScopeGroupId(),
						layoutPageTemplateCollection.
							getLayoutPageTemplateCollectionId(),
						WorkflowConstants.STATUS_APPROVED);

			if (layoutPageTemplateEntriesCount > 0) {
				return layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId();
			}
		}

		return 0;
	}

	public String getFriendlyURLBase() {
		StringBuilder friendlyURLBase = new StringBuilder();

		friendlyURLBase.append(themeDisplay.getPortalURL());

		Layout selLayout = getSelLayout();

		LayoutSet layoutSet = selLayout.getLayoutSet();

		TreeMap<String, String> virtualHostnames =
			layoutSet.getVirtualHostnames();

		if (virtualHostnames.isEmpty() ||
			!_matchesHostname(friendlyURLBase, virtualHostnames)) {

			Group group = getGroup();

			friendlyURLBase.append(
				group.getPathFriendlyURL(isPrivateLayout(), themeDisplay));
			friendlyURLBase.append(HttpUtil.decodeURL(group.getFriendlyURL()));
		}

		return friendlyURLBase.toString();
	}

	public Group getGroup() {
		return _groupDisplayContextHelper.getGroup();
	}

	public Long getGroupId() {
		return _groupDisplayContextHelper.getGroupId();
	}

	public UnicodeProperties getGroupTypeSettings() {
		return _groupDisplayContextHelper.getGroupTypeSettings();
	}

	public LayoutSet getGuestGroupLayoutSet(long companyId)
		throws PortalException {

		Group group = GroupLocalServiceUtil.getGroup(
			companyId, GroupConstants.GUEST);

		return LayoutSetLocalServiceUtil.getLayoutSet(
			group.getGroupId(), isPrivateLayout());
	}

	public String getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(httpServletRequest, "keywords");

		return _keywords;
	}

	public String getLayoutChildrenURL() {
		PortletURL itemChildrenURL = _liferayPortletResponse.createActionURL();

		itemChildrenURL.setParameter(
			ActionRequest.ACTION_NAME, "/layout/get_layout_children");

		return itemChildrenURL.toString();
	}

	public JSONArray getLayoutColumnsJSONArray() throws Exception {
		JSONArray layoutColumnsJSONArray = _getLayoutColumnsJSONArray();

		while (layoutColumnsJSONArray.length() < 3) {
			layoutColumnsJSONArray.put(JSONFactoryUtil.createJSONArray());
		}

		return layoutColumnsJSONArray;
	}

	public String getLayoutConversionPreviewURL(Layout layout) {
		PortletURL layoutConversionPreviewURL =
			_liferayPortletResponse.createActionURL();

		layoutConversionPreviewURL.setParameter(
			ActionRequest.ACTION_NAME, "/layout/layout_conversion_preview");

		layoutConversionPreviewURL.setParameter(
			"redirect", themeDisplay.getURLCurrent());
		layoutConversionPreviewURL.setParameter(
			"selPlid", String.valueOf(layout.getPlid()));

		return layoutConversionPreviewURL.toString();
	}

	public LayoutConverterConfiguration getLayoutConverterConfiguration() {
		return _layoutConverterConfiguration;
	}

	public List<LayoutDescription> getLayoutDescriptions() {
		if (_layoutDescriptions != null) {
			return _layoutDescriptions;
		}

		_layoutDescriptions = LayoutListUtil.getLayoutDescriptions(
			getGroupId(), isPrivateLayout(), getRootNodeName(),
			themeDisplay.getLocale());

		return _layoutDescriptions;
	}

	public String getLayoutEditorType() {
		return _layoutEditorTypeConfiguration.type();
	}

	public Long getLayoutId() {
		if (_layoutId != null) {
			return _layoutId;
		}

		_layoutId = LayoutConstants.DEFAULT_PARENT_LAYOUT_ID;

		Layout selLayout = getSelLayout();

		if (selLayout != null) {
			_layoutId = selLayout.getLayoutId();
		}

		return _layoutId;
	}

	public JSONArray getLayoutsJSONArray(
			long parentLayoutId, boolean privateLayout)
		throws Exception {

		JSONArray layoutsJSONArray = JSONFactoryUtil.createJSONArray();

		List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(
			getSelGroupId(), privateLayout, parentLayoutId, true,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		for (Layout layout : layouts) {
			if (getActiveLayoutSetBranchId() > 0) {
				LayoutRevision layoutRevision =
					LayoutStagingUtil.getLayoutRevision(layout);

				if ((layoutRevision != null) && layoutRevision.isIncomplete()) {
					continue;
				}
			}

			JSONObject layoutJSONObject = JSONUtil.put(
				"actions", StringUtil.merge(getAvailableActions(layout))
			).put(
				"actionURLs", _getActionURLsJSONObject(layout)
			).put(
				"active", isActive(layout.getPlid())
			);

			LayoutTypeController layoutTypeController =
				LayoutTypeControllerTracker.getLayoutTypeController(
					layout.getType());

			ResourceBundle layoutTypeResourceBundle =
				ResourceBundleUtil.getBundle(
					"content.Language", themeDisplay.getLocale(),
					layoutTypeController.getClass());

			layoutJSONObject.put(
				"description",
				LanguageUtil.get(
					httpServletRequest, layoutTypeResourceBundle,
					"layout.types." + layout.getType()));

			if (layout.isTypeContent()) {
				Layout draftLayout = LayoutLocalServiceUtil.fetchLayout(
					PortalUtil.getClassNameId(Layout.class), layout.getPlid());

				boolean published = GetterUtil.getBoolean(
					draftLayout.getTypeSettingsProperty("published"));

				layoutJSONObject.put(
					"conversionPreview", false
				).put(
					"draft",
					(draftLayout.getStatus() ==
						WorkflowConstants.STATUS_DRAFT) ||
					!published
				);
			}
			else {
				Layout draftLayout = LayoutLocalServiceUtil.fetchLayout(
					PortalUtil.getClassNameId(Layout.class), layout.getPlid());

				layoutJSONObject.put(
					"conversionPreview", draftLayout != null
				).put(
					"draft", false
				);
			}

			int childLayoutsCount = LayoutLocalServiceUtil.getLayoutsCount(
				getSelGroup(), layout.isPrivateLayout(), layout.getLayoutId());

			layoutJSONObject.put("hasChild", childLayoutsCount > 0);

			LayoutType layoutType = layout.getLayoutType();

			layoutJSONObject.put(
				"parentable", layoutType.isParentable()
			).put(
				"pending", layout.isDenied() || layout.isPending()
			).put(
				"plid", layout.getPlid()
			);

			PortletURL portletURL = getPortletURL();

			portletURL.setParameter(
				"selPlid", String.valueOf(layout.getPlid()));
			portletURL.setParameter(
				"layoutSetBranchId",
				String.valueOf(getActiveLayoutSetBranchId()));
			portletURL.setParameter(
				"privateLayout", String.valueOf(layout.isPrivateLayout()));

			layoutJSONObject.put(
				"title", layout.getName(themeDisplay.getLocale())
			).put(
				"url", portletURL.toString()
			);

			layoutsJSONArray.put(layoutJSONObject);
		}

		return layoutsJSONArray;
	}

	public SearchContainer getLayoutsSearchContainer() throws PortalException {
		if (_layoutsSearchContainer != null) {
			return _layoutsSearchContainer;
		}

		String emptyResultMessage = "there-are-no-public-pages";

		if (isPrivateLayout()) {
			emptyResultMessage = "there-are-no-private-pages";
		}

		SearchContainer layoutsSearchContainer = new SearchContainer(
			_liferayPortletRequest, getPortletURL(), null, emptyResultMessage);

		layoutsSearchContainer.setOrderByCol(_getOrderByCol());

		String orderByType = _getOrderByType();

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		OrderByComparator<Layout> orderByComparator = null;

		if (Objects.equals(_getOrderByCol(), "create-date")) {
			orderByComparator = new LayoutCreateDateComparator(orderByAsc);
		}

		layoutsSearchContainer.setOrderByComparator(orderByComparator);

		layoutsSearchContainer.setOrderByType(_getOrderByType());

		EmptyOnClickRowChecker emptyOnClickRowChecker =
			new EmptyOnClickRowChecker(_liferayPortletResponse);

		layoutsSearchContainer.setRowChecker(emptyOnClickRowChecker);

		int layoutsCount = LayoutLocalServiceUtil.getLayoutsCount(
			getSelGroup(), isPrivateLayout(), getKeywords(),
			new String[] {
				LayoutConstants.TYPE_CONTENT, LayoutConstants.TYPE_EMBEDDED,
				LayoutConstants.TYPE_LINK_TO_LAYOUT,
				LayoutConstants.TYPE_FULL_PAGE_APPLICATION,
				LayoutConstants.TYPE_PANEL, LayoutConstants.TYPE_PORTLET,
				LayoutConstants.TYPE_URL
			});

		List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(
			getSelGroupId(), isPrivateLayout(), getKeywords(),
			new String[] {
				LayoutConstants.TYPE_CONTENT, LayoutConstants.TYPE_EMBEDDED,
				LayoutConstants.TYPE_LINK_TO_LAYOUT,
				LayoutConstants.TYPE_FULL_PAGE_APPLICATION,
				LayoutConstants.TYPE_PANEL, LayoutConstants.TYPE_PORTLET,
				LayoutConstants.TYPE_URL
			},
			layoutsSearchContainer.getStart(), layoutsSearchContainer.getEnd(),
			layoutsSearchContainer.getOrderByComparator());

		layoutsSearchContainer.setTotal(layoutsCount);
		layoutsSearchContainer.setResults(layouts);

		_layoutsSearchContainer = layoutsSearchContainer;

		return _layoutsSearchContainer;
	}

	public Group getLiveGroup() {
		return _groupDisplayContextHelper.getLiveGroup();
	}

	public Long getLiveGroupId() {
		return _groupDisplayContextHelper.getLiveGroupId();
	}

	public String getMoveLayoutColumnItemURL() {
		PortletURL deleteLayoutURL = _liferayPortletResponse.createActionURL();

		deleteLayoutURL.setParameter(
			ActionRequest.ACTION_NAME, "/layout/move_layout");
		deleteLayoutURL.setParameter("redirect", themeDisplay.getURLCurrent());

		return deleteLayoutURL.toString();
	}

	public String getOrphanPortletsURL(Layout layout) {
		PortletURL orphanPortletsURL =
			_liferayPortletResponse.createRenderURL();

		orphanPortletsURL.setParameter("mvcPath", "/orphan_portlets.jsp");
		orphanPortletsURL.setParameter("backURL", themeDisplay.getURLCurrent());
		orphanPortletsURL.setParameter(
			"selPlid", String.valueOf(layout.getPlid()));

		return orphanPortletsURL.toString();
	}

	public long getParentLayoutId() {
		if (_parentLayoutId != null) {
			return _parentLayoutId;
		}

		_parentLayoutId = Long.valueOf(0);

		Layout layout = getSelLayout();

		if (layout != null) {
			_parentLayoutId = layout.getLayoutId();
		}

		return _parentLayoutId;
	}

	public String getPath(Layout layout, Locale locale) throws PortalException {
		List<Layout> layouts = layout.getAncestors();

		StringBundler sb = new StringBundler(layouts.size() * 4);

		for (Layout curLayout : layouts) {
			sb.append(curLayout.getName(locale));
			sb.append(StringPool.SPACE);
			sb.append(StringPool.GREATER_THAN);
			sb.append(StringPool.SPACE);
		}

		return sb.toString();
	}

	public String getPermissionsURL(Layout layout) throws Exception {
		return PermissionsURLTag.doTag(
			StringPool.BLANK, Layout.class.getName(),
			HtmlUtil.escape(layout.getName(themeDisplay.getLocale())), null,
			String.valueOf(layout.getPlid()),
			LiferayWindowState.POP_UP.toString(), null,
			themeDisplay.getRequest());
	}

	public List<BreadcrumbEntry> getPortletBreadcrumbEntries()
		throws PortalException {

		List<BreadcrumbEntry> breadcrumbEntries = new ArrayList<>();

		JSONArray breadcrumbEntriesJSONArray = getBreadcrumbEntriesJSONArray();

		for (int i = 0; i < breadcrumbEntriesJSONArray.length(); i++) {
			JSONObject breadcrumbEntryJSONObject =
				breadcrumbEntriesJSONArray.getJSONObject(i);

			BreadcrumbEntry breadcrumbEntry = new BreadcrumbEntry();

			breadcrumbEntry.setTitle(
				breadcrumbEntryJSONObject.getString("title"));
			breadcrumbEntry.setURL(breadcrumbEntryJSONObject.getString("url"));

			breadcrumbEntries.add(breadcrumbEntry);
		}

		return breadcrumbEntries;
	}

	public String getPortletResource() {
		String portletResource = ParamUtil.getString(
			httpServletRequest, "portletResource");

		if (Validator.isNull(portletResource)) {
			PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

			portletResource = portletDisplay.getPortletName();
		}

		return portletResource;
	}

	public PortletURL getPortletURL() {
		PortletURL portletURL = _liferayPortletResponse.createRenderURL();

		portletURL.setParameter("tabs1", getTabs1());
		portletURL.setParameter(
			"privateLayout", String.valueOf(isPrivateLayout()));

		String displayStyle = getDisplayStyle();

		if (Validator.isNotNull(displayStyle)) {
			portletURL.setParameter("displayStyle", displayStyle);
		}

		return portletURL;
	}

	public String getRedirect() {
		if (_redirect != null) {
			return _redirect;
		}

		_redirect = ParamUtil.getString(
			_liferayPortletRequest, "redirect", themeDisplay.getURLCurrent());

		return _redirect;
	}

	public PortletURL getRedirectURL() {
		PortletURL portletURL = _liferayPortletResponse.createRenderURL();

		portletURL.setParameter("redirect", getRedirect());
		portletURL.setParameter("groupId", String.valueOf(getSelGroupId()));

		return portletURL;
	}

	public List<BreadcrumbEntry> getRelativeBreadcrumbEntries(Layout layout)
		throws PortalException {

		List<BreadcrumbEntry> breadcrumbEntries = new ArrayList<>();

		List<Layout> curLayouts = layout.getAncestors();

		Collections.reverse(curLayouts);

		boolean showLayoutPath = false;

		if (getSelPlid() <= 0) {
			showLayoutPath = true;
		}

		for (Layout curLayout : curLayouts) {
			if (showLayoutPath) {
				BreadcrumbEntry breadcrumbEntry = new BreadcrumbEntry();

				breadcrumbEntry.setTitle(
					curLayout.getName(themeDisplay.getLocale()));

				breadcrumbEntries.add(breadcrumbEntry);
			}

			if (curLayout.getPlid() == getSelPlid()) {
				showLayoutPath = true;
			}
		}

		BreadcrumbEntry breadcrumbEntry = new BreadcrumbEntry();

		breadcrumbEntry.setTitle(layout.getName(themeDisplay.getLocale()));

		breadcrumbEntries.add(breadcrumbEntry);

		return breadcrumbEntries;
	}

	public String getRobots() {
		String robots = StringPool.BLANK;

		try {
			robots = ParamUtil.getString(
				httpServletRequest, "robots",
				RobotsUtil.getRobots(
					getSelLayoutSet(), httpServletRequest.isSecure()));
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException, portalException);
			}
		}

		return robots;
	}

	public String getRootNodeName() {
		if (_rootNodeName != null) {
			return _rootNodeName;
		}

		_rootNodeName = getRootNodeName(isPrivateLayout());

		return _rootNodeName;
	}

	public String getRootNodeName(boolean privateLayout) {
		Group liveGroup = getLiveGroup();

		return liveGroup.getLayoutRootNodeName(
			privateLayout, themeDisplay.getLocale());
	}

	public PortletURL getScreenNavigationPortletURL() {
		PortletURL portletURL = getPortletURL();

		portletURL.setParameter("mvcRenderCommandName", "/layout/edit_layout");
		portletURL.setParameter("selPlid", String.valueOf(getSelPlid()));

		return portletURL;
	}

	public String getSelectLayoutPageTemplateEntryURL(boolean privateLayout)
		throws PortalException {

		return getSelectLayoutPageTemplateEntryURL(
			getFirstLayoutPageTemplateCollectionId(), privateLayout);
	}

	public String getSelectLayoutPageTemplateEntryURL(
		long layoutPageTemplateCollectionId, boolean privateLayout) {

		return getSelectLayoutPageTemplateEntryURL(
			layoutPageTemplateCollectionId, LayoutConstants.DEFAULT_PLID,
			privateLayout);
	}

	public String getSelectLayoutPageTemplateEntryURL(
		long layoutPageTemplateCollectionId, long selPlid,
		boolean privateLayout) {

		return getSelectLayoutPageTemplateEntryURL(
			layoutPageTemplateCollectionId, selPlid, "basic-templates",
			privateLayout);
	}

	public String getSelectLayoutPageTemplateEntryURL(
		long layoutPageTemplateCollectionId, long selPlid, String selectedTab,
		boolean privateLayout) {

		PortletURL selectLayoutPageTemplateEntryURL =
			_liferayPortletResponse.createRenderURL();

		selectLayoutPageTemplateEntryURL.setParameter(
			"mvcPath", "/select_layout_page_template_entry.jsp");
		selectLayoutPageTemplateEntryURL.setParameter(
			"redirect", getRedirect());
		selectLayoutPageTemplateEntryURL.setParameter(
			"backURL", themeDisplay.getURLCurrent());
		selectLayoutPageTemplateEntryURL.setParameter(
			"groupId", String.valueOf(getSelGroupId()));
		selectLayoutPageTemplateEntryURL.setParameter(
			"selPlid", String.valueOf(selPlid));
		selectLayoutPageTemplateEntryURL.setParameter(
			"privateLayout", String.valueOf(privateLayout));

		if (layoutPageTemplateCollectionId > 0) {
			selectLayoutPageTemplateEntryURL.setParameter(
				"layoutPageTemplateCollectionId",
				String.valueOf(layoutPageTemplateCollectionId));
		}
		else if (Validator.isNotNull(selectedTab)) {
			selectLayoutPageTemplateEntryURL.setParameter(
				"selectedTab", selectedTab);
		}

		return selectLayoutPageTemplateEntryURL.toString();
	}

	public Group getSelGroup() {
		return _groupDisplayContextHelper.getSelGroup();
	}

	public long getSelGroupId() {
		Group selGroup = getSelGroup();

		if (selGroup != null) {
			return selGroup.getGroupId();
		}

		return 0;
	}

	public Layout getSelLayout() {
		if (_selLayout != null) {
			return _selLayout;
		}

		if (getSelPlid() != LayoutConstants.DEFAULT_PLID) {
			_selLayout = LayoutLocalServiceUtil.fetchLayout(getSelPlid());
		}

		return _selLayout;
	}

	public LayoutSet getSelLayoutSet() throws PortalException {
		if (_selLayoutSet != null) {
			return _selLayoutSet;
		}

		Group group = getStagingGroup();

		if (group == null) {
			group = getLiveGroup();
		}

		_selLayoutSet = LayoutSetLocalServiceUtil.getLayoutSet(
			group.getGroupId(), isPrivateLayout());

		return _selLayoutSet;
	}

	public Long getSelPlid() {
		if (_selPlid != null) {
			return _selPlid;
		}

		_selPlid = ParamUtil.getLong(
			_liferayPortletRequest, "selPlid", LayoutConstants.DEFAULT_PLID);

		return _selPlid;
	}

	public Group getStagingGroup() {
		return _groupDisplayContextHelper.getStagingGroup();
	}

	public Long getStagingGroupId() {
		return _groupDisplayContextHelper.getStagingGroupId();
	}

	public String getTabs1() {
		if (_tabs1 != null) {
			return _tabs1;
		}

		_tabs1 = ParamUtil.getString(_liferayPortletRequest, "tabs1", "pages");

		return _tabs1;
	}

	public String getTitle(boolean privatePages) {
		String title = "pages";

		if (isShowPublicPages()) {
			if (privatePages) {
				title = "private-pages";
			}
			else {
				title = "public-pages";
			}
		}

		return LanguageUtil.get(httpServletRequest, title);
	}

	public int getTotalItems() throws Exception {
		return LayoutLocalServiceUtil.getLayoutsCount(
			getSelGroup(), isPrivateLayout());
	}

	public String getViewLayoutURL(Layout layout) throws PortalException {
		String layoutFullURL = null;

		if (layout.getStatus() == WorkflowConstants.STATUS_PENDING) {
			Layout draftLayout = LayoutLocalServiceUtil.fetchLayout(
				PortalUtil.getClassNameId(Layout.class), layout.getPlid());

			layoutFullURL = PortalUtil.getLayoutFullURL(
				draftLayout, themeDisplay);
		}
		else {
			layoutFullURL = PortalUtil.getLayoutFullURL(layout, themeDisplay);
		}

		try {
			layoutFullURL = HttpUtil.setParameter(
				layoutFullURL, "p_l_back_url", themeDisplay.getURLCurrent());
		}
		catch (Exception exception) {
			_log.error(
				"Unable to generate view layout URL for " + layoutFullURL,
				exception);
		}

		return layoutFullURL;
	}

	public String getVirtualHostname() {
		String virtualHostname = StringPool.BLANK;

		try {
			LayoutSet layoutSet = getSelLayoutSet();

			virtualHostname = PortalUtil.getVirtualHostname(layoutSet);

			Group scopeGroup = themeDisplay.getScopeGroup();

			if (Validator.isNull(virtualHostname) &&
				scopeGroup.isStagingGroup()) {

				Group liveGroup = scopeGroup.getLiveGroup();

				LayoutSet liveGroupLayoutSet = liveGroup.getPublicLayoutSet();

				if (layoutSet.isPrivateLayout()) {
					liveGroupLayoutSet = liveGroup.getPrivateLayoutSet();
				}

				virtualHostname = PortalUtil.getVirtualHostname(
					liveGroupLayoutSet);
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException, portalException);
			}
		}

		return virtualHostname;
	}

	public boolean hasLayouts() {
		int privatePagesCount = LayoutLocalServiceUtil.getLayoutsCount(
			getSelGroup(), true, 0);

		int publicPagesCount = LayoutLocalServiceUtil.getLayoutsCount(
			getSelGroup(), false, 0);

		if ((privatePagesCount + publicPagesCount) > 0) {
			return true;
		}

		return false;
	}

	public boolean hasRequiredVocabularies() {
		long classNameId = PortalUtil.getClassNameId(Layout.class);

		List<AssetVocabulary> assetVocabularies =
			AssetVocabularyServiceUtil.getGroupVocabularies(_getGroupIds());

		for (AssetVocabulary assetVocabulary : assetVocabularies) {
			if (assetVocabulary.isAssociatedToClassNameId(classNameId) &&
				assetVocabulary.isRequired(classNameId, 0)) {

				return true;
			}
		}

		return false;
	}

	public boolean isConversionDraft(Layout layout) {
		Layout draftLayout = LayoutLocalServiceUtil.fetchLayout(
			PortalUtil.getClassNameId(Layout.class), layout.getPlid());

		if (Objects.equals(layout.getType(), LayoutConstants.TYPE_PORTLET) &&
			(draftLayout != null)) {

			return true;
		}

		return false;
	}

	public boolean isDraft() {
		Layout layout = getSelLayout();

		if (layout.isSystem() && (layout.getClassPK() > 0) &&
			(layout.getClassNameId() == PortalUtil.getClassNameId(
				Layout.class))) {

			return true;
		}

		return false;
	}

	public boolean isFirstColumn() {
		if (_firstColumn != null) {
			return _firstColumn;
		}

		_firstColumn = ParamUtil.getBoolean(httpServletRequest, "firstColumn");

		return _firstColumn;
	}

	public boolean isLayoutPageTemplateEntry() {
		Layout layout = getSelLayout();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchLayoutPageTemplateEntryByPlid(layout.getPlid());

		if (layout.isTypeAssetDisplay() ||
			((layoutPageTemplateEntry != null) && layout.isSystem())) {

			return true;
		}

		return false;
	}

	public boolean isPagesTab() {
		if (Objects.equals(getTabs1(), "pages")) {
			return true;
		}

		return false;
	}

	public boolean isPrivateLayout() {
		if (_privateLayout != null) {
			return _privateLayout;
		}

		Group selGroup = getSelGroup();

		if (selGroup.isLayoutSetPrototype()) {
			_privateLayout = true;

			return _privateLayout;
		}

		Layout selLayout = getSelLayout();

		if (getSelLayout() != null) {
			_privateLayout = selLayout.isPrivateLayout();

			return _privateLayout;
		}

		Layout layout = themeDisplay.getLayout();

		if (!layout.isTypeControlPanel()) {
			_privateLayout = layout.isPrivateLayout();

			return _privateLayout;
		}

		_privateLayout = ParamUtil.getBoolean(
			_liferayPortletRequest, "privateLayout");

		return _privateLayout;
	}

	public boolean isSearch() {
		if (Validator.isNotNull(getKeywords())) {
			return true;
		}

		return false;
	}

	public boolean isShowAddChildPageAction(Layout layout)
		throws PortalException {

		return LayoutPermissionUtil.contains(
			themeDisplay.getPermissionChecker(), layout, ActionKeys.ADD_LAYOUT);
	}

	public boolean isShowAddRootLayoutButton() throws PortalException {
		return GroupPermissionUtil.contains(
			themeDisplay.getPermissionChecker(), getSelGroup(),
			ActionKeys.ADD_LAYOUT);
	}

	public boolean isShowCategorization() {
		long classNameId = PortalUtil.getClassNameId(Layout.class);

		List<AssetVocabulary> assetVocabularies =
			AssetVocabularyServiceUtil.getGroupVocabularies(_getGroupIds());

		for (AssetVocabulary assetVocabulary : assetVocabularies) {
			if (assetVocabulary.isAssociatedToClassNameId(classNameId) &&
				assetVocabulary.isRequired(classNameId, 0)) {

				int assetVocabularyCategoriesCount =
					AssetCategoryServiceUtil.getVocabularyCategoriesCount(
						assetVocabulary.getGroupId(),
						assetVocabulary.getVocabularyId());

				if (assetVocabularyCategoriesCount > 0) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean isShowConfigureAction(Layout layout) throws PortalException {
		return LayoutPermissionUtil.contains(
			themeDisplay.getPermissionChecker(), layout, ActionKeys.UPDATE);
	}

	public boolean isShowConvertLayoutAction(Layout layout) {
		if (_isLiveGroup()) {
			return false;
		}

		if (!_layoutConverterConfiguration.enabled()) {
			return false;
		}

		if (!Objects.equals(layout.getType(), LayoutConstants.TYPE_PORTLET)) {
			return false;
		}

		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		String layoutTemplateId = typeSettingsUnicodeProperties.getProperty(
			LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID);

		LayoutConverter layoutConverter =
			_layoutConverterRegistry.getLayoutConverter(layoutTemplateId);

		if ((layoutConverter == null) ||
			!layoutConverter.isConvertible(layout)) {

			return false;
		}

		return true;
	}

	public boolean isShowCopyLayoutAction(Layout layout)
		throws PortalException {

		if (!isShowAddRootLayoutButton()) {
			return false;
		}

		if (!layout.isTypePortlet()) {
			return false;
		}

		if (layout.isTypeContent()) {
			Layout draftLayout = LayoutLocalServiceUtil.fetchLayout(
				PortalUtil.getClassNameId(Layout.class), layout.getPlid());

			boolean published = false;

			if (draftLayout != null) {
				published = GetterUtil.getBoolean(
					draftLayout.getTypeSettingsProperty("published"));
			}

			return published;
		}

		return true;
	}

	public boolean isShowDeleteAction(Layout layout) throws PortalException {
		if (StagingUtil.isIncomplete(layout)) {
			return false;
		}

		if (!LayoutPermissionUtil.contains(
				themeDisplay.getPermissionChecker(), layout,
				ActionKeys.DELETE)) {

			return false;
		}

		Group group = layout.getGroup();

		int layoutsCount = LayoutLocalServiceUtil.getLayoutsCount(
			group, false, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

		if (group.isGuest() && !layout.isPrivateLayout() &&
			layout.isRootLayout() && (layoutsCount == 1)) {

			return false;
		}

		return true;
	}

	public boolean isShowFirstColumnConfigureAction() throws PortalException {
		if (!GroupPermissionUtil.contains(
				themeDisplay.getPermissionChecker(), getSelGroupId(),
				ActionKeys.MANAGE_LAYOUTS)) {

			return false;
		}

		return true;
	}

	public boolean isShowOrphanPortletsAction(Layout layout)
		throws PortalException {

		if (StagingUtil.isIncomplete(layout)) {
			return false;
		}

		if (!layout.isSupportsEmbeddedPortlets()) {
			return false;
		}

		if (!isShowAddRootLayoutButton()) {
			return false;
		}

		OrphanPortletsDisplayContext orphanPortletsDisplayContext =
			new OrphanPortletsDisplayContext(
				httpServletRequest, _liferayPortletRequest,
				_liferayPortletResponse);

		if (ListUtil.isEmpty(
				orphanPortletsDisplayContext.getOrphanPortlets(layout))) {

			return false;
		}

		return true;
	}

	public boolean isShowPermissionsAction(Layout layout)
		throws PortalException {

		if (StagingUtil.isIncomplete(layout)) {
			return false;
		}

		Group selGroup = getSelGroup();

		if (selGroup.isLayoutPrototype()) {
			return false;
		}

		return LayoutPermissionUtil.contains(
			themeDisplay.getPermissionChecker(), layout,
			ActionKeys.PERMISSIONS);
	}

	public boolean isShowPublicPages() {
		Group selGroup = getSelGroup();

		if (selGroup.isLayoutSetPrototype() || selGroup.isLayoutPrototype()) {
			return false;
		}

		return true;
	}

	protected long getActiveLayoutSetBranchId() throws PortalException {
		if (_activeLayoutSetBranchId != null) {
			return _activeLayoutSetBranchId;
		}

		_activeLayoutSetBranchId = ParamUtil.getLong(
			httpServletRequest, "layoutSetBranchId");

		Layout layout = getSelLayout();

		if (layout != null) {
			LayoutRevision layoutRevision = LayoutStagingUtil.getLayoutRevision(
				layout);

			if (layoutRevision != null) {
				_activeLayoutSetBranchId =
					layoutRevision.getLayoutSetBranchId();
			}
		}

		List<LayoutSetBranch> layoutSetBranches =
			LayoutSetBranchLocalServiceUtil.getLayoutSetBranches(
				themeDisplay.getScopeGroupId(), isPrivateLayout());

		if ((_activeLayoutSetBranchId == 0) && !layoutSetBranches.isEmpty()) {
			LayoutSetBranch currentUserLayoutSetBranch =
				LayoutSetBranchLocalServiceUtil.getUserLayoutSetBranch(
					themeDisplay.getUserId(), themeDisplay.getScopeGroupId(),
					isPrivateLayout(), 0, 0);

			_activeLayoutSetBranchId =
				currentUserLayoutSetBranch.getLayoutSetBranchId();
		}

		if ((_activeLayoutSetBranchId == 0) && !layoutSetBranches.isEmpty()) {
			LayoutSetBranch layoutSetBranch =
				LayoutSetBranchLocalServiceUtil.getMasterLayoutSetBranch(
					themeDisplay.getScopeGroupId(), isPrivateLayout());

			_activeLayoutSetBranchId = layoutSetBranch.getLayoutSetBranchId();
		}

		return _activeLayoutSetBranchId;
	}

	protected List<String> getAvailableActions(Layout layout)
		throws PortalException {

		List<String> availableActions = new ArrayList<>();

		if (isShowConvertLayoutAction(layout)) {
			availableActions.add("convertSelectedPages");
		}

		if (LayoutPermissionUtil.contains(
				themeDisplay.getPermissionChecker(), layout,
				ActionKeys.DELETE)) {

			availableActions.add("deleteSelectedPages");
		}

		return availableActions;
	}

	protected JSONArray getLayoutSetBranchesJSONArray() throws PortalException {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		List<LayoutSetBranch> layoutSetBranches =
			LayoutSetBranchLocalServiceUtil.getLayoutSetBranches(
				themeDisplay.getScopeGroupId(), isPrivateLayout());

		for (LayoutSetBranch layoutSetBranch : layoutSetBranches) {
			JSONObject jsonObject = JSONUtil.put(
				"active",
				layoutSetBranch.getLayoutSetBranchId() ==
					getActiveLayoutSetBranchId()
			).put(
				"hasChild", true
			).put(
				"id", LayoutConstants.DEFAULT_PLID
			).put(
				"plid", LayoutConstants.DEFAULT_PLID
			).put(
				"title",
				LanguageUtil.get(httpServletRequest, layoutSetBranch.getName())
			);

			PortletURL portletURL = getPortletURL();

			portletURL.setParameter(
				"layoutSetBranchId",
				String.valueOf(layoutSetBranch.getLayoutSetBranchId()));
			portletURL.setParameter(
				"privateLayout",
				String.valueOf(layoutSetBranch.isPrivateLayout()));

			jsonObject.put("url", portletURL.toString());

			jsonArray.put(jsonObject);
		}

		return jsonArray;
	}

	protected boolean isActive(long plid) throws PortalException {
		if (plid == getSelPlid()) {
			return true;
		}

		Layout selLayout = getSelLayout();

		if (selLayout == null) {
			return false;
		}

		for (Layout layout : selLayout.getAncestors()) {
			if (plid == layout.getPlid()) {
				return true;
			}
		}

		return false;
	}

	protected final HttpServletRequest httpServletRequest;
	protected final ThemeDisplay themeDisplay;

	private JSONObject _getActionURLsJSONObject(Layout layout)
		throws Exception {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		if (isShowAddChildPageAction(layout)) {
			jsonObject.put(
				"addURL",
				getSelectLayoutPageTemplateEntryURL(
					getFirstLayoutPageTemplateCollectionId(), layout.getPlid(),
					layout.isPrivateLayout()));
		}

		if (isShowConfigureAction(layout)) {
			jsonObject.put("configureURL", getConfigureLayoutURL(layout));
		}

		Layout draftLayout = LayoutLocalServiceUtil.fetchLayout(
			PortalUtil.getClassNameId(Layout.class), layout.getPlid());

		if (isShowConvertLayoutAction(layout)) {
			if (draftLayout == null) {
				jsonObject.put(
					"layoutConversionPreviewURL",
					getLayoutConversionPreviewURL(layout));
			}
			else {
				jsonObject.put(
					"deleteLayoutConversionPreviewURL",
					getDeleteLayoutURL(draftLayout));
			}
		}

		if (isShowCopyLayoutAction(layout)) {
			jsonObject.put("copyLayoutURL", getCopyLayoutRenderURL(layout));
		}
		else {
			jsonObject.put("copyLayoutURL", StringPool.BLANK);
		}

		if (isShowDeleteAction(layout)) {
			jsonObject.put("deleteURL", getDeleteLayoutURL(layout));
		}

		if (isConversionDraft(layout) && isShowConfigureAction(layout)) {
			jsonObject.put("editConversionLayoutURL", getEditLayoutURL(layout));
		}
		else if (isShowConfigureAction(layout)) {
			String editLayoutURL = getEditLayoutURL(layout);

			if (Validator.isNotNull(editLayoutURL)) {
				jsonObject.put("editLayoutURL", editLayoutURL);
			}
		}

		if (isShowOrphanPortletsAction(layout)) {
			jsonObject.put("orphanPortletsURL", getOrphanPortletsURL(layout));
		}

		if (isShowPermissionsAction(layout)) {
			jsonObject.put("permissionsURL", getPermissionsURL(layout));
		}

		if (layout.isPending()) {
			jsonObject.put("previewLayoutURL", getViewLayoutURL(layout));
		}
		else {
			boolean published = true;

			if (draftLayout != null) {
				published = GetterUtil.getBoolean(
					draftLayout.getTypeSettingsProperty("published"));
			}

			if (!layout.isTypeContent() || published) {
				jsonObject.put("viewLayoutURL", getViewLayoutURL(layout));
			}
			else {
				jsonObject.put("viewLayoutURL", StringPool.BLANK);
			}
		}

		return jsonObject;
	}

	private String _getBackURL() {
		if (_backURL != null) {
			return _backURL;
		}

		_backURL = ParamUtil.getString(_liferayPortletRequest, "backURL");

		return _backURL;
	}

	private JSONObject _getBreadcrumbEntryJSONObject(
		long plid, boolean privateLayout, String title) {

		JSONObject breadcrumbEntryJSONObject = JSONUtil.put("title", title);

		PortletURL portletURL = getPortletURL();

		portletURL.setParameter("selPlid", String.valueOf(plid));
		portletURL.setParameter("privateLayout", String.valueOf(privateLayout));

		breadcrumbEntryJSONObject.put("url", portletURL.toString());

		return breadcrumbEntryJSONObject;
	}

	private String _getDraftLayoutURL(Layout layout) throws Exception {
		Layout draftLayout = LayoutLocalServiceUtil.fetchLayout(
			PortalUtil.getClassNameId(Layout.class), layout.getPlid());

		if (draftLayout == null) {
			UnicodeProperties unicodeProperties =
				layout.getTypeSettingsProperties();

			unicodeProperties.put("published", "true");

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				httpServletRequest);

			draftLayout = LayoutLocalServiceUtil.addLayout(
				layout.getUserId(), layout.getGroupId(),
				layout.isPrivateLayout(), layout.getParentLayoutId(),
				PortalUtil.getClassNameId(Layout.class), layout.getPlid(),
				layout.getNameMap(), layout.getTitleMap(),
				layout.getDescriptionMap(), layout.getKeywordsMap(),
				layout.getRobotsMap(), layout.getType(),
				unicodeProperties.toString(), true, true,
				layout.getMasterLayoutPlid(), Collections.emptyMap(),
				serviceContext);

			draftLayout = _layoutCopyHelper.copyLayout(layout, draftLayout);

			LayoutLocalServiceUtil.updateStatus(
				draftLayout.getUserId(), draftLayout.getPlid(),
				WorkflowConstants.STATUS_APPROVED, serviceContext);
		}

		String layoutFullURL = PortalUtil.getLayoutFullURL(
			draftLayout, themeDisplay);

		layoutFullURL = HttpUtil.setParameter(
			layoutFullURL, "p_l_back_url", themeDisplay.getURLCurrent());

		return HttpUtil.setParameter(layoutFullURL, "p_l_mode", Constants.EDIT);
	}

	private JSONObject _getFirstColumn(boolean privatePages, boolean active)
		throws PortalException {

		JSONObject pagesJSONObject = JSONUtil.put(
			"actionURLs", _getFirstColumnActionURLsJSONObject(privatePages)
		).put(
			"active", active
		).put(
			"hasChild", true
		).put(
			"plid", LayoutConstants.DEFAULT_PLID
		).put(
			"title", getTitle(privatePages)
		);

		PortletURL pagesURL = getPortletURL();

		pagesURL.setParameter(
			"selPlid", String.valueOf(LayoutConstants.DEFAULT_PLID));
		pagesURL.setParameter("privateLayout", String.valueOf(privatePages));

		pagesJSONObject.put("url", pagesURL.toString());

		return pagesJSONObject;
	}

	private JSONObject _getFirstColumnActionURLsJSONObject(boolean privatePages)
		throws PortalException {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		if (isShowFirstColumnConfigureAction()) {
			jsonObject.put(
				"configureURL", getFirstColumnConfigureLayoutURL(privatePages));
		}

		if (isShowAddRootLayoutButton()) {
			jsonObject.put(
				"addURL", getSelectLayoutPageTemplateEntryURL(privatePages));
		}

		return jsonObject;
	}

	private long[] _getGroupIds() {
		try {
			return PortalUtil.getCurrentAndAncestorSiteGroupIds(
				themeDisplay.getScopeGroupId());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception, exception);
			}
		}

		return new long[0];
	}

	private JSONArray _getLayoutColumnsJSONArray() throws Exception {
		JSONArray firstColumnJSONArray = JSONFactoryUtil.createJSONArray();

		Layout selLayout = getSelLayout();

		if (LayoutLocalServiceUtil.hasLayouts(getSelGroup(), false) &&
			isShowPublicPages()) {

			boolean active = !isPrivateLayout();

			if (selLayout != null) {
				active = selLayout.isPublicLayout();
			}

			if (isFirstColumn()) {
				active = false;
			}

			firstColumnJSONArray.put(_getFirstColumn(false, active));
		}

		if (LayoutLocalServiceUtil.hasLayouts(getSelGroup(), true)) {
			boolean active = isPrivateLayout();

			if (selLayout != null) {
				active = selLayout.isPrivateLayout();
			}

			if (isFirstColumn()) {
				active = false;
			}

			firstColumnJSONArray.put(_getFirstColumn(true, active));
		}

		JSONArray layoutColumnsJSONArray = JSONUtil.put(firstColumnJSONArray);

		if (isFirstColumn()) {
			return layoutColumnsJSONArray;
		}

		JSONArray layoutSetBranchesJSONArray = getLayoutSetBranchesJSONArray();

		if (layoutSetBranchesJSONArray.length() > 0) {
			layoutColumnsJSONArray.put(layoutSetBranchesJSONArray);
		}

		if (selLayout == null) {
			layoutColumnsJSONArray.put(
				getLayoutsJSONArray(0, isPrivateLayout()));

			return layoutColumnsJSONArray;
		}

		List<Layout> layouts = ListUtil.copy(selLayout.getAncestors());

		Collections.reverse(layouts);

		layouts.add(selLayout);

		for (Layout layout : layouts) {
			layoutColumnsJSONArray.put(
				getLayoutsJSONArray(
					layout.getParentLayoutId(), selLayout.isPrivateLayout()));
		}

		layoutColumnsJSONArray.put(
			getLayoutsJSONArray(
				selLayout.getLayoutId(), selLayout.isPrivateLayout()));

		return layoutColumnsJSONArray;
	}

	private String _getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = ParamUtil.getString(
			_liferayPortletRequest, "orderByCol", "path");

		if (isSearch()) {
			_orderByCol = "create-date";
		}

		return _orderByCol;
	}

	private String _getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = ParamUtil.getString(
			_liferayPortletRequest, "orderByType", "asc");

		return _orderByType;
	}

	private boolean _isLiveGroup() {
		if (_liveGroup != null) {
			return _liveGroup;
		}

		Group group = themeDisplay.getScopeGroup();

		boolean liveGroup = false;

		if (_stagingGroupHelper.isLocalLiveGroup(group) ||
			_stagingGroupHelper.isRemoteLiveGroup(group)) {

			liveGroup = true;
		}

		_liveGroup = liveGroup;

		return _liveGroup;
	}

	private boolean _matchesHostname(
		StringBuilder friendlyURLBase,
		TreeMap<String, String> virtualHostnames) {

		for (String virtualHostname : virtualHostnames.keySet()) {
			if (friendlyURLBase.indexOf(virtualHostname) != -1) {
				return true;
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutsAdminDisplayContext.class);

	private Long _activeLayoutSetBranchId;
	private String _backURL;
	private String _displayStyle;
	private Boolean _firstColumn;
	private final GroupDisplayContextHelper _groupDisplayContextHelper;
	private String _keywords;
	private final LayoutConverterConfiguration _layoutConverterConfiguration;
	private final LayoutConverterRegistry _layoutConverterRegistry;
	private final LayoutCopyHelper _layoutCopyHelper;
	private List<LayoutDescription> _layoutDescriptions;
	private final LayoutEditorTypeConfiguration _layoutEditorTypeConfiguration;
	private Long _layoutId;
	private SearchContainer _layoutsSearchContainer;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private Boolean _liveGroup;
	private String _orderByCol;
	private String _orderByType;
	private Long _parentLayoutId;
	private Boolean _privateLayout;
	private String _redirect;
	private String _rootNodeName;
	private Layout _selLayout;
	private LayoutSet _selLayoutSet;
	private Long _selPlid;
	private final StagingGroupHelper _stagingGroupHelper;
	private String _tabs1;

}