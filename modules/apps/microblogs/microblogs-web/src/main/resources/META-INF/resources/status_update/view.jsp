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
Group group = themeDisplay.getScopeGroup();
%>

<c:if test="<%= MicroblogsPermission.contains(permissionChecker, scopeGroupId, ActionKeys.ADD_ENTRY) && group.isUser() && !layout.isPublicLayout() %>">
	<liferay-util:include page="/microblogs/edit_microblogs_entry.jsp" servletContext="<%= application %>" />
</c:if>

<c:if test="<%= showStatus %>">
	<div class="microblogs-container microblogs-status-container">

		<%
		List<MicroblogsEntry> microblogsEntries = null;

		long microblogsEntryUserId = themeDisplay.getUserId();

		if (group.isUser()) {
			microblogsEntryUserId = group.getClassPK();
		}

		if (microblogsEntryUserId == themeDisplay.getUserId()) {
			microblogsEntries = MicroblogsEntryLocalServiceUtil.getUserMicroblogsEntries(microblogsEntryUserId, 0, 0, 1);
		}
		else {
			microblogsEntries = MicroblogsEntryServiceUtil.getUserMicroblogsEntries(microblogsEntryUserId, 0, 0, 1);
		}

		request.setAttribute(WebKeys.MICROBLOGS_ENTRIES, microblogsEntries);

		PortletURL portletURL = renderResponse.createRenderURL();

		portletURL.setParameter("mvcPath", "/status_update/view.jsp");
		portletURL.setWindowState(WindowState.NORMAL);

		request.setAttribute(WebKeys.MICROBLOGS_ENTRIES_URL, portletURL);
		%>

		<liferay-util:include page="/microblogs/view_microblogs_entries.jsp" servletContext="<%= application %>" />
	</div>
</c:if>

<aui:script use="aui-base">
	AUI().ready(function() {
		Liferay.Microblogs.init({
			microblogsEntriesURL:
				'<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="mvcPath" value="/status_update/view.jsp" /></portlet:renderURL>',
		});
	});
</aui:script>