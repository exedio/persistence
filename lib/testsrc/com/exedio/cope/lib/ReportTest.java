
package com.exedio.cope.lib;

public class ReportTest extends DatabaseLibTest
{
	private static final String TABLE1 = "SumItem";
	private static final String TABLE1X = "SumItemX";
	private static final String COLUMN1 = "num2";
	private static final String COLUMN1X = "num2X";
	
	public void testReport()
	{
		final String column1Type;
		// OK
		{
			final Report report = model.reportDatabase();

			final ReportTable table = report.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(null, table.getError());
			assertEquals(Report.COLOR_OK, table.getParticularColor());

			final ReportColumn column = table.getColumn(COLUMN1);
			assertEquals(null, column.getError());
			assertEquals(Report.COLOR_OK, column.getParticularColor());
			column1Type = column.getDatabaseType();
			assertNotNull(column1Type);
			
			column.renameTo(COLUMN1X);
		}
		// COLUMN RENAMED
		{
			final Report report = model.reportDatabase();

			final ReportTable table = report.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(null, table.getError());
			assertEquals(Report.COLOR_OK, table.getParticularColor());

			{
				final ReportColumn column = table.getColumn(COLUMN1);
				assertEquals("missing", column.getError());
				assertEquals(Report.COLOR_RED, column.getParticularColor());
				assertEquals(column1Type, column.getDatabaseType());
			}
			{
				final ReportColumn columnX = table.getColumn(COLUMN1X);
				assertEquals("not used", columnX.getError());
				assertEquals(Report.COLOR_YELLOW, columnX.getParticularColor());
				assertEquals(column1Type, columnX.getDatabaseType());

				columnX.renameTo(COLUMN1);
			}
		}
		// OK
		{
			final Report report = model.reportDatabase();

			final ReportTable table = report.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(null, table.getError());
			assertEquals(Report.COLOR_OK, table.getParticularColor());

			final ReportColumn column = table.getColumn(COLUMN1);
			assertEquals(null, column.getError());
			assertEquals(Report.COLOR_OK, column.getParticularColor());
			assertEquals(column1Type, column.getDatabaseType());

			column.drop();
		}
		// COLUMN DROPPED
		{
			final Report report = model.reportDatabase();

			final ReportTable table = report.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(null, table.getError());
			assertEquals(Report.COLOR_OK, table.getParticularColor());

			final ReportColumn column = table.getColumn(COLUMN1);
			assertEquals("missing", column.getError());
			assertEquals(Report.COLOR_RED, column.getParticularColor());
			assertEquals(column1Type, column.getDatabaseType());

			column.create();
		}
		// OK
		{
			final Report report = model.reportDatabase();

			final ReportTable table = report.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(null, table.getError());
			assertEquals(Report.COLOR_OK, table.getParticularColor());

			final ReportColumn column = table.getColumn(COLUMN1);
			assertEquals(null, column.getError());
			assertEquals(Report.COLOR_OK, column.getParticularColor());
			assertEquals(column1Type, column.getDatabaseType());
			
			table.renameTo(TABLE1X);
		}
		// TABLE RENAMED
		{
			final Report report = model.reportDatabase();

			{
				final ReportTable table = report.getTable(TABLE1);
				assertNotNull(table);
				assertEquals("MISSING !!!", table.getError());
				assertEquals(Report.COLOR_RED, table.getParticularColor());

				final ReportColumn column = table.getColumn(COLUMN1);
				assertEquals("missing", column.getError());
				assertEquals(Report.COLOR_RED, column.getParticularColor());
				assertEquals(column1Type, column.getDatabaseType());
			}
			{
				final ReportTable tableX = report.getTable(TABLE1X);
				assertNotNull(tableX);
				assertEquals("not used", tableX.getError());
				assertEquals(Report.COLOR_YELLOW, tableX.getParticularColor());

				final ReportColumn column = tableX.getColumn(COLUMN1);
				assertEquals("not used", column.getError());
				assertEquals(Report.COLOR_YELLOW, column.getParticularColor());
				assertEquals(column1Type, column.getDatabaseType());

				tableX.renameTo(TABLE1);
			}
		}
		// OK
		{
			final Report report = model.reportDatabase();

			final ReportTable table = report.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(null, table.getError());
			assertEquals(Report.COLOR_OK, table.getParticularColor());

			final ReportColumn column = table.getColumn(COLUMN1);
			assertEquals(null, column.getError());
			assertEquals(Report.COLOR_OK, column.getParticularColor());
			assertEquals(column1Type, column.getDatabaseType());
			
			table.drop();
		}
		// TABLE DROPPED
		{
			final Report report = model.reportDatabase();

			{
				final ReportTable table = report.getTable(TABLE1);
				assertNotNull(table);
				assertEquals("MISSING !!!", table.getError());
				assertEquals(Report.COLOR_RED, table.getParticularColor());

				final ReportColumn column = table.getColumn(COLUMN1);
				assertEquals("missing", column.getError());
				assertEquals(Report.COLOR_RED, column.getParticularColor());
				assertEquals(column1Type, column.getDatabaseType());

				table.create();
			}
		}
		// OK
		{
			final Report report = model.reportDatabase();

			final ReportTable table = report.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(null, table.getError());
			assertEquals(Report.COLOR_OK, table.getParticularColor());

			final ReportColumn column = table.getColumn(COLUMN1);
			assertEquals(null, column.getError());
			assertEquals(Report.COLOR_OK, column.getParticularColor());
			assertEquals(column1Type, column.getDatabaseType());
		}
	}
	
}
