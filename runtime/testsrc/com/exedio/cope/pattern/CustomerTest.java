package com.exedio.cope.pattern;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.CheckViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.util.AssertionErrorJobContext;

public class CustomerTest extends AbstractRuntimeTest
{
	private static final Model MODEL = new Model(Customer.TYPE);

	public CustomerTest()
	{
		super(MODEL);
	}

	public void testSetNull()
	{
		final Customer item = deleteOnTearDown(new Customer("111111", 1.1));

		try
		{
			item.setPassword(null);
			fail();
		}
		catch(final CheckViolationException e) // TODO
		{
			assertEquals("check violation on " + item + " for Customer.password-xor", e.getMessage());
		}
		try
		{
			item.set(Customer.password.map(null));
			fail();
		}
		catch(final CheckViolationException e) // TODO
		{
			assertEquals("check violation on " + item + " for Customer.password-xor", e.getMessage());
		}
		try
		{
			new Customer(null, 1.1);
			fail();
		}
		catch(final CheckViolationException e) // TODO
		{
			assertEquals("check violation for Customer.password-xor", e.getMessage());
		}
	}

	public void testMigratePasswordOnChange()
	{
		final Customer item = deleteOnTearDown(new Customer("111111", 1.1));
		assertNotNull(Customer.password.getOldHash().getHash(item));
		assertNull(Customer.password.getNewHash().getHash(item));

		assertTrue(item.checkPassword("111111"));
		assertFalse(item.checkPassword("222222"));

		item.setPassword("222222");
		assertNull(Customer.password.getOldHash().getHash(item));
		assertNotNull(Customer.password.getNewHash().getHash(item));
		assertTrue(item.checkPassword("222222"));
		assertFalse(item.checkPassword("333333"));
		assertFalse(item.checkPassword("111111"));
	}

	public void testMigratePasswordAutomatically()
	{
		final Customer itemA = deleteOnTearDown(new Customer("111111A", 1.1));
		final Customer itemB = deleteOnTearDown(new Customer("111111B", 1.1));
		final Customer itemX = deleteOnTearDown(new Customer("111111X"));
		assertNotNull(Customer.password.getOldHash().getHash(itemA));
		assertNotNull(Customer.password.getOldHash().getHash(itemB));
		assertNull(Customer.password.getOldHash().getHash(itemX));
		assertNull(Customer.password.getNewHash().getHash(itemA));
		assertNull(Customer.password.getNewHash().getHash(itemB));
		assertNotNull(Customer.password.getNewHash().getHash(itemX));
		model.commit();

		{
			final MyJobContext ctx = new MyJobContext();
			Customer.migratePassword(ctx);
			assertEquals(2, ctx.progress);
		}

		model.startTransaction("test result");
		assertNull(Customer.password.getOldHash().getHash(itemA));
		assertNull(Customer.password.getOldHash().getHash(itemB));
		assertNull(Customer.password.getOldHash().getHash(itemX));
		assertNotNull(Customer.password.getNewHash().getHash(itemA));
		assertNotNull(Customer.password.getNewHash().getHash(itemB));
		assertNotNull(Customer.password.getNewHash().getHash(itemX));
		assertTrue(itemA.checkPassword("111111A"));
		assertTrue(itemB.checkPassword("111111B"));
		assertTrue(itemX.checkPassword("111111X"));
		model.commit();

		{
			final MyJobContext ctx = new MyJobContext();
			Customer.migratePassword(ctx);
			assertEquals(0, ctx.progress);
		}

		model.startTransaction("test result");
		assertTrue(itemA.checkPassword("111111A"));
		assertTrue(itemB.checkPassword("111111B"));
		assertTrue(itemX.checkPassword("111111X"));
	}

	private static class MyJobContext extends AssertionErrorJobContext
	{
		int progress = 0;

		MyJobContext()
		{
			// make package private
		}

		@Override
		public void stopIfRequested()
		{
			// nop
		}

		@Override
		public boolean supportsProgress()
		{
			return true;
		}

		@Override
		public void incrementProgress()
		{
			progress++;
		}
	}
}
