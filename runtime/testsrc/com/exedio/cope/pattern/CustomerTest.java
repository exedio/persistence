package com.exedio.cope.pattern;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Model;
import com.exedio.cope.util.AssertionErrorJobContext;

public class CustomerTest extends AbstractRuntimeTest
{
	private static final Model MODEL = new Model(Customer.TYPE);

	public CustomerTest()
	{
		super(MODEL);
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

	public void testMigratePasswordOnChange()
	{
		final Customer customer = deleteOnTearDown(new Customer("111111", 1.1));
		assertNotNull(Customer.password.getOldHash().getHash(customer));
		assertNull(Customer.password.getNewHash().getHash(customer));

		assertTrue(customer.checkPassword("111111"));
		assertFalse(customer.checkPassword("222222"));

		customer.setPassword("222222");
		assertNull(Customer.password.getOldHash().getHash(customer));
		assertNotNull(Customer.password.getNewHash().getHash(customer));
		assertTrue(customer.checkPassword("222222"));
		assertFalse(customer.checkPassword("333333"));
		assertFalse(customer.checkPassword("111111"));
	}

	public void testMigratePasswordAutomatically()
	{
		final Customer customerA = deleteOnTearDown(new Customer("111111A", 1.1));
		final Customer customerB = deleteOnTearDown(new Customer("111111B", 1.1));
		final Customer customerX = deleteOnTearDown(new Customer("111111X"));
		assertNotNull(Customer.password.getOldHash().getHash(customerA));
		assertNotNull(Customer.password.getOldHash().getHash(customerB));
		assertNull(Customer.password.getOldHash().getHash(customerX));
		assertNull(Customer.password.getNewHash().getHash(customerA));
		assertNull(Customer.password.getNewHash().getHash(customerB));
		assertNotNull(Customer.password.getNewHash().getHash(customerX));
		model.commit();

		{
			final MyJobContext ctx = new MyJobContext();
			Customer.migratePassword(ctx);
			assertEquals(2, ctx.progress);
		}

		model.startTransaction("test result");
		assertNull(Customer.password.getOldHash().getHash(customerA));
		assertNull(Customer.password.getOldHash().getHash(customerB));
		assertNull(Customer.password.getOldHash().getHash(customerX));
		assertNotNull(Customer.password.getNewHash().getHash(customerA));
		assertNotNull(Customer.password.getNewHash().getHash(customerB));
		assertNotNull(Customer.password.getNewHash().getHash(customerX));
		assertTrue(customerA.checkPassword("111111A"));
		assertTrue(customerB.checkPassword("111111B"));
		assertTrue(customerX.checkPassword("111111X"));
		model.commit();

		{
			final MyJobContext ctx = new MyJobContext();
			Customer.migratePassword(ctx);
			assertEquals(0, ctx.progress);
		}

		model.startTransaction("test result");
		assertTrue(customerA.checkPassword("111111A"));
		assertTrue(customerB.checkPassword("111111B"));
		assertTrue(customerX.checkPassword("111111X"));
	}
}
