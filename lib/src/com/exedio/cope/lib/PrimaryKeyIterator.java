package com.exedio.cope.lib;


final class PrimaryKeyIterator
{
	private final Table table;
	
	PrimaryKeyIterator(final Table table)
	{
		this.table = table;
	}

	private int nextPkLo = Type.NOT_A_PK;
	private int nextPkHi = Type.NOT_A_PK;
	private boolean nextIsLo;
	
	void flushPK()
	{
		nextPkLo = Type.NOT_A_PK;
		nextPkHi = Type.NOT_A_PK;
	}

	int nextPK()
	{
		if(nextPkLo==Type.NOT_A_PK)
		{
			final int[] nextPks = table.database.getNextPK(table);
			if(nextPks.length!=2)
				throw new RuntimeException(String.valueOf(nextPks.length));
			nextPkLo = nextPks[0];
			nextPkHi = nextPks[1];
			if(nextPkLo>=nextPkHi)
				throw new RuntimeException(String.valueOf(nextPkLo)+">="+String.valueOf(nextPkHi));
			nextIsLo = (-nextPkLo)<=nextPkHi;
			//System.out.println(this.trimmedName+": getNextPK:"+nextPkLo+"/"+nextPkHi+"  nextIs"+(nextIsLo?"Lo":"Hi"));
		}
		
		//System.out.println(this.trimmedName+": nextPK:"+nextPkLo+"/"+nextPkHi+"  nextIs"+(nextIsLo?"Lo":"Hi"));
		final int result = nextIsLo ? nextPkLo-- : nextPkHi++;
		nextIsLo = !nextIsLo;

		if(nextPkLo>=nextPkHi) // TODO : somehow handle pk overflow
			throw new RuntimeException(String.valueOf(nextPkHi)+String.valueOf(nextPkLo));
		return result;
	}

}
