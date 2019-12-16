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
import static com.exedio.cope.SchemaInfoAssert.assertNoUpdateCounterColumn;
import static com.exedio.cope.pattern.HistoryItem.TYPE;
import static com.exedio.cope.pattern.HistoryItem.amount;
import static com.exedio.cope.pattern.HistoryItem.audit;
import static com.exedio.cope.pattern.HistoryItem.auditEventParent;
import static com.exedio.cope.pattern.HistoryItem.comment;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertWithin;
import static com.exedio.cope.tojunit.Assert.list;
import static java.lang.Double.valueOf;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.Type;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.pattern.History.Feature;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HistoryTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(HistoryTest.class, "MODEL");
	}

	public HistoryTest()
	{
		super(MODEL);
	}

	HistoryItem item;

	@BeforeEach final void setUp()
	{
		item = new HistoryItem();
	}

	@Test void testIt()
	{
		final Type<?> eventType = audit.getEventType();
		final Type<?> featureType = audit.getFeatureType();

		// test model
		assertEqualsUnmodifiable(list(
				TYPE,
				eventType,
				featureType
			), model.getTypes());
		assertEqualsUnmodifiable(list(
				TYPE,
				eventType,
				featureType
			), model.getTypesSortedByHierarchy());
		assertEquals(HistoryItem.class, TYPE.getJavaClass());
		assertEquals(true, TYPE.isBound());
		assertEquals(null, TYPE.getPattern());
		assertEqualsUnmodifiable(list(audit.getEventType(), audit.getFeatureType()), audit.getSourceTypes());

		assertEqualsUnmodifiable(list(
				TYPE.getThis(),
				amount,
				comment,
				audit
			), TYPE.getFeatures());
		assertEqualsUnmodifiable(list(
				eventType.getThis(),
				auditEventParent(),
				audit.getEventDate(),
				audit.getEventEvents(),
				audit.getEventAuthor(),
				audit.getEventNew()
			), eventType.getFeatures());
		assertEqualsUnmodifiable(list(
				featureType.getThis(),
				audit.getFeatureEvent(),
				audit.getFeatureFeatures(),
				audit.getFeature(),
				audit.getFeatureId(),
				audit.getFeatureUniqueConstraint(),
				audit.getFeatureName(),
				audit.getFeatureOld(),
				audit.getFeatureNew()
			), featureType.getFeatures());

		assertEquals(TYPE, audit.getType());
		assertEquals("audit", audit.getName());

		assertEquals("HistoryItem-audit-Event", eventType.getID());
		assertEquals(History.Event.class, eventType.getJavaClass());
		assertEquals(false, eventType.isBound());
		assertSame(audit, eventType.getPattern());
		assertEquals(null, eventType.getSupertype());
		assertEqualsUnmodifiable(list(), eventType.getSubtypes());
		assertEquals(false, eventType.isAbstract());
		assertEquals(Item.class, eventType.getThis().getValueClass().getSuperclass());
		assertEquals(eventType, eventType.getThis().getValueType());
		assertEquals(model, eventType.getModel());

		assertEquals("HistoryItem-audit-Feature", featureType.getID());
		assertEquals(History.Feature.class, featureType.getJavaClass());
		assertEquals(false, featureType.isBound());
		assertSame(audit, featureType.getPattern());
		assertEquals(null, featureType.getSupertype());
		assertEqualsUnmodifiable(list(), featureType.getSubtypes());
		assertEquals(false, featureType.isAbstract());
		assertEquals(Item.class, featureType.getThis().getValueClass().getSuperclass());
		assertEquals(featureType, featureType.getThis().getValueType());
		assertEquals(model, featureType.getModel());

		assertEquals(eventType, auditEventParent().getType());
		assertEquals(eventType, audit.getEventDate().getType());
		assertEquals(eventType, audit.getEventAuthor().getType());
		assertEquals(eventType, audit.getEventNew().getType());
		assertEquals(featureType, audit.getFeatureEvent().getType());
		assertEquals(featureType, audit.getFeature().getType());
		assertEquals(featureType, audit.getFeatureId().getType());
		assertEquals(featureType, audit.getFeatureUniqueConstraint().getType());
		assertEquals(featureType, audit.getFeatureName().getType());
		assertEquals(featureType, audit.getFeatureOld().getType());
		assertEquals(featureType, audit.getFeatureNew().getType());

		assertEquals("parent", auditEventParent().getName());
		assertEquals("date", audit.getEventDate().getName());
		assertEquals("author", audit.getEventAuthor().getName());
		assertEquals("new", audit.getEventNew().getName());
		assertEquals("event", audit.getFeatureEvent().getName());
		assertEquals("id", audit.getFeature().getName());
		assertEquals("id-id", audit.getFeatureId().getName());
		assertEquals("uniqueConstraint", audit.getFeatureUniqueConstraint().getName());
		assertEquals("name", audit.getFeatureName().getName());
		assertEquals("old", audit.getFeatureOld().getName());
		assertEquals("new", audit.getFeatureNew().getName());

		assertEqualsUnmodifiable(list(audit.getFeatureEvent(), audit.getFeatureId()), audit.getFeatureUniqueConstraint().getFields());

		assertTrue(eventType.isAssignableFrom(eventType));
		assertTrue(!eventType.isAssignableFrom(featureType));
		assertTrue(!TYPE.isAssignableFrom(eventType));
		assertTrue(!eventType.isAssignableFrom(TYPE));

		assertSame(HistoryItem.class, auditEventParent().getValueClass());
		assertSame(TYPE, auditEventParent().getValueType());
		assertSame(History.Event.class, audit.getFeatureEvent().getValueClass());
		assertSame(audit.getEventType(), audit.getFeatureEvent().getValueType());

		assertSame(auditEventParent(), audit.getEventEvents().getContainer());
		assertEquals(asList(PartOf.orderBy(audit.getEventDate())), audit.getEventEvents().getOrders());
		assertSame(audit.getFeatureEvent(), audit.getFeatureFeatures().getContainer());
		assertEquals(asList(), audit.getFeatureFeatures().getOrders());

		assertTrue(  eventType.isAnnotationPresent(Computed.class));
		assertTrue(featureType.isAnnotationPresent(Computed.class));

		assertEqualsUnmodifiable(list(audit), History.getHistories(TYPE));
		assertEqualsUnmodifiable(list(), History.getHistories(audit.getEventType()));

		final List<PartOf<?>> historyPartOfs = PartOf.getPartOfs(TYPE);
		assertEquals(1, historyPartOfs.size());
		final PartOf<?> eventPartOf = historyPartOfs.get(0);
		assertSame(eventType, eventPartOf.getType());
		assertEquals(list(eventPartOf), PartOf.getPartOfs(audit));
		final List<PartOf<?>> eventPartOfs = PartOf.getPartOfs(audit.getEventType());
		assertEquals(1, eventPartOfs.size());
		final PartOf<?> featurePartOf = eventPartOfs.get(0);
		assertSame(featureType, featurePartOf.getType());
		assertEquals(list(featurePartOf), PartOf.getPartOfs(eventPartOf));

		assertSerializedSame(audit, 377);

		// test persistence
		assertEquals("id", SchemaInfo.getColumnName(audit.getFeatureId()));
		assertNoUpdateCounterColumn(audit.getEventType());
		assertNoUpdateCounterColumn(audit.getFeatureType());

		assertEquals(list(), item.getAuditEvents());

		final Date before1 = new Date();
		final History.Event event1 = item.createAuditEvent("author1", true);
		final Date after1 = new Date();
		assertSame(audit, event1.getPattern());
		assertEquals(item, event1.getParent());
		assertWithin(before1, after1, event1.getDate());
		assertEquals("author1", event1.getAuthor());
		assertEquals(true, event1.isNew());
		assertEquals(list(), event1.getFeatures());
		assertEqualsUnmodifiable(list(event1), item.getAuditEvents());

		final Feature feature11 = event1.createFeature(amount, "Amount", valueOf(1.1), valueOf(2.2));
		assertSame(audit, feature11.getPattern());
		assertEquals(event1, feature11.getEvent());
		assertSame(amount, feature11.getFeature());
		assertEquals(amount.getID(), feature11.getFeatureID());
		assertEquals("Amount", feature11.getName());
		assertEquals("1.1", feature11.getOld());
		assertEquals("2.2", feature11.getNew());
		assertEquals(list(feature11), event1.getFeatures());

		final Feature feature12 = event1.createFeature(comment, "Comment", "blub", "blah");
		assertSame(audit, feature12.getPattern());
		assertEquals(event1, feature12.getEvent());
		assertSame(comment, feature12.getFeature());
		assertEquals(comment.getID(), feature12.getFeatureID());
		assertEquals("Comment", feature12.getName());
		assertEquals("blub", feature12.getOld());
		assertEquals("blah", feature12.getNew());
		assertEquals(list(feature11, feature12), event1.getFeatures());

		final Date before2 = new Date();
		final History.Event event2 = item.createAuditEvent("author2", false);
		final Date after2 = new Date();
		assertSame(audit, event2.getPattern());
		assertEquals(item, event2.getParent());
		assertWithin(before2, after2, event2.getDate());
		assertEquals("author2", event2.getAuthor());
		assertEquals(false, event2.isNew());
		assertEquals(list(), event2.getFeatures());
		assertEqualsUnmodifiable(list(event2, event1), item.getAuditEvents());
		assertEquals(event1, event1);
		assertTrue(!event1.equals(event2));

		// test string length exceeded
		final String LONG_STRING_BASE = "01234567890123456789012345678901234567890123456789012345678901234567890123456";
		final String LONG_STRING_SHORT = LONG_STRING_BASE + "...";
		final String LONG_STRING = LONG_STRING_SHORT + "789X";
		final Feature feature21 = event2.createFeature(comment, "Short", LONG_STRING, "newValue");
		assertEquals(LONG_STRING_SHORT, feature21.getOld());
		assertEquals("newValue", feature21.getNew());

		final Feature feature22 = event2.createFeature(amount, "Short", "oldValue", LONG_STRING);
		assertEquals("oldValue", feature22.getOld());
		assertEquals(LONG_STRING_SHORT, feature22.getNew());
	}
}
