
package com.exedio.cope.lib;

import com.exedio.cope.testmodel.EmptyItem;
import com.exedio.cope.testmodel.QualifiedEmptyQualifier;
import com.exedio.cope.testmodel.QualifiedItem;
import com.exedio.cope.testmodel.QualifiedStringQualifier;

public class QualifierTest extends DatabaseLibTest
{
	QualifiedItem item;
	EmptyItem key1;
	EmptyItem key2;
	
	public void setUp() throws Exception
	{
		super.setUp();
		item = new QualifiedItem();
		key1 = new EmptyItem();
		key2 = new EmptyItem();
	}
	
	public void tearDown() throws Exception
	{
		item.delete();
		key2.delete();
		key1.delete();
		super.tearDown();
	}
	
	public void testQualified()
		throws UniqueViolationException, NotNullViolationException, IntegrityViolationException,
			LengthViolationException, ReadOnlyViolationException
	{
		assertEquals(QualifiedItem.qualifier.getParent(), QualifiedEmptyQualifier.parent);
		assertEquals(QualifiedItem.qualifier.getKey(), QualifiedEmptyQualifier.key);
		assertEquals(QualifiedItem.qualifier.getQualifyUnique(), QualifiedEmptyQualifier.qualifyUnique);
		assertEquals(QualifiedItem.qualifier, QualifiedEmptyQualifier.qualifyUnique.getQualifier());
		assertEquals(list(QualifiedItem.qualifier, QualifiedItem.stringQualifier), QualifiedItem.TYPE.getQualifiers());
		assertEquals(list(QualifiedEmptyQualifier.qualifiedA, QualifiedEmptyQualifier.qualifiedB),
							QualifiedItem.qualifier.getAttributes());

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

		qitem2.delete();
		qitem1.delete();

		assertEquals(null, item.getQualifiedA("key1"));
		assertEquals(null, item.getQualifiedB("key1"));
		assertEquals(null, item.getQualifiedA("key2"));
		assertEquals(null, item.getQualifiedB("key2"));

		item.setQualifiedB("key1", new Integer(4));
		assertEquals(null, item.getQualifiedA("key1"));
		assertEquals(new Integer(4), item.getQualifiedB("key1"));
		assertEquals(null, item.getQualifiedA("key2"));
		assertEquals(null, item.getQualifiedB("key2"));

		item.setQualifiedA("key1", new Integer(8));
		assertEquals(new Integer(8), item.getQualifiedA("key1"));
		assertEquals(new Integer(4), item.getQualifiedB("key1"));
		assertEquals(null, item.getQualifiedA("key2"));
		assertEquals(null, item.getQualifiedB("key2"));

		item.setQualifiedB("key2", new Integer(10));
		assertEquals(new Integer(8), item.getQualifiedA("key1"));
		assertEquals(new Integer(4), item.getQualifiedB("key1"));
		assertEquals(null, item.getQualifiedA("key2"));
		assertEquals(new Integer(10), item.getQualifiedB("key2"));
		
		QualifiedStringQualifier.findByQualifyUnique(item, "key1").delete();
		QualifiedStringQualifier.findByQualifyUnique(item, "key2").delete();
	}
	
}
