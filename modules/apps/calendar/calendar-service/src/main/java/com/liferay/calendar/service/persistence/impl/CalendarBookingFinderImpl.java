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

package com.liferay.calendar.service.persistence.impl;

import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.model.impl.CalendarBookingImpl;
import com.liferay.calendar.model.impl.CalendarBookingModelImpl;
import com.liferay.calendar.service.persistence.CalendarBookingFinder;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo Lundgren
 * @author Fabio Pezzutto
 */
@Component(service = CalendarBookingFinder.class)
public class CalendarBookingFinderImpl
	extends CalendarBookingFinderBaseImpl implements CalendarBookingFinder {

	public static final String COUNT_BY_C_G_C_C_P_T_D_L_S_E_S =
		CalendarBookingFinder.class.getName() + ".countByC_G_C_C_P_T_D_L_S_E_S";

	public static final String FIND_BY_FUTURE_REMINDERS =
		CalendarBookingFinder.class.getName() + ".findByFutureReminders";

	public static final String FIND_BY_C_G_C_C_P_T_D_L_S_E_S =
		CalendarBookingFinder.class.getName() + ".findByC_G_C_C_P_T_D_L_S_E_S";

	@Override
	public int countByKeywords(
		long companyId, long[] groupIds, long[] calendarIds,
		long[] calendarResourceIds, long parentCalendarBookingId,
		String keywords, long startTime, long endTime, int[] statuses) {

		String[] titles = null;
		String[] descriptions = null;
		String[] locations = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			titles = _customSQL.keywords(keywords);
			descriptions = _customSQL.keywords(keywords, false);
			locations = _customSQL.keywords(keywords);
		}
		else {
			andOperator = true;
		}

		return countByC_G_C_C_P_T_D_L_S_E_S(
			companyId, groupIds, calendarIds, calendarResourceIds,
			parentCalendarBookingId, titles, descriptions, locations, startTime,
			endTime, statuses, andOperator);
	}

	@Override
	public int countByC_G_C_C_P_T_D_L_S_E_S(
		long companyId, long[] groupIds, long[] calendarIds,
		long[] calendarResourceIds, long parentCalendarBookingId, String title,
		String description, String location, long startTime, long endTime,
		int[] statuses, boolean andOperator) {

		String[] titles = _customSQL.keywords(title);
		String[] descriptions = _customSQL.keywords(description, false);
		String[] locations = _customSQL.keywords(location);

		return countByC_G_C_C_P_T_D_L_S_E_S(
			companyId, groupIds, calendarIds, calendarResourceIds,
			parentCalendarBookingId, titles, descriptions, locations, startTime,
			endTime, statuses, andOperator);
	}

	@Override
	public int countByC_G_C_C_P_T_D_L_S_E_S(
		long companyId, long[] groupIds, long[] calendarIds,
		long[] calendarResourceIds, long parentCalendarBookingId,
		String[] titles, String[] descriptions, String[] locations,
		long startTime, long endTime, int[] statuses, boolean andOperator) {

		return doCountByC_G_C_C_P_T_D_L_S_E_S(
			companyId, groupIds, calendarIds, calendarResourceIds,
			parentCalendarBookingId, titles, descriptions, locations, startTime,
			endTime, statuses, andOperator, false);
	}

	@Override
	public int filterCountByKeywords(
		long companyId, long[] groupIds, long[] calendarIds,
		long[] calendarResourceIds, long parentCalendarBookingId,
		String keywords, long startTime, long endTime, int[] statuses) {

		String[] titles = null;
		String[] descriptions = null;
		String[] locations = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			titles = _customSQL.keywords(keywords);
			descriptions = _customSQL.keywords(keywords, false);
			locations = _customSQL.keywords(keywords);
		}
		else {
			andOperator = true;
		}

		return filterCountByC_G_C_C_P_T_D_L_S_E_S(
			companyId, groupIds, calendarIds, calendarResourceIds,
			parentCalendarBookingId, titles, descriptions, locations, startTime,
			endTime, statuses, andOperator);
	}

	@Override
	public int filterCountByC_G_C_C_P_T_D_L_S_E_S(
		long companyId, long[] groupIds, long[] calendarIds,
		long[] calendarResourceIds, long parentCalendarBookingId, String title,
		String description, String location, long startTime, long endTime,
		int[] statuses, boolean andOperator) {

		String[] titles = _customSQL.keywords(title);
		String[] descriptions = _customSQL.keywords(description, false);
		String[] locations = _customSQL.keywords(location);

		return filterCountByC_G_C_C_P_T_D_L_S_E_S(
			companyId, groupIds, calendarIds, calendarResourceIds,
			parentCalendarBookingId, titles, descriptions, locations, startTime,
			endTime, statuses, andOperator);
	}

	@Override
	public int filterCountByC_G_C_C_P_T_D_L_S_E_S(
		long companyId, long[] groupIds, long[] calendarIds,
		long[] calendarResourceIds, long parentCalendarBookingId,
		String[] titles, String[] descriptions, String[] locations,
		long startTime, long endTime, int[] statuses, boolean andOperator) {

		return doCountByC_G_C_C_P_T_D_L_S_E_S(
			companyId, groupIds, calendarIds, calendarResourceIds,
			parentCalendarBookingId, titles, descriptions, locations, startTime,
			endTime, statuses, andOperator, true);
	}

	@Override
	public List<CalendarBooking> filterFindByKeywords(
		long companyId, long[] groupIds, long[] calendarIds,
		long[] calendarResourceIds, long parentCalendarBookingId,
		String keywords, long startTime, long endTime, boolean recurring,
		int[] statuses, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator) {

		String[] titles = null;
		String[] descriptions = null;
		String[] locations = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			titles = _customSQL.keywords(keywords);
			descriptions = _customSQL.keywords(keywords, false);
			locations = _customSQL.keywords(keywords);
		}
		else {
			andOperator = true;
		}

		return filterFindByC_G_C_C_P_T_D_L_S_E_S(
			companyId, groupIds, calendarIds, calendarResourceIds,
			parentCalendarBookingId, titles, descriptions, locations, startTime,
			endTime, recurring, statuses, andOperator, start, end,
			orderByComparator);
	}

	@Override
	public List<CalendarBooking> filterFindByC_G_C_C_P_T_D_L_S_E_S(
		long companyId, long[] groupIds, long[] calendarIds,
		long[] calendarResourceIds, long parentCalendarBookingId, String title,
		String description, String location, long startTime, long endTime,
		boolean recurring, int[] statuses, boolean andOperator, int start,
		int end, OrderByComparator<CalendarBooking> orderByComparator) {

		String[] titles = _customSQL.keywords(title);
		String[] descriptions = _customSQL.keywords(description, false);
		String[] locations = _customSQL.keywords(location);

		return filterFindByC_G_C_C_P_T_D_L_S_E_S(
			companyId, groupIds, calendarIds, calendarResourceIds,
			parentCalendarBookingId, titles, descriptions, locations, startTime,
			endTime, recurring, statuses, andOperator, start, end,
			orderByComparator);
	}

	@Override
	public List<CalendarBooking> filterFindByC_G_C_C_P_T_D_L_S_E_S(
		long companyId, long[] groupIds, long[] calendarIds,
		long[] calendarResourceIds, long parentCalendarBookingId,
		String[] titles, String[] descriptions, String[] locations,
		long startTime, long endTime, boolean recurring, int[] statuses,
		boolean andOperator, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return doFindByC_G_C_C_P_T_D_L_S_E_S(
			companyId, groupIds, calendarIds, calendarResourceIds,
			parentCalendarBookingId, titles, descriptions, locations, startTime,
			endTime, recurring, statuses, andOperator, start, end,
			orderByComparator, true);
	}

	@Override
	public List<CalendarBooking> findByFutureReminders(long startTime) {
		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_FUTURE_REMINDERS);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("CalendarBooking", CalendarBookingImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(startTime);

			return sqlQuery.list(true);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<CalendarBooking> findByKeywords(
		long companyId, long[] groupIds, long[] calendarIds,
		long[] calendarResourceIds, long parentCalendarBookingId,
		String keywords, long startTime, long endTime, boolean recurring,
		int[] statuses, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator) {

		String[] titles = null;
		String[] descriptions = null;
		String[] locations = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			titles = _customSQL.keywords(keywords);
			descriptions = _customSQL.keywords(keywords, false);
			locations = _customSQL.keywords(keywords);
		}
		else {
			andOperator = true;
		}

		return findByC_G_C_C_P_T_D_L_S_E_S(
			companyId, groupIds, calendarIds, calendarResourceIds,
			parentCalendarBookingId, titles, descriptions, locations, startTime,
			endTime, recurring, statuses, andOperator, start, end,
			orderByComparator);
	}

	@Override
	public List<CalendarBooking> findByC_G_C_C_P_T_D_L_S_E_S(
		long companyId, long[] groupIds, long[] calendarIds,
		long[] calendarResourceIds, long parentCalendarBookingId, String title,
		String description, String location, long startTime, long endTime,
		boolean recurring, int[] statuses, boolean andOperator, int start,
		int end, OrderByComparator<CalendarBooking> orderByComparator) {

		String[] titles = _customSQL.keywords(title);
		String[] descriptions = _customSQL.keywords(description, false);
		String[] locations = _customSQL.keywords(location);

		return findByC_G_C_C_P_T_D_L_S_E_S(
			companyId, groupIds, calendarIds, calendarResourceIds,
			parentCalendarBookingId, titles, descriptions, locations, startTime,
			endTime, recurring, statuses, andOperator, start, end,
			orderByComparator);
	}

	@Override
	public List<CalendarBooking> findByC_G_C_C_P_T_D_L_S_E_S(
		long companyId, long[] groupIds, long[] calendarIds,
		long[] calendarResourceIds, long parentCalendarBookingId,
		String[] titles, String[] descriptions, String[] locations,
		long startTime, long endTime, boolean recurring, int[] statuses,
		boolean andOperator, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return doFindByC_G_C_C_P_T_D_L_S_E_S(
			companyId, groupIds, calendarIds, calendarResourceIds,
			parentCalendarBookingId, titles, descriptions, locations, startTime,
			endTime, recurring, statuses, andOperator, start, end,
			orderByComparator, false);
	}

	protected int doCountByC_G_C_C_P_T_D_L_S_E_S(
		long companyId, long[] groupIds, long[] calendarIds,
		long[] calendarResourceIds, long parentCalendarBookingId,
		String[] titles, String[] descriptions, String[] locations,
		long startTime, long endTime, int[] statuses, boolean andOperator,
		boolean inlineSQLHelper) {

		titles = _customSQL.keywords(titles);
		descriptions = _customSQL.keywords(descriptions, false);
		locations = _customSQL.keywords(locations);

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				getClass(), COUNT_BY_C_G_C_C_P_T_D_L_S_E_S);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, CalendarBooking.class.getName(),
					"CalendarBooking.calendarBookingId", groupIds);
			}

			sql = StringUtil.replace(
				sql, "[$GROUP_ID$]", getGroupIds(groupIds));
			sql = StringUtil.replace(
				sql, "[$CALENDAR_ID$]", getCalendarIds(calendarIds));
			sql = StringUtil.replace(
				sql, "[$CALENDAR_RESOURCE_ID$]",
				getCalendarResourceIds(calendarResourceIds));
			sql = StringUtil.replace(sql, "[$STATUS$]", getStatuses(statuses));

			if (parentCalendarBookingId < 0) {
				sql = StringUtil.removeSubstring(
					sql, "(parentCalendarBookingId = ?) AND");
			}

			sql = _customSQL.replaceKeywords(
				sql, "LOWER(title)", StringPool.LIKE, false, titles);
			sql = _customSQL.replaceKeywords(
				sql, "description", StringPool.LIKE, false, descriptions);
			sql = _customSQL.replaceKeywords(
				sql, "LOWER(location)", StringPool.LIKE, true, locations);
			sql = _customSQL.replaceAndOperator(sql, andOperator);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);
			queryPos.add(groupIds);

			if (ArrayUtil.isNotEmpty(calendarIds)) {
				queryPos.add(calendarIds);
			}

			if (ArrayUtil.isNotEmpty(calendarResourceIds)) {
				queryPos.add(calendarResourceIds);
			}

			if (parentCalendarBookingId >= 0) {
				queryPos.add(parentCalendarBookingId);
			}

			queryPos.add(titles, 2);
			queryPos.add(descriptions, 2);
			queryPos.add(locations, 2);
			queryPos.add(startTime);
			queryPos.add(endTime);
			queryPos.add(startTime);
			queryPos.add(endTime);
			queryPos.add(startTime);
			queryPos.add(endTime);
			queryPos.add(endTime);
			queryPos.add(startTime);
			queryPos.add(startTime);
			queryPos.add(endTime);

			if (ArrayUtil.isNotEmpty(statuses)) {
				queryPos.add(statuses);
			}

			Iterator<Long> itr = sqlQuery.iterate();

			if (itr.hasNext()) {
				Long count = itr.next();

				if (count != null) {
					return count.intValue();
				}
			}

			return 0;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected List<CalendarBooking> doFindByC_G_C_C_P_T_D_L_S_E_S(
		long companyId, long[] groupIds, long[] calendarIds,
		long[] calendarResourceIds, long parentCalendarBookingId,
		String[] titles, String[] descriptions, String[] locations,
		long startTime, long endTime, boolean recurring, int[] statuses,
		boolean andOperator, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean inlineSQLHelper) {

		titles = _customSQL.keywords(titles);
		descriptions = _customSQL.keywords(descriptions, false);
		locations = _customSQL.keywords(locations);

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				getClass(), FIND_BY_C_G_C_C_P_T_D_L_S_E_S);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, CalendarBooking.class.getName(),
					"CalendarBooking.calendarBookingId", groupIds);
			}

			sql = StringUtil.replace(
				sql, "[$GROUP_ID$]", getGroupIds(groupIds));
			sql = StringUtil.replace(
				sql, "[$CALENDAR_ID$]", getCalendarIds(calendarIds));
			sql = StringUtil.replace(
				sql, "[$CALENDAR_RESOURCE_ID$]",
				getCalendarResourceIds(calendarResourceIds));
			sql = StringUtil.replace(
				sql, "[$RECURRENCE$]", getRecurrence(recurring));
			sql = StringUtil.replace(sql, "[$STATUS$]", getStatuses(statuses));

			if (parentCalendarBookingId < 0) {
				sql = StringUtil.removeSubstring(
					sql, "(parentCalendarBookingId = ?) AND");
			}

			sql = _customSQL.replaceKeywords(
				sql, "LOWER(title)", StringPool.LIKE, false, titles);
			sql = _customSQL.replaceKeywords(
				sql, "description", StringPool.LIKE, false, descriptions);
			sql = _customSQL.replaceKeywords(
				sql, "LOWER(location)", StringPool.LIKE, true, locations);
			sql = _customSQL.replaceAndOperator(sql, andOperator);

			StringBundler sb = new StringBundler();

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, "CalendarBooking.", orderByComparator);
			}

			sql = StringUtil.replace(sql, "[$ORDER_BY$]", sb.toString());

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("CalendarBooking", CalendarBookingImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			if (ArrayUtil.isNotEmpty(groupIds)) {
				queryPos.add(groupIds);
			}

			if (ArrayUtil.isNotEmpty(calendarIds)) {
				queryPos.add(calendarIds);
			}

			if (ArrayUtil.isNotEmpty(calendarResourceIds)) {
				queryPos.add(calendarResourceIds);
			}

			if (parentCalendarBookingId >= 0) {
				queryPos.add(parentCalendarBookingId);
			}

			queryPos.add(titles, 2);
			queryPos.add(descriptions, 2);
			queryPos.add(locations, 2);
			queryPos.add(startTime);
			queryPos.add(endTime);
			queryPos.add(startTime);
			queryPos.add(endTime);
			queryPos.add(startTime);
			queryPos.add(endTime);
			queryPos.add(endTime);
			queryPos.add(startTime);
			queryPos.add(startTime);
			queryPos.add(endTime);

			if (ArrayUtil.isNotEmpty(statuses)) {
				queryPos.add(statuses);
			}

			return (List<CalendarBooking>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected String getCalendarIds(long[] calendarIds) {
		if (ArrayUtil.isEmpty(calendarIds)) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(calendarIds.length * 2 + 1);

		sb.append(" (");

		for (int i = 0; i < calendarIds.length; i++) {
			sb.append("calendarId = ? ");

			if ((i + 1) != calendarIds.length) {
				sb.append("OR ");
			}
		}

		sb.append(") AND");

		return sb.toString();
	}

	protected String getCalendarResourceIds(long[] calendarResourceIds) {
		if (ArrayUtil.isEmpty(calendarResourceIds)) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(
			calendarResourceIds.length * 2 + 1);

		sb.append(" (");

		for (int i = 0; i < calendarResourceIds.length; i++) {
			sb.append("calendarResourceId = ? ");

			if ((i + 1) != calendarResourceIds.length) {
				sb.append("OR ");
			}
		}

		sb.append(") AND");

		return sb.toString();
	}

	protected String getGroupIds(long[] groupIds) {
		if (ArrayUtil.isEmpty(groupIds)) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(groupIds.length * 2);

		sb.append("(");

		for (int i = 0; i < groupIds.length; i++) {
			sb.append("groupId = ?");

			if ((i + 1) < groupIds.length) {
				sb.append(" OR ");
			}
		}

		sb.append(") AND");

		return sb.toString();
	}

	protected String getRecurrence(boolean recurring) {
		if (recurring) {
			return "OR ((recurrence IS NOT NULL AND recurrence != ''))";
		}

		return StringPool.BLANK;
	}

	protected String getStatuses(int[] statuses) {
		if (ArrayUtil.isEmpty(statuses)) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(statuses.length * 2 + 1);

		sb.append("AND (");

		for (int i = 0; i < statuses.length; i++) {
			sb.append("status = ? ");

			if ((i + 1) != statuses.length) {
				sb.append("OR ");
			}
		}

		sb.append(") ");

		return sb.toString();
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CalendarBookingModelImpl.TABLE_COLUMNS_MAP;
	}

	@Reference
	private CustomSQL _customSQL;

}