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

package com.liferay.segments.asah.rest.internal.resource.v1_0.factory;

import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.segments.asah.rest.resource.v1_0.ExperimentResource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Generated;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author Javier Gamarra
 * @generated
 */
@Component(immediate = true, service = ExperimentResource.Factory.class)
@Generated("")
public class ExperimentResourceFactoryImpl
	implements ExperimentResource.Factory {

	@Override
	public ExperimentResource.Builder create() {
		return new ExperimentResource.Builder() {

			@Override
			public ExperimentResource build() {
				if (_user == null) {
					throw new IllegalArgumentException("User is not set");
				}

				return (ExperimentResource)ProxyUtil.newProxyInstance(
					ExperimentResource.class.getClassLoader(),
					new Class<?>[] {ExperimentResource.class},
					(proxy, method, arguments) -> _invoke(
						method, arguments, _checkPermissions,
						_httpServletRequest, _user));
			}

			@Override
			public ExperimentResource.Builder checkPermissions(
				boolean checkPermissions) {

				_checkPermissions = checkPermissions;

				return this;
			}

			@Override
			public ExperimentResource.Builder httpServletRequest(
				HttpServletRequest httpServletRequest) {

				_httpServletRequest = httpServletRequest;

				return this;
			}

			@Override
			public ExperimentResource.Builder user(User user) {
				_user = user;

				return this;
			}

			private boolean _checkPermissions = true;
			private HttpServletRequest _httpServletRequest;
			private User _user;

		};
	}

	@Activate
	protected void activate() {
		ExperimentResource.FactoryHolder.factory = this;
	}

	@Deactivate
	protected void deactivate() {
		ExperimentResource.FactoryHolder.factory = null;
	}

	private Object _invoke(
			Method method, Object[] arguments, boolean checkPermissions,
			HttpServletRequest httpServletRequest, User user)
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

		ExperimentResource experimentResource =
			_componentServiceObjects.getService();

		experimentResource.setContextAcceptLanguage(
			new AcceptLanguageImpl(user));

		Company company = _companyLocalService.getCompany(user.getCompanyId());

		experimentResource.setContextCompany(company);

		experimentResource.setContextHttpServletRequest(httpServletRequest);
		experimentResource.setContextUser(user);

		try {
			return method.invoke(experimentResource, arguments);
		}
		catch (InvocationTargetException invocationTargetException) {
			throw invocationTargetException.getTargetException();
		}
		finally {
			_componentServiceObjects.ungetService(experimentResource);

			PrincipalThreadLocal.setName(name);

			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ExperimentResource>
		_componentServiceObjects;

	@Reference
	private PermissionCheckerFactory _defaultPermissionCheckerFactory;

	@Reference(target = "(permission.checker.type=liberal)")
	private PermissionCheckerFactory _liberalPermissionCheckerFactory;

	@Reference
	private UserLocalService _userLocalService;

	private class AcceptLanguageImpl implements AcceptLanguage {

		public AcceptLanguageImpl(User user) {
			_user = user;
		}

		@Override
		public List<Locale> getLocales() {
			return Collections.emptyList();
		}

		@Override
		public String getPreferredLanguageId() {
			return LocaleUtil.toLanguageId(getPreferredLocale());
		}

		@Override
		public Locale getPreferredLocale() {
			List<Locale> locales = getLocales();

			if (ListUtil.isNotEmpty(locales)) {
				return locales.get(0);
			}

			return _user.getLocale();
		}

		@Override
		public boolean isAcceptAllLanguages() {
			return false;
		}

		private final User _user;

	}

}