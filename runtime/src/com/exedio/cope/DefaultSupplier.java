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

import com.exedio.cope.util.Clock;
import java.lang.reflect.AnnotatedElement;

abstract class DefaultSupplier<E>
{
	/**
	 * Generates a default value.
	 * <ul>
	 * <li>The result may or may not be stable, i.e.
	 * multiple calls even with the same <tt>ctx</tt>
	 * may return different results.</li>
	 * <li>The result may or may not depend on parameter <tt>ctx</tt>.
	 * However the result must not depend on {@link System#currentTimeMillis()},
	 * but use {@link Context#currentTimeMillis() ctx.currentTimeMillis()} instead.
	 * </li>
	 * <li>Calling this method may or may not cause side effects,
	 * such as incrementing a sequence or exhausting random entropy.</li>
	 * </ul>
	 * @param ctx
	 * The caller must provide a {@link Context} here.
	 * However the caller may reuse the {@link Context} for multiple calls,
	 * even for different {@code DefaultSupplier}s.
	 */
	abstract E generate(Context ctx);

	static final class Context
	{
		private long now = Long.MIN_VALUE;
		private boolean needsNow = true;

		long currentTimeMillis()
		{
			if(needsNow)
			{
				now = Clock.currentTimeMillis();
				needsNow = false;
			}
			return now;
		}
	}

	abstract DefaultSupplier<E> forNewField();

	abstract void mount(FunctionField<E> field);

	/**
	 * @param type used in subclasses
	 * @param name used in subclasses
	 * @param annotationSource used in subclasses
	 */
	void mount(final Type<?> type, final String name, final AnnotatedElement annotationSource)
	{
		// empty
	}
}
