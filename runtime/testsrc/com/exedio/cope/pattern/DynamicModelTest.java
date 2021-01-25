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

import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.SchemaInfo.getColumnValue;
import static com.exedio.cope.pattern.DynamicModel.ValueType.BOOLEAN;
import static com.exedio.cope.pattern.DynamicModel.ValueType.DOUBLE;
import static com.exedio.cope.pattern.DynamicModel.ValueType.ENUM;
import static com.exedio.cope.pattern.DynamicModel.ValueType.INTEGER;
import static com.exedio.cope.pattern.DynamicModel.ValueType.STRING;
import static com.exedio.cope.pattern.DynamicModelItem.TYPE;
import static com.exedio.cope.pattern.DynamicModelItem.features;
import static com.exedio.cope.pattern.DynamicModelItem.small;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.list;
import static java.util.Arrays.asList;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.Feature;
import com.exedio.cope.Field;
import com.exedio.cope.Model;
import com.exedio.cope.Pattern;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.Type;
import com.exedio.cope.pattern.DynamicModel.Enum;
import com.exedio.cope.pattern.DynamicModel.ValueType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DynamicModelTest extends TestWithEnvironment
{
	public static final Model MODEL = new Model(TYPE, DynamicModelLocalizationItem.TYPE);

	static
	{
		MODEL.enableSerialization(DynamicModelTest.class, "MODEL");
	}

	public DynamicModelTest()
	{
		super(MODEL);
	}

	DynamicModelItem item, item2;
	DynamicModelLocalizationItem de, en;

	@BeforeEach final void setUp()
	{
		item = new DynamicModelItem("item1");
		item2 = new DynamicModelItem("item2");
		de = new DynamicModelLocalizationItem("de");
		en = new DynamicModelLocalizationItem("en");
	}

	private static void assertIt(final Pattern pattern, final Field<?> field, final String postfix)
	{
		assertEquals(pattern.getType(), field.getType());
		assertEquals(pattern.getName() + '-' + postfix, field.getName());
		assertEquals(pattern, field.getPattern());
	}

	@Test void testIt()
	{
		// test model
		assertEquals(String .class, STRING .getValueClass());
		assertEquals(Boolean.class, BOOLEAN.getValueClass());
		assertEquals(Integer.class, INTEGER.getValueClass());
		assertEquals(Double .class, DOUBLE .getValueClass());
		assertEquals(Enum   .class, ENUM   .getValueClass());

		assertEquals(TYPE, features.getType());
		assertEquals("features", features.getName());

		assertSame(features.getTypeType(), features.getTypeField().getValueType());
		assertEquals("features-type", features.getTypeField().getName());

		assertEquals(asList(new Type<?>[]{
				TYPE,
				features.getTypeType(), features.getTypeLocalizationType(),
				features.getFieldType(), features.getFieldLocalizationType(),
				features.getEnumType(), features.getEnumLocalizationType(),
				small.getTypeType(), small.getTypeLocalizationType(),
				small.getFieldType(), small.getFieldLocalizationType(),
				// no getEnumType()
				DynamicModelLocalizationItem.TYPE,
			}), model.getTypes());
		assertEquals(asList(new Feature[]{
				TYPE.getThis(),
				DynamicModelItem.name,
				features,
				features.getField(STRING , 0, null),
				features.getField(BOOLEAN, 0, null),
				features.getField(INTEGER, 0, null),
				features.getField(DOUBLE , 0, null),
				features.getField(ENUM   , 0, null),
				features.getField(ENUM   , 1, null),
				features.getTypeField(),
				small,
				small.getField(STRING, 0, null),
				small.getTypeField(),
			}), TYPE.getFeatures());
		assertIt(features, features.getField(STRING,  0, null), "string0");
		assertIt(features, features.getField(BOOLEAN, 0, null), "bool0");
		assertIt(features, features.getField(INTEGER, 0, null), "int0");
		assertIt(features, features.getField(DOUBLE,  0, null), "double0");
		assertIt(features, features.getField(ENUM,    0, null), "enum0");
		assertIt(features, features.getField(ENUM,    1, null), "enum1");
		assertIt(small, small.getField(STRING, 0, null), "string0");
		assertSerializedSame(features, 390);
		assertSerializedSame(small   , 387);

		assertEquals(asList(STRING, BOOLEAN, INTEGER, DOUBLE, ENUM), asList(ValueType.values()));
		assertEquals(10, getColumnValue(STRING ));
		assertEquals(20, getColumnValue(BOOLEAN));
		assertEquals(30, getColumnValue(INTEGER));
		assertEquals(40, getColumnValue(DOUBLE ));
		assertEquals(50, getColumnValue(ENUM   ));

		// test persistence
		assertContains(features.getTypes());

		final DynamicModel.Type<DynamicModelLocalizationItem> cellPhone = features.createType("cellPhone");
		assertEquals(TYPE, cellPhone.getParentType());
		assertEquals(features, cellPhone.getModel());
		assertEquals("cellPhone", cellPhone.getCode());
		assertContains(cellPhone, features.getTypes());
		assertEquals(cellPhone, features.getType("cellPhone"));
		assertEquals(null, features.getType("cellPhoneX"));
		assertContains(cellPhone.getFields());

		final DynamicModel.Field<DynamicModelLocalizationItem> akkuTime = cellPhone.addIntegerField("akkuTime");
		assertEquals(INTEGER, akkuTime.getValueType());
		assertEquals(cellPhone, akkuTime.getParent());
		assertEquals(0, akkuTime.getPosition());
		assertEquals("akkuTime", akkuTime.getCode());
		assertSame(TYPE, akkuTime.getField().getType());
		assertEquals("features-int0", akkuTime.getField().getName());
		assertEquals(Integer.class, akkuTime.getField().getValueClass());
		assertEquals(list(akkuTime), cellPhone.getFields());
		assertEquals(akkuTime, cellPhone.getField("akkuTime"));
		assertEquals(null, cellPhone.getField("akkuTimeX"));

		final DynamicModel.Field<DynamicModelLocalizationItem> memory = cellPhone.addStringField("memory");
		assertEquals(STRING, memory.getValueType());
		assertEquals(cellPhone, memory.getParent());
		assertEquals(1, memory.getPosition());
		assertEquals("memory", memory.getCode());
		assertSame(TYPE, memory.getField().getType());
		assertEquals("features-string0", memory.getField().getName());
		assertEquals(String.class, memory.getField().getValueClass());
		assertEquals(list(akkuTime, memory), cellPhone.getFields());

		assertEquals(null, item.getFeaturesType());

		item.setFeaturesType(cellPhone);
		assertEquals(cellPhone, item.getFeaturesType());
		assertEquals(null, item.getFeatures(akkuTime));
		assertEquals(null, item.getFeatures(memory));

		item.setFeatures(akkuTime, 5);
		assertEquals(5, item.getFeatures(akkuTime));
		assertEquals(5, akkuTime.get(item));
		assertEquals(null, item.getFeatures(memory));

		akkuTime.set(item, 10);
		assertEquals(10, item.getFeatures(akkuTime));
		assertEquals(10, akkuTime.get(item));
		assertEquals(null, item.getFeatures(memory));

		item.setFeatures(akkuTime, 80);
		assertEquals(80, item.getFeatures(akkuTime));
		assertEquals(null, item.getFeatures(memory));

		item.setFeatures(memory, "80TB");
		assertEquals(80, item.getFeatures(akkuTime));
		assertEquals(80, akkuTime.get(item));
		assertEquals("80TB", item.getFeatures(memory));
		assertEquals("80TB", memory.get(item));

		final DynamicModel.Type<DynamicModelLocalizationItem> organizer = features.createType("organizer");
		assertEquals(TYPE, organizer.getParentType());
		assertEquals(features, organizer.getModel());
		assertEquals("organizer", organizer.getCode());
		assertContains(cellPhone, organizer, features.getTypes());

		final DynamicModel.Field<DynamicModelLocalizationItem> weight = organizer.addIntegerField("weight");
		assertEquals(INTEGER, weight.getValueType());
		assertEquals(organizer, weight.getParent());
		assertEquals(0, weight.getPosition());
		assertEquals("weight", weight.getCode());
		assertSame(akkuTime.getField(), weight.getField());
		assertEquals(list(weight), organizer.getFields());

		item2.setFeaturesType(organizer);
		assertEquals(organizer, item2.getFeaturesType());
		assertEquals(null, item2.getFeatures(weight));

		item2.setFeatures(weight, 500);
		assertEquals(500, item2.getFeatures(weight));

		final DynamicModel.Field<DynamicModelLocalizationItem> bluetooth = organizer.addBooleanField("bluetooth");
		assertEquals(BOOLEAN, bluetooth.getValueType());
		assertEquals(organizer, bluetooth.getParent());
		assertEquals(1, bluetooth.getPosition());
		assertEquals("bluetooth", bluetooth.getCode());
		assertSame(TYPE, bluetooth.getField().getType());
		assertEquals("features-bool0", bluetooth.getField().getName());
		assertEquals(Boolean.class, bluetooth.getField().getValueClass());
		assertEquals(list(weight, bluetooth), organizer.getFields());

		final DynamicModel.Field<DynamicModelLocalizationItem> length = organizer.addDoubleField("length");
		assertEquals(DOUBLE, length.getValueType());
		assertEquals(organizer, length.getParent());
		assertEquals(2, length.getPosition());
		assertEquals("length", length.getCode());
		assertSame(TYPE, length.getField().getType());
		assertEquals("features-double0", length.getField().getName());
		assertEquals(Double.class, length.getField().getValueClass());
		assertEquals(list(weight, bluetooth, length), organizer.getFields());

		assertEquals(null, item2.getFeatures(bluetooth));
		assertEquals(null, item2.getFeatures(length));
		item2.setFeatures(bluetooth, true);
		item2.setFeatures(length, 2.2);
		assertEquals(true, item2.getFeatures(bluetooth));
		assertEquals(2.2, item2.getFeatures(length));

		final DynamicModel.Field<DynamicModelLocalizationItem> color = organizer.addEnumField("color");
		assertEquals(ENUM, color.getValueType());
		assertEquals(organizer, color.getParent());
		assertEquals(3, color.getPosition());
		assertEquals("color", color.getCode());
		assertSame(TYPE, color.getFieldEnum().getType());
		assertEquals("features-enum0", color.getFieldEnum().getName());
		assertEquals(features.getEnumType(), color.getFieldEnum().getValueType());
		assertEquals(list(weight, bluetooth, length, color), organizer.getFields());
		assertContains(color.getEnumValues());
		assertEquals(null, item2.getFeatures(color));
		assertSame(color.getFieldEnum(), color.getField());

		final Enum<DynamicModelLocalizationItem> colorRed = color.addEnumValue("red");
		assertEquals(color, colorRed.getParent());
		assertEquals(0, colorRed.getPosition());
		assertEquals("red", colorRed.getCode());
		assertContains(colorRed, color.getEnumValues());
		assertEquals(colorRed, color.getEnumValue("red"));
		assertEquals(null, color.getEnumValue("redX"));

		final DynamicModel.Enum<DynamicModelLocalizationItem> colorBlue = color.addEnumValue("blue");
		assertEquals(color, colorBlue.getParent());
		assertEquals(1, colorBlue.getPosition());
		assertEquals("blue", colorBlue.getCode());
		assertContains(colorRed, colorBlue, color.getEnumValues());

		item2.setFeatures(color, colorBlue);
		assertEquals(colorBlue, item2.getFeatures(color));

		final DynamicModel.Field<DynamicModelLocalizationItem> manufacturer = organizer.addEnumField("manufacturer");
		assertEquals(ENUM, manufacturer.getValueType());
		assertEquals(organizer, manufacturer.getParent());
		assertEquals(4, manufacturer.getPosition());
		assertEquals("manufacturer", manufacturer.getCode());
		assertSame(TYPE, manufacturer.getFieldEnum().getType());
		assertEquals("features-enum1", manufacturer.getFieldEnum().getName());
		assertEquals(features.getEnumType(), manufacturer.getFieldEnum().getValueType());
		assertEquals(list(weight, bluetooth, length, color, manufacturer), organizer.getFields());
		assertContains(manufacturer.getEnumValues());
		assertEquals(null, item2.getFeatures(manufacturer));
		assertSame(manufacturer.getFieldEnum(), manufacturer.getField());

		final Enum<DynamicModelLocalizationItem> manufacturer1 = manufacturer.addEnumValue("manufacturer1");
		assertEquals(manufacturer, manufacturer1.getParent());
		assertEquals(0, manufacturer1.getPosition());
		assertEquals("manufacturer1", manufacturer1.getCode());
		assertContains(manufacturer1, manufacturer.getEnumValues());

		item2.setFeatures(manufacturer, manufacturer1);
		assertEquals(manufacturer1, item2.getFeatures(manufacturer));
		try
		{
			item2.setFeatures(manufacturer, colorBlue);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("dynamic model mismatch: enum value " + colorBlue + " has type " + color + ", but must be " + manufacturer, e.getMessage());
		}
		assertEquals(manufacturer1, item2.getFeatures(manufacturer));


		// wrong value type
		try
		{
			akkuTime.getFieldEnum();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("operation allowed for getValueType()==ENUM fields only, but was INTEGER", e.getMessage());
		}
		try
		{
			memory.getFieldEnum();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("operation allowed for getValueType()==ENUM fields only, but was STRING", e.getMessage());
		}
		try
		{
			bluetooth.getFieldEnum();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("operation allowed for getValueType()==ENUM fields only, but was BOOLEAN", e.getMessage());
		}
		try
		{
			length.getFieldEnum();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("operation allowed for getValueType()==ENUM fields only, but was DOUBLE", e.getMessage());
		}
		try
		{
			item2.setFeatures(weight, "510");
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("Cannot cast " + String.class.getName() + " to " + Integer.class.getName(), e.getMessage());
		}
		assertEquals(500, item2.getFeatures(weight));

		// wrong dtype
		try
		{
			item.getFeatures(weight);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("dynamic type mismatch: field has type organizer, but item has cellPhone", e.getMessage());
		}
		try
		{
			item.setFeatures(weight, 510);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("dynamic type mismatch: field has type organizer, but item has cellPhone", e.getMessage());
		}
		assertEquals(500, item2.getFeatures(weight));

		assertContains(akkuTime, memory, cellPhone.getFields());
		try
		{
			cellPhone.addStringField("tooMuch");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"Capacity for STRING exceeded, " +
					"1 available, but tried to allocate 2. " +
					"Use DynamicModel#newModel(" +
						"FunctionField<L> locale, " +
						"int stringCapacity, " +
						"int booleanCapacity, " +
						"int integerCapacity, " +
						"int doubleCapacity, " +
						"int enumCapacity) " +
					"to increase capacities.", e.getMessage());
		}
		assertContains(akkuTime, memory, cellPhone.getFields());

		/* TODO assertContains(akkuTime, memory, cellPhone.getAttributes());
		try
		{
			cellPhone.addDoubleAttribute("akkuTime");
			fail();
		}
		catch(UniqueViolationException e)
		{
			assertEquals("unique violation for DAttribute#uniqueConstraintName", e.getMessage());
		}
		assertContains(akkuTime, memory, cellPhone.getAttributes());*/

		try
		{
			akkuTime.getEnumValues();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("operation allowed for getValueType()==ENUM fields only, but was " + INTEGER, e.getMessage());
		}
		try
		{
			akkuTime.getEnumValue(null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("operation allowed for getValueType()==ENUM fields only, but was " + INTEGER, e.getMessage());
		}
		try
		{
			akkuTime.addEnumValue(null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("operation allowed for getValueType()==ENUM fields only, but was " + INTEGER, e.getMessage());
		}


		// test cleaning of fields on setting type
		item2.setFeaturesType(cellPhone);
		assertEquals(null, item2.getFeatures(akkuTime)); // must not be 500 left from weight
		assertEquals(null, item2.getFeatures(memory));

		item.setFeaturesType(null);
		assertEquals(null, item.getFeaturesType());
		item2.setFeaturesType(null);
		assertEquals(null, item2.getFeaturesType());

		// test very small model without enums
		final DynamicModel.Type<DynamicModelLocalizationItem> smallType1 = small.createType("small1");
		final DynamicModelItem item3 = new DynamicModelItem("item3");
		item3.setSmallType(smallType1);
		final DynamicModel.Field<DynamicModelLocalizationItem> smallField1 = smallType1.addStringField("smallStringField1");
		item3.setSmall(smallField1, "hallo");
		assertEquals("hallo", item3.getSmall(smallField1));

		// test localization
		assertEquals(null, cellPhone.getName(de));
		assertEquals(null, cellPhone.getName(en));
		assertEquals(null, akkuTime.getName(de));
		assertEquals(null, akkuTime.getName(en));
		assertEquals(null, colorBlue.getName(de));
		assertEquals(null, colorBlue.getName(en));

		cellPhone.setName(de, "cellPhoneDE");
		akkuTime.setName(de, "akkuTimeDE");
		colorBlue.setName(de, "colorBlueDE");
		assertEquals("cellPhoneDE", cellPhone.getName(de));
		assertEquals(null, cellPhone.getName(en));
		assertEquals("akkuTimeDE", akkuTime.getName(de));
		assertEquals(null, akkuTime.getName(en));
		assertEquals("colorBlueDE", colorBlue.getName(de));
		assertEquals(null, colorBlue.getName(en));

		cellPhone.setName(de, "cellPhoneDE2");
		akkuTime.setName(de, "akkuTimeDE2");
		colorBlue.setName(de, "colorBlueDE2");
		assertEquals("cellPhoneDE2", cellPhone.getName(de));
		assertEquals(null, cellPhone.getName(en));
		assertEquals("akkuTimeDE2", akkuTime.getName(de));
		assertEquals(null, akkuTime.getName(en));
		assertEquals("colorBlueDE2", colorBlue.getName(de));
		assertEquals(null, colorBlue.getName(en));

		cellPhone.setName(en, "cellPhoneEN");
		akkuTime.setName(en, "akkuTimeEN");
		colorBlue.setName(en, "colorBlueEN");
		assertEquals("cellPhoneDE2", cellPhone.getName(de));
		assertEquals("cellPhoneEN", cellPhone.getName(en));
		assertEquals("akkuTimeDE2", akkuTime.getName(de));
		assertEquals("akkuTimeEN", akkuTime.getName(en));
		assertEquals("colorBlueDE2", colorBlue.getName(de));
		assertEquals("colorBlueEN", colorBlue.getName(en));

		cellPhone.setName(de, null);
		akkuTime.setName(de, null);
		colorBlue.setName(de, null);
		assertEquals(null, cellPhone.getName(de));
		assertEquals("cellPhoneEN", cellPhone.getName(en));
		assertEquals(null, akkuTime.getName(de));
		assertEquals("akkuTimeEN", akkuTime.getName(en));
		assertEquals(null, colorBlue.getName(de));
		assertEquals("colorBlueEN", colorBlue.getName(en));

		{
			assertSame("colorBlueEN", colorBlue.getName(en));
			@SuppressWarnings("UnnecessaryLocalVariable")
			final DynamicModel.Enum<?> colorBlueWildcard = colorBlue;
			assertSame("colorBlueEN", color.as(colorBlueWildcard).getName(en));
		}

		// test model mismatch
		try
		{
			item3.setSmallType(cellPhone);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("dynamic model mismatch: new type has model DynamicModelItem.features, but must be DynamicModelItem.small", e.getMessage());
		}
	}
}
