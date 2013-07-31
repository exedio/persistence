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
			// nop
		}
	}

	public void testMigratePasswordOnChange()
	{
		final Customer customer = deleteOnTearDown(new Customer("newnew"));
		customer.set(Customer.password.getOldHash().map("111111"), Customer.password.getNewHash().map(null));
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
		final Customer customerA = deleteOnTearDown(new Customer("newnew"));
		final Customer customerB = deleteOnTearDown(new Customer("newnew"));
		customerA.set(Customer.password.getOldHash().map("111111A"), Customer.password.getNewHash().map(null));
		customerB.set(Customer.password.getOldHash().map("111111B"), Customer.password.getNewHash().map(null));
		model.commit();

		Customer.migratePassword(new MyJobContext());

		model.startTransaction("test result");
		assertNull(Customer.password.getOldHash().getHash(customerA));
		assertNull(Customer.password.getOldHash().getHash(customerB));
		assertNotNull(Customer.password.getNewHash().getHash(customerA));
		assertNotNull(Customer.password.getNewHash().getHash(customerB));
		assertTrue(customerA.checkPassword("111111A"));
		assertTrue(customerB.checkPassword("111111B"));
	}
}
