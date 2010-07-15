/*
 * Copyright (C) 2000  Ralf Wiebicke
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.instrument;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implements a modifying java parser.
 * This means, the input stream is continuously written
 * into an output stream, and may be modified before writing.
 *
 * The parser recognizes java meta information only,
 * which is anything outside method bodies and attribute
 * initializers.
 *
 * To use the parser, provide an implementation of the
 * ParseConsumer interface to the constructor.
 * @see ParseConsumer
 *
 * @author Ralf Wiebicke
 */
final class Tokenizer
{
	final char[] input;
	int inputPosition = 0;
	private final int inputLength;

	private final StringBuilder output;
	final String fileName;

	private final StringBuilder bufTokenizer = new StringBuilder();

	private boolean do_block = false;
	private boolean start_block = false;
	private boolean collect_when_blocking = false;
	private final StringBuilder collector = new StringBuilder();

	private boolean discardNextFeature = false;

	/**
	 * Constructs a new java parser.
	 * @param inputFile
	 * the input stream to be parsed.
	 */
	public Tokenizer(final File inputFile,
							final JavaFile javaFile)
		throws IOException
	{
		final byte[] inputBytes = new byte[(int)inputFile.length()];
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream(inputFile);
			final int readBytes = fis.read(inputBytes);
			if(readBytes!=inputBytes.length)
				throw new RuntimeException(inputFile.getAbsolutePath() + '(' + readBytes + ')');
		}
		finally
		{
			if(fis!=null)
				fis.close();
		}
		final Charset charset = Charset.defaultCharset(); // TODO make configurable
		final CharsetDecoder decoder = charset.newDecoder();

		this.input = decoder.decode(ByteBuffer.wrap(inputBytes)).array();
		this.inputLength = input.length;

		this.fileName = inputFile.getName();
		this.output = javaFile.buffer;
	}

	private char outbuf;
	private boolean outbufvalid = false;

	private final char read() throws EndException
	{
		if(inputPosition>=inputLength)
		{
			if(!do_block && outbufvalid && !discardNextFeature)
				output.append(outbuf);
			throw new EndException();
		}

		final char c = input[inputPosition++];

		if(!do_block && outbufvalid && !discardNextFeature)
			output.append(outbuf);

		if (do_block && collect_when_blocking)
			collector.append(outbuf);
		outbuf = c;
		outbufvalid = true;
		//System.out.print((char)c);
		return c;
	}

	void scheduleBlock(final boolean collect_when_blocking)
	{
		if (do_block || collector.length() > 0)
			throw new IllegalArgumentException();
		start_block = true;
		this.collect_when_blocking = collect_when_blocking;
	}

	boolean do_block()
	{
		return do_block;
	}

	boolean collect_when_blocking()
	{
		return collect_when_blocking;
	}

	String getCollector()
	{
		do_block = false;
		start_block = false;
		final String s = collector.toString();
		collector.setLength(0);
		//System.out.println("  collector: >"+s+"<");
		return s;
	}

	void discardNextFeature(final boolean b)
	{
		discardNextFeature = b;
	}

	void flushOutbuf()
	{
		if (outbufvalid)
		{
			if (do_block)
			{
				if (collect_when_blocking)
					collector.append(outbuf);
			}
			else
			{
				output.append(outbuf);
			}
			outbufvalid = false;
		}
	}

	void write(final String s)
	{
		output.append(s);
	}

	/**
	 * Reads a comment.
	 * Is started after the initial '/' character.
	 * If the next character is either '/' or '*',
	 * the rest of the comment is read, and a value of -1 is returned.
	 * If not, there is no comment,
	 * and this next character is returned, casted to int.
	 */
	private int readComment() throws EndException
	{
		char x;
		switch (x = read())
		{
			case '*' :
				if (read() == '*')
				{
					// definitly a doc comment, see Java Lang. Spec. 3.7.
					//System.out.println("this is a '/** .. */' doc-comment");
				}
				//System.out.println("this is a '/* .. */' comment");
				while (true)
				{
					if (read() != '*')
						continue;
					char c;
					while ((c = read()) == '*');
					if (c == '/')
						break;
				}
				break;
			case '/' :
				//System.out.println("this is a '//' comment");
				do;
				while (read() != '\n');
				break;
			default :
				return x;
		}
		return -1;
	}

	private char tokenBuf = '\0';
	private String commentBuf = null;

	/**
	 * Splits the character stream into tokens.
	 * This tokenizer works only outside of method bodys.
	 */
	Token readToken() throws EndException
	{
		char c;

		if (tokenBuf != '\0')
		{
			c = tokenBuf;
			tokenBuf = '\0';
			//System.out.println("<<"+c+">>");
			return new CharToken(c);
		}

		if (commentBuf != null)
		{
			final CommentToken result = new CommentToken(commentBuf);
			commentBuf = null;
			//System.out.println("<<"+comment+">>");
			return result;
		}

		bufTokenizer.setLength(0);

		while (true)
		{
			switch (c = read())
			{
				case '/' :
					boolean commentcollector = false;
					if (!do_block && start_block)
					{
						do_block = true;
						commentcollector = true;
					}
					readComment();
					if (commentcollector)
						flushOutbuf();
					if (bufTokenizer.length() > 0)
					{
						if (commentcollector)
							commentBuf = getCollector();
						//System.out.println("<"+buf+">");
						return new StringToken(bufTokenizer.toString());
					}
					if (commentcollector)
					{
						final String comment = getCollector();
						//System.out.println("<<"+comment+">>");
						return new CommentToken(comment);
					}
					break;
				case ' ' :
				case '\t' :
				case '\n' :
				case '\r' :
					if (bufTokenizer.length() > 0)
					{
						//System.out.println("ws||"+buf+"|| ("+positionLine+':'+positionColumn+')');
						return new StringToken(bufTokenizer.toString());
					}
					break;
				case '{' :
				case '}' :
				case '(' :
				case ')' :
				case ';' :
				case '=' :
				case ',' :
				case '@' :
					if (bufTokenizer.length() > 0)
					{
						tokenBuf = c;
						//System.out.println("se||"+buf+"|| ("+positionLine+':'+positionColumn+')');
						return new StringToken(bufTokenizer.toString());
					}
					//System.out.println("<<"+c+">>");
					return new CharToken(c);
				case '<' :
					bufTokenizer.append(c);
					while(true)
					{
						c = read();
						bufTokenizer.append(c);
						if(c=='>')
							break;
					}
					//System.out.println("gn||"+buf+"|| ("+positionLine+':'+positionColumn+')');
					break;
				default :
					if (!do_block && start_block)
						do_block = true;
					bufTokenizer.append(c);
					//System.out.println("df||"+buf+"|| ("+positionLine+':'+positionColumn+')');
					break;
			}
		}
	}

	/**
	 * Parses a method body or an attribute initializer,
	 * depending on the parameter.
	 * For method bodys, the input stream must be directly behind
	 * the first opening curly bracket of the body.
	 * For attribute initializers, the input stream must be directly
	 * behind the '='.
	 * @return
	 * the delimiter, which terminated the attribute initializer
	 * (';' or ',') or '}' for methods.
	 */
	CharToken parseBody(final boolean attribute, final InitializerConsumer tokenConsumer)
		throws EndException, ParseException
	{
		//System.out.println("    body("+(attribute?"attribute":"method")+")");

		int bracketdepth = (attribute ? 0 : 1);
		char c = read();
		while (true)
		{
			switch (c)
			{
				case '/' :
					final int i = readComment();
					if (i >= 0)
						c = (char)i;
					else
						c = read();
					break;
				case '{' :
				case '(' :
					bracketdepth++;
					//System.out.print("<("+bracketdepth+")>");
					if(tokenConsumer!=null)
						tokenConsumer.addToInitializer(c);
					c = read();
					break;
				case '}' :
				case ')' :
					bracketdepth--;
					//System.out.print("<("+bracketdepth+")>");
					if (bracketdepth == 0 && !attribute)
						return new CharToken('}');
					if (bracketdepth < 0)
						throw newParseException("';' expected.");
					if(tokenConsumer!=null)
						tokenConsumer.addToInitializer(c);
					c = read();
					break;
				case ';' :
					// dont have to test for "attribute" here
					// since then the test in the '}' branch would have
					// already terminated the loop
					if (bracketdepth == 0)
						return new CharToken(';');
					c = read();
					break;
				case ',' :
					if (bracketdepth == 0)
						return new CharToken(',');
					if(tokenConsumer!=null)
						tokenConsumer.addToInitializer(c);
					c = read();
					break;
					// ignore brackets inside of literal String's
				case '"' :
					if(tokenConsumer!=null)
						tokenConsumer.addToInitializer(c);
					il : while (true)
					{
						switch (c=read())
						{
							case '"' :
								if(tokenConsumer!=null)
									tokenConsumer.addToInitializer(c);
								break il;
							case '\\' :
								if(tokenConsumer!=null)
									tokenConsumer.addToInitializer(c);
								final char escapedChar = read();
								if(tokenConsumer!=null)
									tokenConsumer.addToInitializer(escapedChar);
								break; // ignore escaped characters for tokenConsumer.addToken()
							default:
								if(tokenConsumer!=null)
									tokenConsumer.addToInitializer(c);
						}
					}
					c = read();
					break;
					// ignore brackets inside of literal characters
				case '\'' :
					if(tokenConsumer!=null)
						tokenConsumer.addToInitializer(c);
					il : while (true)
					{
						switch(c = read())
						{
							case '\'' :
								if(tokenConsumer!=null)
									tokenConsumer.addToInitializer(c);
								break il;
							case '\\' :
								if(tokenConsumer!=null)
									tokenConsumer.addToInitializer(c);
								final char escapedChar = read();
								if(tokenConsumer!=null)
									tokenConsumer.addToInitializer(escapedChar);
								break; // ignore escaped characters for tokenConsumer.addToken()
							default:
								if(tokenConsumer!=null)
									tokenConsumer.addToInitializer(c);
						}
					}
					c = read();
					break;
				case '<' :
					if(bracketdepth>0)
					{
						if(tokenConsumer!=null)
							tokenConsumer.addToInitializer(c);
						c = read();
					}
					else
					{
						while(true)
						{
							if(tokenConsumer!=null)
								tokenConsumer.addToInitializer(c);
							c = read();
							if(c=='>')
								break;
						}
						//System.out.println("gb||"+buf+"|| ("+positionLine+':'+positionColumn+')');
					}
					break;
				default :
					if(tokenConsumer!=null)
						tokenConsumer.addToInitializer(c);
					c = read();
					break;
			}
		}
	}

	static final class EndException extends Exception
	{
		private static final long serialVersionUID = 1l;

	}

	ParseException newParseException(final String message)
	{
		return new ParseException(message);
	}

	final class ParseException extends ParserException
	{
		private static final long serialVersionUID = 1l;

		final int line;
		final int column;

		ParseException(final String message)
		{
			super(message);

			final char[] input = Tokenizer.this.input;
			final int inputPosition = Tokenizer.this.inputPosition;

			int line = 1;
			int column = 0;
			for(int i = 0; i<inputPosition; i++)
			{
				if(input[i]=='\n')
				{
					line++;
					column = -1;
				}
				else
				{
					column++;
				}
			}
			this.line = line;
			this.column = column;
		}

		@Override
		public String getMessage()
		{
			return
				"("
				+ fileName
				+ ':'
				+ line
				+ ':'
				+ column
				+ ')'
				+ ' '
				+ super.getMessage();
		}
	}

	public final static boolean hasTag(final String doccomment, final String tagname)
	{
		if(doccomment==null)
			return false;

		final String s = '@' + tagname;
		final int pos = doccomment.indexOf(s);
		if(pos<0)
			return false;
		if(pos+s.length()==doccomment.length())
			return true;
		return Character.isWhitespace(doccomment.charAt(pos+s.length()));
	}

	/**
	 * @param tagname the tag name without the '@' prefix
	 * @return the first line following the tag
	 */
	public final static String findDocTagLine(final String doccomment, final String tagname)
	{
		if(doccomment==null)
			return null;

		final String s = '@' + tagname + ' ';
		int start = doccomment.indexOf(s);
		if (start < 0)
			return null;
		start += s.length();

		int end;
		li : for (end = start; end < doccomment.length(); end++)
		{
			switch (doccomment.charAt(end))
			{
				case '\n' :
				case '\r' :
				case '*' :
					break li;
			}
		}
		final String result = doccomment.substring(start, end).trim();
		//System.out.println("doctag:>"+tagname+"< >"+docComment.substring(start, end)+"<");
		return result;
	}

	public static final String removeGenerics(final String s)
	{
		final int lt = s.indexOf('<');
		//System.out.println("--------evaluate("+s+")"+lt);
		if(lt>=0)
		{
			final int gt = s.indexOf('>', lt);
			if(gt<0)
				throw new RuntimeException(s);

			//System.out.println("--------evaluate("+s+")"+gt);
			if(gt<s.length())
				return s.substring(0, lt) + s.substring(gt+1);
			else
				return s.substring(0, lt);
		}
		else
			return s;
	}

	public static final List<String> getGenerics(final String s)
	{
		final int lt = s.indexOf('<');
		if(lt>=0)
		{
			final ArrayList<String> result = new ArrayList<String>();

			final int gt = s.indexOf('>', lt);
			if(gt<0)
				throw new RuntimeException(s);

			int lastcomma = lt;
			for(int comma = s.indexOf(',', lt); comma>=0&&comma<gt; comma = s.indexOf(',', comma+1))
			{
				result.add(s.substring(lastcomma+1, comma).trim());
				lastcomma = comma;
			}
			result.add(s.substring(lastcomma+1, gt).trim());

			return result;
		}
		else
			return Collections.emptyList();
	}

	abstract class Token
	{
		abstract boolean contains(char c);

		void expect(final char c)
		{
			if(!contains(c))
				throw new ParseException("'" + c + "' expected");
		}

		String getString(final String message) throws ParseException
		{
			throw new ParseException(message);
		}

		@Override
		public abstract String toString();

		/**
		 * @deprecated for debugging only, should never be used in committed code
		 */
		@Deprecated
		final Token print()
		{
			System.out.println("+++++"+this);
			return this;
		}
	}

	final class CharToken extends Token
	{
		final char value;

		CharToken(final char value)
		{
			this.value = value;
			if(value=='\0')
				throw new IllegalArgumentException();
			if(value=='c')
				throw new IllegalArgumentException();
		}

		@Override
		boolean contains(final char c)
		{
			return c==value;
		}

		@Override
		public String toString()
		{
			return "char(" + value + ')';
		}
	}

	final class StringToken extends Token
	{
		final String value;

		StringToken(final String value)
		{
			this.value = value;
		}

		@Override
		boolean contains(final char c)
		{
			return false;
		}

		@Override
		String getString(final String message)
		{
			return value;
		}

		@Override
		public String toString()
		{
			return "string(" + value + ')';
		}
	}

	final class CommentToken extends Token
	{
		final String comment;

		CommentToken(final String comment)
		{
			this.comment = comment;
		}

		@Override
		boolean contains(final char c)
		{
			return false;
		}

		@Override
		public String toString()
		{
			return "comment(" + comment + ')';
		}
	}

	int outputLength()
	{
		return output.length();
	}
}
