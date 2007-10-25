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

package com.exedio.copernica;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.exedio.cope.Field;

public class TransientSection
	extends TransientComponent
	implements CopernicaSection
{
	private final List<? extends Field> attributes;
	
	public TransientSection(final String id, final Field[] attributes)
	{
		super(id);
		this.attributes = Collections.unmodifiableList(Arrays.asList(attributes));
	}
	
	public Collection<? extends Field> getCopernicaAttributes()
	{
		return attributes;
	}
}
