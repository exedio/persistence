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

import com.exedio.cope.instrument.WrapImplementsInterim;

@FunctionalInterface
@WrapImplementsInterim(addMethods=true)
@SuppressWarnings({"InterfaceNeverImplemented","RedundantThrows"})
public interface Dispatchable
{
	void dispatch(Dispatcher dispatcher) throws Exception;

	/**
	 * The default implementation does nothing.
	 * @param dispatcher used by subclasses
	 * @param cause used by subclasses
	 */
	default void notifyFinalFailure(final Dispatcher dispatcher, final Exception cause)
	{
		// empty default implementation
	}
}
