/*
 * Copyright (C) 2000  Ralf Wiebicke
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

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
final class Lexer
{
	private final char[] input;
	private int inputPosition = 0;
	private final int inputLength;

	private final StringBuilder output;
	private char outbuf;
	private boolean outbufvalid = false;
	final String fileName;

	private Token tokenBuf = null;

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
	public Lexer(final File inputFile,
							final Charset charset,
							final JavaFile javaFile)
		throws IOException
	{
		final byte[] inputBytes = new byte[(int)inputFile.length()];
		try(final FileInputStream fis = new FileInputStream(inputFile))
		{
			final int readBytes = fis.read(inputBytes);
			if(readBytes!=inputBytes.length)
				throw new RuntimeException(inputFile.getAbsolutePath() + '(' + readBytes + ')');
		}
		final CharsetDecoder decoder = charset.newDecoder();

		final CharBuffer buffer = decoder.decode(ByteBuffer.wrap(inputBytes));
		this.input = buffer.array();
		this.inputLength = buffer.length(); // BEWARE: May be less than input.length

		this.fileName = inputFile.getName();
		this.output = javaFile.buffer;
	}

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
	@SuppressFBWarnings("UCF_USELESS_CONTROL_FLOW") // evaluate !!!
	private int readComment() throws EndException
	{
		char x;
		switch (x = read())
		{
			case '*' :
				if (read() == '*')
				{
					// definitly a doc comment, see Java Lang. Spec. 3.7.
				}
				while (true)
				{
					if (read() != '*')
						continue;
					char c;
					while ((c = read()) == '*')
					{
						// do nothing
					}
					if (c == '/')
						break;
				}
				break;
			case '/' :
				// this is a '//' comment
				do
				{
					// do nothing
				}
				while (read() != '\n');
				break;
			default :
				return x;
		}
		return -1;
	}

	/**
	 * Splits the character stream into tokens.
	 * This tokenizer works only outside of method bodys.
	 */
	Token readToken() throws EndException
	{
		if(tokenBuf!=null)
		{
			final Token result = tokenBuf;
			tokenBuf = null;
			return result;
		}

		final StringBuilder buf = new StringBuilder();
		char c;

		while (true)
		{
			final int start = inputPosition;
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
					final int end = inputPosition;
					if (commentcollector)
						flushOutbuf();
					if (buf.length() > 0)
					{
						if (commentcollector)
							tokenBuf = new CommentToken(getCollector(), start, end);
						return new StringToken(buf);
					}
					if (commentcollector)
						return new CommentToken(getCollector(), start, end);
					break;
				case ' ' :
				case '\t' :
				case '\n' :
				case '\r' :
					if (buf.length() > 0)
						return new StringToken(buf);
					break;
				case '{' :
				case '}' :
				case '(' :
				case ')' :
				case ';' :
				case '=' :
				case ',' :
				case '@' :
					if (buf.length() > 0)
					{
						tokenBuf = new CharToken(c);
						return new StringToken(buf);
					}
					return new CharToken(c);
				case '<' :
					buf.append(c);
					while(true)
					{
						c = read();
						buf.append(c);
						if(c=='>')
							break;
					}
					break;
				default :
					if (!do_block && start_block)
						do_block = true;
					buf.append(c);
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
					if(tokenConsumer!=null)
						tokenConsumer.addToInitializer(c);
					c = read();
					break;
				case '}' :
				case ')' :
					bracketdepth--;
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
		return new ParseException(message, input, inputPosition);
	}

	@SuppressFBWarnings("SE_BAD_FIELD_INNER_CLASS") // Non-serializable class has a serializable inner class
	final class ParseException extends ParserException
	{
		private static final long serialVersionUID = 1l;

		final int line;
		final int column;

		ParseException(final String message, final char[] input, final int inputPosition)
		{
			super(message);

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

	abstract class Token
	{
		abstract boolean contains(char c);
		abstract boolean contains(String s);

		void expect(final char c)
		{
			if(!contains(c))
				throw newParseException("'" + c + "' expected");
		}

		String getString(final String message) throws ParseException
		{
			throw newParseException(message);
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
		boolean contains(final String s)
		{
			return false;
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

		StringToken(final StringBuilder buf)
		{
			this.value = buf.toString();
		}

		@Override
		boolean contains(final char c)
		{
			return false;
		}

		@Override
		boolean contains(final String s)
		{
			return value.equals(s);
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

		CommentToken(final String comment, final int start, final int end)
		{
			this.comment = comment;
			@SuppressWarnings("synthetic-access")
			final String compareTo = new String(input, start, end-start);
			if(!comment.equals(compareTo))
				throw new RuntimeException(comment + "---" + compareTo);
		}

		@Override
		boolean contains(final char c)
		{
			return false;
		}

		@Override
		boolean contains(final String s)
		{
			return false;
		}

		boolean isDocumentation()
		{
			return comment.startsWith("/**");
		}

		boolean isSkipped()
		{
			return
				(comment.indexOf('@'+Generator.TAG_GENERATED)>=0) ||
				(comment.indexOf("<p><small>Generated by the cope instrumentor.</small>")>=0) || // detect legacy markers
				(comment.indexOf("@author cope instrumentor")>=0);
		}

		@Override
		public String toString()
		{
			return "comment(" + comment + ')';
		}
	}

	boolean inputEqual(final StringBuilder bf)
	{
		if(inputLength!=bf.length())
			return false;

		for(int i = 0; i<inputLength; i++)
			if(input[i]!=bf.charAt(i))
				return false;

		return true;
	}

	int outputLength()
	{
		return output.length();
	}
}
