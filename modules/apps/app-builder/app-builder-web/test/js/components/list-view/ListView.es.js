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

import {waitForElementToBeRemoved} from '@testing-library/dom';
import {act, cleanup, fireEvent, render} from '@testing-library/react';
import {createMemoryHistory} from 'history';
import React from 'react';
import {HashRouter, Router} from 'react-router-dom';

import ListView from '../../../../src/main/resources/META-INF/resources/js/components/list-view/ListView.es';
import {
	ACTIONS,
	BODY,
	COLUMNS,
	EMPTY_STATE,
	ENDPOINT,
	RESPONSES,
} from '../../constants.es';

describe('ListView', () => {
	afterEach(() => {
		cleanup();
		jest.restoreAllMocks();
	});

	it('renders with empty state', async () => {
		fetch.mockResponse(JSON.stringify(RESPONSES.NO_ITEMS));

		const {queryByText} = render(
			<HashRouter>
				<ListView
					columns={COLUMNS}
					emptyState={EMPTY_STATE}
					endpoint={ENDPOINT}
				>
					{BODY}
				</ListView>
			</HashRouter>
		);

		await waitForElementToBeRemoved(() =>
			document.querySelector('span.loading-animation')
		);

		expect(queryByText(EMPTY_STATE.title)).toBeTruthy();
		expect(queryByText(EMPTY_STATE.description)).toBeTruthy();
	});

	it('renders with 1 item', async () => {
		fetch.mockResponse(JSON.stringify(RESPONSES.ONE_ITEM));

		const {container, queryAllByTestId} = render(
			<HashRouter>
				<ListView
					actions={ACTIONS}
					columns={COLUMNS}
					emptyState={EMPTY_STATE}
					endpoint={ENDPOINT}
				>
					{BODY}
				</ListView>
			</HashRouter>
		);

		await waitForElementToBeRemoved(() =>
			document.querySelector('span.loading-animation')
		);

		expect(queryAllByTestId('item').length).toBe(1);
		expect(container.querySelectorAll('li.page-item').length).toBe(0);
	});

	it('renders with 21 items and 2 pages', async () => {
		fetch.mockResponse(JSON.stringify(RESPONSES.TWENTY_ONE_ITEMS));

		const {container, queryAllByTestId, queryAllByText} = render(
			<HashRouter>
				<ListView
					actions={ACTIONS}
					columns={COLUMNS}
					emptyState={EMPTY_STATE}
					endpoint={ENDPOINT}
				>
					{BODY}
				</ListView>
			</HashRouter>
		);

		await waitForElementToBeRemoved(() => {
			return document.querySelector('span.loading-animation');
		});

		expect(queryAllByTestId('item').length).toBe(20);
		expect(container.querySelectorAll('li.page-item').length).toBe(4);
		expect(
			container.querySelector('li.page-item.active').firstElementChild
				.textContent
		).toBe('1');
		expect(queryAllByText('Showing 1 to 20 of 21').length).toBe(1);
	});

	it('current page is greater than total pages', async () => {
		const history = createMemoryHistory();
		history.push('/test?page=2');
		fetch.mockResponse(JSON.stringify(RESPONSES.ONE_ITEM));
		const {container, queryAllByTestId} = render(
			<Router history={history}>
				<ListView
					actions={ACTIONS}
					columns={COLUMNS}
					emptyState={EMPTY_STATE}
					endpoint={ENDPOINT}
					history={history}
				>
					{BODY}
				</ListView>
			</Router>
		);

		await waitForElementToBeRemoved(() => {
			return document.querySelector('span.loading-animation');
		});

		expect(queryAllByTestId('item').length).toBe(1);
		expect(container.querySelectorAll('li.page-item').length).toBe(0);
	});

	it('calls actions promises', async () => {
		const refreshAction = jest.fn().mockResolvedValue(true);
		const nonRefreshAction = jest.fn().mockResolvedValue(false);
		fetch.mockResponse(JSON.stringify(RESPONSES.ONE_ITEM));

		const actions = [
			{
				name: 'Action without action',
			},
			{
				action: refreshAction,
				name: 'Action that forces refresh',
			},
			{
				action: nonRefreshAction,
				name: "Action that doesn't refresh",
			},
		];

		const {
			container,
			getAllByRole,
			queryAllByText,
			queryByPlaceholderText,
		} = render(
			<HashRouter>
				<ListView
					actions={actions}
					columns={COLUMNS}
					emptyState={EMPTY_STATE}
					endpoint={ENDPOINT}
					history={history}
				>
					{BODY}
				</ListView>
			</HashRouter>
		);

		await waitForElementToBeRemoved(() => {
			return document.querySelector('span.loading-animation');
		});

		let buttons = getAllByRole('button');
		const refreshButton = buttons[buttons.length - 2];

		await act(async () => {
			fireEvent.click(refreshButton);
		});

		expect(refreshAction.mock.calls.length).toBe(1);

		buttons = getAllByRole('button');
		const nonRefreshButton = buttons[buttons.length - 1];
		fireEvent.click(nonRefreshButton);

		expect(nonRefreshAction.mock.calls.length).toBe(1);
		expect(fetch.mock.calls.length).toEqual(2);

		const input = queryByPlaceholderText('search...');
		fireEvent.change(input, {target: {value: 'value'}});

		expect(input.value).toBe('value');
		expect(container.querySelector('.subnav-tbar')).toBeFalsy();

		const submit = container.querySelector('span > button:nth-child(2)');
		fireEvent.click(submit);

		expect(container.querySelector('.subnav-tbar')).toBeTruthy();

		const [clear] = queryAllByText('Clear');
		fireEvent.click(clear);

		expect(input.value).toBe('');
	});
});
