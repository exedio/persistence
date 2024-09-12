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
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.Feature;
import com.exedio.cope.Field;
import com.exedio.cope.Model;
import com.exedio.cope.Pattern;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.pattern.DynamicModel.Enum;
import com.exedio.cope.pattern.DynamicModel.ValueType;
import java.util.List;
import org.junit.jupiter.api.Test;

public class DynamicModelModelTest
{
	public static final Model MODEL = new Model(TYPE, DynamicModelLocalizationItem.TYPE);

	static
	{
		MODEL.enableSerialization(DynamicModelModelTest.class, "MODEL");
	}

	private static void assertIt(final Pattern pattern, final Field<?> field, final String postfix)
	{
		assertEquals(pattern.getType(), field.getType());
		assertEquals(pattern.getName() + '-' + postfix, field.getName());
		assertEquals(pattern, field.getPattern());
	}

	@Test void testIt()
	{
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
			}), MODEL.getTypes());
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
	}

	@Test void testTypeType()
	{
		assertEquals(asList(new Feature[]{
				features.getTypeType().getThis(),
				features.typeCode,
				features.typeCode.getImplicitUniqueConstraint(),
				features.typeLocalization,
			}), features.getTypeType().getFeatures());
	}

	@Test void testFieldType()
	{
		final Type<DynamicModel.Field<DynamicModelLocalizationItem>> type = features.getFieldType();
		final UniqueConstraint parentAndPosition          = (UniqueConstraint)type.getFeature("uniqueConstraint");
		final UniqueConstraint parentValueTypeAndPosition = (UniqueConstraint)type.getFeature("uniqueConstraintPerValueType");
		final UniqueConstraint parentAndCode              = (UniqueConstraint)type.getFeature("uniqueConstraintCode");
		assertEquals(asList(new Feature[]{
				type.getThis(),
				features.mount().fieldParent(),
				features.fieldPosition,
				parentAndPosition,
				features.fieldValueType,
				features.fieldPositionPerValueType,
				parentValueTypeAndPosition,
				features.fieldCode,
				parentAndCode,
				features.fieldLocalization,
			}), type.getFeatures());
		assertSame(features.getTypeType(), features.mount().fieldParent().getValueType());
		assertEquals(
				List.of(features.mount().fieldParent(), features.fieldPosition),
				parentAndPosition.getFields());
		assertEquals(
				List.of(features.mount().fieldParent(), features.fieldValueType, features.fieldPositionPerValueType),
				parentValueTypeAndPosition.getFields());
		assertEquals(
				List.of(features.mount().fieldParent(), features.fieldCode),
				parentAndCode.getFields());
	}

	@Test void testEnumType()
	{
		final Type<Enum<DynamicModelLocalizationItem>> type = features.getEnumType();
		final UniqueConstraint parentAndPosition = (UniqueConstraint)type.getFeature("uniquePosition");
		final UniqueConstraint parentAndCode     = (UniqueConstraint)type.getFeature("uniqueCode");
		assertEquals(asList(new Feature[]{
				type.getThis(),
				features.mount().enumParent(),
				features.enumPosition,
				parentAndPosition,
				features.enumCode,
				parentAndCode,
				features.enumLocalization,
			}), type.getFeatures());
		assertSame(features.getFieldType(), features.mount().enumParent().getValueType());
		assertEquals(
				List.of(features.mount().enumParent(), features.enumPosition),
				parentAndPosition.getFields());
		assertEquals(
				List.of(features.mount().enumParent(), features.enumCode),
				parentAndCode.getFields());
	}

	@Test void testSerialization()
	{
		assertSerializedSame(features, 395);
		assertSerializedSame(small   , 392);
	}

	@Test void testSchema()
	{
		assertEquals(asList(STRING, BOOLEAN, INTEGER, DOUBLE, ENUM), asList(ValueType.values()));
		assertEquals(10, getColumnValue(STRING ));
		assertEquals(20, getColumnValue(BOOLEAN));
		assertEquals(30, getColumnValue(INTEGER));
		assertEquals(40, getColumnValue(DOUBLE ));
		assertEquals(50, getColumnValue(ENUM   ));
	}
}
