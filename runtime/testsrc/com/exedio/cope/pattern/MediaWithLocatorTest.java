package com.exedio.cope.pattern;

import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class MediaWithLocatorTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(MyItem.TYPE);

	static
	{
		MODEL.enableSerialization(MediaWithLocatorTest.class, "MODEL");
	}
	public MediaWithLocatorTest()
	{
		super(MODEL);
	}

	@Test
	void locator()
	{
		final MyItem withoutData = new MyItem();
		final MyItem withData = new MyItem();
		withData.setWithLocator(new byte[]{1, 2, 3}, "content/type");
		withData.setNoLocator(new byte[]{1, 2, 3}, "content/type");
		final MyItem withImage = new MyItem();
		withImage.setWithLocator(new byte[]{1, 2, 3}, MediaType.PNG);
		withImage.setNoLocator(new byte[]{1, 2, 3}, MediaType.PNG);

		assertEquals(null, MyItem.withLocator.getLocator(withoutData));
		assertFails(
				() -> MyItem.noLocator.getLocator(withoutData),
				UnsupportedOperationException.class,
				"not supported for " + MyItem.noLocator + " because isWithLocator()==false"
		);
		assertFails(
				() -> MyItem.noLocator.getURL(withoutData),
				UnsupportedOperationException.class,
				"not supported for " + MyItem.noLocator + " because isWithLocator()==false"
		);
		assertEquals(null, MyItem.thumbOfNoLocator.getLocator(withoutData));
		assertFails(
				() -> MyItem.noLocatorThumb.getLocator(withoutData),
				UnsupportedOperationException.class,
				"not supported for " + MyItem.noLocatorThumb + " because isWithLocator()==false"
		);
		assertFails(
				() -> MyItem.thumbOfNoLocator.getLocatorWithFallbackToSource(withoutData),
				UnsupportedOperationException.class,
				"not supported for " + MyItem.noLocator + " because isWithLocator()==false"
		);
		assertFails(
				() -> MyItem.noLocatorThumb.getLocatorWithFallbackToSource(withoutData),
				UnsupportedOperationException.class,
				"not supported for " + MyItem.noLocatorThumb + " because isWithLocator()==false"
		);

		assertEquals("MyItem/withLocator/" + withData, MyItem.withLocator.getLocator(withData).getPath());
		assertFails(
				() -> MyItem.noLocator.getLocator(withData),
				UnsupportedOperationException.class,
				"not supported for " + MyItem.noLocator + " because isWithLocator()==false"
		);
		assertEquals(null, MyItem.thumbOfNoLocator.getLocator(withData));
		assertFails(
				() -> MyItem.noLocatorThumb.getLocator(withData),
				UnsupportedOperationException.class,
				"not supported for " + MyItem.noLocatorThumb + " because isWithLocator()==false"
		);
		assertFails(
				() -> MyItem.thumbOfNoLocator.getLocatorWithFallbackToSource(withData),
				UnsupportedOperationException.class,
				"not supported for " + MyItem.noLocator + " because isWithLocator()==false"
		);
		assertFails(
				() -> MyItem.noLocatorThumb.getLocatorWithFallbackToSource(withData).getPath(),
				UnsupportedOperationException.class,
				"not supported for " + MyItem.noLocatorThumb + " because isWithLocator()==false"
		);

		assertEquals("MyItem/withLocator/" + withImage + ".png", MyItem.withLocator.getLocator(withImage).getPath());
		assertFails(
				() -> MyItem.noLocator.getLocator(withImage),
				UnsupportedOperationException.class,
				"not supported for " + MyItem.noLocator + " because isWithLocator()==false"
		);
		assertEquals("MyItem/thumbOfNoLocator/" + withImage + ".jpg", MyItem.thumbOfNoLocator.getLocator(withImage).getPath());
		assertFails(
				() -> MyItem.noLocatorThumb.getLocator(withImage),
				UnsupportedOperationException.class,
				"not supported for " + MyItem.noLocatorThumb + " because isWithLocator()==false"
		);
		assertEquals("MyItem/thumbOfNoLocator/" + withImage + ".jpg", MyItem.thumbOfNoLocator.getLocatorWithFallbackToSource(withImage).getPath());
		assertFails(
				() -> MyItem.noLocatorThumb.getLocatorWithFallbackToSource(withImage).getPath(),
				UnsupportedOperationException.class,
				"not supported for " + MyItem.noLocatorThumb + " because isWithLocator()==false"
		);
	}

	@WrapperType(indent=2, comments=false)
	static class MyItem extends Item
	{
		static final Media withLocator = new Media().optional();

		static final Media noLocator = new Media().withLocator(false).optional();

		static final MediaThumbnail thumbOfNoLocator = new MediaThumbnail(noLocator, 100, 100);

		static final MediaThumbnail noLocatorThumb = new MediaThumbnail(withLocator, 100, 100).withLocator(false);

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		MyItem()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		final java.lang.String getWithLocatorURL()
		{
			return MyItem.withLocator.getURL(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		final com.exedio.cope.pattern.MediaPath.Locator getWithLocatorLocator()
		{
			return MyItem.withLocator.getLocator(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		final java.lang.String getWithLocatorContentType()
		{
			return MyItem.withLocator.getContentType(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final boolean isWithLocatorNull()
		{
			return MyItem.withLocator.isNull(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		final java.util.Date getWithLocatorLastModified()
		{
			return MyItem.withLocator.getLastModified(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final long getWithLocatorLength()
		{
			return MyItem.withLocator.getLength(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		final byte[] getWithLocatorBody()
		{
			return MyItem.withLocator.getBody(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void getWithLocatorBody(@javax.annotation.Nonnull final java.io.OutputStream body)
				throws
					java.io.IOException
		{
			MyItem.withLocator.getBody(this,body);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void getWithLocatorBody(@javax.annotation.Nonnull final java.nio.file.Path body)
				throws
					java.io.IOException
		{
			MyItem.withLocator.getBody(this,body);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@java.lang.Deprecated
		final void getWithLocatorBody(@javax.annotation.Nonnull final java.io.File body)
				throws
					java.io.IOException
		{
			MyItem.withLocator.getBody(this,body);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setWithLocator(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value withLocator)
				throws
					java.io.IOException
		{
			MyItem.withLocator.set(this,withLocator);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setWithLocator(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
		{
			MyItem.withLocator.set(this,body,contentType);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setWithLocator(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
				throws
					java.io.IOException
		{
			MyItem.withLocator.set(this,body,contentType);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setWithLocator(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
				throws
					java.io.IOException
		{
			MyItem.withLocator.set(this,body,contentType);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setWithLocator(@javax.annotation.Nullable final java.io.File body,@javax.annotation.Nullable final java.lang.String contentType)
				throws
					java.io.IOException
		{
			MyItem.withLocator.set(this,body,contentType);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		final java.lang.String getNoLocatorContentType()
		{
			return MyItem.noLocator.getContentType(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final boolean isNoLocatorNull()
		{
			return MyItem.noLocator.isNull(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		final java.util.Date getNoLocatorLastModified()
		{
			return MyItem.noLocator.getLastModified(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final long getNoLocatorLength()
		{
			return MyItem.noLocator.getLength(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		final byte[] getNoLocatorBody()
		{
			return MyItem.noLocator.getBody(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void getNoLocatorBody(@javax.annotation.Nonnull final java.io.OutputStream body)
				throws
					java.io.IOException
		{
			MyItem.noLocator.getBody(this,body);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void getNoLocatorBody(@javax.annotation.Nonnull final java.nio.file.Path body)
				throws
					java.io.IOException
		{
			MyItem.noLocator.getBody(this,body);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@java.lang.Deprecated
		final void getNoLocatorBody(@javax.annotation.Nonnull final java.io.File body)
				throws
					java.io.IOException
		{
			MyItem.noLocator.getBody(this,body);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setNoLocator(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value noLocator)
				throws
					java.io.IOException
		{
			MyItem.noLocator.set(this,noLocator);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setNoLocator(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
		{
			MyItem.noLocator.set(this,body,contentType);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setNoLocator(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
				throws
					java.io.IOException
		{
			MyItem.noLocator.set(this,body,contentType);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setNoLocator(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
				throws
					java.io.IOException
		{
			MyItem.noLocator.set(this,body,contentType);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setNoLocator(@javax.annotation.Nullable final java.io.File body,@javax.annotation.Nullable final java.lang.String contentType)
				throws
					java.io.IOException
		{
			MyItem.noLocator.set(this,body,contentType);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		final java.lang.String getThumbOfNoLocatorURL()
		{
			return MyItem.thumbOfNoLocator.getURL(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		final com.exedio.cope.pattern.MediaPath.Locator getThumbOfNoLocatorLocator()
		{
			return MyItem.thumbOfNoLocator.getLocator(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		final byte[] getThumbOfNoLocator()
				throws
					java.io.IOException
		{
			return MyItem.thumbOfNoLocator.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		final byte[] getNoLocatorThumb()
				throws
					java.io.IOException
		{
			return MyItem.noLocatorThumb.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
