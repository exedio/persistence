/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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
import static java.lang.Math.max;

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
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.instrument.Wrapper;

public final class DynamicModel<L> extends Pattern
{
	private final FunctionField<L> localeTemplate;
	final StringField typeCode = new StringField().toFinal().unique();
	com.exedio.cope.Type<?> typeType = null;
	Localization typeLocalization = null;
	
	ItemField<?> fieldParent = null;
	final IntegerField fieldPosition = new IntegerField().toFinal();
	final EnumField<ValueType> fieldValueType = Item.newEnumField(ValueType.class).toFinal();
	final IntegerField fieldPositionPerValueType;
	final StringField fieldCode = new StringField().toFinal();
	com.exedio.cope.Type<?> fieldType = null;
	Localization fieldLocalization = null;
	
	ItemField<?> enumParent = null;
	final IntegerField enumPosition = new IntegerField().toFinal();
	final StringField enumCode = new StringField().toFinal();
	com.exedio.cope.Type<?> enumType = null;
	Localization enumLocalization = null;
	
	private ItemField<?> type = null;
	private final FunctionField<?>[] fields;

	private final StringField[]  strings;
	private final BooleanField[] booleans;
	private final IntegerField[] integers;
	private final DoubleField[]  doubles;
	private final ItemField<?>[]  enums;
	
	private DynamicModel(
			final FunctionField<L> locale,
			final int stringCapacity,
			final int booleanCapacity,
			final int integerCapacity,
			final int doubleCapacity,
			final int enumCapacity)
	{
		this.localeTemplate = locale;
		fieldPositionPerValueType = new IntegerField().toFinal().range(0,
				max(max(max(max(stringCapacity, booleanCapacity), integerCapacity), doubleCapacity), enumCapacity));
		strings  = new StringField[stringCapacity];
		booleans = new BooleanField[booleanCapacity];
		integers = new IntegerField[integerCapacity];
		doubles  = new DoubleField [doubleCapacity];
		enums    = new ItemField[enumCapacity];
		fields   = new FunctionField[strings.length + booleans.length + integers.length + doubles.length + enums.length];

		int n = 0;
		for(int i = 0; i<strings.length; i++)
			registerSource(fields[n++] = strings [i] = new StringField().optional(),  ValueType.STRING .postfix + (i+1));
		for(int i = 0; i<booleans.length; i++)
			registerSource(fields[n++] = booleans[i] = new BooleanField().optional(), ValueType.BOOLEAN.postfix + (i+1));
		for(int i = 0; i<integers.length; i++)
			registerSource(fields[n++] = integers[i] = new IntegerField().optional(), ValueType.INTEGER.postfix + (i+1));
		for(int i = 0; i<doubles.length; i++)
			registerSource(fields[n++] = doubles [i] = new DoubleField().optional(),  ValueType.DOUBLE .postfix + (i+1));
	}
	
	public static final <L> DynamicModel<L> newModel(
			final FunctionField<L> locale,
			final int stringCapacity,
			final int booleanCapacity,
			final int integerCapacity,
			final int doubleCapacity,
			final int enumCapacity)
	{
		return new DynamicModel<L>(locale,
				stringCapacity, booleanCapacity, integerCapacity, doubleCapacity, enumCapacity);
	}
	
	public static final <L> DynamicModel<L> newModel(final FunctionField<L> locale)
	{
		return new DynamicModel<L>(locale, 5, 5, 5, 5, 5);
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
		final LinkedHashMap<String, com.exedio.cope.Feature> features = new LinkedHashMap<String, com.exedio.cope.Feature>();
		features.put("code", typeCode);
		typeType = newType(PatternItem.class, features, "Type");
		typeLocalization = new Localization(typeType, localeTemplate, "Type");
		
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
		fieldType = newType(PatternItem.class, features, "Field");
		fieldLocalization = new Localization(fieldType, localeTemplate, "Field");
		
		if(enums.length>0)
		{
			features.clear();
			enumParent = fieldType.newItemField(CASCADE).toFinal();
			features.put("parent", enumParent);
			features.put("position", enumPosition);
			features.put("uniquePosition", new UniqueConstraint(enumParent, enumPosition));
			features.put("code", enumCode);
			features.put("uniqueCode", new UniqueConstraint(enumParent, enumCode));
			enumType = newType(PatternItem.class, features, "Enum");
			enumLocalization = new Localization(enumType, localeTemplate, "Enum");
			
			final int enumOffset = strings.length + booleans.length + integers.length + doubles.length;
			for(int i = 0; i<enums.length; i++)
				registerSource(fields[i+enumOffset] = enums[i] = enumType.newItemField(FORBID).optional(), ValueType.ENUM.postfix + (i+1));
		}
		
		registerSource(type = typeType.newItemField(FORBID).optional(), "Type");
	}
	
	public DynamicModel<L>.Type createType(final String code)
	{
		return new Type(typeType.newItem(typeCode.map(code)));
	}
	
	public List<Type> getTypes()
	{
		final List<? extends Item> backingItems = typeType.search();
		final ArrayList<Type> result = new ArrayList<Type>(backingItems.size());
		for(final Item backingItem : backingItems)
			result.add(new Type(backingItem));
		return Collections.unmodifiableList(result);
	}
	
	public Type getType(final String code)
	{
		return toType(typeType.searchSingleton(typeCode.equal(code)));
	}
	
	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());
		
		result.add(
			new Wrapper("getType").
			addComment("Returns the dynamic type of this item in the model {0}.").
			setReturn(Wrapper.DynamicModelType.class));
		
		result.add(
			new Wrapper("setType").
			addComment("Sets the dynamic type of this item in the model {0}.").
			addParameter(Wrapper.DynamicModelType.class, "type"));
			
		result.add(
			new Wrapper("get").
			addComment("Returns the value of <tt>field</tt> for this item in the model {0}.").
			setReturn(Object.class).
			addParameter(Wrapper.DynamicModelField.class, "field"));
			
		result.add(
			new Wrapper("set").
			addComment("Sets the value of <tt>field</tt> for this item in the model {0}.").
			addParameter(Wrapper.DynamicModelField.class, "field").
			addParameter(Object.class, "value"));
		
		return Collections.unmodifiableList(result);
	}
	
	public Type getType(final Item item)
	{
		return toType(this.type.get(item));
	}
	
	public com.exedio.cope.Type<?> getTypeType()
	{
		return typeType;
	}
	
	public com.exedio.cope.Type getFieldType()
	{
		return fieldType;
	}
	
	public com.exedio.cope.Type getEnumType()
	{
		return enumType;
	}
	
	public com.exedio.cope.Type getTypeLocalizationType()
	{
		return typeLocalization.type;
	}
	
	public com.exedio.cope.Type getFieldLocalizationType()
	{
		return fieldLocalization.type;
	}
	
	public com.exedio.cope.Type getEnumLocalizationType()
	{
		return enumLocalization.type;
	}
	
	public ItemField<?> getTypeField()
	{
		return type;
	}
	
	public void setTypeBacked(final Item item, final Item typeBackingItem)
	{
		setType(item, toType(typeBackingItem));
	}
	
	public void setType(final Item item, final Type type)
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
	
	private void assertType(final Item item, final Field field)
	{
		final Item fieldType = fieldParent.get(field.getBackingItem());
		final Item itemType = type.get(item);
		if(!fieldType.equals(itemType))
			throw new IllegalArgumentException(
					"dynamic type mismatch: field has type " + typeCode.get(fieldType) +
					", but item has " + (itemType!=null ? typeCode.get(itemType) : "none"));
	}
	
	FunctionField<?> getField(final Field field)
	{
		final ValueType valueType = field.getValueType();
		final int pos = field.getPositionPerValueType();
		return getField(valueType, pos, field);
	}

	FunctionField<?> getField(final ValueType valueType, final int pos, final Field field)
	{
		final FunctionField[] array = array(valueType);
		
		// make a more verbose exception instead
		// of the ArrayIndexOutOfBoundException
		// thrown by the last line.
		final int capacity = array.length;
		if(capacity<=pos)
			throw new RuntimeException("accessing " + field + " exceeded capacity for " + valueType + ", " + capacity + " available, but tried to access " + (pos+1));

		return array[pos];
	}
	
	public Object get(final Item item, final Field field)
	{
		assertType(item, field);
		final Object backingValue = getField(field).get(item);
		if(backingValue!=null && backingValue instanceof Item)
			return new Enum((Item)backingValue);
		else
			return backingValue;
	}
	
	public void set(final Item item, final DynamicModel<L>.Field field, final Object value)
	{
		assertType(item, field);
		
		final Object backingValue;
		if(value!=null &&
			value instanceof DynamicModel.Enum &&
			field.getValueType()==ValueType.ENUM)
		{
			final Enum enumValue = (Enum)value;
			final Field enumValueParent = enumValue.getParent();
			if(!enumValueParent.equals(field))
				throw new IllegalArgumentException("dynamic model mismatch: enum value " + enumValue + " has type " + enumValueParent + ", but must be " + field);
			backingValue = enumValue.getBackingItem();
		}
		else
			backingValue = value;
		
		Cope.setAndCast(getField(field), item, backingValue);
	}
	
	private Type toType(final Item backingItem)
	{
		return backingItem!=null ? new Type(backingItem) : null;
	}

	public static enum ValueType
	{
		STRING (String.class,            "String"),
		BOOLEAN(Boolean.class,           "Bool"),
		INTEGER(Integer.class,           "Int"),
		DOUBLE (Double.class,            "Double"),
		ENUM   (DynamicModel.Enum.class, "Enum");
		
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

	public final class Type extends BackedItem
	{
		Type(final Item backingItem)
		{
			super(backingItem);
			assert backingItem.getCopeType()==typeType;
		}
		
		public Field addField(final String code, final ValueType valueType)
		{
			final List<Field> fields = getFields(); // TODO make more efficient
			final int position = fields.isEmpty() ? 0 : (fields.get(fields.size()-1).getPosition()+1);
			final List<Field> fieldsPerValuetype = getFields(valueType); // TODO make more efficient
			final int positionPerValuetype = fieldsPerValuetype.isEmpty() ? 0 : (fieldsPerValuetype.get(fieldsPerValuetype.size()-1).getPositionPerValueType()+1);
			DynamicModel.this.assertCapacity(valueType, positionPerValuetype);
			return new Field(
					fieldType.newItem(
							Cope.mapAndCast(fieldParent, backingItem),
							fieldPosition.map(position),
							fieldCode.map(code),
							fieldValueType.map(valueType),
							fieldPositionPerValueType.map(positionPerValuetype)));
		}

		public Field addStringField(final String code)
		{
			return addField(code, ValueType.STRING);
		}

		public Field addBooleanField(final String code)
		{
			return addField(code, ValueType.BOOLEAN);
		}
		
		public Field addIntegerField(final String code)
		{
			return addField(code, ValueType.INTEGER);
		}
		
		public Field addDoubleField(final String code)
		{
			return addField(code, ValueType.DOUBLE);
		}
		
		public Field addEnumField(final String code)
		{
			return addField(code, ValueType.ENUM);
		}

		public List<Field> getFields()
		{
			final List<? extends Item> backingItems = fieldType.search(Cope.equalAndCast(fieldParent, backingItem), fieldPosition, true);
			final ArrayList<Field> result = new ArrayList<Field>(backingItems.size());
			for(final Item backingItem : backingItems)
				result.add(new Field(backingItem));
			return Collections.unmodifiableList(result);
		}

		public Field getField(final String code)
		{
			return toField(fieldType.searchSingleton(Cope.equalAndCast(fieldParent, backingItem).and(fieldCode.equal(code))));
		}
		
		private List<Field> getFields(final ValueType valueType)
		{
			final List<? extends Item> backingItems = fieldType.search(Cope.equalAndCast(fieldParent, backingItem).and(fieldValueType.equal(valueType)), fieldPositionPerValueType, true);
			final ArrayList<Field> result = new ArrayList<Field>(backingItems.size());
			for(final Item backingItem : backingItems)
				result.add(new Field(backingItem));
			return Collections.unmodifiableList(result);
		}
		
		public com.exedio.cope.Type getParentType()
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
		
		private Field toField(final Item backingItem)
		{
			return backingItem!=null ? new Field(backingItem) : null;
		}
		
		public String getName(final L locale)
		{
			return typeLocalization.getName(backingItem, locale);
		}
		
		public void setName(final L locale, final String value)
		{
			typeLocalization.setName(backingItem, locale, value);
		}
		
		/**
		 * @deprecated Use {@link #addField(String,com.exedio.cope.pattern.DynamicModel.ValueType)} instead
		 */
		@Deprecated
		public Field addAttribute(final String code, final ValueType valueType)
		{
			return addField(code, valueType);
		}
		
		/**
		 * @deprecated Use {@link #addStringField(String)} instead
		 */
		@Deprecated
		public Field addStringAttribute(final String code)
		{
			return addStringField(code);
		}
		
		/**
		 * @deprecated Use {@link #addBooleanField(String)} instead
		 */
		@Deprecated
		public Field addBooleanAttribute(final String code)
		{
			return addBooleanField(code);
		}
		
		/**
		 * @deprecated Use {@link #addIntegerField(String)} instead
		 */
		@Deprecated
		public Field addIntegerAttribute(final String code)
		{
			return addIntegerField(code);
		}

		/**
		 * @deprecated Use {@link #addDoubleField(String)} instead
		 */
		@Deprecated
		public Field addDoubleAttribute(final String code)
		{
			return addDoubleField(code);
		}

		/**
		 * @deprecated Use {@link #addEnumField(String)} instead
		 */
		@Deprecated
		public Field addEnumAttribute(final String code)
		{
			return addEnumField(code);
		}
		
		/**
		 * @deprecated Use {@link #getFields()} instead
		 */
		@Deprecated
		public List<Field> getAttributes()
		{
			return getFields();
		}
		
		/**
		 * @deprecated Use {@link #getField(String)} instead
		 */
		@Deprecated
		public Field getAttribute(final String code)
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
	
	public final class Field extends BackedItem
	{
		Field(final Item backingItem)
		{
			super(backingItem);
			assert backingItem.getCopeType()==fieldType;
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
		
		public List<Enum> getEnumValues()
		{
			assertEnum();
			final List<? extends Item> backingItems = enumType.search(Cope.equalAndCast(enumParent, backingItem), enumPosition, true);
			final ArrayList<Enum> result = new ArrayList<Enum>(backingItems.size());
			for(final Item backingItem : backingItems)
				result.add(new Enum(backingItem));
			return Collections.unmodifiableList(result);
		}
		
		public Enum getEnumValue(final String code)
		{
			assertEnum();
			return toEnum(enumType.searchSingleton(Cope.equalAndCast(enumParent, backingItem).and(enumCode.equal(code))));
		}
		
		public Enum addEnumValue(final String code)
		{
			assertEnum();
			final List<Enum> values = getEnumValues(); // TODO make more efficient
			final int position = values.isEmpty() ? 0 : (values.get(values.size()-1).getPosition()+1);
			return new Enum(
					enumType.newItem(
							Cope.mapAndCast(enumParent, backingItem),
							enumPosition.map(position),
							enumCode.map(code)));
		}
		
		public Type getParent()
		{
			return new Type(fieldParent.get(backingItem));
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
		
		private Enum toEnum(final Item backingItem)
		{
			return backingItem!=null ? new Enum(backingItem) : null;
		}
		
		public String getName(final L locale)
		{
			return fieldLocalization.getName(backingItem, locale);
		}
		
		public void setName(final L locale, final String value)
		{
			fieldLocalization.setName(backingItem, locale, value);
		}
	}

	public final class Enum extends BackedItem
	{
		Enum(final Item backingItem)
		{
			super(backingItem);
			assert backingItem.getCopeType()==enumType;
		}
		
		public Field getParent()
		{
			return new Field(enumParent.get(backingItem));
		}
		
		public int getPosition()
		{
			return enumPosition.getMandatory(backingItem);
		}
		
		public String getCode()
		{
			return enumCode.get(backingItem);
		}
		
		public String getName(final L locale)
		{
			return enumLocalization.getName(backingItem, locale);
		}
		
		public void setName(final L locale, final String value)
		{
			enumLocalization.setName(backingItem, locale, value);
		}
	}
	
	// just for making newType accessible
	com.exedio.cope.Type newLocalizationType(final LinkedHashMap<String, com.exedio.cope.Feature> features, final String postfix)
	{
		return newType(PatternItem.class, features, postfix);
	}
	
	class Localization
	{
		final ItemField<?> parent;
		final FunctionField<L> locale;
		final StringField value;
		final UniqueConstraint uniqueConstraint;
		final com.exedio.cope.Type<?> type;
		
		Localization(final com.exedio.cope.Type<?> type, final FunctionField<L> localeTemplate, final String id)
		{
			assert type!=null;
			
			this.parent = type.newItemField(CASCADE).toFinal();
			this.locale = localeTemplate.copy();
			this.uniqueConstraint = new UniqueConstraint(parent, locale);
			this.value = new StringField();
			final LinkedHashMap<String, com.exedio.cope.Feature> features = new LinkedHashMap<String, com.exedio.cope.Feature>();
			features.put("parent", parent);
			features.put("locale", locale);
			features.put("uniqueConstraint", uniqueConstraint);
			features.put("value", value);
			this.type = newLocalizationType(features, id + "Loc");
		}
		
		public final String getName(final Item item, final L locale)
		{
			final Item relationItem = uniqueConstraint.searchUnique(item, locale);
			if(relationItem!=null)
				return value.get(relationItem);
			else
				return null;
		}
		
		public final void setName(final Item item, final L locale, final String value)
		{
			final Item relationItem = uniqueConstraint.searchUnique(item, locale);
			if(relationItem==null)
			{
				if(value!=null)
					uniqueConstraint.getType().newItem(
							Cope.mapAndCast(this.parent, item),
							this.locale.map(locale),
							this.value.map(value)
					);
			}
			else
			{
				if(value!=null)
					this.value.set(relationItem, value);
				else
					relationItem.deleteCopeItem();
			}
		}
	}
}
