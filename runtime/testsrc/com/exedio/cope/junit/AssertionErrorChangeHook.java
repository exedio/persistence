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

package com.exedio.cope.junit;

import com.exedio.cope.ChangeHook;
import com.exedio.cope.Item;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;
import java.util.Arrays;

public class AssertionErrorChangeHook implements ChangeHook
{
	@Override
	public SetValue<?>[] beforeNew(final Type<?> type, final SetValue<?>[] setValues)
	{
		throw new AssertionError(type + Arrays.toString(setValues));
	}

	@Override
	public void afterNew(final Item item)
	{
		throw new AssertionError(item);
	}

	@Override
	public SetValue<?>[] beforeSet(final Item item, final SetValue<?>[] setValues)
	{
		throw new AssertionError(item + Arrays.toString(setValues));
	}

	@Override
	public void beforeDelete(final Item item)
	{
		throw new AssertionError(item);
	}

	@Override
	public String toString()
	{
		throw new AssertionError();
	}
}
