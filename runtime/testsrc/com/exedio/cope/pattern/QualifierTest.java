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

import java.util.Arrays;

import com.exedio.cope.Feature;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.IntegrityViolationException;
import com.exedio.cope.LengthViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.SetValue;
import com.exedio.cope.TestmodelTest;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.testmodel.EmptyItem;
import com.exedio.cope.testmodel.QualifiedEmptyQualifier;
import com.exedio.cope.testmodel.QualifiedIntegerEnumQualifier;
import com.exedio.cope.testmodel.QualifiedItem;
import com.exedio.cope.testmodel.QualifiedStringQualifier;
import com.exedio.cope.testmodel.QualifiedSubItem;

public class QualifierTest extends TestmodelTest
{
	QualifiedItem item;
	EmptyItem key1;
	EmptyItem key2;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		key1 = deleteOnTearDown(new EmptyItem());
		key2 = deleteOnTearDown(new EmptyItem());
		item = deleteOnTearDown(new QualifiedItem());
	}
	
	public void testQualified()
		throws UniqueViolationException, MandatoryViolationException, IntegrityViolationException,
			LengthViolationException, FinalViolationException
	{
		// test model
		assertEquals(QualifiedEmptyQualifier.TYPE, QualifiedEmptyQualifier.qualifier.getType());
		assertEquals("qualifier", QualifiedEmptyQualifier.qualifier.getName());
		assertEquals(QualifiedEmptyQualifier.parent, QualifiedEmptyQualifier.qualifier.getParent());
		assertEquals(list(QualifiedEmptyQualifier.qualifier), QualifiedEmptyQualifier.parent.getPatterns());
		assertEqualsUnmodifiable(list(QualifiedEmptyQualifier.key), QualifiedEmptyQualifier.qualifier.getKeys());
		assertEquals(list(QualifiedEmptyQualifier.qualifier), QualifiedEmptyQualifier.key.getPatterns());
		assertEquals(QualifiedEmptyQualifier.qualifyUnique, QualifiedEmptyQualifier.qualifier.getUniqueConstraint());
		assertEqualsUnmodifiable(list(item.TYPE.getThis(), item.number), item.TYPE.getFeatures());
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				QualifiedEmptyQualifier.TYPE.getThis(),
				QualifiedEmptyQualifier.parent,
				QualifiedEmptyQualifier.key,
				QualifiedEmptyQualifier.qualifyUnique,
				QualifiedEmptyQualifier.qualifier,
				QualifiedEmptyQualifier.qualifiedA,
				QualifiedEmptyQualifier.qualifiedB,
			}),
			QualifiedEmptyQualifier.TYPE.getFeatures());
		assertEqualsUnmodifiable(list(QualifiedEmptyQualifier.qualifiedA, QualifiedEmptyQualifier.qualifiedB),
				QualifiedEmptyQualifier.qualifier.getFeatures());
		assertEqualsUnmodifiable(list(QualifiedEmptyQualifier.qualifiedA, QualifiedEmptyQualifier.qualifiedB),
				QualifiedEmptyQualifier.qualifier.getFields());
		
		assertEqualsUnmodifiable(list(QualifiedEmptyQualifier.qualifier, QualifiedStringQualifier.stringQualifier, QualifiedIntegerEnumQualifier.intEnumQualifier), Qualifier.getDeclaredQualifiers(QualifiedItem.TYPE));
		assertEqualsUnmodifiable(list(), Qualifier.getDeclaredQualifiers(QualifiedSubItem.TYPE));
		assertEqualsUnmodifiable(list(), Qualifier.getDeclaredQualifiers(QualifiedEmptyQualifier.TYPE));
		assertEqualsUnmodifiable(list(), Qualifier.getDeclaredQualifiers(EmptyItem.TYPE)); // make sure, that key types dont influence the result

		assertEqualsUnmodifiable(list(QualifiedEmptyQualifier.qualifier, QualifiedStringQualifier.stringQualifier, QualifiedIntegerEnumQualifier.intEnumQualifier), Qualifier.getQualifiers(QualifiedItem.TYPE));
		assertEqualsUnmodifiable(list(QualifiedEmptyQualifier.qualifier, QualifiedStringQualifier.stringQualifier, QualifiedIntegerEnumQualifier.intEnumQualifier), Qualifier.getQualifiers(QualifiedSubItem.TYPE));
		assertEqualsUnmodifiable(list(), Qualifier.getQualifiers(QualifiedEmptyQualifier.TYPE));
		assertEqualsUnmodifiable(list(), Qualifier.getQualifiers(EmptyItem.TYPE)); // make sure, that key types dont influence the result

		// test caching
		assertSame(Qualifier.getDeclaredQualifiers(QualifiedItem.TYPE), Qualifier.getDeclaredQualifiers(QualifiedItem.TYPE));
		assertSame(Qualifier.getDeclaredQualifiers(QualifiedEmptyQualifier.TYPE), Qualifier.getDeclaredQualifiers(QualifiedEmptyQualifier.TYPE));
		assertSame(Qualifier.getQualifiers(QualifiedItem.TYPE), Qualifier.getQualifiers(QualifiedItem.TYPE));
		assertSame(Qualifier.getQualifiers(QualifiedEmptyQualifier.TYPE), Qualifier.getQualifiers(QualifiedEmptyQualifier.TYPE));
		
		// test persistence
		assertEquals(null, item.getQualifier(key1));
		assertEquals(null, item.getQualifiedA(key1));
		assertEquals(null, item.getQualifiedB(key1));
		assertEquals(null, item.getQualifier(key2));
		assertEquals(null, item.getQualifiedA(key2));
		assertEquals(null, item.getQualifiedB(key2));

		final QualifiedEmptyQualifier qitem1 = new QualifiedEmptyQualifier(item, key1);
		assertEquals(qitem1, item.getQualifier(key1));
		assertEquals(null, item.getQualifiedA(key1));
		assertEquals(null, item.getQualifiedB(key1));
		assertEquals(null, item.getQualifier(key2));
		assertEquals(null, item.getQualifiedA(key2));
		assertEquals(null, item.getQualifiedB(key2));

		item.setQualifiedA(key1, "value1A");
		assertEquals("value1A", qitem1.getQualifiedA());
		assertEquals(qitem1, item.getQualifier(key1));
		assertEquals("value1A", item.getQualifiedA(key1));
		assertEquals(null, item.getQualifiedB(key1));
		assertEquals(null, item.getQualifier(key2));
		assertEquals(null, item.getQualifiedA(key2));
		assertEquals(null, item.getQualifiedB(key2));
		
		item.setQualifiedB(key1, "value1B");
		assertEquals("value1B", qitem1.getQualifiedB());
		assertEquals(qitem1, item.getQualifier(key1));
		assertEquals("value1A", item.getQualifiedA(key1));
		assertEquals("value1B", item.getQualifiedB(key1));
		assertEquals(null, item.getQualifier(key2));
		assertEquals(null, item.getQualifiedA(key2));
		assertEquals(null, item.getQualifiedB(key2));

		item.setQualifiedB(key2, "value2B");
		final QualifiedEmptyQualifier qitem2 = QualifiedEmptyQualifier.findByQualifyUnique(item, key2);
		assertEquals("value2B", qitem2.getQualifiedB());
		assertEquals(qitem1, item.getQualifier(key1));
		assertEquals("value1A", item.getQualifiedA(key1));
		assertEquals("value1B", item.getQualifiedB(key1));
		assertEquals(qitem2, item.getQualifier(key2));
		assertEquals(null, item.getQualifiedA(key2));
		assertEquals("value2B", item.getQualifiedB(key2));

		item.setQualifiedB(key1, null);
		assertEquals(null, qitem1.getQualifiedB());
		assertEquals(qitem1, item.getQualifier(key1));
		assertEquals("value1A", item.getQualifiedA(key1));
		assertEquals(null, item.getQualifiedB(key1));
		assertEquals(qitem2, item.getQualifier(key2));
		assertEquals(null, item.getQualifiedA(key2));
		assertEquals("value2B", item.getQualifiedB(key2));

		qitem2.deleteCopeItem();
		qitem1.deleteCopeItem();

		assertEquals(null, item.getQualifiedA("key1"));
		assertEquals(null, item.getQualifiedB("key1"));
		assertEquals(null, item.getQualifiedA("key2"));
		assertEquals(null, item.getQualifiedB("key2"));

		item.setQualifiedB("key1", Integer.valueOf(4));
		assertEquals(null, item.getQualifiedA("key1"));
		assertEquals(Integer.valueOf(4), item.getQualifiedB("key1"));
		assertEquals(null, item.getQualifiedA("key2"));
		assertEquals(null, item.getQualifiedB("key2"));

		item.setQualifiedA("key1", Integer.valueOf(8));
		assertEquals(Integer.valueOf(8), item.getQualifiedA("key1"));
		assertEquals(Integer.valueOf(4), item.getQualifiedB("key1"));
		assertEquals(null, item.getQualifiedA("key2"));
		assertEquals(null, item.getQualifiedB("key2"));

		item.setQualifiedB("key2", Integer.valueOf(10));
		assertEquals(Integer.valueOf(8), item.getQualifiedA("key1"));
		assertEquals(Integer.valueOf(4), item.getQualifiedB("key1"));
		assertEquals(null, item.getQualifiedA("key2"));
		assertEquals(Integer.valueOf(10), item.getQualifiedB("key2"));
		
		QualifiedStringQualifier.findByQualifyUnique(item, "key1").deleteCopeItem();
		QualifiedStringQualifier.findByQualifyUnique(item, "key2").deleteCopeItem();

		assertEquals(QualifiedIntegerEnumQualifier.up, QualifiedIntegerEnumQualifier.intEnumQualifier.getParent());
		assertEqualsUnmodifiable(
				list(QualifiedIntegerEnumQualifier.keyX, QualifiedIntegerEnumQualifier.keyY),
				QualifiedIntegerEnumQualifier.intEnumQualifier.getKeys());
		assertEquals(QualifiedIntegerEnumQualifier.qualifyUnique, QualifiedIntegerEnumQualifier.intEnumQualifier.getUniqueConstraint());
		
		assertEquals(null, item.getQualifiedB(Integer.valueOf(20), QualifiedIntegerEnumQualifier.KeyEnum.key1));
		assertEquals(null, item.getQualifiedA(Integer.valueOf(20), QualifiedIntegerEnumQualifier.KeyEnum.key1));
		assertEquals(null, item.getQualifiedB(Integer.valueOf(21), QualifiedIntegerEnumQualifier.KeyEnum.key1));
		assertEquals(null, item.getQualifiedB(Integer.valueOf(20), QualifiedIntegerEnumQualifier.KeyEnum.key2));
		
		item.setQualifiedB(Integer.valueOf(20), QualifiedIntegerEnumQualifier.KeyEnum.key1, "B-20-key1");
		assertEquals("B-20-key1", item.getQualifiedB(Integer.valueOf(20), QualifiedIntegerEnumQualifier.KeyEnum.key1));
		assertEquals(null,        item.getQualifiedA(Integer.valueOf(20), QualifiedIntegerEnumQualifier.KeyEnum.key1));
		assertEquals(null,        item.getQualifiedB(Integer.valueOf(21), QualifiedIntegerEnumQualifier.KeyEnum.key1));
		assertEquals(null,        item.getQualifiedB(Integer.valueOf(20), QualifiedIntegerEnumQualifier.KeyEnum.key2));
	
		item.setQualifiedA(Integer.valueOf(20), QualifiedIntegerEnumQualifier.KeyEnum.key1, "A-20-key1");
		assertEquals("B-20-key1", item.getQualifiedB(Integer.valueOf(20), QualifiedIntegerEnumQualifier.KeyEnum.key1));
		assertEquals("A-20-key1", item.getQualifiedA(Integer.valueOf(20), QualifiedIntegerEnumQualifier.KeyEnum.key1));
		assertEquals(null,        item.getQualifiedB(Integer.valueOf(21), QualifiedIntegerEnumQualifier.KeyEnum.key1));
		assertEquals(null,        item.getQualifiedB(Integer.valueOf(20), QualifiedIntegerEnumQualifier.KeyEnum.key2));
	
		item.setQualifiedB(Integer.valueOf(21), QualifiedIntegerEnumQualifier.KeyEnum.key1, "A-21-key1");
		assertEquals("B-20-key1", item.getQualifiedB(Integer.valueOf(20), QualifiedIntegerEnumQualifier.KeyEnum.key1));
		assertEquals("A-20-key1", item.getQualifiedA(Integer.valueOf(20), QualifiedIntegerEnumQualifier.KeyEnum.key1));
		assertEquals("A-21-key1", item.getQualifiedB(Integer.valueOf(21), QualifiedIntegerEnumQualifier.KeyEnum.key1));
		assertEquals(null,        item.getQualifiedB(Integer.valueOf(20), QualifiedIntegerEnumQualifier.KeyEnum.key2));
	
		item.setQualifiedB(Integer.valueOf(20), QualifiedIntegerEnumQualifier.KeyEnum.key2, "A-20-key2");
		assertEquals("B-20-key1", item.getQualifiedB(Integer.valueOf(20), QualifiedIntegerEnumQualifier.KeyEnum.key1));
		assertEquals("A-20-key1", item.getQualifiedA(Integer.valueOf(20), QualifiedIntegerEnumQualifier.KeyEnum.key1));
		assertEquals("A-21-key1", item.getQualifiedB(Integer.valueOf(21), QualifiedIntegerEnumQualifier.KeyEnum.key1));
		assertEquals("A-20-key2", item.getQualifiedB(Integer.valueOf(20), QualifiedIntegerEnumQualifier.KeyEnum.key2));
	
		assertEquals(null, QualifiedIntegerEnumQualifier.findByQualifyUnique(item, 21, QualifiedIntegerEnumQualifier.KeyEnum.key2));

		QualifiedIntegerEnumQualifier.findByQualifyUnique(item, 20, QualifiedIntegerEnumQualifier.KeyEnum.key1).deleteCopeItem();
		QualifiedIntegerEnumQualifier.findByQualifyUnique(item, 21, QualifiedIntegerEnumQualifier.KeyEnum.key1).deleteCopeItem();
		QualifiedIntegerEnumQualifier.findByQualifyUnique(item, 20, QualifiedIntegerEnumQualifier.KeyEnum.key2).deleteCopeItem();

		assertEquals(null, item.getIntEnumQualifier(Integer.valueOf(20), QualifiedIntegerEnumQualifier.KeyEnum.key1));
		assertEquals(null, item.getQualifiedA(Integer.valueOf(20), QualifiedIntegerEnumQualifier.KeyEnum.key1));
		assertEquals(null, item.getQualifiedB(Integer.valueOf(20), QualifiedIntegerEnumQualifier.KeyEnum.key1));
		
		QualifiedIntegerEnumQualifier.intEnumQualifier.set(
				new Object[]{item, Integer.valueOf(20), QualifiedIntegerEnumQualifier.KeyEnum.key1},
				new SetValue[]{QualifiedIntegerEnumQualifier.qualifiedA.map("A-20-key1"), QualifiedIntegerEnumQualifier.qualifiedB.map("B-20-key1")});
		final QualifiedIntegerEnumQualifier setterItem =
			item.getIntEnumQualifier(Integer.valueOf(20), QualifiedIntegerEnumQualifier.KeyEnum.key1);
		assertNotNull(setterItem);
		assertEquals("A-20-key1", item.getQualifiedA(Integer.valueOf(20), QualifiedIntegerEnumQualifier.KeyEnum.key1));
		assertEquals("B-20-key1", item.getQualifiedB(Integer.valueOf(20), QualifiedIntegerEnumQualifier.KeyEnum.key1));
		
		QualifiedIntegerEnumQualifier.intEnumQualifier.set(
				new Object[]{item, Integer.valueOf(20), QualifiedIntegerEnumQualifier.KeyEnum.key1},
				new SetValue[]{QualifiedIntegerEnumQualifier.qualifiedA.map("A-20-key1-c"), QualifiedIntegerEnumQualifier.qualifiedB.map("B-20-key1-c")});
		assertEquals(setterItem,
				item.getIntEnumQualifier(Integer.valueOf(20), QualifiedIntegerEnumQualifier.KeyEnum.key1));
		assertEquals("A-20-key1-c", item.getQualifiedA(Integer.valueOf(20), QualifiedIntegerEnumQualifier.KeyEnum.key1));
		assertEquals("B-20-key1-c", item.getQualifiedB(Integer.valueOf(20), QualifiedIntegerEnumQualifier.KeyEnum.key1));
	}
}
