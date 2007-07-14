/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.ConstraintViolationException;
import com.exedio.cope.Item;

public final class IllegalContentTypeException extends ConstraintViolationException
{
	private static final long serialVersionUID = 1l;
	
	private final Media feature;
	private final String contentType;
	
	/**
	 * Creates a new IllegalContentTypeException with the neccessary information about the violation.
	 * @param item initializes, what is returned by {@link #getItem()}.
	 * @param feature initializes, what is returned by {@link #getFeature()}.
	 */
	IllegalContentTypeException(final Media feature, final Item item, final String contentType)
	{
		super(item, null);
		this.feature = feature;
		this.contentType = contentType;
	}
	
	@Override
	public Media getFeature()
	{
		return feature;
	}
	
	public String getContentType()
	{
		return contentType;
	}

	@Override
	public String getMessage()
	{
		return
			"illegal content type '" + contentType + 
			"' on " + getItemID() + 
			" for " + feature + 
			", allowed is '" + feature.getContentTypeDescription() + "' only.";
	}
}
