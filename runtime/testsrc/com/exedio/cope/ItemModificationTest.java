package com.exedio.cope;

import static com.exedio.cope.instrument.Visibility.DEFAULT;
import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.misc.DatabaseListener;
import com.exedio.cope.tojunit.ConnectionRule;
import com.exedio.cope.tojunit.SI;
import com.exedio.cope.tojunit.TestSources;
import com.exedio.cope.util.Properties;
import com.exedio.cope.util.Sources;
import com.exedio.cope.vaultmock.VaultMockService;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class ItemModificationTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(ModifiableParent.TYPE, UnmodifiableParent.TYPE, MyItem.TYPE);

	private final ConnectionRule connection = new ConnectionRule(model);

	protected ItemModificationTest()
	{
		super(MODEL);
	}

	@Override
	public Properties.Source override(final Properties.Source s)
	{
		return Sources.cascade(
				TestSources.single("vault", "true"),
				TestSources.single("vault.isAppliedToAllFields", false),
				TestSources.single("vault.default.service", VaultMockService.class),
				s);
	}

	@AfterEach
	void clearDatabaseListener()
	{
		model.setDatabaseListener(null);
	}

	@Test
	void checkModel()
	{
		assertEquals(false, model.getConnectProperties().vault.isAppliedToAllFields());
		assertEquals(null, MyItem.blobData.getVaultBucket());
		assertEquals("default", MyItem.vaultData.getVaultBucket());
	}

	private <E, F extends Feature & Settable<E>> void check(final F settable, final E initial, final E changeTo,
			final int expectedUpdateCounterValue, final List<Update> expectedUpdates) throws SQLException
	{
		check(
				new SetValue<?>[]{SetValue.map(settable, initial)},
				new SetValue<?>[]{SetValue.map(settable, changeTo)},
				expectedUpdateCounterValue, expectedUpdates
		);
	}

	private void check(final SetValue<?>[] initial, final SetValue<?>[] changeTo,
			final int expectedUpdateCounterValue, final List<Update> expectedUpdates) throws SQLException
	{
		final MyItem item = new MyItem(initial);
		commit();
		assertEquals(0, queryUpdateCount(item, MyItem.TYPE));
		assertEquals(null, UnmodifiableParent.TYPE.getTable().updateCounter);
		assertEquals(0, queryUpdateCount(item, ModifiableParent.TYPE));

		startTransaction();
		item.getField(); // load item
		assertEquals(0, getEntityUpdateCount(item));
		final UpdateCounter updateCounter = new UpdateCounter();
		model.setDatabaseListener(updateCounter);
		item.set(changeTo);
		assertEquals(expectedUpdates, updateCounter.tables);
		assertEquals(expectedUpdateCounterValue, getEntityUpdateCount(item));
		commit();
		assertEquals(expectedUpdateCounterValue, queryUpdateCount(item, MyItem.TYPE));
		assertEquals(expectedUpdateCounterValue, queryUpdateCount(item, ModifiableParent.TYPE));
	}

	@SuppressWarnings("deprecation")
	private static int getEntityUpdateCount(final Item item)
	{
		return item.getEntity().getUpdateCount();
	}

	@Test
	void fieldNullToA() throws SQLException
	{
		check(
				MyItem.field, null, "A",
				1,
				List.of(
						new Update(ModifiableParent.TYPE, true, ModifiableParent.modifiableAtParent),
						new Update(MyItem.TYPE, true, MyItem.field, MyItem.reference, MyItem.vaultData)
				)
		);
	}

	@Test
	void fieldNullToNull() throws SQLException
	{
		check(
				MyItem.field, null, null,
				1,
				List.of(
						new Update(ModifiableParent.TYPE, true, ModifiableParent.modifiableAtParent),
						new Update(MyItem.TYPE, true, MyItem.field, MyItem.reference, MyItem.vaultData)
				)
		);
	}

	@Test
	void fieldAToNull() throws SQLException
	{
		check(
				MyItem.field, "A", null,
				1,
				List.of(
						new Update(ModifiableParent.TYPE, true, ModifiableParent.modifiableAtParent),
						new Update(MyItem.TYPE, true, MyItem.field, MyItem.reference, MyItem.vaultData)
				)
		);
	}

	@Test
	void fieldAToA() throws SQLException
	{
		check(
				MyItem.field, "A", "A",
				1,
				List.of(
						new Update(ModifiableParent.TYPE, true, ModifiableParent.modifiableAtParent),
						new Update(MyItem.TYPE, true, MyItem.field, MyItem.reference, MyItem.vaultData)
				)
		);
	}

	@Test
	void modifiableAtParentOneToTwo() throws SQLException
	{
		check(
				ModifiableParent.modifiableAtParent, 1, 2,
				1,
				List.of(
						new Update(ModifiableParent.TYPE, true, ModifiableParent.modifiableAtParent),
						new Update(MyItem.TYPE, true, MyItem.field, MyItem.reference, MyItem.vaultData)
				)
		);
	}

	@Test
	void modifiableAtParentOneToOne() throws SQLException
	{
		check(
				ModifiableParent.modifiableAtParent, 1, 1,
				1,
				List.of(
						new Update(ModifiableParent.TYPE, true, ModifiableParent.modifiableAtParent),
						new Update(MyItem.TYPE, true, MyItem.field, MyItem.reference, MyItem.vaultData)
				)
		);
	}

	@Test
	void referenceNullToA() throws SQLException
	{
		check(
				MyItem.reference, null, new MyItem(),
				1,
				List.of(
						new Update(ModifiableParent.TYPE, true, ModifiableParent.modifiableAtParent),
						new Update(MyItem.TYPE, true, MyItem.field, MyItem.reference, MyItem.vaultData)
				)
		);
	}

	@Test
	void referenceNullToNull() throws SQLException
	{
		check(
				MyItem.reference, null, null,
				1,
				List.of(
						new Update(ModifiableParent.TYPE, true, ModifiableParent.modifiableAtParent),
						new Update(MyItem.TYPE, true, MyItem.field, MyItem.reference, MyItem.vaultData)
				)
		);
	}

	@Test
	void referenceAToNull() throws SQLException
	{
		check(
				MyItem.reference, new MyItem(), null,
				1,
				List.of(
						new Update(ModifiableParent.TYPE, true, ModifiableParent.modifiableAtParent),
						new Update(MyItem.TYPE, true, MyItem.field, MyItem.reference, MyItem.vaultData)
				)
		);
	}

	@Test
	void referenceAToA() throws SQLException
	{
		final MyItem item = new MyItem();
		check(
				MyItem.reference, item, item,
				1,
				List.of(
						new Update(ModifiableParent.TYPE, true, ModifiableParent.modifiableAtParent),
						new Update(MyItem.TYPE, true, MyItem.field, MyItem.reference, MyItem.vaultData)
				)
		);
	}

	@Test
	void blobDataNullToX() throws SQLException
	{
		check(
				MyItem.blobData, null, DataField.toValue(new byte[]{1}),
				0,
				List.of(
						new Update(ModifiableParent.TYPE, false, ModifiableParent.modifiableAtParent),
						new Update(MyItem.TYPE, false, MyItem.field, MyItem.reference, MyItem.blobData, MyItem.vaultData)
				)
		);
	}

	@Test
	void blobDataNullToNull() throws SQLException
	{
		check(
				MyItem.blobData, null, null,
				0,
				List.of(
						new Update(ModifiableParent.TYPE, false, ModifiableParent.modifiableAtParent),
						new Update(MyItem.TYPE, false, MyItem.field, MyItem.reference, MyItem.blobData, MyItem.vaultData)
				)
		);
	}

	@Test
	void blobDataXToNull() throws SQLException
	{
		check(
				MyItem.blobData, DataField.toValue(new byte[]{1}), null,
				0,
				List.of(
						new Update(ModifiableParent.TYPE, false, ModifiableParent.modifiableAtParent),
						new Update(MyItem.TYPE, false, MyItem.field, MyItem.reference, MyItem.blobData, MyItem.vaultData)
				)
		);
	}

	@Test
	void blobDataXToX() throws SQLException
	{
		check(
				MyItem.blobData, DataField.toValue(new byte[]{1}), DataField.toValue(new byte[]{1}),
				0,
				List.of(
						new Update(ModifiableParent.TYPE, false, ModifiableParent.modifiableAtParent),
						new Update(MyItem.TYPE, false, MyItem.field, MyItem.reference, MyItem.blobData, MyItem.vaultData)
				)
		);
	}

	@Test
	void changeFieldAndBlob() throws SQLException
	{
		check(
				new SetValue<?>[]{
						SetValue.map(MyItem.field, "A"),
						SetValue.map(MyItem.blobData, DataField.toValue(new byte[]{1}))
				},
				new SetValue<?>[]{
						SetValue.map(MyItem.field, "B"),
						SetValue.map(MyItem.blobData, DataField.toValue(new byte[]{2}))
				},
				1,
				List.of(
						new Update(ModifiableParent.TYPE, true, ModifiableParent.modifiableAtParent),
						new Update(MyItem.TYPE, true, MyItem.field, MyItem.reference, MyItem.blobData, MyItem.vaultData)
				)
		);
	}

	@Test
	void changeAtSubAndParent() throws SQLException
	{
		check(
				new SetValue<?>[]{
						SetValue.map(ModifiableParent.modifiableAtParent, null),
						SetValue.map(MyItem.field, "A")
				},
				new SetValue<?>[]{
						SetValue.map(ModifiableParent.modifiableAtParent, 2),
						SetValue.map(MyItem.field, "B")
				},
				1,
				List.of(
						new Update(ModifiableParent.TYPE, true, ModifiableParent.modifiableAtParent),
						new Update(MyItem.TYPE, true, MyItem.field, MyItem.reference, MyItem.vaultData)
				)
		);
	}

	@Test
	void vaultDataNullToX() throws SQLException
	{
		check(
				MyItem.vaultData, null, DataField.toValue(new byte[]{1}),
				1,
				List.of(
						new Update(ModifiableParent.TYPE, true, ModifiableParent.modifiableAtParent),
						new Update(MyItem.TYPE, true, MyItem.field, MyItem.reference, MyItem.vaultData)
				)
		);
	}

	@Test
	void vaultDataNullToNull() throws SQLException
	{
		check(
				MyItem.vaultData, null, null,
				1,
				List.of(
						new Update(ModifiableParent.TYPE, true, ModifiableParent.modifiableAtParent),
						new Update(MyItem.TYPE, true, MyItem.field, MyItem.reference, MyItem.vaultData)
				)
		);
	}

	@Test
	void vaultDataXToNull() throws SQLException
	{
		check(
				MyItem.vaultData, DataField.toValue(new byte[]{1}), null,
				1,
				List.of(
						new Update(ModifiableParent.TYPE, true, ModifiableParent.modifiableAtParent),
						new Update(MyItem.TYPE, true, MyItem.field, MyItem.reference, MyItem.vaultData)
				)
		);
	}

	@Test
	void vaultDataXToX() throws SQLException
	{
		check(
				MyItem.vaultData, DataField.toValue(new byte[]{1}), DataField.toValue(new byte[]{1}),
				1,
				List.of(
						new Update(ModifiableParent.TYPE, true, ModifiableParent.modifiableAtParent),
						new Update(MyItem.TYPE, true, MyItem.field, MyItem.reference, MyItem.vaultData)
				)
		);
	}

	private <T extends Item> long queryUpdateCount(final T i, final Type<? super T> type) throws SQLException
	{
		return queryLong(
				"select " + SI.update(type) +
				" from " + SI.tab(type) +
				" where " + SI.pk(type) + " = " + SchemaInfo.getPrimaryKeyColumnValueL(i)
		);
	}

	private long queryLong(final String sql) throws SQLException
	{
		try(ResultSet rs = connection.executeQuery(sql))
		{
			assertTrue(rs.next());
			final long result = rs.getLong(1);
			assertFalse(rs.next());
			return result;
		}
	}

	record Update(String table, Set<String> columns)
	{
		Update(final Type<?> type, final boolean updateCatch, final Field<?>... fields)
		{
			this(
					SchemaInfo.quoteName(type.getModel(), SchemaInfo.getTableName(type)),
					columnNames(type, updateCatch, fields)
			);
		}

		private static Set<String> columnNames(final Type<?> type, final boolean updateCatch, final Field<?>... fields)
		{
			final Set<String> result = new HashSet<>();
			if (updateCatch)
				result.add(SI.update(type));
			for(final Field<?> field : fields)
			{
				result.add(SI.col(field));
				if (field instanceof final ItemField<?> itemField && !itemField.getValueType().getSubtypes().isEmpty())
					assertTrue(result.add(SI.type(itemField)));
			}
			return result;
		}
	}

	private final class UpdateCounter implements DatabaseListener
	{
		int count;
		final List<Update> tables = new ArrayList<>();

		@Override
		public void onStatement(final String sql,
										final List<Object> parameters,
										final long durationPrepare,
										final long durationExecute,
										final long durationRead,
										final long durationClose)
		{
			if (sql.startsWith("MERGE INTO "+SchemaInfo.quoteName(model, "VaultTrail_default"))
					|| sql.startsWith("INSERT INTO "+SchemaInfo.quoteName(model, "VaultTrail_default")))
				return;
			final Pattern p = Pattern.compile("UPDATE (.*) SET (.*) WHERE .*");
			final Matcher matcher = p.matcher(sql);
			assertTrue(matcher.matches(), sql);
			count++;
			tables.add(
					new Update(matcher.group(1), getColumns(matcher.group(2)))
			);
		}

		private static Set<String> getColumns(final String sqlSet)
		{
			final Pattern p = Pattern.compile("(.*)=.*");
			final Set<String> columns = new HashSet<>();
			for(final String s : sqlSet.split(","))
			{
				final Matcher matcher = p.matcher(s);
				if (!matcher.matches())
					fail(s);
				assertTrue(columns.add(matcher.group(1)));
			}
			return columns;
		}
	}

	@WrapperType(indent=2, comments=false)
	private static class ModifiableParent extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		static final IntegerField modifiableAtParent = new IntegerField().optional();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private ModifiableParent()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		protected ModifiableParent(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ModifiableParent> TYPE = com.exedio.cope.TypesBound.newType(ModifiableParent.class,ModifiableParent::new);

		@com.exedio.cope.instrument.Generated
		protected ModifiableParent(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static class UnmodifiableParent extends ModifiableParent
	{
		@Wrapper(wrap="*", visibility=NONE)
		@SuppressWarnings("unused")
		static final DataField parentBlobData = new DataField().optional();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private UnmodifiableParent()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		protected UnmodifiableParent(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<UnmodifiableParent> TYPE = com.exedio.cope.TypesBound.newType(UnmodifiableParent.class,UnmodifiableParent::new);

		@com.exedio.cope.instrument.Generated
		protected UnmodifiableParent(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static final class MyItem extends UnmodifiableParent
	{
		@Wrapper(wrap="*", visibility=NONE)
		@Wrapper(wrap="get", visibility=DEFAULT)
		static final StringField field = new StringField().optional();

		@Wrapper(wrap="*", visibility=NONE)
		static final ItemField<ModifiableParent> reference = ItemField.create(ModifiableParent.class).optional();

		@Wrapper(wrap="*", visibility=NONE)
		static final DataField blobData = new DataField().optional();

		@Vault
		@Wrapper(wrap="*", visibility=NONE)
		static final DataField vaultData = new DataField().optional();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private MyItem()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.lang.String getField()
		{
			return MyItem.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
