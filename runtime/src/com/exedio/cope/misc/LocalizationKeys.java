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

import com.exedio.cope.CopeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Obeys @{@link CopeName}.
 */
public final class LocalizationKeys
{
	/**
	 * @return a {@link Collections#unmodifiableList(List) unmodifiable list}.
	 */
	public static List<String> get(final Class<?> clazz)
	{
		final String withoutPackage = withoutPackage(clazz);
		final Package pack = clazz.getPackage();

		if(pack==null || // default package until jdk1.8
			pack.getName().isEmpty()) // default package since jdk9
			return Collections.singletonList(withoutPackage);

		return Collections.unmodifiableList(Arrays.asList(
				pack.getName() + '.' + withoutPackage,
				withoutPackage));
	}

	private static String withoutPackage(final Class<?> clazz)
	{
		final String simple = CopeNameUtil.getAndFallbackToSimpleName(clazz);
		final Class<?> enclosing = clazz.getEnclosingClass();
		return
			enclosing!=null
			? withoutPackage(enclosing) + '.' + simple
			:                                   simple;
	}


	/**
	 * @return a {@link Collections#unmodifiableList(List) unmodifiable list}.
	 */
	public static List<String> get(final Enum<?> value)
	{
		final String suffix = '.' + CopeNameUtil.getAndFallbackToName(value);
		final List<String> byType = get(value.getDeclaringClass());
		final ArrayList<String> result = new ArrayList<>(byType.size());
		for(final String prefix : byType)
			result.add(prefix + suffix);
		return ListUtil.trimUnmodifiable(result);
	}


	private LocalizationKeys()
	{
		// prevent instantiation
	}
}
