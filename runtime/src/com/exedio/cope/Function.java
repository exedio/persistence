/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

public interface Function<E> extends Selectable<E>
{
	E get(Item item);

	/**
	 * Casts <tt>o</tt> to <tt>E</tt>.
	 * @throws ClassCastException if <tt>o</tt> is not assignable to <tt>E</tt>
	 * @see Class#cast(Object)
	 */
	E cast(Object o);
	
	void append(Statement bf, Join join);
	void appendParameter(Statement bf, E value);
	
	// convenience methods for conditions and views ---------------------------------
	
	EqualCondition equal(E value);

	/**
	 * {@link #cast(Object) Casts}
	 * <tt>value</tt> to <tt>E</tt> before calling
	 * {@link #equal(Object)}
	 * @throws ClassCastException if <tt>value</tt> is not assignable to <tt>E</tt>
	 */
	EqualCondition equalAndCast(Object value);
	
	EqualCondition equal(Join join, E value);
	CompositeCondition in(Collection<E> value);

	NotEqualCondition notEqual(E value);
	
	/**
	 * {@link #cast(Object) Casts}
	 * <tt>value</tt> to <tt>E</tt> before calling
	 * {@link #notEqual(Object)}
	 * @throws ClassCastException if <tt>value</tt> is not assignable to <tt>E</tt>
	 */
	NotEqualCondition notEqualAndCast(Object value);
	
	EqualFunctionCondition equal(Function<E> right);
	CompareCondition less(E value);

	/**
	 * {@link #cast(Object) Casts}
	 * <tt>value</tt> to <tt>E</tt> before calling
	 * {@link #less(Object)}
	 * @throws ClassCastException if <tt>value</tt> is not assignable to <tt>E</tt>
	 */
	CompareCondition lessAndCast(Object value);

	CompareCondition lessOrEqual(E value);

	/**
	 * {@link #cast(Object) Casts}
	 * <tt>value</tt> to <tt>E</tt> before calling
	 * {@link #lessOrEqual(Object)}
	 * @throws ClassCastException if <tt>value</tt> is not assignable to <tt>E</tt>
	 */
	CompareCondition lessOrEqualAndCast(Object value);
	
	CompareCondition greater(E value);

	/**
	 * {@link #cast(Object) Casts}
	 * <tt>value</tt> to <tt>E</tt> before calling
	 * {@link #greater(Object)}
	 * @throws ClassCastException if <tt>value</tt> is not assignable to <tt>E</tt>
	 */
	CompareCondition greaterAndCast(Object value);
	
	CompareCondition greaterOrEqual(E value);

	/**
	 * {@link #cast(Object) Casts}
	 * <tt>value</tt> to <tt>E</tt> before calling
	 * {@link #greaterOrEqual(Object)}
	 * @throws ClassCastException if <tt>value</tt> is not assignable to <tt>E</tt>
	 */
	CompareCondition greaterOrEqualAndCast(Object value);
}
