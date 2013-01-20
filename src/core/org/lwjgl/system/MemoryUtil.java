/*
 * Copyright LWJGL. All rights reserved.
 * License terms: http://lwjgl.org/license.php
 */
package org.lwjgl.system;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.PointerBuffer;
import org.lwjgl.Sys;
import org.lwjgl.system.MemoryAccess.MemoryAccessor;

import java.nio.*;
import java.nio.charset.*;

/**
 * This class provides functionality for managing native memory.
 * <p/>
 * All methods in this class will make use of {@link sun.misc.Unsafe} if it's available,
 * for performance. If Unsafe is not available, the fallback implementations make use
 * of reflection and, in the worst-case, JNI.
 */
public final class MemoryUtil {

	//private static final Charset ASCII;
	private static final Charset UTF8;
	//private static final Charset UTF16;

	private static final MemoryAccessor ACCESSOR;

	/** The memory page size, in bytes. This value is always a power-of-two. */
	public static final int PAGE_SIZE;

	static {
		Sys.touch();

		//ASCII = Charset.forName("ISO-8859-1");
		UTF8 = Charset.forName("UTF-8");
		//UTF16 = Charset.forName(ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN ? "UTF-16BE" : "UTF-16LE");

		ACCESSOR = MemoryAccess.getInstance();
		PAGE_SIZE = ACCESSOR.getPageSize();

		LWJGLUtil.log("MemoryUtil MemoryAccessor: " + ACCESSOR.getClass().getSimpleName());
	}

	private MemoryUtil() {
	}

	/**
	 * Returns the memory address of the specified buffer. [INTERNAL USE ONLY]
	 *
	 * @param buffer the buffer
	 *
	 * @return the memory address
	 */
	public static long memAddress0(Buffer buffer) { return ACCESSOR.getAddress(buffer); }

	public static long memAddress0Safe(Buffer buffer) { return buffer == null ? 0L : ACCESSOR.getAddress(buffer); }

	public static long memAddress0(PointerBuffer buffer) { return ACCESSOR.getAddress(buffer.getBuffer()); }

	public static long memAddress0Safe(PointerBuffer buffer) { return buffer == null ? 0L : ACCESSOR.getAddress(buffer.getBuffer()); }

	// --- [ Buffer address utilities ] ---

	public static long memAddress(ByteBuffer buffer) { return memAddress(buffer, buffer.position()); }

	public static long memAddress(ByteBuffer buffer, int position) { return memAddress0(buffer) + position; }

	public static long memAddress(ShortBuffer buffer) { return memAddress(buffer, buffer.position()); }

	public static long memAddress(ShortBuffer buffer, int position) { return memAddress0(buffer) + (position << 1); }

	public static long memAddress(CharBuffer buffer) { return memAddress(buffer, buffer.position()); }

	public static long memAddress(CharBuffer buffer, int position) { return memAddress0(buffer) + (position << 1); }

	public static long memAddress(IntBuffer buffer) { return memAddress(buffer, buffer.position()); }

	public static long memAddress(IntBuffer buffer, int position) { return memAddress0(buffer) + (position << 2); }

	public static long memAddress(FloatBuffer buffer) { return memAddress(buffer, buffer.position()); }

	public static long memAddress(FloatBuffer buffer, int position) { return memAddress0(buffer) + (position << 2); }

	public static long memAddress(LongBuffer buffer) { return memAddress(buffer, buffer.position()); }

	public static long memAddress(LongBuffer buffer, int position) { return memAddress0(buffer) + (position << 3); }

	public static long memAddress(DoubleBuffer buffer) { return memAddress(buffer, buffer.position()); }

	public static long memAddress(DoubleBuffer buffer, int position) { return memAddress0(buffer) + (position << 3); }

	public static long memAddress(PointerBuffer buffer) { return memAddress(buffer, buffer.position()); }

	public static long memAddress(PointerBuffer buffer, int position) { return memAddress0(buffer) + (position * PointerBuffer.getPointerSize()); }

	// --- [ Buffer address utilities - Safe ] ---

	public static long memAddressSafe(ByteBuffer buffer) { return buffer == null ? 0L : memAddress(buffer); }

	public static long memAddressSafe(ByteBuffer buffer, int position) { return buffer == null ? 0L : memAddress(buffer, position); }

	public static long memAddressSafe(ShortBuffer buffer) { return buffer == null ? 0L : memAddress(buffer); }

	public static long memAddressSafe(ShortBuffer buffer, int position) { return buffer == null ? 0L : memAddress(buffer, position); }

	public static long memAddressSafe(CharBuffer buffer) { return buffer == null ? 0L : memAddress(buffer); }

	public static long memAddressSafe(CharBuffer buffer, int position) { return buffer == null ? 0L : memAddress(buffer, position); }

	public static long memAddressSafe(IntBuffer buffer) { return buffer == null ? 0L : memAddress(buffer); }

	public static long memAddressSafe(IntBuffer buffer, int position) { return buffer == null ? 0L : memAddress(buffer, position); }

	public static long memAddressSafe(FloatBuffer buffer) { return buffer == null ? 0L : memAddress(buffer); }

	public static long memAddressSafe(FloatBuffer buffer, int position) { return buffer == null ? 0L : memAddress(buffer, position); }

	public static long memAddressSafe(LongBuffer buffer) { return buffer == null ? 0L : memAddress(buffer); }

	public static long memAddressSafe(LongBuffer buffer, int position) { return buffer == null ? 0L : memAddress(buffer, position); }

	public static long memAddressSafe(DoubleBuffer buffer) { return buffer == null ? 0L : memAddress(buffer); }

	public static long memAddressSafe(DoubleBuffer buffer, int position) { return buffer == null ? 0L : memAddress(buffer, position); }

	public static long memAddressSafe(PointerBuffer buffer) { return buffer == null ? 0L : memAddress(buffer); }

	public static long memAddressSafe(PointerBuffer buffer, int position) { return buffer == null ? 0L : memAddress(buffer, position); }

	// --- [ Buffer allocation utilities ] ---

	/**
	 * Creates a new direct ByteBuffer that starts at the given memory
	 * address and has the given capacity. The returned ByteBuffer instance
	 * will be set to the native ByteOrder.
	 *
	 * @param address  the starting memory address
	 * @param capacity the buffer capacity
	 *
	 * @return the new ByteBuffer
	 */
	public static ByteBuffer memByteBuffer(long address, int capacity) {
		if ( LWJGLUtil.DEBUG && (address <= 0L || capacity < 0) )
			throw new IllegalArgumentException();

		return ACCESSOR.newByteBuffer(address, capacity);
	}

	/**
	 * Overloads {@link #memByteBuffer(long, int)} with a long capacity parameter. This
	 * is used by the auto-generated code, for simplicity.
	 *
	 * @param address  the starting memory address
	 * @param capacity the buffer capacity. Will be cast to an integer.
	 *
	 * @return the new ByteBuffer
	 */
	public static ByteBuffer memByteBuffer(long address, long capacity) {
		return memByteBuffer(address, (int)capacity);
	}

	/**
	 * Creates a new direct ByteBuffer that starts at the given memory
	 * address and has capacity equal to the null-terminated string
	 * starting at that address. A single \0 character will terminate
	 * the string. The returned buffer will NOT include the \0 byte.
	 * <p/>
	 * This method is useful for reading ASCII and UTF8 encoded text.
	 *
	 * @param address the starting memory address
	 *
	 * @return the new ByteBuffer
	 */
	public static ByteBuffer memByteBufferNT1(long address) {
		if ( address == 0L )
			return null;

		final ByteBuffer infPointer = ACCESSOR.newByteBuffer(address, Integer.MAX_VALUE);

		int size = 0;
		while ( infPointer.get(size) != 0 )
			size++;

		return memSetupBuffer(infPointer, address, size);
	}

	/**
	 * Creates a new direct ByteBuffer that starts at the given memory
	 * address and has capacity equal to the null-terminated string
	 * starting at that address. Two \0 characters will terminate
	 * the string. The returned buffer will NOT include the \0 bytes.
	 * <p/>
	 * This method is useful for reading UTF16 encoded text.
	 *
	 * @param address the starting memory address
	 *
	 * @return the new ByteBuffer
	 */
	public static ByteBuffer memByteBufferNT2(long address) {
		if ( address == 0L )
			return null;

		final ByteBuffer infPointer = ACCESSOR.newByteBuffer(address, Integer.MAX_VALUE);

		int size = 0;
		while ( true ) {
			if ( infPointer.getChar(size) == 0 )
				break;
			size += 2;
		}

		return memSetupBuffer(infPointer, address, size);
	}

	/**
	 * Creates a new direct ShortBuffer that starts at the given memory address and has the given capacity.
	 *
	 * @param address  the starting memory address
	 * @param capacity the buffer capacity
	 *
	 * @return the new ShortBuffer
	 */
	public static ShortBuffer memShortBuffer(long address, int capacity) {
		if ( LWJGLUtil.DEBUG && (address <= 0L || capacity < 0) )
			throw new IllegalArgumentException();

		return ACCESSOR.newShortBuffer(address, capacity);
	}

	/**
	 * Creates a new direct CharBuffer that starts at the given memory address and has the given capacity.
	 *
	 * @param address  the starting memory address
	 * @param capacity the buffer capacity
	 *
	 * @return the new CharBuffer
	 */
	public static CharBuffer memCharBuffer(long address, int capacity) {
		if ( LWJGLUtil.DEBUG && (address <= 0L || capacity < 0) )
			throw new IllegalArgumentException();

		return ACCESSOR.newCharBuffer(address, capacity);
	}

	/**
	 * Creates a new direct IntBuffer that starts at the given memory address and has the given capacity.
	 *
	 * @param address  the starting memory address
	 * @param capacity the buffer capacity
	 *
	 * @return the new IntBuffer
	 */
	public static IntBuffer memIntBuffer(long address, int capacity) {
		if ( LWJGLUtil.DEBUG && (address <= 0L || capacity < 0) )
			throw new IllegalArgumentException();

		return ACCESSOR.newIntBuffer(address, capacity);
	}

	/**
	 * Creates a new direct LongBuffer that starts at the given memory address and has the given capacity.
	 *
	 * @param address  the starting memory address
	 * @param capacity the buffer capacity
	 *
	 * @return the new LongBuffer
	 */
	public static LongBuffer memLongBuffer(long address, int capacity) {
		if ( LWJGLUtil.DEBUG && (address <= 0L || capacity < 0) )
			throw new IllegalArgumentException();

		return ACCESSOR.newLongBuffer(address, capacity);
	}

	/**
	 * Creates a new direct FloatBuffer that starts at the given memory address and has the given capacity.
	 *
	 * @param address  the starting memory address
	 * @param capacity the buffer capacity
	 *
	 * @return the new FloatBuffer
	 */
	public static FloatBuffer memFloatBuffer(long address, int capacity) {
		if ( LWJGLUtil.DEBUG && (address <= 0L || capacity < 0) )
			throw new IllegalArgumentException();

		return ACCESSOR.newFloatBuffer(address, capacity);
	}

	/**
	 * Creates a new direct DoubleBuffer that starts at the given memory address and has the given capacity.
	 *
	 * @param address  the starting memory address
	 * @param capacity the buffer capacity
	 *
	 * @return the new DoubleBuffer
	 */
	public static DoubleBuffer memDoubleBuffer(long address, int capacity) {
		if ( LWJGLUtil.DEBUG && (address <= 0L || capacity < 0) )
			throw new IllegalArgumentException();

		return ACCESSOR.newDoubleBuffer(address, capacity);
	}

	/**
	 * This method is an alternative to {@link #memByteBuffer} that allows the reuse of an existing direct ByteBuffer instance.
	 * It modifies that instance so that it starts at the given memory address and has the given capacity. The instance passed
	 * to this method should not own native memory, i.e. it should not be an instance created using {@link ByteBuffer#allocateDirect}.
	 * Using such an instance will cause an exception to be thrown. Other instances are allowed and their parent reference will be
	 * cleared before this method returns.
	 * <p/>
	 * ByteBuffer instance modification might not be possible. In that case this method behaves exactly like {@link #memByteBuffer},
	 * so the returned instance should always replace the input one.
	 *
	 * @param buffer   the ByteBuffer to modify
	 * @param address  the starting memory address
	 * @param capacity the buffer capacity
	 *
	 * @return the modified ByteBuffer
	 */
	public static ByteBuffer memSetupBuffer(ByteBuffer buffer, long address, int capacity) {
		if ( LWJGLUtil.DEBUG && (address <= 0L || capacity < 0) )
			throw new IllegalArgumentException();

		return ACCESSOR.setupBuffer(buffer, address, capacity);
	}

	/** ShortBuffer version of: {@link #memSetupBuffer(java.nio.ByteBuffer, long, int)} */
	public static ShortBuffer memSetupBuffer(ShortBuffer buffer, long address, int capacity) {
		if ( LWJGLUtil.DEBUG && (address <= 0L || capacity < 0) )
			throw new IllegalArgumentException();

		return ACCESSOR.setupBuffer(buffer, address, capacity);
	}

	/** CharBuffer version of: {@link #memSetupBuffer(java.nio.ByteBuffer, long, int)} */
	public static CharBuffer memSetupBuffer(CharBuffer buffer, long address, int capacity) {
		if ( LWJGLUtil.DEBUG && (address <= 0L || capacity < 0) )
			throw new IllegalArgumentException();

		return ACCESSOR.setupBuffer(buffer, address, capacity);
	}

	/** IntBuffer version of: {@link #memSetupBuffer(java.nio.ByteBuffer, long, int)} */
	public static IntBuffer memSetupBuffer(IntBuffer buffer, long address, int capacity) {
		if ( LWJGLUtil.DEBUG && (address <= 0L || capacity < 0) )
			throw new IllegalArgumentException();

		return ACCESSOR.setupBuffer(buffer, address, capacity);
	}

	/** LongBuffer version of: {@link #memSetupBuffer(java.nio.ByteBuffer, long, int)} */
	public static LongBuffer memSetupBuffer(LongBuffer buffer, long address, int capacity) {
		if ( LWJGLUtil.DEBUG && (address <= 0L || capacity < 0) )
			throw new IllegalArgumentException();

		return ACCESSOR.setupBuffer(buffer, address, capacity);
	}

	/** FloatBuffer version of: {@link #memSetupBuffer(java.nio.ByteBuffer, long, int)} */
	public static FloatBuffer memSetupBuffer(FloatBuffer buffer, long address, int capacity) {
		if ( LWJGLUtil.DEBUG && (address <= 0L || capacity < 0) )
			throw new IllegalArgumentException();

		return ACCESSOR.setupBuffer(buffer, address, capacity);
	}

	/** DoubleBuffer version of: {@link #memSetupBuffer(java.nio.ByteBuffer, long, int)} */
	public static DoubleBuffer memSetupBuffer(DoubleBuffer buffer, long address, int capacity) {
		if ( LWJGLUtil.DEBUG && (address <= 0L || capacity < 0) )
			throw new IllegalArgumentException();

		return ACCESSOR.setupBuffer(buffer, address, capacity);
	}

	// --- [ Direct memory access ] ---

	/**
	 * Sets all bytes in a given block of memory to a fixed value (usually zero).
	 *
	 * @param ptr   the starting memory address
	 * @param value the value to set (memSet will convert it to unsigned byte)
	 * @param bytes the number of bytes to set
	 */
	public static void memSet(long ptr, int value, int bytes) {
		if ( LWJGLUtil.DEBUG && ptr <= 0L )
			throw new IllegalArgumentException();

		ACCESSOR.memSet(ptr, value, bytes);
	}

	/**
	 * Sets all bytes in a given block of memory to a copy of another block.
	 *
	 * @param src   the source memory address
	 * @param dst   the destination memory address
	 * @param bytes the number of bytes to copy
	 */
	public static void memCopy(long src, long dst, int bytes) {
		if ( LWJGLUtil.DEBUG && (src <= 0L || dst <= 0L || bytes < 0) )
			throw new IllegalArgumentException();

		ACCESSOR.memCopy(src, dst, bytes);
	}

	// --- [ JNI utilities ] ---

	/**
	 * Returns the pointer size in bytes for the process that loaded LWJGL.
	 *
	 * @return the process pointer size in bytes.
	 */
	public static native int memPointerSize();

	/**
	 * Creates a new global reference to the specified Object.
	 *
	 * @param obj the Object
	 *
	 * @return the GlobalRef memory address.
	 */
	public static native long memGlobalRefNew(Object obj);

	/**
	 * Deletes a global reference.
	 *
	 * @param globalRef the GlobalRef memory address.
	 */
	public static native void memGlobalRefDelete(long globalRef);

	// The standard C memset function
	static native void nMemSet(long ptr, int value, long bytes);

	// The standard C memcpy function
	static native void nMemCopy(long dst, long src, long bytes);

	// Returns the buffer memory address
	static native long nGetAddress(Buffer buffer);

	// Returns a new direct ByteBuffer instance
	static native ByteBuffer nNewBuffer(long address, int capacity);

	// --- [ String utilities ] ---

	/**
	 * Returns a ByteBuffer containing the specified text ASCII encoded and null-terminated.
	 * If text is null, null is returned.
	 *
	 * @param text the text to encode
	 *
	 * @return the encoded text or null
	 */
	public static ByteBuffer memEncodeASCII(final CharSequence text) {
		return memEncodeASCII(text, true);
	}

	/**
	 * Returns a ByteBuffer containing the specified text ASCII encoded and optionally null-terminated.
	 * If text is null, null is returned.
	 *
	 * @param text           the text to encode
	 * @param nullTerminated if true, the text will be terminated with a '\0'.
	 *
	 * @return the encoded text or null
	 */
	public static ByteBuffer memEncodeASCII(final CharSequence text, final boolean nullTerminated) {
		if ( text == null )
			return null;

		final ByteBuffer buffer = BufferUtils.createByteBuffer(text.length() + (nullTerminated ? 1 : 0));

		for ( int i = 0; i < text.length(); i++ )
			buffer.put(i, (byte)text.charAt(i));

		if ( nullTerminated )
			buffer.put(text.length(), (byte)0);

		return buffer;
	}

	/**
	 * Returns a ByteBuffer containing the specified text UTF-8 encoded and null-terminated.
	 * If text is null, null is returned.
	 *
	 * @param text the text to encode
	 *
	 * @return the encoded text or null
	 */
	public static ByteBuffer memEncodeUTF8(final CharSequence text) {
		return memEncodeUTF8(text, true);
	}

	/**
	 * Returns a ByteBuffer containing the specified text UTF-8 encoded and optionally null-terminated.
	 * If text is null, null is returned.
	 *
	 * @param text           the text to encode
	 * @param nullTerminated if true, the text will be terminated with a '\0'.
	 *
	 * @return the encoded text or null
	 */
	public static ByteBuffer memEncodeUTF8(final CharSequence text, final boolean nullTerminated) {
		return memEncode(text, UTF8, nullTerminated);
	}

	/**
	 * Returns a ByteBuffer containing the specified text UTF-16 encoded and null-terminated.
	 * If text is null, null is returned.
	 *
	 * @param text the text to encode
	 *
	 * @return the encoded text
	 */
	public static ByteBuffer memEncodeUTF16(final CharSequence text) {
		return memEncodeUTF16(text, true);
	}

	/**
	 * Returns a ByteBuffer containing the specified text UTF-16 encoded and optionally null-terminated.
	 * If text is null, null is returned.
	 *
	 * @param text           the text to encode
	 * @param nullTerminated if true, the text will be terminated with a '\0'.
	 *
	 * @return the encoded text
	 */
	public static ByteBuffer memEncodeUTF16(final CharSequence text, final boolean nullTerminated) {
		if ( text == null )
			return null;

		final ByteBuffer buffer = BufferUtils.createByteBuffer((text.length() + (nullTerminated ? 1 : 0)) << 1);

		for ( int i = 0; i < text.length(); i++ )
			buffer.putChar(i << 1, text.charAt(i));

		if ( nullTerminated )
			buffer.putChar(buffer.capacity() - 2, '\0');

		return buffer;
	}

	/**
	 * Wraps the specified text in a CharBuffer and encodes it using the specified Charset and null-terminated.
	 *
	 * @param text    the text to encode
	 * @param charset the charset to use for encoding
	 *
	 * @return the encoded text
	 */
	public static ByteBuffer memEncode(final CharSequence text, final Charset charset) {
		return memEncode(text, charset, true);
	}

	/**
	 * Wraps the specified text in an optionally null-terminated CharBuffer and encodes it using the specified Charset.
	 *
	 * @param text           the text to encode
	 * @param charset        the charset to use for encoding
	 * @param nullTerminated if true, the text will be terminated with a '\0'.
	 *
	 * @return the encoded text
	 */
	public static ByteBuffer memEncode(final CharSequence text, final Charset charset, final boolean nullTerminated) {
		if ( text == null )
			return null;

		return encode(CharBuffer.wrap(nullTerminated ? new CharSequenceNT(text) : text), charset);
	}

	/**
	 * A {@link java.nio.charset.CharsetEncoder#encode(java.nio.CharBuffer)} implementation that uses {@link org.lwjgl.BufferUtils#createByteBuffer(int)}
	 * instead of {@link java.nio.ByteBuffer#allocate(int)}.
	 *
	 * @see java.nio.charset.CharsetEncoder#encode(java.nio.CharBuffer)
	 */
	private static ByteBuffer encode(final CharBuffer in, final Charset charset) {
		final CharsetEncoder encoder = charset.newEncoder(); // encoders are not thread-safe, create a new one on every call

		int n = (int)(in.remaining() * encoder.averageBytesPerChar());
		ByteBuffer out = BufferUtils.createByteBuffer(n);

		if ( n == 0 && in.remaining() == 0 )
			return out;

		encoder.reset();
		while ( true ) {
			CoderResult cr = in.hasRemaining() ? encoder.encode(in, out, true) : CoderResult.UNDERFLOW;
			if ( cr.isUnderflow() )
				cr = encoder.flush(out);

			if ( cr.isUnderflow() )
				break;

			if ( cr.isOverflow() ) {
				n = 2 * n + 1;    // Ensure progress; n might be 0!
				ByteBuffer o = BufferUtils.createByteBuffer(n);
				out.flip();
				o.put(out);
				out = o;
				continue;
			}

			try {
				cr.throwException();
			} catch (CharacterCodingException e) {
				throw new RuntimeException(e);
			}
		}
		out.flip();
		return out;
	}

	public static String memDecodeASCII(final ByteBuffer buffer) {
		if ( buffer == null )
			return null;

		final char[] chars = new char[buffer.remaining()];

		final int pos = buffer.position();
		for ( int i = 0; i < chars.length; i++ )
			chars[i] = (char)buffer.get(pos + i);

		return new String(chars);
	}

	public static String memDecodeUTF8(final ByteBuffer buffer) {
		return memDecode(buffer, UTF8);
	}

	public static String memDecodeUTF16(final ByteBuffer buffer) {
		if ( buffer == null )
			return null;

		final char[] chars = new char[buffer.remaining() >> 1];

		final int pos = buffer.position();
		for ( int i = 0; i < chars.length; i++ )
			chars[i] = buffer.getChar(pos + (i << 1));

		return new String(chars);
	}

	public static String memDecode(final ByteBuffer buffer, final Charset charset) {
		if ( buffer == null )
			return null;

		return decodeImpl(buffer, charset);
	}

	private static String decodeImpl(final ByteBuffer in, final Charset charset) {
		final CharsetDecoder decoder = charset.newDecoder(); // decoders are not thread-safe, create a new one on every call

		int n = (int)(in.remaining() * decoder.averageCharsPerByte());
		CharBuffer out = BufferUtils.createCharBuffer(n);

		if ( (n == 0) && (in.remaining() == 0) )
			return "";

		decoder.reset();
		for (; ; ) {
			CoderResult cr = in.hasRemaining() ? decoder.decode(in, out, true) : CoderResult.UNDERFLOW;
			if ( cr.isUnderflow() )
				cr = decoder.flush(out);

			if ( cr.isUnderflow() )
				break;
			if ( cr.isOverflow() ) {
				n = 2 * n + 1;    // Ensure progress; n might be 0!
				CharBuffer o = BufferUtils.createCharBuffer(n);
				out.flip();
				o.put(out);
				out = o;
				continue;
			}
			try {
				cr.throwException();
			} catch (CharacterCodingException e) {
				throw new RuntimeException(e);
			}
		}
		out.flip();
		return out.toString();
	}

}