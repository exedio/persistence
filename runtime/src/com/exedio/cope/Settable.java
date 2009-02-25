/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import java.util.Set;

public interface Settable<E>
{
	SetValue map(E value);
	SetValue[] execute(E value, Item exceptionItem);
	
	/**
	 * Returns true, if a value for the settable can be specified
	 * on the creation of an item only, thus cannot be modified later.
	 */
	boolean isFinal();
	
	// used by instrumentor for creation constructors
	
	/**
	 * Returns true, if a value for the settable should be specified
	 * on the creation of an item.
	 */
	boolean isInitial();
	
	java.lang.reflect.Type getInitialType();
	
	/**
	 * Returns the exceptions possibly thrown,
	 * when setting a value for this settable.
	 */
	Set<Class<? extends Throwable>> getInitialExceptions();
}
