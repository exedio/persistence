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

import java.util.Arrays;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Feature;
import com.exedio.cope.Field;
import com.exedio.cope.ItemField;
import com.exedio.cope.Model;
import com.exedio.cope.Pattern;
import com.exedio.cope.Type;
import com.exedio.cope.pattern.DynamicModel.Enum;
import com.exedio.cope.pattern.DynamicModel.ValueType;

public class DynamicModelTest extends AbstractRuntimeTest
{
	public static final Model MODEL = new Model(DynamicModelItem.TYPE, DynamicModelLocalizationItem.TYPE);

	public DynamicModelTest()
	{
		super(MODEL);
	}
	
	DynamicModelItem item, item2;
	DynamicModelLocalizationItem de, en;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new DynamicModelItem("item1"));
		item2 = deleteOnTearDown(new DynamicModelItem("item2"));
		de = deleteOnTearDown(new DynamicModelLocalizationItem("de"));
		en = deleteOnTearDown(new DynamicModelLocalizationItem("en"));
	}
	
	private static final void assertIt(final Pattern pattern, final Field field, final String postfix)
	{
		assertEquals(pattern.getType(), field.getType());
		assertEquals(pattern.getName() + postfix, field.getName());
		assertEquals(pattern, field.getPattern());
	}
	
	public void testIt()
	{
		// test model
		assertEquals(String .class, ValueType.STRING .getValueClass());
		assertEquals(Boolean.class, ValueType.BOOLEAN.getValueClass());
		assertEquals(Integer.class, ValueType.INTEGER.getValueClass());
		assertEquals(Double .class, ValueType.DOUBLE .getValueClass());
		assertEquals(Enum   .class, ValueType.ENUM   .getValueClass());
		
		assertEquals(item.TYPE, item.features.getType());
		assertEquals("features", item.features.getName());
		
		assertSame(item.features.getTypeType(), item.features.getTypeField().getValueType());
		assertEquals("featuresType", item.features.getTypeField().getName());
		
		assertEquals(Arrays.asList(new Type[]{
				DynamicModelItem.TYPE,
				DynamicModelItem.features.getTypeType(), DynamicModelItem.features.getTypeLocalizationType(),
				DynamicModelItem.features.getFieldType(), DynamicModelItem.features.getFieldLocalizationType(),
				DynamicModelItem.features.getEnumType(), DynamicModelItem.features.getEnumLocalizationType(),
				DynamicModelItem.small.getTypeType(), DynamicModelItem.small.getTypeLocalizationType(),
				DynamicModelItem.small.getFieldType(), DynamicModelItem.small.getFieldLocalizationType(),
				// no getEnumType()
				DynamicModelLocalizationItem.TYPE,
			}), model.getTypes());
		assertEquals(Arrays.asList(new Feature[]{
				DynamicModelItem.TYPE.getThis(),
				DynamicModelItem.name,
				DynamicModelItem.features,
				DynamicModelItem.features.getField(ValueType.STRING, 0, null),
				DynamicModelItem.features.getField(ValueType.BOOLEAN, 0, null),
				DynamicModelItem.features.getField(ValueType.INTEGER, 0, null),
				DynamicModelItem.features.getField(ValueType.DOUBLE, 0, null),
				DynamicModelItem.features.getField(ValueType.ENUM, 0, null),
				DynamicModelItem.features.getField(ValueType.ENUM, 1, null),
				DynamicModelItem.features.getTypeField(),
				DynamicModelItem.small,
				DynamicModelItem.small.getField(ValueType.STRING, 0, null),
				DynamicModelItem.small.getTypeField(),
			}), DynamicModelItem.TYPE.getFeatures());
		assertIt(DynamicModelItem.features, DynamicModelItem.features.getField(ValueType.STRING,  0, null), "String0");
		assertIt(DynamicModelItem.features, DynamicModelItem.features.getField(ValueType.BOOLEAN, 0, null), "Bool0");
		assertIt(DynamicModelItem.features, DynamicModelItem.features.getField(ValueType.INTEGER, 0, null), "Int0");
		assertIt(DynamicModelItem.features, DynamicModelItem.features.getField(ValueType.DOUBLE,  0, null), "Double0");
		assertIt(DynamicModelItem.features, DynamicModelItem.features.getField(ValueType.ENUM,    0, null), "Enum0");
		assertIt(DynamicModelItem.features, DynamicModelItem.features.getField(ValueType.ENUM,    1, null), "Enum1");
		assertIt(DynamicModelItem.small, DynamicModelItem.small.getField(ValueType.STRING, 0, null), "String0");
		
		// test persistence
		assertContains(item.features.getTypes());
		
		final DynamicModel.Type<DynamicModelLocalizationItem> cellPhone = deleteOnTearDown(item.features.createType("cellPhone"));
		assertEquals(item.TYPE, cellPhone.getParentType());
		assertEquals(item.features, cellPhone.getModel());
		assertEquals("cellPhone", cellPhone.getCode());
		assertContains(cellPhone, item.features.getTypes());
		assertEquals(cellPhone, item.features.getType("cellPhone"));
		assertEquals(null, item.features.getType("cellPhoneX"));
		assertContains(cellPhone.getFields());

		final DynamicModel.Field<DynamicModelLocalizationItem> akkuTime = cellPhone.addIntegerField("akkuTime");
		assertEquals(ValueType.INTEGER, akkuTime.getValueType());
		assertEquals(0, akkuTime.getPosition());
		assertEquals("akkuTime", akkuTime.getCode());
		assertSame(item.TYPE, akkuTime.getField().getType());
		assertEquals("featuresInt0", akkuTime.getField().getName());
		assertEquals(Integer.class, akkuTime.getField().getValueClass());
		assertEquals(list(akkuTime), cellPhone.getFields());
		assertEquals(akkuTime, cellPhone.getField("akkuTime"));
		assertEquals(null, cellPhone.getField("akkuTimeX"));

		final DynamicModel.Field<DynamicModelLocalizationItem> memory = cellPhone.addStringField("memory");
		assertEquals(ValueType.STRING, memory.getValueType());
		assertEquals(1, memory.getPosition());
		assertEquals("memory", memory.getCode());
		assertSame(item.TYPE, memory.getField().getType());
		assertEquals("featuresString0", memory.getField().getName());
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
		
		final DynamicModel.Type<DynamicModelLocalizationItem> organizer = deleteOnTearDown(item.features.createType("organizer"));
		assertEquals(item.TYPE, organizer.getParentType());
		assertEquals(item.features, organizer.getModel());
		assertEquals("organizer", organizer.getCode());
		assertContains(cellPhone, organizer, item.features.getTypes());

		final DynamicModel.Field<DynamicModelLocalizationItem> weight = organizer.addIntegerField("weight");
		assertEquals(ValueType.INTEGER, weight.getValueType());
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
		assertEquals(ValueType.BOOLEAN, bluetooth.getValueType());
		assertEquals(1, bluetooth.getPosition());
		assertEquals("bluetooth", bluetooth.getCode());
		assertSame(item.TYPE, bluetooth.getField().getType());
		assertEquals("featuresBool0", bluetooth.getField().getName());
		assertEquals(Boolean.class, bluetooth.getField().getValueClass());
		assertEquals(list(weight, bluetooth), organizer.getFields());
		
		final DynamicModel.Field<DynamicModelLocalizationItem> length = organizer.addDoubleField("length");
		assertEquals(ValueType.DOUBLE, length.getValueType());
		assertEquals(2, length.getPosition());
		assertEquals("length", length.getCode());
		assertSame(item.TYPE, length.getField().getType());
		assertEquals("featuresDouble0", length.getField().getName());
		assertEquals(Double.class, length.getField().getValueClass());
		assertEquals(list(weight, bluetooth, length), organizer.getFields());
		
		assertEquals(null, item2.getFeatures(bluetooth));
		assertEquals(null, item2.getFeatures(length));
		item2.setFeatures(bluetooth, true);
		item2.setFeatures(length, 2.2);
		assertEquals(true, item2.getFeatures(bluetooth));
		assertEquals(2.2, item2.getFeatures(length));
		
		final DynamicModel.Field<DynamicModelLocalizationItem> color = organizer.addEnumField("color");
		assertEquals(ValueType.ENUM, color.getValueType());
		assertEquals(3, color.getPosition());
		assertEquals("color", color.getCode());
		assertSame(item.TYPE, color.getField().getType());
		assertEquals("featuresEnum0", color.getField().getName());
		assertEquals(item.features.getEnumType(), ((ItemField)color.getField()).getValueType());
		assertEquals(list(weight, bluetooth, length, color), organizer.getFields());
		assertContains(color.getEnumValues());
		assertEquals(null, item2.getFeatures(color));
		
		final Enum colorRed = color.addEnumValue("red");
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
		assertEquals(ValueType.ENUM, manufacturer.getValueType());
		assertEquals(4, manufacturer.getPosition());
		assertEquals("manufacturer", manufacturer.getCode());
		assertSame(item.TYPE, manufacturer.getField().getType());
		assertEquals("featuresEnum1", manufacturer.getField().getName());
		assertEquals(item.features.getEnumType(), ((ItemField)manufacturer.getField()).getValueType());
		assertEquals(list(weight, bluetooth, length, color, manufacturer), organizer.getFields());
		assertContains(manufacturer.getEnumValues());
		assertEquals(null, item2.getFeatures(manufacturer));
		
		final Enum manufacturer1 = manufacturer.addEnumValue("manufacturer1");
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
		catch(IllegalArgumentException e)
		{
			assertEquals("dynamic model mismatch: enum value " + colorBlue + " has type " + color + ", but must be " + manufacturer, e.getMessage());
		}
		assertEquals(manufacturer1, item2.getFeatures(manufacturer));

		
		// wrong value type
		try
		{
			item2.setFeatures(weight, "510");
			fail();
		}
		catch(ClassCastException e)
		{
			assertEquals("expected a " + Integer.class.getName() + ", but was a " + String.class.getName(), e.getMessage());
		}
		assertEquals(500, item2.getFeatures(weight));

		// wrong dtype
		try
		{
			item.getFeatures(weight);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("dynamic type mismatch: field has type organizer, but item has cellPhone", e.getMessage());
		}
		try
		{
			item.setFeatures(weight, 510);
			fail();
		}
		catch(IllegalArgumentException e)
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
		catch(IllegalArgumentException e)
		{
			assertEquals("capacity for STRING exceeded, 1 available, but tried to allocate 2", e.getMessage());
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
		catch(IllegalArgumentException e)
		{
			assertEquals("operation allowed for getValueType()==ENUM fields only, but was " + ValueType.INTEGER, e.getMessage());
		}
		try
		{
			akkuTime.getEnumValue(null);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("operation allowed for getValueType()==ENUM fields only, but was " + ValueType.INTEGER, e.getMessage());
		}
		try
		{
			akkuTime.addEnumValue(null);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("operation allowed for getValueType()==ENUM fields only, but was " + ValueType.INTEGER, e.getMessage());
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
		final DynamicModel.Type<DynamicModelLocalizationItem> smallType1 = deleteOnTearDown(item.small.createType("small1"));
		final DynamicModelItem item3 = deleteOnTearDown(new DynamicModelItem("item3"));
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
		
		// test model mismatch
		try
		{
			item3.setSmallType(cellPhone);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("dynamic model mismatch: new type has model DynamicModelItem.features, but must be DynamicModelItem.small", e.getMessage());
		}
	}
}
