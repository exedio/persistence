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

public final class DynamicModel extends Pattern
{
	final StringField typeCode = new StringField().toFinal().unique();
	private Type<?> typeType = null;
	
	ItemField<?> fieldParent = null;
	final IntegerField fieldPosition = new IntegerField().toFinal();
	final EnumField<ValueType> fieldValueType = Item.newEnumField(ValueType.class).toFinal();
	final IntegerField fieldPositionPerValueType = new IntegerField().toFinal();
	final StringField fieldCode = new StringField().toFinal();
	Type<?> fieldType = null;
	
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
	
	public DynamicModel(
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
	
	private FunctionField<?>[] array(final ValueType valueType)
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
	
	void assertCapacity(final ValueType valueType, final int positionPerValuetype)
	{
		final int capacity = array(valueType).length;
		if(capacity<=positionPerValuetype)
			throw new IllegalArgumentException(
					"capacity for " + valueType + " exceeded, " +
					capacity + " available, " +
					"but tried to allocate " + (positionPerValuetype+1));
	}
	
	@Override
	public void initialize()
	{
		final String name = getName();
		
		final LinkedHashMap<String, com.exedio.cope.Feature> features = new LinkedHashMap<String, com.exedio.cope.Feature>();
		features.put("code", typeCode);
		typeType = newType(features, "Type");
		
		features.clear();
		fieldParent = typeType.newItemField(CASCADE).toFinal();
		features.put("parent", fieldParent);
		features.put("position", fieldPosition);
		features.put("uniqueConstraint", new UniqueConstraint(fieldParent, fieldPosition));
		features.put("valueType", fieldValueType);
		features.put("positionPerValueType", fieldPositionPerValueType);
		features.put("uniqueConstraintPerValueType", new UniqueConstraint(fieldParent, fieldValueType, fieldPositionPerValueType));
		features.put("code", fieldCode);
		features.put("uniqueConstraintCode", new UniqueConstraint(fieldParent, fieldCode));
		fieldType = newType(features, "Field");
		
		registerSource(type = typeType.newItemField(FORBID).optional());
		initialize(type, name + "Type");

		if(enums.length>0)
		{
			features.clear();
			enumValueParent = fieldType.newItemField(CASCADE).toFinal();
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
		
		for(final ValueType valueType : ValueType.values())
		{
			final FunctionField<?>[] array = array(valueType);
			final String postfix = valueType.postfix;
			for(int i = 0; i<array.length; i++)
				initialize(array[i], name + postfix + (i+1/*TODO: make this '1' customizable*/));
		}
	}
	
	public DType createType(final String code)
	{
		return new DType(typeType.newItem(typeCode.map(code)));
	}
	
	public List<DType> getTypes()
	{
		final List<? extends Item> backingItems = typeType.search();
		final ArrayList<DType> result = new ArrayList<DType>(backingItems.size());
		for(final Item backingItem : backingItems)
			result.add(new DType(backingItem));
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
			"Returns the dynamic type of this item in the model {0}.",
			"getter"));
		
		result.add(new Wrapper(
			void.class, "setType",
			"Sets the dynamic type of this item in the model {0}.",
			"setter"
			).
			addParameter(DType.class, "type"));
			
		result.add(new Wrapper(
			Object.class, "get",
			"Returns the value of <tt>field</tt> for this item in the model {0}.",
			"getter").
			addParameter(DField.class, "field"));
			
		result.add(new Wrapper(
			void.class, "set",
			"Sets the value of <tt>field</tt> for this item in the model {0}.",
			"setter").
			addParameter(DField.class, "field").
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
	
	public Type getFieldType()
	{
		return fieldType;
	}
	
	public Type getEnumValueType()
	{
		return enumValueType;
	}
	
	public ItemField<?> getTypeField()
	{
		return type;
	}
	
	public void setType(final Item item, final DType type)
	{
		if(type!=null && !this.equals(type.getModel()))
			throw new IllegalArgumentException(
					"dynamic model mismatch: new type has model " + type.getModel() +
					", but must be " + toString());
		
		final SetValue[] values = new SetValue[1+fields.length];
		values[0] = Cope.mapAndCast(this.type, type!=null ? type.getBackingItem() : null);
		for(int i = 0; i<fields.length; i++)
			values[1+i] = fields[i].map(null);
		item.set(values);
	}
	
	private void assertType(final Item item, final DField field)
	{
		final Item fieldType = fieldParent.get(field.getBackingItem());
		final Item itemType = type.get(item);
		if(!fieldType.equals(itemType))
			throw new IllegalArgumentException(
					"dynamic type mismatch: field has type " + typeCode.get(fieldType) +
					", but item has " + (itemType!=null ? typeCode.get(itemType) : "none"));
	}
	
	FunctionField<?> getField(final DField field)
	{
		final ValueType valueType = field.getValueType();
		final int pos = field.getPositionPerValueType();

		final FunctionField[] array = array(valueType);
		
		// make a more verbose exception instead
		// of the ArrayIndexOutOfBoundException
		// thrown by the last line.
		final int capacity = array.length;
		if(capacity<=pos)
			throw new RuntimeException("accessing " + field + " exceeded capacity for " + valueType + ", " + capacity + " available, but tried to access " + (pos+1));

		return array[pos];
	}
	
	public Object get(final Item item, final DField field)
	{
		assertType(item, field);
		final Object backingValue = getField(field).get(item);
		if(backingValue!=null && backingValue instanceof Item)
			return new DEnumValue((Item)backingValue);
		else
			return backingValue;
	}
	
	public void set(final Item item, final DField field, final Object value)
	{
		assertType(item, field);
		
		final Object backingValue;
		if(value!=null &&
			value instanceof DEnumValue &&
			field.getValueType()==ValueType.ENUM)
		{
			final DEnumValue enumValue = (DEnumValue)value;
			final DField enumValueParent = enumValue.getParent();
			if(!enumValueParent.equals(field))
				throw new IllegalArgumentException("dynamic model mismatch: enum value " + enumValue + " has type " + enumValueParent + ", but must be " + field);
			backingValue = enumValue.getBackingItem();
		}
		else
			backingValue = value;
		
		Cope.setAndCast(getField(field), item, backingValue);
	}
	
	private DType toDType(final Item backingItem)
	{
		return backingItem!=null ? new DType(backingItem) : null;
	}

	public static enum ValueType
	{
		STRING (String.class,     "String"),
		BOOLEAN(Boolean.class,    "Bool"),
		INTEGER(Integer.class,    "Int"),
		DOUBLE (Double.class,     "Double"),
		ENUM   (DEnumValue.class, "Enum");
		
		final Class valueClass;
		final String postfix;
		
		ValueType(final Class valueClass, final String postfix)
		{
			this.valueClass = valueClass;
			this.postfix = postfix;
		}
		
		public final Class getValueClass()
		{
			return valueClass;
		}
	}

	public final class DType extends BackedItem
	{
		DType(final Item backingItem)
		{
			super(backingItem);
		}
		
		public DField addField(final String code, final ValueType valueType)
		{
			final List<DField> fields = getFields(); // TODO make more efficient
			final int position = fields.isEmpty() ? 0 : (fields.get(fields.size()-1).getPosition()+1);
			final List<DField> fieldsPerValuetype = getFields(valueType); // TODO make more efficient
			final int positionPerValuetype = fieldsPerValuetype.isEmpty() ? 0 : (fieldsPerValuetype.get(fieldsPerValuetype.size()-1).getPositionPerValueType()+1);
			DynamicModel.this.assertCapacity(valueType, positionPerValuetype);
			return new DField(
					fieldType.newItem(
							Cope.mapAndCast(fieldParent, backingItem),
							fieldPosition.map(position),
							fieldCode.map(code),
							fieldValueType.map(valueType),
							fieldPositionPerValueType.map(positionPerValuetype)));
		}

		public DField addStringField(final String code)
		{
			return addField(code, ValueType.STRING);
		}

		public DField addBooleanField(final String code)
		{
			return addField(code, ValueType.BOOLEAN);
		}
		
		public DField addIntegerField(final String code)
		{
			return addField(code, ValueType.INTEGER);
		}
		
		public DField addDoubleField(final String code)
		{
			return addField(code, ValueType.DOUBLE);
		}
		
		public DField addEnumField(final String code)
		{
			return addField(code, ValueType.ENUM);
		}

		public List<DField> getFields()
		{
			final List<? extends Item> backingItems = fieldType.search(Cope.equalAndCast(fieldParent, backingItem), fieldPosition, true);
			final ArrayList<DField> result = new ArrayList<DField>(backingItems.size());
			for(final Item backingItem : backingItems)
				result.add(new DField(backingItem));
			return Collections.unmodifiableList(result);
		}

		public DField getField(final String code)
		{
			return toDField(fieldType.searchSingleton(Cope.equalAndCast(fieldParent, backingItem).and(fieldCode.equal(code))));
		}
		
		private List<DField> getFields(final ValueType valueType)
		{
			final List<? extends Item> backingItems = fieldType.search(Cope.equalAndCast(fieldParent, backingItem).and(fieldValueType.equal(valueType)), fieldPositionPerValueType, true);
			final ArrayList<DField> result = new ArrayList<DField>(backingItems.size());
			for(final Item backingItem : backingItems)
				result.add(new DField(backingItem));
			return Collections.unmodifiableList(result);
		}
		
		public Type getParentType()
		{
			return DynamicModel.this.getType();
		}
		
		public DynamicModel getModel()
		{
			return DynamicModel.this;
		}
		
		public String getCode()
		{
			return typeCode.get(backingItem);
		}
		
		private DField toDField(final Item backingItem)
		{
			return backingItem!=null ? new DField(backingItem) : null;
		}
		
		/**
		 * @deprecated Use {@link #addField(String,ValueType)} instead
		 */
		@Deprecated
		public DField addAttribute(final String code, final ValueType valueType)
		{
			return addField(code, valueType);
		}
		
		/**
		 * @deprecated Use {@link #addStringField(String)} instead
		 */
		@Deprecated
		public DField addStringAttribute(final String code)
		{
			return addStringField(code);
		}
		
		/**
		 * @deprecated Use {@link #addBooleanField(String)} instead
		 */
		@Deprecated
		public DField addBooleanAttribute(final String code)
		{
			return addBooleanField(code);
		}
		
		/**
		 * @deprecated Use {@link #addIntegerField(String)} instead
		 */
		@Deprecated
		public DField addIntegerAttribute(final String code)
		{
			return addIntegerField(code);
		}

		/**
		 * @deprecated Use {@link #addDoubleField(String)} instead
		 */
		@Deprecated
		public DField addDoubleAttribute(final String code)
		{
			return addDoubleField(code);
		}

		/**
		 * @deprecated Use {@link #addEnumField(String)} instead
		 */
		@Deprecated
		public DField addEnumAttribute(final String code)
		{
			return addEnumField(code);
		}
		
		/**
		 * @deprecated Use {@link #getFields()} instead
		 */
		@Deprecated
		public List<DField> getAttributes()
		{
			return getFields();
		}
		
		/**
		 * @deprecated Use {@link #getField(String)} instead
		 */
		@Deprecated
		public DField getAttribute(final String code)
		{
			return getField(code);
		}
		
		/**
		 * @deprecated Use {@link #getModel()} instead
		 */
		@Deprecated
		public DynamicModel getDtypeSystem()
		{
			return getModel();
		}
	}
	
	public final class DField extends BackedItem
	{
		DField(final Item backingItem)
		{
			super(backingItem);
		}
		
		public Object get(final Item item)
		{
			return DynamicModel.this.get(item, this);
		}
		
		public void set(final Item item, final Object value)
		{
			DynamicModel.this.set(item, this, value);
		}
		
		private void assertEnum()
		{
			final ValueType vt = getValueType();
			if(vt!=ValueType.ENUM)
				throw new IllegalArgumentException("operation allowed for getValueType()==ENUM fields only, but was " + vt);
		}
		
		public List<DEnumValue> getEnumValues()
		{
			assertEnum();
			final List<? extends Item> backingItems = enumValueType.search(Cope.equalAndCast(enumValueParent, backingItem), enumValuePosition, true);
			final ArrayList<DEnumValue> result = new ArrayList<DEnumValue>(backingItems.size());
			for(final Item backingItem : backingItems)
				result.add(new DEnumValue(backingItem));
			return Collections.unmodifiableList(result);
		}
		
		public DEnumValue getEnumValue(final String code)
		{
			assertEnum();
			return toDEnumValue(enumValueType.searchSingleton(Cope.equalAndCast(enumValueParent, backingItem).and(enumValueCode.equal(code))));
		}
		
		public DEnumValue addEnumValue(final String code)
		{
			assertEnum();
			final List<DEnumValue> values = getEnumValues(); // TODO make more efficient
			final int position = values.isEmpty() ? 0 : (values.get(values.size()-1).getPosition()+1);
			return new DEnumValue(
					enumValueType.newItem(
							Cope.mapAndCast(enumValueParent, backingItem),
							enumValuePosition.map(position),
							enumValueCode.map(code)));
		}
		
		public DType getParent()
		{
			return new DType(fieldParent.get(backingItem));
		}
		
		public int getPosition()
		{
			return fieldPosition.getMandatory(backingItem);
		}
		
		public ValueType getValueType()
		{
			return fieldValueType.get(backingItem);
		}
		
		int getPositionPerValueType()
		{
			return fieldPositionPerValueType.getMandatory(backingItem);
		}
		
		public String getCode()
		{
			return fieldCode.get(backingItem);
		}
		
		public FunctionField<?> getField()
		{
			return DynamicModel.this.getField(this);
		}
		
		private DEnumValue toDEnumValue(final Item backingItem)
		{
			return backingItem!=null ? new DEnumValue(backingItem) : null;
		}
	}

	public final class DEnumValue extends BackedItem
	{
		DEnumValue(final Item backingItem)
		{
			super(backingItem);
		}
		
		public DField getParent()
		{
			return new DField(enumValueParent.get(backingItem));
		}
		
		public int getPosition()
		{
			return enumValuePosition.getMandatory(backingItem);
		}
		
		public String getCode()
		{
			return enumValueCode.get(backingItem);
		}
	}
}
