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

package com.liferay.headless.delivery.resource.v1_0;

import com.liferay.headless.delivery.dto.v1_0.MessageBoardAttachment;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.multipart.MultipartBody;
import com.liferay.portal.vulcan.pagination.Page;

import javax.annotation.Generated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.osgi.annotation.versioning.ProviderType;

/**
 * To access this resource, run:
 *
 *     curl -u your@email.com:yourpassword -D - http://localhost:8080/o/headless-delivery/v1.0
 *
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@ProviderType
public interface MessageBoardAttachmentResource {

	public static Builder builder() {
		return FactoryHolder.factory.create();
	}

	public void deleteMessageBoardAttachment(Long messageBoardAttachmentId)
		throws Exception;

	public Response deleteMessageBoardAttachmentBatch(
			String callbackURL, Object object)
		throws Exception;

	public MessageBoardAttachment getMessageBoardAttachment(
			Long messageBoardAttachmentId)
		throws Exception;

	public Page<MessageBoardAttachment>
			getMessageBoardMessageMessageBoardAttachmentsPage(
				Long messageBoardMessageId)
		throws Exception;

	public MessageBoardAttachment postMessageBoardMessageMessageBoardAttachment(
			Long messageBoardMessageId, MultipartBody multipartBody)
		throws Exception;

	public Response postMessageBoardMessageMessageBoardAttachmentBatch(
			Long messageBoardMessageId, MultipartBody multipartBody,
			String callbackURL, Object object)
		throws Exception;

	public Page<MessageBoardAttachment>
			getMessageBoardThreadMessageBoardAttachmentsPage(
				Long messageBoardThreadId)
		throws Exception;

	public MessageBoardAttachment postMessageBoardThreadMessageBoardAttachment(
			Long messageBoardThreadId, MultipartBody multipartBody)
		throws Exception;

	public Response postMessageBoardThreadMessageBoardAttachmentBatch(
			Long messageBoardThreadId, MultipartBody multipartBody,
			String callbackURL, Object object)
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

		public MessageBoardAttachmentResource build();

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