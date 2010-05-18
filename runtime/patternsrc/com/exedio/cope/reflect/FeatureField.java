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

package com.exedio.cope.reflect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.exedio.cope.Feature;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.util.Cast;

public final class FeatureField<E extends Feature> extends Pattern implements Settable<E>
{
	private static final long serialVersionUID = 1l;
	
	private final Class<E> valueClass;
	private final StringField idField;
	private final boolean isfinal;
	private final boolean optional;
	
	public static FeatureField<Feature> newField()
	{
		return newField(Feature.class);
	}
	
	public static <E extends Feature> FeatureField<E> newField(final Class<E> valueClass)
	{
		return new FeatureField<E>(valueClass, new StringField());
	}
	
	private FeatureField(final Class<E> valueClass, final StringField integer)
	{
		if(valueClass==null)
			throw new NullPointerException("valueClass");
		
		this.valueClass = valueClass;
		this.idField = integer;
		addSource(integer, "id", CustomAnnotatedElement.create(ComputedInstance.getAnnotation(), CopeSchemaNameEmpty.get()));
		this.isfinal = integer.isFinal();
		this.optional = !integer.isMandatory();
	}
	
	public FeatureField<E> toFinal()
	{
		return new FeatureField<E>(valueClass, idField.toFinal());
	}
	
	public FeatureField<E> optional()
	{
		return new FeatureField<E>(valueClass, idField.optional());
	}
	
	public Class<E> getValueClass()
	{
		return valueClass;
	}
	
	public StringField getIdField()
	{
		return idField;
	}
	
	public boolean isInitial()
	{
		return idField.isInitial();
	}
	
	public boolean isFinal()
	{
		return isfinal;
	}
	
	public Class getInitialType()
	{
		return valueClass;
	}
	
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		return idField.getInitialExceptions();
	}
	
	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());
		
		result.add(
			new Wrapper("get").
			addComment("Returns the value of {0}.").
			setReturn(Wrapper.TypeVariable0.class));
		
		if(!isfinal)
		{
			result.add(
				new Wrapper("set").
				addComment("Sets a new value for {0}.").
				addThrows(getInitialExceptions()).
				addParameter(Wrapper.TypeVariable0.class));
		}
			
		return Collections.unmodifiableList(result);
	}
	
	public E get(final Item item)
	{
		return Cast.verboseCast(valueClass, getType().getModel().getFeature(idField.get(item)));
	}
	
	public String getId(final Item item)
	{
		return idField.get(item);
	}
	
	public void set(final Item item, final E value)
	{
		if(isfinal)
			throw new FinalViolationException(this, this, item);
		if(value==null && !optional)
			throw new MandatoryViolationException(this, this, item);
		
		idField.set(item, value!=null ? value.getID() : null);
	}
	
	public SetValue<E> map(final E value)
	{
		return new SetValue<E>(this, value);
	}
	
	public SetValue[] execute(final E value, final Item exceptionItem)
	{
		if(value==null && !optional)
			throw new MandatoryViolationException(this, this, exceptionItem);
		
		return new SetValue[]{ idField.map(value!=null ? value.getID() : null) };
	}
}
