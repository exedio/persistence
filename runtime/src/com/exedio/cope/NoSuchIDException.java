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

/**
 * Signals, that an attempt to find an item by it's ID has been failed,
 * because there is no item with such an ID.
 *
 * This exception will be thrown by {@link Model#getItem(String) Model.getItem},
 * if there is no item with the given ID.
 *
 * @author Ralf Wiebicke
 */
public final class NoSuchIDException extends Exception
{
	private static final long serialVersionUID = 1l;
	
	private final String id;
	private final boolean notAnID;
	private final String detail;
	
	NoSuchIDException(final String id, final boolean notAnID, final String detail)
	{
		this.id = id;
		this.notAnID = notAnID;
		this.detail = detail;
	}

	NoSuchIDException(final String id, final NumberFormatException cause, final String numberString)
	{
		super(cause);
		this.id = id;
		this.notAnID = true;
		this.detail = "wrong number format <"+numberString+">";
	}

	@Override
	public String getMessage()
	{
		return "no such id <" + id + ">, " + detail;
	}

	/**
	 * Returns, whether the id is invalid on principle
	 * within the currently deployed model,
	 * this means, there will never be and has never been an
	 * item for this id.
	 */
	public boolean notAnID()
	{
		return notAnID;
	}
}
