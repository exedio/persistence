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

import java.io.Serializable;
import java.util.function.Consumer;

public interface Selectable<E> extends Serializable
{
	/**
	 * Returns {@link #getValueType()}.{@link SelectType#getJavaClass() getJavaClass()}
	 */
	Class<E> getValueClass();

	@SuppressWarnings("ClassEscapesDefinedScope")
	SelectType<E> getValueType();

	Type<?> getType();

	void toString(StringBuilder sb, Type<?> defaultType);

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Deprecated // OK: for internal use within COPE only
	void check(@SuppressWarnings("ClassEscapesDefinedScope") TC tc, Join join);

	/**
	 * @see Iterable#forEach(Consumer)
	 * @see Condition#forEachFieldCovered(Consumer)
	 */
	void forEachFieldCovered(Consumer<Field<?>> action);

	/**
	 * @deprecated Use and override {@link #forEachFieldCovered(Consumer)} instead.
	 * DO NOT OVERRIDE THIS FUNCTION.
	 */
	@Deprecated
	default void acceptFieldsCovered(final Consumer<Field<?>> consumer)
	{
		forEachFieldCovered(consumer);
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Deprecated // OK: for internal use within COPE only
	void append(@SuppressWarnings("ClassEscapesDefinedScope") Statement bf, Join join);
}
