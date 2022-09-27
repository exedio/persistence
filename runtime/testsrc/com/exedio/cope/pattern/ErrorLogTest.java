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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.pattern.MediaCounter.counter;
import static com.exedio.cope.tojunit.Assert.assertWithin;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import java.io.IOException;
import java.util.Date;
import org.junit.jupiter.api.Test;

public class ErrorLogTest
{
	@Test void testValues()
	{
		final ErrorLog l = new ErrorLog(3, counter("s", "d"));
		l.onMount(MyItem.values);
		l.count(new Request(0), null);

		assertEquals(1, l.get());
		final MediaRequestLog l0 = l.getLogs().get(0);
		assertEquals("RemoteAddr0", l0.getRemoteAddr());
		assertEquals(false, l0.isSecure());
		assertEquals("PathInfo0", l0.getPathInfo());
		assertNotNull(l0.getDate());
		assertEquals("QueryString0", l0.getQueryString());
		assertEquals("HeaderHost0", l0.getHost());
		assertEquals("HeaderReferer0", l0.getReferer());
		assertEquals("HeaderUser-Agent0", l0.getUserAgent());
	}

	@Test void testSecure()
	{
		final ErrorLog l = new ErrorLog(3, counter("s", "d"));
		l.onMount(MyItem.secure);
		l.count(new Request(0, true), null);

		assertEquals(1, l.get());
		final MediaRequestLog l0 = l.getLogs().get(0);
		assertEquals(true, l0.isSecure());
		assertEquals("PathInfo0", l0.getPathInfo());
	}

	@Test void testOverflow()
	{
		final ErrorLog l = new ErrorLog(3, counter("testOverflow", "d"));
		l.onMount(MyItem.overflow);
		assertEquals(0, l.get());
		assertEquals(list(), l.getLogs());

		l.count(new Request(0), null);
		assertEquals(1, l.get());
		final MediaRequestLog l0 = l.getLogs().get(0);
		assertEquals("PathInfo0", l0.getPathInfo());
		assertEquals(list(l0), l.getLogs());

		l.count(new Request(1), null);
		assertEquals(2, l.get());
		final MediaRequestLog l1 = l.getLogs().get(1);
		assertEquals("PathInfo1", l1.getPathInfo());
		assertEquals(list(l0, l1), l.getLogs());

		l.count(new Request(2), null);
		assertEquals(3, l.get());
		final MediaRequestLog l2 = l.getLogs().get(2);
		assertEquals("PathInfo2", l2.getPathInfo());
		assertEquals(list(l0, l1, l2), l.getLogs());

		l.count(new Request(3), null);
		assertEquals(4, l.get());
		final MediaRequestLog l3 = l.getLogs().get(2);
		assertEquals("PathInfo3", l3.getPathInfo());
		assertEquals(list(l1, l2, l3), l.getLogs()); // l0 is gone

		l.count(new Request(4), null);
		assertEquals(5, l.get());
		final MediaRequestLog l4 = l.getLogs().get(2);
		assertEquals("PathInfo4", l4.getPathInfo());
		assertEquals(list(l2, l3, l4), l.getLogs()); // l1 is gone
	}

	@Test void testDate()
	{
		final ErrorLog l = new ErrorLog(3, counter("s", "d"));

		final Date before = new Date();
		l.count(new Request(0), null);
		final Date after = new Date();

		final MediaRequestLog l0 = l.getLogs().get(0);
		assertEquals("PathInfo0", l0.getPathInfo());
		assertWithin(before, after, l0.getDate());
	}

	@Test void testException()
	{
		final IOException e0 = new IOException();
		final NullPointerException e1 = new NullPointerException();

		final ErrorLog l = new ErrorLog(10, counter("s", "d"));
		l.count(new Request(0), e0);
		l.count(new Request(1), e1);
		l.count(new Request(2), null);

		final MediaRequestLog l0 = l.getLogs().get(0);
		final MediaRequestLog l1 = l.getLogs().get(1);
		final MediaRequestLog l2 = l.getLogs().get(2);

		assertEquals("PathInfo0", l0.getPathInfo());
		assertEquals("PathInfo1", l1.getPathInfo());
		assertEquals("PathInfo2", l2.getPathInfo());

		assertEquals(e0,   l0.getException());
		assertEquals(e1,   l1.getException());
		assertEquals(null, l2.getException());
	}

	static class Request extends AssertionFailedHttpServletRequest
	{
		private final int n;
		private final boolean secure;

		Request(final int n)
		{
			this.n = n;
			this.secure = false;
		}

		Request(final int n, final boolean secure)
		{
			this.n = n;
			this.secure = secure;
		}

		@Override
		public String getRemoteAddr()
		{
			return "RemoteAddr" + n;
		}

		@Override
		public boolean isSecure()
		{
			return secure;
		}

		@Override
		public String getPathInfo()
		{
			return "PathInfo" + n;
		}

		@Override
		public String getQueryString()
		{
			return "QueryString" + n;
		}

		@Override
		public String getHeader(final String name)
		{
			if("Host".equals(name) || "Referer".equals(name) || "User-Agent".equals(name))
				return "Header" + name + n;

			throw new AssertionError();
		}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class MyItem extends Item
	{
		@WrapperIgnore static final Media values   = new Media();
		@WrapperIgnore static final Media secure   = new Media();
		@WrapperIgnore static final Media overflow = new Media();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

