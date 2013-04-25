package org.jwow.contentImpl;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;

import org.jwow.IContent;
import org.jwow.channel.ChannelIO;

public class StringContent implements IContent {

	private static Charset charset = Charset.forName("UTF-8");
	private String type;
	private String content;

	public StringContent(CharSequence c, String t) {
		content = c.toString();
		if (!content.endsWith("\n"))
			content += "\n";
		type = t + "; charset=UTF-8";
	}

	public StringContent(CharSequence c) {
		this(c, "text/plain");
	}

	public StringContent(Exception x) {
		StringWriter sw = new StringWriter();
		x.printStackTrace(new PrintWriter(sw));
		type = "text/plain; charset=UTF-8";
		content = sw.toString();
	}

	public String type() {
		return type;
	}

	private ByteBuffer bb = null;

	private void encode() {
		if (bb == null)
			bb = charset.encode(CharBuffer.wrap(content));
	}

	public long length() {
		encode();
		return bb.remaining();
	}

	public void prepare() {
		encode();
		bb.rewind();
	}

	public boolean send(ChannelIO cio) throws IOException {
		if (bb == null)
			throw new IllegalStateException();
		cio.write(bb);

		return bb.hasRemaining();
	}

	public void release() throws IOException {
	}
}