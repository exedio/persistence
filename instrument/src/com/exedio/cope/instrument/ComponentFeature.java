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

package com.exedio.cope.instrument;

final class ComponentFeature extends CopeFeature
{
	private final CopeFeature container;
	private final Object component;
	private final String postfix;

	ComponentFeature(final CopeFeature container, final Object component, final String postfix)
	{
		super(container.parent);
		this.container = container;
		this.component = component;
		this.postfix = postfix;
	}

	@Override
	String getName()
	{
		return container.getName()+"_"+postfix;
	}

	@Override
	int getModifier()
	{
		return container.getModifier();
	}

	@Override
	String getJavadocReference()
	{
		return "'"+postfix+"' of "+container.getJavadocReference();
	}

	@Override
	Boolean getInitialByConfiguration()
	{
		return false;
	}

	@Override
	String getType()
	{
		return "";
	}

	@Override
	Object evaluate()
	{
		return component;
	}

}
