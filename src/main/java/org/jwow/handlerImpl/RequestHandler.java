package org.jwow.handlerImpl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

import org.jwow.IHandler;
import org.jwow.channel.ChannelIO;
import org.jwow.contentImpl.FileContent;
import org.jwow.contentImpl.StringContent;
import org.jwow.exception.MalformedRequestException;
import org.jwow.protocol.Request;
import org.jwow.protocol.Response;

public class RequestHandler implements IHandler {
	private ChannelIO channelIO;
	private ByteBuffer requestByteBuffer = null;

	private boolean requestReceived = false;
	private Request request = null;
	private Response response = null;

	RequestHandler(ChannelIO channelIO) {
		this.channelIO = channelIO;
	}

	private boolean receive(SelectionKey sk) throws IOException {

		if (requestReceived)
			return true;

		if ((channelIO.read() < 0)
				|| Request.isComplete(channelIO.getReadBuf())) {
			requestByteBuffer = channelIO.getReadBuf();
			return (requestReceived = true);
		}
		return false;
	}

	private boolean parse() throws IOException {
		try {
			request = Request.parse(requestByteBuffer);
			return true;
		} catch (MalformedRequestException x) {

			response = new Response(Response.Code.BAD_REQUEST,
					new StringContent(x));
		}
		return false;
	}

	private void build() throws IOException {
		Request.Action action = request.action();

		if ((action != Request.Action.GET) && (action != Request.Action.HEAD)) {
			response = new Response(Response.Code.METHOD_NOT_ALLOWED,
					new StringContent("Method Not Allowed"));
		} else {
			response = new Response(Response.Code.OK, new FileContent(request
					.uri()), action);
		}
	}

	public void handle(SelectionKey sk) throws IOException {
		try {
			if (request == null) {
				if (!receive(sk))
					return;
				requestByteBuffer.flip();

				if (parse())
					build();

				try {
					response.prepare();
				} catch (IOException x) {
					response.release();
					response = new Response(Response.Code.NOT_FOUND,
							new StringContent(x));
					response.prepare();
				}

				if (send()) {

					sk.interestOps(SelectionKey.OP_WRITE);
				} else {
					channelIO.close();
					response.release();
				}
			} else {
				if (!send()) {
					channelIO.close();
					response.release();
				}
			}
		} catch (IOException x) {
			x.printStackTrace();
			channelIO.close();
			if (response != null) {
				response.release();
			}
		}
	}

	private boolean send() throws IOException {
		return response.send(channelIO);
	}
}
