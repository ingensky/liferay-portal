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

package com.liferay.portal.workflow.metrics.rest.internal.resource.v1_0.factory;

import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.TimeRangeResource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author Rafael Praxedes
 */
@Component(immediate = true, service = TimeRangeResource.Factory.class)
public class TimeRangeResourceFactoryImpl implements TimeRangeResource.Factory {

	@Override
	public TimeRangeResource.Builder create() {
		return new TimeRangeResource.Builder() {

			@Override
			public TimeRangeResource build() {
				if (_user == null) {
					throw new IllegalArgumentException("User is not set");
				}

				return (TimeRangeResource)ProxyUtil.newProxyInstance(
					TimeRangeResource.class.getClassLoader(),
					new Class<?>[] {TimeRangeResource.class},
					(proxy, method, arguments) -> _invoke(
						method, arguments, _checkPermissions, _user));
			}

			@Override
			public TimeRangeResource.Builder checkPermissions(
				boolean checkPermissions) {

				_checkPermissions = checkPermissions;

				return this;
			}

			@Override
			public TimeRangeResource.Builder user(User user) {
				_user = user;

				return this;
			}

			private boolean _checkPermissions = true;
			private User _user;

		};
	}

	@Activate
	protected void activate() {
		TimeRangeResource.FactoryHolder.factory = this;
	}

	@Deactivate
	protected void deactivate() {
		TimeRangeResource.FactoryHolder.factory = null;
	}

	private Object _invoke(
			Method method, Object[] arguments, boolean checkPermissions,
			User user)
		throws Throwable {

		String name = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(user.getUserId());

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (checkPermissions) {
			PermissionThreadLocal.setPermissionChecker(
				_defaultPermissionCheckerFactory.create(user));
		}
		else {
			PermissionThreadLocal.setPermissionChecker(
				_liberalPermissionCheckerFactory.create(user));
		}

		TimeRangeResource timeRangeResource =
			_componentServiceObjects.getService();

		Company company = _companyLocalService.getCompany(user.getCompanyId());

		timeRangeResource.setContextCompany(company);

		timeRangeResource.setContextUser(user);

		try {
			return method.invoke(timeRangeResource, arguments);
		}
		catch (InvocationTargetException invocationTargetException) {
			throw invocationTargetException.getTargetException();
		}
		finally {
			_componentServiceObjects.ungetService(timeRangeResource);

			PrincipalThreadLocal.setName(name);

			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<TimeRangeResource> _componentServiceObjects;

	@Reference
	private PermissionCheckerFactory _defaultPermissionCheckerFactory;

	@Reference(target = "(permission.checker.type=liberal)")
	private PermissionCheckerFactory _liberalPermissionCheckerFactory;

	@Reference
	private UserLocalService _userLocalService;

}