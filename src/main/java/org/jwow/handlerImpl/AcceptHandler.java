package org.jwow.handlerImpl;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.jwow.IHandler;
import org.jwow.channel.ChannelIO;

public class AcceptHandler implements IHandler {
	public void handle(SelectionKey key) throws IOException {
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key
				.channel();
		SocketChannel socketChannel = serverSocketChannel.accept();
		if (socketChannel == null)
			return;
		
		ChannelIO cio = new ChannelIO(socketChannel, false);
		RequestHandler requestHandler = new RequestHandler(cio);
		socketChannel.register(key.selector(), SelectionKey.OP_READ, requestHandler);
	}
}