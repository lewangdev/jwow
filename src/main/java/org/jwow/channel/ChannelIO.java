package org.jwow.channel;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

/**
 * Wrapped the class ByteBuffer
 * It's a good way to 
 * @author root
 *
 */
public class ChannelIO {
	protected SocketChannel socketChannel;
	protected ByteBuffer requestBuffer;
	private static int requestBufferSize = 4096;

	public ChannelIO(SocketChannel socketChannel, boolean blocking)
			throws IOException {
		this.socketChannel = socketChannel;
		socketChannel.configureBlocking(blocking);
		requestBuffer = ByteBuffer.allocate(requestBufferSize);
	}

	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	protected void resizeRequestBuffer(int remaining) {
		if (requestBuffer.remaining() < remaining) {
			ByteBuffer bb = ByteBuffer.allocate(requestBuffer.capacity() * 2);
			requestBuffer.flip();
			bb.put(requestBuffer);
			requestBuffer = bb;
		}
	}

	public int read() throws IOException {
		//Before read, you need to check whether there
		//is enough space for the action.
		resizeRequestBuffer(requestBufferSize / 20);
		return socketChannel.read(requestBuffer);
	}

	/**
	 * I think this is a dangerous way to get the Buffer.
	 * How can you know the msg sent from client is end?
	 * If you do any read action from that you will get 
	 * unexpectable results.
	 * @return
	 */
	public ByteBuffer getReadBuf() {
		return requestBuffer;
	}

	//Common way to write.
	public int write(ByteBuffer src) throws IOException {
		return socketChannel.write(src);
	}

	public long transferTo(FileChannel fc, long pos, long len)
			throws IOException {
		//Cool way to put the file into the socket.
		return fc.transferTo(pos, len, socketChannel);
	}

	public void close() throws IOException {
		socketChannel.close();
	}
}