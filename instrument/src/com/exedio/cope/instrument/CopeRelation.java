/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import java.util.List;

import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.pattern.Relation;
import com.exedio.cope.pattern.VectorRelation;

final class CopeRelation extends CopeFeature
{
	final boolean vector;
	final String sourceTypeString;
	final String targetTypeString;
	
	public CopeRelation(final CopeType parent, final JavaAttribute javaAttribute, final boolean vector)
	{
		super(parent, javaAttribute);
		this.vector = vector;

		final List<String> ends = Injector.getGenerics(javaAttribute.type);
		if(ends.size()!=2)
			throw new RuntimeException("Relation must be typed with two types, but was " + javaAttribute.type);

		this.sourceTypeString = ends.get(0);
		this.targetTypeString = ends.get(1);
	}

	private CopeType sourceType = null;
	private CopeType targetType = null;
	
	@Override
	void endBuildStage()
	{
		sourceType = javaAttribute.file.repository.getCopeType(sourceTypeString);
		targetType = javaAttribute.file.repository.getCopeType(targetTypeString);
		sourceType.addRelation(this, true);
		targetType.addRelation(this, false);
	}
	
	String getEndType(final boolean source)
	{
		return (source ? sourceType : targetType).javaClass.getFullName();
	}
	
	String getEndName(final boolean source)
	{
		final ItemField<? extends Item> endAttribute;
		if(vector)
		{
			final VectorRelation<? extends Item,? extends Item> instance = (VectorRelation<? extends Item,? extends Item>)getInstance();
			endAttribute = source ? instance.getSource() : instance.getTarget();
		}
		else
		{
			final Relation<? extends Item,? extends Item> instance = (Relation<? extends Item,? extends Item>)getInstance();
			endAttribute = source ? instance.getSource() : instance.getTarget();
		}
		
		for(final CopeFeature feature : parent.getFeatures())
		{
			if(endAttribute==feature.getInstance())
				return feature.name;
		}
		throw new RuntimeException("no " + (source?"source":"target") + " found for relation " + parent.javaClass.getFullName() + '#' + name);
	}
	
	@Override
	boolean isBoxed()
	{
		return false;
	}
	
	@Override
	String getBoxedType()
	{
		throw new RuntimeException();
	}
}
