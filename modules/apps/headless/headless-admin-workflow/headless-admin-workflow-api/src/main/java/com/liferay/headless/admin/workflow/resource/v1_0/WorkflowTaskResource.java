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

package com.liferay.headless.admin.workflow.resource.v1_0;

import com.liferay.headless.admin.workflow.dto.v1_0.ChangeTransition;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowTask;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowTaskAssignToMe;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowTaskAssignToRole;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowTaskAssignToUser;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowTasksBulkSelection;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import javax.annotation.Generated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.core.UriInfo;

import org.osgi.annotation.versioning.ProviderType;

/**
 * To access this resource, run:
 *
 *     curl -u your@email.com:yourpassword -D - http://localhost:8080/o/headless-admin-workflow/v1.0
 *
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@ProviderType
public interface WorkflowTaskResource {

	public static Builder builder() {
		return FactoryHolder.factory.create();
	}

	public Page<WorkflowTask> getWorkflowInstanceWorkflowTasksPage(
			Long workflowInstanceId, Boolean completed, Pagination pagination)
		throws Exception;

	public Page<WorkflowTask> getWorkflowInstanceWorkflowTasksAssignedToMePage(
			Long workflowInstanceId, Boolean completed, Pagination pagination)
		throws Exception;

	public Page<WorkflowTask>
			getWorkflowInstanceWorkflowTasksAssignedToUserPage(
				Long workflowInstanceId, Long assigneeId, Boolean completed,
				Pagination pagination)
		throws Exception;

	public Page<WorkflowTask> postWorkflowTasksPage(
			Pagination pagination, Sort[] sorts,
			WorkflowTasksBulkSelection workflowTasksBulkSelection)
		throws Exception;

	public void patchWorkflowTaskAssignToUser(
			WorkflowTaskAssignToUser[] workflowTaskAssignToUsers)
		throws Exception;

	public Page<WorkflowTask> getWorkflowTasksAssignedToMePage(
			Pagination pagination)
		throws Exception;

	public Page<WorkflowTask> getWorkflowTasksAssignedToMyRolesPage(
			Pagination pagination)
		throws Exception;

	public Page<WorkflowTask> getWorkflowTasksAssignedToRolePage(
			Long roleId, Pagination pagination)
		throws Exception;

	public Page<WorkflowTask> getWorkflowTasksAssignedToUserPage(
			Long assigneeId, Pagination pagination)
		throws Exception;

	public Page<WorkflowTask> getWorkflowTasksAssignedToUserRolesPage(
			Long assigneeId, Pagination pagination)
		throws Exception;

	public void patchWorkflowTaskChangeTransition(
			ChangeTransition[] changeTransitions)
		throws Exception;

	public Page<WorkflowTask> getWorkflowTasksSubmittingUserPage(
			Long creatorId, Pagination pagination)
		throws Exception;

	public void patchWorkflowTaskUpdateDueDate(
			WorkflowTaskAssignToMe[] workflowTaskAssignToMes)
		throws Exception;

	public WorkflowTask getWorkflowTask(Long workflowTaskId) throws Exception;

	public WorkflowTask postWorkflowTaskAssignToMe(
			Long workflowTaskId, WorkflowTaskAssignToMe workflowTaskAssignToMe)
		throws Exception;

	public WorkflowTask postWorkflowTaskAssignToRole(
			Long workflowTaskId,
			WorkflowTaskAssignToRole workflowTaskAssignToRole)
		throws Exception;

	public WorkflowTask postWorkflowTaskAssignToUser(
			Long workflowTaskId,
			WorkflowTaskAssignToUser workflowTaskAssignToUser)
		throws Exception;

	public WorkflowTask postWorkflowTaskChangeTransition(
			Long workflowTaskId, ChangeTransition changeTransition)
		throws Exception;

	public String getWorkflowTaskHasAssignableUsers(Long workflowTaskId)
		throws Exception;

	public WorkflowTask postWorkflowTaskUpdateDueDate(
			Long workflowTaskId, WorkflowTaskAssignToMe workflowTaskAssignToMe)
		throws Exception;

	public default void setContextAcceptLanguage(
		AcceptLanguage contextAcceptLanguage) {
	}

	public void setContextCompany(
		com.liferay.portal.kernel.model.Company contextCompany);

	public default void setContextHttpServletRequest(
		HttpServletRequest contextHttpServletRequest) {
	}

	public default void setContextHttpServletResponse(
		HttpServletResponse contextHttpServletResponse) {
	}

	public default void setContextUriInfo(UriInfo contextUriInfo) {
	}

	public void setContextUser(
		com.liferay.portal.kernel.model.User contextUser);

	public static class FactoryHolder {

		public static volatile Factory factory;

	}

	@ProviderType
	public interface Builder {

		public WorkflowTaskResource build();

		public Builder checkPermissions(boolean checkPermissions);

		public Builder httpServletRequest(
			HttpServletRequest httpServletRequest);

		public Builder user(com.liferay.portal.kernel.model.User user);

	}

	@ProviderType
	public interface Factory {

		public Builder create();

	}

}