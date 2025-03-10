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

package com.liferay.redirect.model;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is used by SOAP remote services.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class RedirectNotFoundEntrySoap implements Serializable {

	public static RedirectNotFoundEntrySoap toSoapModel(
		RedirectNotFoundEntry model) {

		RedirectNotFoundEntrySoap soapModel = new RedirectNotFoundEntrySoap();

		soapModel.setMvccVersion(model.getMvccVersion());
		soapModel.setRedirectNotFoundEntryId(
			model.getRedirectNotFoundEntryId());
		soapModel.setGroupId(model.getGroupId());
		soapModel.setCompanyId(model.getCompanyId());
		soapModel.setCreateDate(model.getCreateDate());
		soapModel.setModifiedDate(model.getModifiedDate());
		soapModel.setHits(model.getHits());
		soapModel.setUrl(model.getUrl());

		return soapModel;
	}

	public static RedirectNotFoundEntrySoap[] toSoapModels(
		RedirectNotFoundEntry[] models) {

		RedirectNotFoundEntrySoap[] soapModels =
			new RedirectNotFoundEntrySoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static RedirectNotFoundEntrySoap[][] toSoapModels(
		RedirectNotFoundEntry[][] models) {

		RedirectNotFoundEntrySoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels =
				new RedirectNotFoundEntrySoap[models.length][models[0].length];
		}
		else {
			soapModels = new RedirectNotFoundEntrySoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static RedirectNotFoundEntrySoap[] toSoapModels(
		List<RedirectNotFoundEntry> models) {

		List<RedirectNotFoundEntrySoap> soapModels =
			new ArrayList<RedirectNotFoundEntrySoap>(models.size());

		for (RedirectNotFoundEntry model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(
			new RedirectNotFoundEntrySoap[soapModels.size()]);
	}

	public RedirectNotFoundEntrySoap() {
	}

	public long getPrimaryKey() {
		return _redirectNotFoundEntryId;
	}

	public void setPrimaryKey(long pk) {
		setRedirectNotFoundEntryId(pk);
	}

	public long getMvccVersion() {
		return _mvccVersion;
	}

	public void setMvccVersion(long mvccVersion) {
		_mvccVersion = mvccVersion;
	}

	public long getRedirectNotFoundEntryId() {
		return _redirectNotFoundEntryId;
	}

	public void setRedirectNotFoundEntryId(long redirectNotFoundEntryId) {
		_redirectNotFoundEntryId = redirectNotFoundEntryId;
	}

	public long getGroupId() {
		return _groupId;
	}

	public void setGroupId(long groupId) {
		_groupId = groupId;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public void setCompanyId(long companyId) {
		_companyId = companyId;
	}

	public Date getCreateDate() {
		return _createDate;
	}

	public void setCreateDate(Date createDate) {
		_createDate = createDate;
	}

	public Date getModifiedDate() {
		return _modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		_modifiedDate = modifiedDate;
	}

	public long getHits() {
		return _hits;
	}

	public void setHits(long hits) {
		_hits = hits;
	}

	public String getUrl() {
		return _url;
	}

	public void setUrl(String url) {
		_url = url;
	}

	private long _mvccVersion;
	private long _redirectNotFoundEntryId;
	private long _groupId;
	private long _companyId;
	private Date _createDate;
	private Date _modifiedDate;
	private long _hits;
	private String _url;

}