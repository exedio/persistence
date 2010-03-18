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

class RenamedSchemaPattern extends Pattern
{
	private static final long serialVersionUID = 1l;
	
	final IntegerField sourceFeature;

	final StringField sourceTypeField = new StringField();
	private Type<?> sourceType = null;
	
	RenamedSchemaPattern()
	{
		this.sourceFeature = new IntegerField();
		addSource(sourceFeature, "sourceFeature");
	}
	
	@Override
	protected void onMount()
	{
		super.onMount();
		
		final Features features = new Features();
		features.put("field", sourceTypeField);
		this.sourceType = newSourceType(SourceType.class, features);
	}
	
	Type<?> getSourceType()
	{
		if(sourceType==null)
			throw new IllegalStateException();
		
		return sourceType;
	}
	
	static final class SourceType extends Item
	{
		private static final long serialVersionUID = 1l;
		
		private SourceType(final ActivationParameters ap)
		{
			super(ap);
		}
	}
}
