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

package com.exedio.cope.pattern;

import static com.exedio.cope.ItemField.DeletePolicy.CASCADE;
import static com.exedio.cope.ItemField.DeletePolicy.FORBID;
import static java.lang.Math.max;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.BooleanField;
import com.exedio.cope.Cope;
import com.exedio.cope.DoubleField;
import com.exedio.cope.EnumField;
import com.exedio.cope.Features;
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
	com.exedio.cope.Type<Type<L>> typeType = null;
	final MapField<L, String> typeLocalization;
	
	ItemField<Type<L>> fieldParent = null;
	final IntegerField fieldPosition = new IntegerField().toFinal();
	final EnumField<ValueType> fieldValueType = Item.newEnumField(ValueType.class).toFinal();
	final IntegerField fieldPositionPerValueType;
	final StringField fieldCode = new StringField().toFinal();
	com.exedio.cope.Type<Field<L>> fieldType = null;
	final MapField<L, String> fieldLocalization;
	
	ItemField<Field<L>> enumParent = null;
	final IntegerField enumPosition = new IntegerField().toFinal();
	final StringField enumCode = new StringField().toFinal();
	com.exedio.cope.Type<Enum<L>> enumType = null;
	final MapField<L, String> enumLocalization;
	
	private ItemField<Type<L>> type = null;
	private final FunctionField<?>[] fields;

	private final StringField [] strings;
	private final BooleanField[] booleans;
	private final IntegerField[] integers;
	private final DoubleField [] doubles;
	private final ItemField<?>[] enums;
	
	private DynamicModel(
			final FunctionField<L> locale,
			final int stringCapacity,
			final int booleanCapacity,
			final int integerCapacity,
			final int doubleCapacity,
			final int enumCapacity)
	{
		this.localeTemplate = locale;
		
		this.typeLocalization  = newLocalization();
		this.fieldLocalization = newLocalization();
		this.enumLocalization  = newLocalization();
		
		fieldPositionPerValueType = new IntegerField().toFinal().range(0,
				max(max(max(max(stringCapacity, booleanCapacity), integerCapacity), doubleCapacity), enumCapacity));
		strings  = new StringField [stringCapacity];
		booleans = new BooleanField[booleanCapacity];
		integers = new IntegerField[integerCapacity];
		doubles  = new DoubleField [doubleCapacity];
		enums    = new ItemField   [enumCapacity];
		fields   = new FunctionField[strings.length + booleans.length + integers.length + doubles.length + enums.length];

		int n = 0;
		for(int i = 0; i<strings.length; i++)
			addSource(fields[n++] = strings [i] = new StringField().optional(),  "String"+i);
		for(int i = 0; i<booleans.length; i++)
			addSource(fields[n++] = booleans[i] = new BooleanField().optional(), "Bool"  +i);
		for(int i = 0; i<integers.length; i++)
			addSource(fields[n++] = integers[i] = new IntegerField().optional(), "Int"   +i);
		for(int i = 0; i<doubles.length; i++)
			addSource(fields[n++] = doubles [i] = new DoubleField().optional(),  "Double"+i);
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
	protected void onMount()
	{
		final Features features = new Features();
		features.put("code", typeCode);
		features.put("name", typeLocalization);
		typeType = castType(newSourceType(Type.class, features, "Type"));
		
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
		features.put("name", fieldLocalization);
		fieldType = castField(newSourceType(Field.class, features, "Field"));
		
		if(enums.length>0)
		{
			features.clear();
			enumParent = fieldType.newItemField(CASCADE).toFinal();
			features.put("parent", enumParent);
			features.put("position", enumPosition);
			features.put("uniquePosition", new UniqueConstraint(enumParent, enumPosition));
			features.put("code", enumCode);
			features.put("uniqueCode", new UniqueConstraint(enumParent, enumCode));
			features.put("name", enumLocalization);
			enumType = castEnum(newSourceType(Enum.class, features, "Enum"));
			
			final int enumOffset = strings.length + booleans.length + integers.length + doubles.length;
			for(int i = 0; i<enums.length; i++)
				addSource(fields[i+enumOffset] = enums[i] = enumType.newItemField(FORBID).optional(), "Enum"+i);
		}
		
		addSource(type = typeType.newItemField(FORBID).optional(), "Type");
	}
	
	private MapField<L, String> newLocalization()
	{
		return MapField.newMap(localeTemplate.copy(), new StringField());
	}
	
	@SuppressWarnings("unchecked")
	private com.exedio.cope.Type<Type<L>> castType(final com.exedio.cope.Type t)
	{
		return t;
	}
	
	@SuppressWarnings("unchecked")
	private com.exedio.cope.Type<Field<L>> castField(final com.exedio.cope.Type t)
	{
		return t;
	}
	
	@SuppressWarnings("unchecked")
	private com.exedio.cope.Type<Enum<L>> castEnum(final com.exedio.cope.Type t)
	{
		return t;
	}
	
	public DynamicModel.Type<L> createType(final String code)
	{
		return typeType.newItem(typeCode.map(code));
	}
	
	public List<Type<L>> getTypes()
	{
		return typeType.search();
	}
	
	public Type getType(final String code)
	{
		return typeType.searchSingleton(typeCode.equal(code));
	}
	
	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());
		
		result.add(
			new Wrapper("getType").
			addComment("Returns the dynamic type of this item in the model {0}.").
			setReturn(Wrapper.generic(Type.class, Wrapper.TypeVariable0.class)));
		
		result.add(
			new Wrapper("setType").
			addComment("Sets the dynamic type of this item in the model {0}.").
			addParameter(Wrapper.generic(Type.class, Wrapper.TypeVariable0.class), "type"));
			
		result.add(
			new Wrapper("get").
			addComment("Returns the value of <tt>field</tt> for this item in the model {0}.").
			setReturn(Object.class).
			addParameter(Wrapper.generic(Field.class, Wrapper.TypeVariable0.class), "field"));
			
		result.add(
			new Wrapper("set").
			addComment("Sets the value of <tt>field</tt> for this item in the model {0}.").
			addParameter(Wrapper.generic(Field.class, Wrapper.TypeVariable0.class), "field").
			addParameter(Object.class, "value"));
		
		return Collections.unmodifiableList(result);
	}
	
	public Type<L> getType(final Item item)
	{
		return this.type.get(item);
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
		return typeLocalization.getRelationType();
	}
	
	public com.exedio.cope.Type getFieldLocalizationType()
	{
		return fieldLocalization.getRelationType();
	}
	
	public com.exedio.cope.Type getEnumLocalizationType()
	{
		return enumLocalization.getRelationType();
	}
	
	public ItemField<?> getTypeField()
	{
		return type;
	}
	
	public void setType(final Item item, final Type<L> type)
	{
		if(type!=null && !this.equals(type.getModel()))
			throw new IllegalArgumentException(
					"dynamic model mismatch: new type has model " + type.getModel() +
					", but must be " + toString());
		
		final SetValue[] values = new SetValue[1+fields.length];
		values[0] = this.type.map(type);
		for(int i = 0; i<fields.length; i++)
			values[1+i] = fields[i].map(null);
		item.set(values);
	}
	
	private void assertType(final Item item, final Field field)
	{
		final Item fieldType = fieldParent.get(field);
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
		return getField(field).get(item);
	}
	
	public void set(final Item item, final DynamicModel.Field<L> field, final Object value)
	{
		assertType(item, field);
		
		if(value!=null &&
			value instanceof DynamicModel.Enum &&
			field.getValueType()==ValueType.ENUM)
		{
			final Enum enumValue = (Enum)value;
			final Field enumValueParent = enumValue.getParent();
			if(!enumValueParent.equals(field))
				throw new IllegalArgumentException("dynamic model mismatch: enum value " + enumValue + " has type " + enumValueParent + ", but must be " + field);
		}
		
		Cope.setAndCast(getField(field), item, value);
	}
	
	public static enum ValueType
	{
		STRING (String.class),
		BOOLEAN(Boolean.class),
		INTEGER(Integer.class),
		DOUBLE (Double.class),
		ENUM   (DynamicModel.Enum.class);
		
		final Class valueClass;
		
		ValueType(final Class valueClass)
		{
			this.valueClass = valueClass;
		}
		
		public final Class getValueClass()
		{
			return valueClass;
		}
	}

	public static final class Type<L> extends Item
	{
		private static final long serialVersionUID = 1l;
		
		Type(final ActivationParameters ap)
		{
			super(ap);
		}
		
		@SuppressWarnings("unchecked")
		public DynamicModel<L> getPattern()
		{
			return (DynamicModel<L>)getCopeType().getPattern();
		}
		
		public Field<L> addField(final String code, final ValueType valueType)
		{
			final DynamicModel<L> p = getPattern();
			final List<Field<L>> fields = getFields(); // TODO make more efficient
			final int position = fields.isEmpty() ? 0 : (fields.get(fields.size()-1).getPosition()+1);
			final List<Field<L>> fieldsPerValuetype = getFields(p, valueType); // TODO make more efficient
			final int positionPerValuetype = fieldsPerValuetype.isEmpty() ? 0 : (fieldsPerValuetype.get(fieldsPerValuetype.size()-1).getPositionPerValueType()+1);
			p.assertCapacity(valueType, positionPerValuetype);
			return
					p.fieldType.newItem(
							p.fieldParent.map(this),
							p.fieldPosition.map(position),
							p.fieldCode.map(code),
							p.fieldValueType.map(valueType),
							p.fieldPositionPerValueType.map(positionPerValuetype));
		}
		
		private List<Field<L>> getFields(final DynamicModel<L> p, final ValueType valueType)
		{
			return p.fieldType.search(p.fieldParent.equal(this).and(p.fieldValueType.equal(valueType)), p.fieldPositionPerValueType, true);
		}

		public Field<L> addStringField(final String code)
		{
			return addField(code, ValueType.STRING);
		}

		public Field<L> addBooleanField(final String code)
		{
			return addField(code, ValueType.BOOLEAN);
		}
		
		public Field<L> addIntegerField(final String code)
		{
			return addField(code, ValueType.INTEGER);
		}
		
		public Field<L> addDoubleField(final String code)
		{
			return addField(code, ValueType.DOUBLE);
		}
		
		public Field<L> addEnumField(final String code)
		{
			return addField(code, ValueType.ENUM);
		}

		public List<Field<L>> getFields()
		{
			final DynamicModel<L> p = getPattern();
			return p.fieldType.search(p.fieldParent.equal(this), p.fieldPosition, true);
		}

		public Field<L> getField(final String code)
		{
			final DynamicModel<L> p = getPattern();
			return p.fieldType.searchSingleton(p.fieldParent.equal(this).and(p.fieldCode.equal(code)));
		}
		
		public com.exedio.cope.Type getParentType()
		{
			return getPattern().getType();
		}
		
		public DynamicModel getModel()
		{
			return getPattern();
		}
		
		public String getCode()
		{
			return getPattern().typeCode.get(this);
		}
		
		
		public String getName(final L locale)
		{
			return getPattern().typeLocalization.get(this, locale);
		}
		
		public void setName(final L locale, final String value)
		{
			getPattern().typeLocalization.set(this, locale, value);
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
		public List<Field<L>> getAttributes()
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
	
	public static final class Field<L> extends Item
	{
		private static final long serialVersionUID = 1l;
		
		Field(final ActivationParameters ap)
		{
			super(ap);
		}
		
		@SuppressWarnings("unchecked")
		public DynamicModel<L> getPattern()
		{
			return (DynamicModel)getCopeType().getPattern();
		}
		
		public Object get(final Item item)
		{
			return getPattern().get(item, this);
		}
		
		public void set(final Item item, final Object value)
		{
			getPattern().set(item, this, value);
		}
		
		private void assertEnum()
		{
			final ValueType vt = getValueType();
			if(vt!=ValueType.ENUM)
				throw new IllegalArgumentException("operation allowed for getValueType()==ENUM fields only, but was " + vt);
		}
		
		public List<Enum<L>> getEnumValues()
		{
			assertEnum();
			final DynamicModel<L> p = getPattern();
			return p.enumType.search(p.enumParent.equal(this), p.enumPosition, true);
		}
		
		public Enum getEnumValue(final String code)
		{
			final DynamicModel<L> p = getPattern();
			assertEnum();
			return p.enumType.searchSingleton(p.enumParent.equal(this).and(p.enumCode.equal(code)));
		}
		
		public Enum<L> addEnumValue(final String code)
		{
			final DynamicModel<L> p = getPattern();
			assertEnum();
			final List<Enum<L>> values = getEnumValues(); // TODO make more efficient
			final int position = values.isEmpty() ? 0 : (values.get(values.size()-1).getPosition()+1);
			return
					p.enumType.newItem(
							p.enumParent.map(this),
							p.enumPosition.map(position),
							p.enumCode.map(code));
		}
		
		public Type getParent()
		{
			return getPattern().fieldParent.get(this);
		}
		
		public int getPosition()
		{
			return getPattern().fieldPosition.getMandatory(this);
		}
		
		public ValueType getValueType()
		{
			return getPattern().fieldValueType.get(this);
		}
		
		int getPositionPerValueType()
		{
			return getPattern().fieldPositionPerValueType.getMandatory(this);
		}
		
		public String getCode()
		{
			return getPattern().fieldCode.get(this);
		}
		
		public FunctionField<?> getField()
		{
			return getPattern().getField(this);
		}
		
		
		public String getName(final L locale)
		{
			return getPattern().fieldLocalization.get(this, locale);
		}
		
		public void setName(final L locale, final String value)
		{
			getPattern().fieldLocalization.set(this, locale, value);
		}
	}

	public static final class Enum<L> extends Item
	{
		private static final long serialVersionUID = 1l;
		
		Enum(final ActivationParameters ap)
		{
			super(ap);
		}
		
		@SuppressWarnings("unchecked")
		public DynamicModel<L> getPattern()
		{
			return (DynamicModel)getCopeType().getPattern();
		}
		
		public Field getParent()
		{
			return getPattern().enumParent.get(this);
		}
		
		public int getPosition()
		{
			return getPattern().enumPosition.getMandatory(this);
		}
		
		public String getCode()
		{
			return getPattern().enumCode.get(this);
		}
		
		public String getName(final L locale)
		{
			return getPattern().enumLocalization.get(this, locale);
		}
		
		public void setName(final L locale, final String value)
		{
			getPattern().enumLocalization.set(this, locale, value);
		}
	}
}
