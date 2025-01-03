package com.exedio.cope.pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Condition;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.Visibility;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.MyTemporaryFolder;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MediaTypeConditionTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(MediaItem.TYPE);

	static
	{
		MODEL.enableSerialization(MediaTypeConditionTest.class, "MODEL");
	}

	MediaTypeConditionTest()
	{
		super(MODEL);
	}

	private MediaItem video, image, empty;
	private final MyTemporaryFolder files = new MyTemporaryFolder();

	@BeforeEach
	void setUp() throws IOException
	{
		video = new MediaItem();
		video.setMp4(files.newPath(MediaTypeMediaTest.class, "teapot.mp4"), "video/mp4");

		image = new MediaItem();
		image.setPng(files.newPath(MediaTypeConditionTest.class, "thumbnail-test.png"), "image/png");

		empty = new MediaItem();
	}

	@Test
	void testVideo() throws IOException
	{
		final Condition mismatch = MediaItem.mp4.bodyMismatchesContentTypeIfSupported();
		final Condition inverted = mismatch.not();

		assertEquals(List.of(), search(mismatch));
		assertEquals(isVault(List.of(video), List.of(video, image, empty)), search(inverted)); // TODO bug, should be video, image, empty

		video.setMp4(files.newPath(MediaTypeMediaTest.class, "teapot_mp42.mp4"), "video/mp4");

		assertEquals(List.of(), search(mismatch));
		assertEquals(isVault(List.of(), List.of(video, image, empty)), search(inverted)); // TODO bug, should be video, image, empty

		video.setMp4(files.newPath(MediaTypeMediaTest.class, "teapot.ogg"), "video/mp4");

		assertEquals(isVault(List.of(), List.of(video)), search(mismatch)); // TODO bug, should be video
		assertEquals(isVault(List.of(), List.of(image, empty)), search(inverted)); // TODO bug, should be image, empty
	}

	@Test
	void testImage() throws IOException
	{
		final Condition mismatch = MediaItem.png.bodyMismatchesContentTypeIfSupported();
		final Condition inverted = mismatch.not();
		assertEquals(List.of(), search(mismatch));
		assertEquals(isVault(mysql?List.of(image):List.of(), List.of(video, image, empty)), search(inverted)); // TODO bug, should be video, image, empty

		image.setPng(files.newPath(MediaTypeConditionTest.class, "thumbnail-test.jpg"), "image/png");
		assertEquals(isVault(List.of(), List.of(image)), search(mismatch)); // TODO bug, should be image
		assertEquals(isVault(List.of(), List.of(video, empty)), search(inverted)); // TODO bug, should be video, empty
	}

	// TODO should not depend on vault
	private static <T> T isVault(final T vaultValue, final T nonVaultValue)
	{
		return MediaItem.mp4.getBody().getVaultBucket() != null ? vaultValue : nonVaultValue;
	}

	private static List<MediaItem> search(final Condition condition)
	{
		return MediaItem.TYPE.search(condition, MediaItem.TYPE.getThis(), true);
	}

	@WrapperType(indent = 2, comments = false)
	private static class MediaItem extends Item
	{
		@Wrapper(wrap = "set", parameters = {Path.class, String.class}, visibility=Visibility.PACKAGE)
		@Wrapper(wrap = "*", visibility=Visibility.NONE)
		static final Media mp4 = new Media().optional().contentType("video/mp4");
		
		@Wrapper(wrap = "set", parameters = {Path.class, String.class}, visibility=Visibility.PACKAGE)
		@Wrapper(wrap = "*", visibility=Visibility.NONE)
		static final Media png = new Media().optional().contentType("image/png");

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private MediaItem()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		protected MediaItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setMp4(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
				throws
					java.io.IOException
		{
			MediaItem.mp4.set(this,body,contentType);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setPng(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
				throws
					java.io.IOException
		{
			MediaItem.png.set(this,body,contentType);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MediaItem> TYPE = com.exedio.cope.TypesBound.newType(MediaItem.class,MediaItem::new);

		@com.exedio.cope.instrument.Generated
		protected MediaItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
