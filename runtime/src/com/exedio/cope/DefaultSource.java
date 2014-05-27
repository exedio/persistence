/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import java.lang.reflect.AnnotatedElement;

abstract class DefaultSource<E>
{
	/**
	 * Generates a default value.
	 * <ul>
	 * <li>The result may or may not be stable, i.e.
	 * multiple calls even with the same value for <tt>now</tt>
	 * may return different results.</li>
	 * <li>The result may or may not depend on parameter <tt>now</tt>.
	 * However the result must not depend on {@link System#currentTimeMillis()},
	 * but use <tt>now</tt> instead.
	 * </li>
	 * <li>Calling this method may or may not cause side effects,
	 * such as incrementing a sequence or exhausting random entropy.</li>
	 * </ul>
	 * @param now
	 * The caller must provide the results of {@link System#currentTimeMillis()} here.
	 * However the caller may reuse the results of {@link System#currentTimeMillis()} for multiple calls,
	 * even for different {@link DefaultSource}s.
	 */
	abstract E generate(long now);

	abstract DefaultSource<E> forNewField();

	abstract void mount(FunctionField<E> field);

	void mount(final Type<? extends Item> type, final String name, final AnnotatedElement annotationSource)
	{
		// empty
	}
}
