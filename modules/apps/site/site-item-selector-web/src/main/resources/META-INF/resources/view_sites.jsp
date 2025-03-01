<%--
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
--%>

<%@ include file="/init.jsp" %>

<%
SitesItemSelectorViewDisplayContext siteItemSelectorViewDisplayContext = (SitesItemSelectorViewDisplayContext)request.getAttribute(SitesItemSelectorWebKeys.SITES_ITEM_SELECTOR_DISPLAY_CONTEXT);
GroupURLProvider groupURLProvider = (GroupURLProvider)request.getAttribute(SiteWebKeys.GROUP_URL_PROVIDER);

String displayStyle = siteItemSelectorViewDisplayContext.getDisplayStyle();

GroupItemSelectorCriterion groupItemSelectorCriterion = siteItemSelectorViewDisplayContext.getGroupItemSelectorCriterion();

String target = ParamUtil.getString(request, "target", groupItemSelectorCriterion.getTarget());

GroupSearch groupSearch = siteItemSelectorViewDisplayContext.getGroupSearch();
%>

<clay:management-toolbar
	displayContext="<%= new SitesItemSelectorViewManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, siteItemSelectorViewDisplayContext) %>"
/>

<aui:form action="<%= siteItemSelectorViewDisplayContext.getPortletURL() %>" cssClass="container-fluid-1280" method="post" name="selectGroupFm">
	<c:if test="<%= siteItemSelectorViewDisplayContext.isShowChildSitesLink() %>">
		<div id="breadcrumb">
			<liferay-ui:breadcrumb
				showCurrentGroup="<%= false %>"
				showGuestGroup="<%= false %>"
				showLayout="<%= false %>"
				showPortletBreadcrumb="<%= true %>"
			/>
		</div>
	</c:if>

	<liferay-ui:search-container
		searchContainer="<%= groupSearch %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.model.Group"
			escapedModel="<%= true %>"
			keyProperty="groupId"
			modelVar="group"
			rowIdProperty="friendlyURL"
			rowVar="row"
		>

			<%
			List<Group> childGroups = GroupServiceUtil.getGroups(group.getCompanyId(), group.getGroupId(), true);

			Map<String, Object> data = new HashMap<String, Object>();

			data.put("groupdescriptivename", group.getDescriptiveName(locale));
			data.put("groupid", group.getGroupId());
			data.put("groupscopelabel", LanguageUtil.get(resourceBundle, group.getScopeLabel(themeDisplay)));
			data.put("grouptarget", target);
			data.put("grouptype", LanguageUtil.get(resourceBundle, group.getTypeLabel()));
			data.put("url", groupURLProvider.getGroupURL(group, liferayPortletRequest));
			data.put("uuid", group.getUuid());

			String childGroupsHREF = null;

			if (!childGroups.isEmpty()) {
				PortletURL childGroupsURL = siteItemSelectorViewDisplayContext.getPortletURL();

				childGroupsURL.setParameter("groupId", String.valueOf(group.getGroupId()));

				childGroupsHREF = childGroupsURL.toString();
			}
			%>

			<c:choose>
				<c:when test='<%= displayStyle.equals("descriptive") %>'>
					<c:choose>
						<c:when test="<%= Validator.isNotNull(group.getLogoURL(themeDisplay, false)) %>">
							<liferay-ui:search-container-column-image
								src="<%= group.getLogoURL(themeDisplay, false) %>"
							/>
						</c:when>
						<c:otherwise>
							<liferay-ui:search-container-column-icon
								icon="<%= group.getIconCssClass() %>"
							/>
						</c:otherwise>
					</c:choose>

					<liferay-ui:search-container-column-text
						colspan="<%= 2 %>"
					>
						<h5>
							<aui:a cssClass="selector-button" data="<%= data %>" href="javascript:;">
								<%= HtmlUtil.escape(siteItemSelectorViewDisplayContext.getGroupName(group)) %>
							</aui:a>

							<c:if test="<%= groupItemSelectorCriterion.isAllowNavigation() %>">
								<aui:a href="<%= groupURLProvider.getGroupURL(group, liferayPortletRequest) %>" target="_blank" />
							</c:if>
						</h5>

						<h6 class="text-default">
							<span><%= LanguageUtil.get(request, group.getScopeLabel(themeDisplay)) %></span>
						</h6>

						<c:if test="<%= siteItemSelectorViewDisplayContext.isShowChildSitesLink() %>">
							<h6>
								<aui:a cssClass='<%= !childGroups.isEmpty() ? "text-default" : "disabled text-muted" %>' href="<%= childGroupsHREF %>">
									<liferay-ui:message arguments="<%= String.valueOf(childGroups.size()) %>" key="x-child-sites" />
								</aui:a>
							</h6>
						</c:if>
					</liferay-ui:search-container-column-text>
				</c:when>
				<c:when test='<%= displayStyle.equals("icon") %>'>

					<%
					row.setCssClass("entry-card lfr-asset-item " + row.getCssClass());

					Map<String, Object> linkData = new HashMap<>();

					linkData.put("prevent-selection", true);

					SiteVerticalCard siteVerticalCard = new SiteVerticalCard(group, liferayPortletRequest);
					%>

					<liferay-ui:search-container-column-text>
						<div class="card card-horizontal">
							<div class="card-body">
								<div class="card-row">
									<div class="autofit-col">
										<span class="sticker sticker-secondary">
											<c:choose>
												<c:when test="<%= Validator.isNotNull(siteVerticalCard.getImageSrc()) %>">
													<span class="sticker-overlay">
														<img alt="" class="sticker-img" src="<%= siteVerticalCard.getImageSrc() %>" />
													</span>
												</c:when>
												<c:otherwise>
													<liferay-ui:icon
														icon="<%= group.getIconCssClass() %>"
														markupView="lexicon"
													/>
												</c:otherwise>
											</c:choose>
										</span>
									</div>

									<div class="autofit-col autofit-col-expand autofit-col-gutters">
										<aui:a cssClass="card-title selector-button text-truncate" data="<%= data %>" href="javascript:;" title="<%= siteVerticalCard.getSubtitle() %>">
											<%= siteVerticalCard.getTitle() %>
										</aui:a>

										<c:if test="<%= siteItemSelectorViewDisplayContext.isShowChildSitesLink() %>">
											<aui:a cssClass='<%= "card-subtitle text-truncate selector-button " + (!childGroups.isEmpty() ? "text-default" : "text-muted") %>' data="<%= linkData %>" href="<%= childGroupsHREF %>" title="<%= siteVerticalCard.getSubtitle() %>">
												<%= siteVerticalCard.getSubtitle() %>
											</aui:a>
										</c:if>
									</div>

									<c:if test="<%= groupItemSelectorCriterion.isAllowNavigation() %>">
										<div class="autofit-col">
											<aui:a cssClass="btn btn-outline-borderless btn-outline-secondary" href="<%= siteVerticalCard.getHref() %>" target="_blank" />
										</div>
									</c:if>
								</div>
							</div>
						</div>
					</liferay-ui:search-container-column-text>
				</c:when>
				<c:when test='<%= displayStyle.equals("list") %>'>
					<liferay-ui:search-container-column-text
						name="name"
						truncate="<%= true %>"
					>
						<aui:a cssClass="selector-button" data="<%= data %>" href="javascript:;">
							<%= HtmlUtil.escape(siteItemSelectorViewDisplayContext.getGroupName(group)) %>
						</aui:a>

						<c:if test="<%= groupItemSelectorCriterion.isAllowNavigation() %>">
							<aui:a href="<%= groupURLProvider.getGroupURL(group, liferayPortletRequest) %>" target="_blank" />
						</c:if>
					</liferay-ui:search-container-column-text>

					<c:if test="<%= siteItemSelectorViewDisplayContext.isShowChildSitesLink() %>">
						<liferay-ui:search-container-column-text
							name="child-sites"
							truncate="<%= true %>"
						>
							<aui:a cssClass='<%= !childGroups.isEmpty() ? "text-default" : "disabled text-muted" %>' href="<%= childGroupsHREF %>">
								<liferay-ui:message arguments="<%= String.valueOf(childGroups.size()) %>" key="x-child-sites" />
							</aui:a>
						</liferay-ui:search-container-column-text>
					</c:if>

					<liferay-ui:search-container-column-text
						name="type"
						value="<%= LanguageUtil.get(request, group.getScopeLabel(themeDisplay)) %>"
					/>
				</c:when>
			</c:choose>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= displayStyle %>"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>

<aui:script use="aui-base">
	Liferay.Util.selectEntityHandler(
		'#<portlet:namespace />selectGroupFm',
		'<%= HtmlUtil.escapeJS(siteItemSelectorViewDisplayContext.getItemSelectedEventName()) %>'
	);
</aui:script>