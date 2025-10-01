package com.exedio.cope.pattern;

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.instrument.Visibility.PACKAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Condition;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.MyTemporaryFolder;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * @see MediaTypeConditionImageTest
 * @see MediaTypeConditionVideoTest
 */
public class MediaTypeConditionGzipTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(AnItem.TYPE);

	static
	{
		MODEL.enableSerialization(MediaTypeConditionGzipTest.class, "MODEL");
	}

	MediaTypeConditionGzipTest()
	{
		super(MODEL);
	}

	private final MyTemporaryFolder files = new MyTemporaryFolder();

	@Test
	void test() throws IOException
	{
		final AnItem item = new AnItem();
		item.setField(files.newPath(MediaTypeMediaTest.class, "thumbnail-test.png"), "image/png");
		final AnItem empty = new AnItem();

		final Condition mismatch = AnItem.field.bodyMismatchesContentTypeIfSupported();
		final Condition inverted = mismatch.not();
		assertEquals(List.of(), search(mismatch));
		assertEquals(isVault(mysql?List.of(item):List.of(), List.of(item, empty)), search(inverted)); // TODO bug, should be item, empty

		AnItem.field.getBody().set(item, files.newPath(MediaTypeMediaTest.class, "thumbnail-test.png"));
		assertEquals(isVault(List.of(), List.of(item)), search(mismatch)); // TODO bug, should be item
		assertEquals(isVault(List.of(), List.of(empty)), search(inverted)); // TODO bug, should be empty
	}

	// TODO should not depend on vault
	private static <T> T isVault(final T vaultValue, final T nonVaultValue)
	{
		return AnItem.field.getBody().getVaultBucket() != null ? vaultValue : nonVaultValue;
	}

	private static List<AnItem> search(final Condition condition)
	{
		return AnItem.TYPE.search(condition, AnItem.TYPE.getThis(), true);
	}

	@WrapperType(indent=2, comments=false)
	private static class AnItem extends Item
	{
		@Wrapper(wrap="set", parameters={Path.class, String.class}, visibility=PACKAGE)
		@Wrapper(wrap="*", visibility=NONE)
		static final Media field = new Media().optional().gzip();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private AnItem()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		protected AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setField(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
				throws
					java.io.IOException
		{
			AnItem.field.set(this,body,contentType);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		@com.exedio.cope.instrument.Generated
		protected AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
