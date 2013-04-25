package org.jwow.protocol;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import org.jwow.IContent;
import org.jwow.Sendable;
import org.jwow.channel.ChannelIO;

public class Response implements Sendable {
	static public class Code {
		private int number;
		private String reason;

		private Code(int i, String r) {
			number = i;
			reason = r;
		}

		@Override
		public String toString() {
			return number + " " + reason;
		}

		public static Code OK = new Code(200, "OK");
		public static Code BAD_REQUEST = new Code(400, "Bad Request");
		public static Code NOT_FOUND = new Code(404, "Not Found");
		public static Code METHOD_NOT_ALLOWED = new Code(405, "Method Not Allowed");
	}

	private Code code;
	private IContent content;
	private boolean headersOnly;
	private ByteBuffer headerBuffer = null;

	public Response(Code rc, IContent c) {
		this(rc, c, null);
	}

	public Response(Code rc, IContent c, Request.Action head) {
		code = rc;
		content = c;
		headersOnly = (head == Request.Action.HEAD);
	}

	private static String CRLF = "\r\n";
	private static String serverName = "SimpleHTTPServer";
	private static Charset responseCharset = Charset.forName("UTF-8");

	private ByteBuffer headers() {
		CharBuffer cb = CharBuffer.allocate(1024);
		for (;;) {
			try {
				cb.put("HTTP/1.1 ").put(code.toString()).put(CRLF);
				cb.put("Server: ").put(serverName).put(CRLF);
				cb.put("Content-type: ").put(content.type()).put(CRLF);
				cb.put("Content-length: ").put(Long.toString(content.length()))
						.put(CRLF);
				cb.put(CRLF);
				break;
			} catch (BufferOverflowException x) {
				assert (cb.capacity() < (1 << 16));
				cb = CharBuffer.allocate(cb.capacity() * 2);
				continue;
			}
		}
		cb.flip();
		return responseCharset.encode(cb);
	}

	public void prepare() throws IOException {
		content.prepare();
		headerBuffer = headers();
	}

	public boolean send(ChannelIO cio) throws IOException {
		if (headerBuffer == null)
			throw new IllegalStateException();

		if (headerBuffer.hasRemaining()) {
			if (cio.write(headerBuffer) <= 0)
				return true;
		}

		if (!headersOnly) {
			if (content.send(cio))
				return true;
		}

		return false;
	}

	public void release() throws IOException {
		content.release();
	}
}