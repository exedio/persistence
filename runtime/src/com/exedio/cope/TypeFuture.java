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

package com.exedio.cope;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import org.jspecify.annotations.NonNull;

public interface TypeFuture<T extends Item>
{
	Type<T> get(Types types);

	final class Types
	{
		private final Set<Type<?>> types;

		Types(final Collection<Type<?>> types)
		{
			this.types = new TreeSet<>(Comparator.comparing(Type::getID)); // make exception message deterministic
			this.types.addAll(types);
		}

		boolean contains(final Type<?> type)
		{
			return types.contains(type);
		}

		<T extends Item> Type<T> findSubtypeHoaxingCompiler(final Class<? super T> superClass)
		{
			Type<T> result = null;
			for(final Type<?> type : types)
			{
				final Class<?> javaClass = type.getJavaClass();
				if(superClass!=javaClass && superClass.isAssignableFrom(javaClass))
				{
					if(result==null)
						result = castHoaxingCompiler(type);
					else
						throw new IllegalArgumentException(
								"createBySubtypeEXPERIMENTAL: multiple types assignable to " + superClass.getName() + ": " + result + " and " + type);
				}
			}
			if(result==null)
				throw new IllegalArgumentException(
						"createBySubtypeEXPERIMENTAL: no type assignable to " + superClass.getName());

			return result;
		}

		@SuppressWarnings("unchecked")
		private static <T extends Item> Type<T> castHoaxingCompiler(final Type<?> type)
		{
			return (Type<T>)type;
		}

		/**
		 * Needed for {@link ItemField#createBySubtypeEXPERIMENTAL(Class)}.
		 */
		static final Types DUMMY = new Types(Set.of());
	}

	/**
	 * The result of this method is used for creating informative error messages.
	 */
	@NonNull String describe();
}
