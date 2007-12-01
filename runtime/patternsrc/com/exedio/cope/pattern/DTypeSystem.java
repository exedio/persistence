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

import static com.exedio.cope.ItemField.DeletePolicy.CASCADE;
import static com.exedio.cope.ItemField.DeletePolicy.FORBID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import com.exedio.cope.BooleanField;
import com.exedio.cope.Cope;
import com.exedio.cope.DoubleField;
import com.exedio.cope.EnumField;
import com.exedio.cope.FunctionField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.Wrapper;
import com.exedio.cope.pattern.DAttribute.ValueType;

public final class DTypeSystem extends Pattern
{
	final StringField typeCode = new StringField().toFinal().unique();
	private Type<?> typeType = null;
	
	ItemField<?> attributeParent = null;
	final IntegerField attributePosition = new IntegerField().toFinal();
	final EnumField<ValueType> attributeValueType = Item.newEnumField(ValueType.class).toFinal();
	final IntegerField attributePositionPerValueType = new IntegerField().toFinal();
	final StringField attributeCode = new StringField().toFinal();
	Type<?> attributeType = null;
	
	ItemField<?> enumValueParent = null;
	final IntegerField enumValuePosition = new IntegerField().toFinal();
	final StringField enumValueCode = new StringField().toFinal();
	Type<?> enumValueType = null;
	
	private ItemField<?> type = null;
	private final FunctionField<?>[] fields;

	private final StringField[]  strings;
	private final BooleanField[] booleans;
	private final IntegerField[] integers;
	private final DoubleField[]  doubles;
	private final ItemField<?>[]  enums;
	
	public DTypeSystem(
			final int stringCapacity,
			final int booleanCapacity,
			final int integerCapacity,
			final int doubleCapacity,
			final int enumCapacity)
	{
		strings  = new StringField[stringCapacity];
		booleans = new BooleanField[booleanCapacity];
		integers = new IntegerField[integerCapacity];
		doubles  = new DoubleField [doubleCapacity];
		enums    = new ItemField[enumCapacity];
		fields   = new FunctionField[strings.length + booleans.length + integers.length + doubles.length + enums.length];

		int n = 0;
		for(int i = 0; i<strings.length; i++)
			registerSource(fields[n++] = strings [i] = new StringField().optional());
		for(int i = 0; i<booleans.length; i++)
			registerSource(fields[n++] = booleans[i] = new BooleanField().optional());
		for(int i = 0; i<integers.length; i++)
			registerSource(fields[n++] = integers[i] = new IntegerField().optional());
		for(int i = 0; i<doubles.length; i++)
			registerSource(fields[n++] = doubles [i] = new DoubleField().optional());
	}
	
	private FunctionField<?>[] array(final DAttribute.ValueType valueType)
	{
		switch(valueType)
		{
			case STRING:  return strings;
			case BOOLEAN: return booleans;
			case INTEGER: return integers;
			case DOUBLE:  return doubles;
			case ENUM:    return enums;
			default:
				throw new RuntimeException(valueType.toString());
		}
	}
	
	void assertCapacity(final DAttribute.ValueType valueType, final int positionPerValuetype)
	{
		final int capacity = array(valueType).length;
		if(capacity<=positionPerValuetype)
			throw new IllegalArgumentException("capacity for " + valueType + " exceeded, " + capacity + " available, but tried to allocate " + (positionPerValuetype+1));
	}
	
	@Override
	public void initialize()
	{
		final String name = getName();
		
		final LinkedHashMap<String, com.exedio.cope.Feature> features = new LinkedHashMap<String, com.exedio.cope.Feature>();
		features.put("code", typeCode);
		typeType = newType(features, "Type");
		
		features.clear();
		attributeParent = typeType.newItemField(CASCADE).toFinal();
		features.put("parent", attributeParent);
		features.put("position", attributePosition);
		features.put("uniqueConstraint", new UniqueConstraint(attributeParent, attributePosition));
		features.put("valueType", attributeValueType);
		features.put("positionPerValueType", attributePositionPerValueType);
		features.put("uniqueConstraintPerValueType", new UniqueConstraint(attributeParent, attributeValueType, attributePositionPerValueType));
		features.put("code", attributeCode);
		features.put("uniqueConstraintCode", new UniqueConstraint(attributeParent, attributeCode));
		attributeType = newType(features, "Field");
		
		registerSource(type = typeType.newItemField(FORBID).optional());
		initialize(type, name + "Type");

		if(enums.length>0)
		{
			features.clear();
			enumValueParent = attributeType.newItemField(CASCADE).toFinal();
			features.put("parent", enumValueParent);
			features.put("position", enumValuePosition);
			features.put("uniquePosition", new UniqueConstraint(enumValueParent, enumValuePosition));
			features.put("code", enumValueCode);
			features.put("uniqueCode", new UniqueConstraint(enumValueParent, enumValueCode));
			enumValueType = newType(features, "Enum");
			
			final int enumOffset = strings.length + booleans.length + integers.length + doubles.length;
			for(int i = 0; i<enums.length; i++)
				registerSource(fields[i+enumOffset] = enums[i] = enumValueType.newItemField(FORBID).optional());
		}
		
		for(final DAttribute.ValueType valueType : DAttribute.ValueType.values())
		{
			final FunctionField<?>[] array = array(valueType);
			final String postfix = valueType.postfix;
			for(int i = 0; i<array.length; i++)
				initialize(array[i], name + postfix + (i+1/*TODO: make this '1' customizable*/));
		}
	}
	
	public DType createType(final String code)
	{
		return new DType(this, typeType.newItem(typeCode.map(code)));
	}
	
	public List<DType> getTypes()
	{
		final List<? extends Item> backingItems = typeType.search();
		final ArrayList<DType> result = new ArrayList<DType>(backingItems.size());
		for(final Item backingItem : backingItems)
			result.add(new DType(this, backingItem));
		return Collections.unmodifiableList(result);
	}
	
	public DType getType(final String code)
	{
		return toDType(typeType.searchSingleton(typeCode.equal(code)));
	}
	
	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());
		
		result.add(new Wrapper(
			DType.class, "getType",
			"Returns the dynamic type of this item in the type system {0}.",
			"getter"));
		
		result.add(new Wrapper(
			void.class, "setType",
			"Sets the dynamic type of this item in the type system {0}.",
			"setter"
			).
			addParameter(DType.class, "type"));
			
		result.add(new Wrapper(
			Object.class, "get",
			"Returns the value of <tt>attribute</tt> for this item in the type system {0}.",
			"getter").
			addParameter(DAttribute.class, "attribute"));
			
		result.add(new Wrapper(
			void.class, "set",
			"Sets the value of <tt>attribute</tt> for this item in the type system {0}.",
			"setter").
			addParameter(DAttribute.class, "attribute").
			addParameter(Object.class, "value"));
		
		return Collections.unmodifiableList(result);
	}
	
	public DType getType(final Item item)
	{
		return toDType(this.type.get(item));
	}
	
	public Type getTypeType()
	{
		return typeType;
	}
	
	public ItemField<?> getTypeField()
	{
		return type;
	}
	
	public void setType(final Item item, final DType type)
	{
		if(type!=null && !this.equals(type.getDtypeSystem()))
			throw new RuntimeException("dynamic type system mismatch: new type has system " + type.getDtypeSystem() + ", but must be " + toString());
		
		final SetValue[] values = new SetValue[1+fields.length];
		values[0] = Cope.mapAndCast(this.type, type!=null ? type.backingItem : null);
		for(int i = 0; i<fields.length; i++)
			values[1+i] = fields[i].map(null);
		item.set(values);
	}
	
	private void assertType(final Item item, final DAttribute attribute)
	{
		final Item attributeType = attributeParent.get(attribute.backingItem);
		final Item itemType = type.get(item);
		if(!attributeType.equals(itemType))
			throw new IllegalArgumentException("dynamic type mismatch: attribute has type " + typeCode.get(attributeType) + ", but item has " + (itemType!=null ? typeCode.get(itemType) : "none"));
	}
	
	FunctionField<?> getField(final DAttribute attribute)
	{
		final DAttribute.ValueType valueType = attribute.getValueType();
		final int pos = attribute.getPositionPerValueType();

		final FunctionField[] array = array(valueType);
		
		// make a more verbose exception instead
		// of the ArrayIndexOutOfBoundException
		// thrown by the last line.
		final int capacity = array.length;
		if(capacity<=pos)
			throw new RuntimeException("accessing " + attribute + " exceeded capacity for " + valueType + ", " + capacity + " available, but tried to access " + (pos+1));

		return array[pos];
	}
	
	public Object get(final Item item, final DAttribute attribute)
	{
		assertType(item, attribute);
		final Object backingValue = getField(attribute).get(item);
		if(backingValue!=null && backingValue instanceof Item)
			return new DEnumValue(this, (Item)backingValue);
		else
			return backingValue;
	}
	
	public void set(final Item item, final DAttribute attribute, final Object value)
	{
		assertType(item, attribute);
		
		final Object backingValue;
		if(value!=null &&
			value instanceof DEnumValue &&
			attribute.getValueType()==DAttribute.ValueType.ENUM)
		{
			final DEnumValue enumValue = (DEnumValue)value;
			final DAttribute enumValueParent = enumValue.getParent();
			if(!enumValueParent.equals(attribute))
				throw new IllegalArgumentException("dynamic type system mismatch: enum value " + enumValue + " has type " + enumValueParent + ", but must be " + attribute);
			backingValue = enumValue.backingItem;
		}
		else
			backingValue = value;
		
		Cope.setAndCast(getField(attribute), item, backingValue);
	}
	
	private DType toDType(final Item backingItem)
	{
		return backingItem!=null ? new DType(this, backingItem) : null;
	}
}
