/*
 * Copyright (C) 2004-2012 exedio GmbH (www.exedio.com) This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package com.exedio.cope.pattern;

import com.exedio.cope.ConstraintViolationException;
import com.exedio.cope.Item;

/**
 * Thrown if {@link UniqueHashedMedia#execute(com.exedio.cope.pattern.UniqueHashedMedia.Value, Item)} is called with
 * a {@link com.exedio.cope.pattern.UniqueHashedMedia.Value} having an message digest algorithm not equal to the
 * algorithm of the UniqueHashedMedia.
 */
public final class IllegalAlgorithmException extends ConstraintViolationException
{
	private static final long serialVersionUID = 1l;

	private final UniqueHashedMedia feature;
	private final String messageDigestAlgorithm;

	/**
	 * Creates a new IllegalAlgorithmException with the necessary information
	 * about the violation.
	 */
	IllegalAlgorithmException(final UniqueHashedMedia feature, final Item item, final String messageDigestAlgorithm)
	{
		super(item, null);
		this.feature = feature;
		this.messageDigestAlgorithm = messageDigestAlgorithm;
	}

	@Override
	public UniqueHashedMedia getFeature()
	{
		return feature;
	}

	public String getMessageDigestAlgorithm()
	{
		return messageDigestAlgorithm;
	}

	@Override
	public String getMessage(final boolean withFeature)
	{
		return "illegal message digest algorithm  '" + messageDigestAlgorithm + '\'' + getItemPhrase() + (withFeature ? (" for " + feature) : "") + ", allowed is '"
				+ feature.getMessageDigestAlgorithm() + "' only.";
	}
}
