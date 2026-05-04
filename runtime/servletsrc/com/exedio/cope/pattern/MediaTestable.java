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

package com.exedio.cope.pattern;

import com.exedio.cope.Feature;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({
		"RedundantThrows", "unused"}) // TODO there is no implementation of MediaTestable on cope anymore, moved to copeim4java
public interface MediaTestable extends Feature
{
	/**
	 * @deprecated Use {@link #getTests()} <u>and</u> {@link Callable#call()} instead.
	 */
	@Deprecated
	void test() throws Exception;

	default List<Callable<?>> getTests()
	{
		return List.of(new Callable<>()
		{
			@Override
			public Void call() throws Exception
			{
				test();
				return null;
			}

			@Override
			public String toString()
			{
				return "all";
			}
		});
	}
}
