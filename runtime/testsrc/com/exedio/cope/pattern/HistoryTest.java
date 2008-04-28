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

import java.util.Date;

import com.exedio.cope.AbstractLibTest;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.pattern.History.Feature;

public class HistoryTest extends AbstractLibTest
{
	private static final Model MODEL = new Model(HistoryItem.TYPE);
	
	public HistoryTest()
	{
		super(MODEL);
	}

	HistoryItem item;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new HistoryItem());
	}
	
	public void testIt() throws Exception
	{
		final Type<?> eventType = item.audit.getEventType();
		final Type<?> featureType = item.audit.getFeatureType();
		
		// test model
		assertEqualsUnmodifiable(list(
				item.TYPE,
				eventType,
				featureType
			), model.getTypes());
		assertEqualsUnmodifiable(list(
				item.TYPE,
				eventType,
				featureType
			), model.getTypesSortedByHierarchy());
		assertEquals(HistoryItem.class, item.TYPE.getJavaClass());
		assertEquals(true, item.TYPE.hasUniqueJavaClass());
		assertEquals(null, item.TYPE.getPattern());

		assertEqualsUnmodifiable(list(
				item.TYPE.getThis(),
				item.amount,
				item.comment,
				item.audit
			), item.TYPE.getFeatures());
		assertEqualsUnmodifiable(list(
				eventType.getThis(),
				item.auditEventParent(),
				item.audit.getEventDate(),
				item.audit.getEventAuthor(),
				item.audit.getEventNew()
			), eventType.getFeatures());
		assertEqualsUnmodifiable(list(
				featureType.getThis(),
				item.audit.getFeatureEvent(),
				item.audit.getFeatureId(),
				item.audit.getFeatureUniqueConstraint(),
				item.audit.getFeatureName(),
				item.audit.getFeatureOld(),
				item.audit.getFeatureNew()
			), featureType.getFeatures());

		assertEquals(item.TYPE, item.audit.getType());
		assertEquals("audit", item.audit.getName());

		assertEquals("HistoryItem.auditEvent", eventType.getID());
		assertEquals(History.Event.class, eventType.getJavaClass());
		assertEquals(false, eventType.hasUniqueJavaClass());
		assertSame(HistoryItem.audit, eventType.getPattern());
		assertEquals(null, eventType.getSupertype());
		assertEqualsUnmodifiable(list(), eventType.getSubTypes());
		assertEquals(false, eventType.isAbstract());
		assertEquals(Item.class, eventType.getThis().getValueClass().getSuperclass());
		assertEquals(eventType, eventType.getThis().getValueType());
		assertEquals(model, eventType.getModel());

		assertEquals("HistoryItem.auditFeature", featureType.getID());
		assertEquals(History.Feature.class, featureType.getJavaClass());
		assertEquals(false, featureType.hasUniqueJavaClass());
		assertSame(HistoryItem.audit, featureType.getPattern());
		assertEquals(null, featureType.getSupertype());
		assertEqualsUnmodifiable(list(), featureType.getSubTypes());
		assertEquals(false, featureType.isAbstract());
		assertEquals(Item.class, featureType.getThis().getValueClass().getSuperclass());
		assertEquals(featureType, featureType.getThis().getValueType());
		assertEquals(model, featureType.getModel());

		assertEquals(eventType, item.auditEventParent().getType());
		assertEquals(eventType, item.audit.getEventDate().getType());
		assertEquals(eventType, item.audit.getEventAuthor().getType());
		assertEquals(eventType, item.audit.getEventNew().getType());
		assertEquals(featureType, item.audit.getFeatureEvent().getType());
		assertEquals(featureType, item.audit.getFeatureId().getType());
		assertEquals(featureType, item.audit.getFeatureUniqueConstraint().getType());
		assertEquals(featureType, item.audit.getFeatureName().getType());
		assertEquals(featureType, item.audit.getFeatureOld().getType());
		assertEquals(featureType, item.audit.getFeatureNew().getType());

		assertEquals("parent", item.auditEventParent().getName());
		assertEquals("date", item.audit.getEventDate().getName());
		assertEquals("author", item.audit.getEventAuthor().getName());
		assertEquals("new", item.audit.getEventNew().getName());
		assertEquals("event", item.audit.getFeatureEvent().getName());
		assertEquals("id", item.audit.getFeatureId().getName());
		assertEquals("uniqueConstraint", item.audit.getFeatureUniqueConstraint().getName());
		assertEquals("name", item.audit.getFeatureName().getName());
		assertEquals("old", item.audit.getFeatureOld().getName());
		assertEquals("new", item.audit.getFeatureNew().getName());

		assertEqualsUnmodifiable(list(item.audit.getFeatureEvent(), item.audit.getFeatureId()), item.audit.getFeatureUniqueConstraint().getFields());

		assertTrue(eventType.isAssignableFrom(eventType));
		assertTrue(!eventType.isAssignableFrom(featureType));
		assertTrue(!item.TYPE.isAssignableFrom(eventType));
		assertTrue(!eventType.isAssignableFrom(item.TYPE));
		
		assertSame(HistoryItem.class, item.auditEventParent().getValueClass());
		assertSame(HistoryItem.TYPE, item.auditEventParent().getValueType());
		assertSame(History.Event.class, item.audit.getFeatureEvent().getValueClass());
		assertSame(item.audit.getEventType(), item.audit.getFeatureEvent().getValueType());
		
		assertEqualsUnmodifiable(list(HistoryItem.audit), History.getHistories(HistoryItem.TYPE));
		assertEqualsUnmodifiable(list(), History.getHistories(HistoryItem.audit.getEventType()));
		
		// test persistence
		assertEquals(list(), item.getAuditEvents());
		
		final Date before1 = new Date();
		History.Event event1 = item.createAuditEvent("author1", true);
		final Date after1 = new Date();
		assertSame(item.audit, event1.getPattern());
		assertEquals(item, event1.getParent());
		assertWithin(before1, after1, event1.getDate());
		assertEquals("author1", event1.getAuthor());
		assertEquals(true, event1.isNew());
		assertEquals(list(), event1.getFeatures());
		assertEqualsUnmodifiable(list(event1), item.getAuditEvents());
		
		final Feature feature11 = event1.createFeature(item.amount, "Amount", new Double(1.1), new Double(2.2));
		assertSame(item.audit, feature11.getPattern());
		assertEquals(event1, feature11.getEvent());
		assertSame(item.amount, feature11.getFeature());
		assertEquals(item.amount.getID(), feature11.getId());
		assertEquals("Amount", feature11.getName());
		assertEquals("1.1", feature11.getOld());
		assertEquals("2.2", feature11.getNew());
		assertEquals(list(feature11), event1.getFeatures());
		
		final Feature feature12 = event1.createFeature(item.comment, "Comment", "blub", "blah");
		assertSame(item.audit, feature12.getPattern());
		assertEquals(event1, feature12.getEvent());
		assertSame(item.comment, feature12.getFeature());
		assertEquals(item.comment.getID(), feature12.getId());
		assertEquals("Comment", feature12.getName());
		assertEquals("blub", feature12.getOld());
		assertEquals("blah", feature12.getNew());
		assertEquals(list(feature11, feature12), event1.getFeatures());
		
		final Date before2 = new Date();
		Thread.sleep(1L);
		History.Event event2 = item.createAuditEvent("author2", false);
		Thread.sleep(1L);
		final Date after2 = new Date();
		assertSame(item.audit, event2.getPattern());
		assertEquals(item, event2.getParent());
		assertWithin(before2, after2, event2.getDate());
		assertEquals("author2", event2.getAuthor());
		assertEquals(false, event2.isNew());
		assertEquals(list(), event2.getFeatures());
		assertEqualsUnmodifiable(list(event2, event1), item.getAuditEvents());
		assertEquals(event1, event1);
		assertTrue(!event1.equals(event2));
	}
}
