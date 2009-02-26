/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import java.sql.Connection;

import com.exedio.dsmf.Schema;

interface SequenceImpl
{
	void makeSchema(Schema schema);
	int next(Connection connection);
	
	/**
	 * Returns the same value as {@link #next(Connection)},
	 * but without incrementing the internal state.
	 * This multiple calls to {@link #getNext(Connection)} do
	 * return the same value again and again.
	 */
	int getNext(Connection connection);
	void flush();
}
