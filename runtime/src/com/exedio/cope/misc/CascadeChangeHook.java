/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope.misc;

import com.exedio.cope.ChangeHook;
import com.exedio.cope.Item;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;

final class CascadeChangeHook implements ChangeHook
{
	private final ChangeHook[] hooks;

	CascadeChangeHook(final ChangeHook[] hooks)
	{
		//noinspection AssignmentToCollectionOrArrayFieldFromParameter
		this.hooks = hooks;

		assert hooks!=null;
		assert hooks.length>1;
	}

	@Override
	public SetValue<?>[] beforeNew(final Type<?> type, SetValue<?>[] setValues)
	{
		for(final ChangeHook hook : hooks)
		{
			setValues = hook.beforeNew(type, setValues);
			if(setValues==null)
				throw new NullPointerException(
						"result of ChangeHook#beforeNew: " + hook);
		}
		return setValues;
	}

	@Override
	public void afterNew(final Item item)
	{
		for(final ChangeHook hook : hooks)
			hook.afterNew(item);
	}

	@Override
	public SetValue<?>[] beforeSet(final Item item, SetValue<?>[] setValues)
	{
		for(final ChangeHook hook : hooks)
		{
			setValues = hook.beforeSet(item, setValues);
			if(setValues==null)
				throw new NullPointerException(
						"result of ChangeHook#beforeSet: " + hook);
		}
		return setValues;
	}

	@Override
	public void beforeDelete(final Item item)
	{
		for(final ChangeHook hook : hooks)
			hook.beforeDelete(item);
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for(final ChangeHook hook : hooks)
		{
			if(first)
				first = false;
			else
				sb.append(" / ");

			sb.append(hook);
		}
		return sb.toString();
	}
}
