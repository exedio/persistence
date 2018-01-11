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

package com.exedio.cope.pattern;

import static com.exedio.cope.ItemField.DeletePolicy.CASCADE;
import static com.exedio.cope.ItemField.DeletePolicy.FORBID;
import static java.lang.Math.max;

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
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@WrapFeature
public final class DynamicModel<L> extends Pattern
{
	private static final long serialVersionUID = 1l;

	private final FunctionField<L> localeTemplate;
	final StringField typeCode = new StringField().toFinal().unique();
	final MapField<L, String> typeLocalization;

	final IntegerField fieldPosition = new IntegerField().toFinal();
	final EnumField<ValueType> fieldValueType = EnumField.create(ValueType.class).toFinal();
	final IntegerField fieldPositionPerValueType;
	final StringField fieldCode = new StringField().toFinal();
	final MapField<L, String> fieldLocalization;

	final IntegerField enumPosition = new IntegerField().toFinal();
	final StringField enumCode = new StringField().toFinal();
	final MapField<L, String> enumLocalization;

	private final FunctionField<?>[] fields;

	private final StringField [] strings;
	private final BooleanField[] booleans;
	private final IntegerField[] integers;
	private final DoubleField [] doubles;
	private final ItemField<Enum<L>>[] enums;

	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private Mount<L> mountIfMounted = null;

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
		enums    = newItemField    (enumCapacity);
		fields   = new FunctionField<?>[strings.length + booleans.length + integers.length + doubles.length + enums.length];

		int n = 0;
		for(int i = 0; i<strings.length; i++)
			fields[n++] = strings [i] = addSourceFeature(new StringField().optional(),  "string"+i);
		for(int i = 0; i<booleans.length; i++)
			fields[n++] = booleans[i] = addSourceFeature(new BooleanField().optional(), "bool"  +i);
		for(int i = 0; i<integers.length; i++)
			fields[n++] = integers[i] = addSourceFeature(new IntegerField().optional(), "int"   +i);
		for(int i = 0; i<doubles.length; i++)
			fields[n++] = doubles [i] = addSourceFeature(new DoubleField().optional(),  "double"+i);
	}

	private MapField<L, String> newLocalization()
	{
		return MapField.create(localeTemplate.copy(), new StringField());
	}

	@SuppressWarnings({"unchecked", "rawtypes", "static-method"}) // OK: no generic arrays
	private ItemField<Enum<L>>[] newItemField(final int length)
	{
		return new ItemField[length];
	}

	public static <L> DynamicModel<L> create(
			final FunctionField<L> locale,
			final int stringCapacity,
			final int booleanCapacity,
			final int integerCapacity,
			final int doubleCapacity,
			final int enumCapacity)
	{
		return new DynamicModel<>(locale,
				stringCapacity, booleanCapacity, integerCapacity, doubleCapacity, enumCapacity);
	}

	public static <L> DynamicModel<L> create(final FunctionField<L> locale)
	{
		return new DynamicModel<>(locale, 5, 5, 5, 5, 5);
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
					"Capacity for " + valueType + " exceeded, " +
					capacity + " available, " +
					"but tried to allocate " + (positionPerValuetype+1) + ". " +
					"Use DynamicModel#newModel(" +
						"FunctionField<L> locale, " +
						"int stringCapacity, " +
						"int booleanCapacity, " +
						"int integerCapacity, " +
						"int doubleCapacity, " +
						"int enumCapacity) " +
					"to increase capacities.");
	}

	@Override
	protected void onMount()
	{
		super.onMount();

		final Features features = new Features();
		features.put("code", typeCode);
		features.put("name", typeLocalization);
		final com.exedio.cope.Type<Type<L>> typeType = castType(newSourceType(Type.class, features, "Type"));

		features.clear();
		final ItemField<Type<L>> fieldParent = typeType.newItemField(CASCADE).toFinal();
		features.put("parent", fieldParent);
		features.put("position", fieldPosition);
		features.put("uniqueConstraint", UniqueConstraint.create(fieldParent, fieldPosition));
		features.put("valueType", fieldValueType);
		features.put("positionPerValueType", fieldPositionPerValueType);
		features.put("uniqueConstraintPerValueType", UniqueConstraint.create(fieldParent, fieldValueType, fieldPositionPerValueType));
		features.put("code", fieldCode);
		features.put("uniqueConstraintCode", UniqueConstraint.create(fieldParent, fieldCode));
		features.put("name", fieldLocalization);
		final com.exedio.cope.Type<Field<L>> fieldType = castField(newSourceType(Field.class, features, "Field"));

		ItemField<Field<L>> enumParent = null;
		com.exedio.cope.Type<Enum<L>> enumType = null;
		if(enums.length>0)
		{
			features.clear();
			enumParent = fieldType.newItemField(CASCADE).toFinal();
			features.put("parent", enumParent);
			features.put("position", enumPosition);
			features.put("uniquePosition", UniqueConstraint.create(enumParent, enumPosition));
			features.put("code", enumCode);
			features.put("uniqueCode", UniqueConstraint.create(enumParent, enumCode));
			features.put("name", enumLocalization);
			enumType = castEnum(newSourceType(Enum.class, features, "Enum"));

			final int enumOffset = strings.length + booleans.length + integers.length + doubles.length;
			for(int i = 0; i<enums.length; i++)
				fields[i+enumOffset] = enums[i] =
						addSourceFeature(enumType.newItemField(FORBID).optional(), "enum"+i);
		}

		final ItemField<Type<L>> type =
				addSourceFeature(typeType.newItemField(FORBID).optional(), "type");

		this.mountIfMounted = new Mount<>(typeType, fieldParent, fieldType, enumParent, enumType, type);
	}

	private static final class Mount<L>
	{
		final com.exedio.cope.Type<Type<L>> typeType;
		final ItemField<Type<L>> fieldParent;
		final com.exedio.cope.Type<Field<L>> fieldType;
		final ItemField<Field<L>> enumParent;
		final com.exedio.cope.Type<Enum<L>> enumType;
		final ItemField<Type<L>> type;

		Mount(final com.exedio.cope.Type<Type<L>> typeType,
				final ItemField<Type<L>> fieldParent,
				final com.exedio.cope.Type<Field<L>> fieldType,
				final ItemField<Field<L>> enumParent,
				final com.exedio.cope.Type<Enum<L>> enumType,
				final ItemField<Type<L>> type)
		{
			this.typeType =typeType;
			this.fieldParent = fieldParent;
			this.fieldType = fieldType;
			this.enumParent = enumParent;
			this.enumType = enumType;
			this.type = type;
		}
	}

	Mount<L> mount()
	{
		return requireMounted(mountIfMounted);
	}

	@SuppressWarnings({"unchecked", "rawtypes", "static-method"})
	private com.exedio.cope.Type<Type<L>> castType(final com.exedio.cope.Type t)
	{
		return t;
	}

	@SuppressWarnings({"unchecked", "rawtypes", "static-method"})
	private com.exedio.cope.Type<Field<L>> castField(final com.exedio.cope.Type t)
	{
		return t;
	}

	@SuppressWarnings({"unchecked", "rawtypes", "static-method"})
	private com.exedio.cope.Type<Enum<L>> castEnum(final com.exedio.cope.Type t)
	{
		return t;
	}

	public Type<L> createType(final String code)
	{
		return mount().typeType.newItem(typeCode.map(code));
	}

	public List<Type<L>> getTypes()
	{
		return mount().typeType.search();
	}

	public Type<L> getType(final String code)
	{
		return mount().typeType.searchSingleton(typeCode.equal(code));
	}

	@Wrap(order=10, doc="Returns the dynamic type of this item in the model {0}.")
	@Nullable
	public Type<L> getType(@Nonnull final Item item)
	{
		return mount().type.get(item);
	}

	public com.exedio.cope.Type<Type<L>> getTypeType()
	{
		return mount().typeType;
	}

	public com.exedio.cope.Type<Field<L>> getFieldType()
	{
		return mount().fieldType;
	}

	public com.exedio.cope.Type<Enum<L>> getEnumType()
	{
		return mount().enumType;
	}

	public com.exedio.cope.Type<?> getTypeLocalizationType()
	{
		return typeLocalization.getRelationType();
	}

	public com.exedio.cope.Type<?> getFieldLocalizationType()
	{
		return fieldLocalization.getRelationType();
	}

	public com.exedio.cope.Type<?> getEnumLocalizationType()
	{
		return enumLocalization.getRelationType();
	}

	public ItemField<Type<L>> getTypeField()
	{
		return mount().type;
	}

	@Wrap(order=20, doc="Sets the dynamic type of this item in the model {0}.")
	public void setType(
			@Nonnull final Item item,
			@Nullable @Parameter("type") final Type<L> type)
	{
		if(type!=null && !this.equals(type.getModel()))
			throw new IllegalArgumentException(
					"dynamic model mismatch: new type has model " + type.getModel() +
					", but must be " + this);

		final SetValue<?>[] values = new SetValue<?>[1+fields.length];
		values[0] = mount().type.map(type);
		for(int i = 0; i<fields.length; i++)
			values[1+i] = fields[i].map(null);
		item.set(values);
	}

	private void assertType(final Item item, final Field<L> field)
	{
		final Mount<L> mount = mount();
		final Item fieldType = mount.fieldParent.get(field);
		final Item itemType = mount.type.get(item);
		if(!fieldType.equals(itemType))
			throw new IllegalArgumentException(
					"dynamic type mismatch: field has type " + typeCode.get(fieldType) +
					", but item has " + (itemType!=null ? typeCode.get(itemType) : "none"));
	}

	FunctionField<?> getField(final Field<L> field)
	{
		final ValueType valueType = field.getValueType();
		final int pos = field.getPositionPerValueType();
		return getField(valueType, pos, field);
	}

	FunctionField<?> getField(final ValueType valueType, final int pos, final Field<?> field)
	{
		final FunctionField<?>[] array = array(valueType);

		// make a more verbose exception instead
		// of the ArrayIndexOutOfBoundException
		// thrown by the last line.
		final int capacity = array.length;
		if(capacity<=pos)
			throw new RuntimeException("accessing " + field + " exceeded capacity for " + valueType + ", " + capacity + " available, but tried to access " + (pos+1));

		return array[pos];
	}

	@SuppressWarnings("static-method")
	private void assertValueType(final Field<L> field, final ValueType valueType)
	{
		final ValueType fieldValueType = field.getValueType();
		if(valueType!=fieldValueType)
			throw new IllegalArgumentException("operation allowed for getValueType()==" + valueType + " fields only, but was " + fieldValueType);
	}

	ItemField<Enum<L>> getFieldEnum(final Field<L> field)
	{
		assertValueType(field, ValueType.ENUM);

		final int pos = field.getPositionPerValueType();

		// make a more verbose exception instead
		// of the ArrayIndexOutOfBoundException
		// thrown by the last line.
		final int capacity = enums.length;
		if(capacity<=pos)
			throw new RuntimeException("accessing " + field + " exceeded capacity, " + capacity + " available, but tried to access " + (pos+1));

		return enums[pos];
	}

	@Wrap(order=30, doc="Returns the value of <tt>field</tt> for this item in the model {0}.")
	@Nullable
	public Object get(
			@Nonnull final Item item,
			@Nonnull @Parameter("field") final Field<L> field)
	{
		assertType(item, field);
		return getField(field).get(item);
	}

	@Wrap(order=40, doc="Sets the value of <tt>field</tt> for this item in the model {0}.")
	public void set(
			@Nonnull final Item item,
			@Nonnull @Parameter("field") final Field<L> field,
			@Nullable @Parameter("value") final Object value)
	{
		assertType(item, field);

		if(value instanceof Enum<?> &&
		   field.getValueType()==ValueType.ENUM)
		{
			final Enum<?> enumValue = (Enum<?>)value;
			final Field<?> enumValueParent = enumValue.getParent();
			if(!enumValueParent.equals(field))
				throw new IllegalArgumentException("dynamic model mismatch: enum value " + enumValue + " has type " + enumValueParent + ", but must be " + field);
		}

		Cope.setAndCast(getField(field), item, value);
	}

	void checkMatchingLocalization(final DynamicModel<?> pattern)
	{
		if(localeTemplate.getValueClass()!=pattern.localeTemplate.getValueClass())
			throw new ClassCastException(
					"expected a " + localeTemplate.getValueClass().getName() +
					", but was a " + pattern.localeTemplate.getValueClass().getName());
	}

	public enum ValueType
	{
		STRING (String.class),
		BOOLEAN(Boolean.class),
		INTEGER(Integer.class),
		DOUBLE (Double.class),
		ENUM   (Enum.class);

		final Class<?> valueClass;

		ValueType(final Class<?> valueClass)
		{
			this.valueClass = valueClass;
		}

		public final Class<?> getValueClass()
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
			final Mount<L> m = p.mount();
			final List<Field<L>> fields = getFields(); // TODO make more efficient
			final int position = fields.isEmpty() ? 0 : (fields.get(fields.size()-1).getPosition()+1);
			final List<Field<L>> fieldsPerValuetype = getFields(p, m, valueType); // TODO make more efficient
			final int positionPerValuetype = fieldsPerValuetype.isEmpty() ? 0 : (fieldsPerValuetype.get(fieldsPerValuetype.size()-1).getPositionPerValueType()+1);
			p.assertCapacity(valueType, positionPerValuetype);
			return
					m.fieldType.newItem(
							m.fieldParent.map(this),
							p.fieldPosition.map(position),
							p.fieldCode.map(code),
							p.fieldValueType.map(valueType),
							p.fieldPositionPerValueType.map(positionPerValuetype));
		}

		private List<Field<L>> getFields(final DynamicModel<L> p, final Mount<L> m, final ValueType valueType)
		{
			return m.fieldType.search(m.fieldParent.equal(this).and(p.fieldValueType.equal(valueType)), p.fieldPositionPerValueType, true);
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
			final Mount<L> m = p.mount();
			return
				m.fieldType.search(
						m.fieldParent.equal(this),
						p.fieldPosition,
						true);
		}

		public Field<L> getField(final String code)
		{
			final DynamicModel<L> p = getPattern();
			final Mount<L> m = p.mount();
			return
				m.fieldType.searchSingleton(Cope.and(
						m.fieldParent.equal(this),
						p.fieldCode.equal(code)));
		}

		public com.exedio.cope.Type<?> getParentType()
		{
			return getPattern().getType();
		}

		public DynamicModel<L> getModel()
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
		 * @deprecated Use {@link #addField(String,DynamicModel.ValueType)} instead
		 */
		@Deprecated
		@SuppressWarnings("UnnecessarilyQualifiedInnerClassAccess") // otherwise javadoc issues warnings
		public Field<L> addAttribute(final String code, final ValueType valueType)
		{
			return addField(code, valueType);
		}

		/**
		 * @deprecated Use {@link #addStringField(String)} instead
		 */
		@Deprecated
		public Field<L> addStringAttribute(final String code)
		{
			return addStringField(code);
		}

		/**
		 * @deprecated Use {@link #addBooleanField(String)} instead
		 */
		@Deprecated
		public Field<L> addBooleanAttribute(final String code)
		{
			return addBooleanField(code);
		}

		/**
		 * @deprecated Use {@link #addIntegerField(String)} instead
		 */
		@Deprecated
		public Field<L> addIntegerAttribute(final String code)
		{
			return addIntegerField(code);
		}

		/**
		 * @deprecated Use {@link #addDoubleField(String)} instead
		 */
		@Deprecated
		public Field<L> addDoubleAttribute(final String code)
		{
			return addDoubleField(code);
		}

		/**
		 * @deprecated Use {@link #addEnumField(String)} instead
		 */
		@Deprecated
		public Field<L> addEnumAttribute(final String code)
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
		public Field<L> getAttribute(final String code)
		{
			return getField(code);
		}

		/**
		 * @deprecated Use {@link #getModel()} instead
		 */
		@Deprecated
		public DynamicModel<?> getDtypeSystem()
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
			final Mount<L> m = p.mount();
			return
				m.enumType.search(
						m.enumParent.equal(this),
						p.enumPosition,
						true);
		}

		public Enum<L> getEnumValue(final String code)
		{
			final DynamicModel<L> p = getPattern();
			final Mount<L> m = p.mount();
			assertEnum();
			return
				m.enumType.searchSingleton(Cope.and(
						m.enumParent.equal(this),
						p.enumCode.equal(code)));
		}

		public Enum<L> addEnumValue(final String code)
		{
			final DynamicModel<L> p = getPattern();
			final Mount<L> m = p.mount();
			assertEnum();
			final List<Enum<L>> values = getEnumValues(); // TODO make more efficient
			final int position = values.isEmpty() ? 0 : (values.get(values.size()-1).getPosition()+1);
			return
					m.enumType.newItem(
							m.enumParent.map(this),
							p.enumPosition.map(position),
							p.enumCode.map(code));
		}

		public Enum<L> as(final Enum<?> value)
		{
			assertEnum();
			getPattern().checkMatchingLocalization(value.getPattern());

			@SuppressWarnings("unchecked") // OK: is checked on runtime
			final Enum<L> result = (Enum<L>)value;
			return result;
		}

		public Type<L> getParent()
		{
			return getPattern().mount().fieldParent.get(this);
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

		public ItemField<Enum<L>> getFieldEnum()
		{
			return getPattern().getFieldEnum(this);
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

		public Field<L> getParent()
		{
			return getPattern().mount().enumParent.get(this);
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

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #create(FunctionField,int,int,int,int,int)} instead
	 */
	@Deprecated
	public static <L> DynamicModel<L> newModel(
			final FunctionField<L> locale,
			final int stringCapacity,
			final int booleanCapacity,
			final int integerCapacity,
			final int doubleCapacity,
			final int enumCapacity)
	{
		return create(locale, stringCapacity, booleanCapacity, integerCapacity,
				doubleCapacity, enumCapacity);
	}

	/**
	 * @deprecated Use {@link #create(FunctionField)} instead
	 */
	@Deprecated
	public static <L> DynamicModel<L> newModel(final FunctionField<L> locale)
	{
		return create(locale);
	}
}
