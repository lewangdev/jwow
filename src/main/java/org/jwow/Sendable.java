package org.jwow;

import java.io.IOException;

import org.jwow.channel.ChannelIO;

public interface Sendable {

	/**
	 * You have to call this before call the other methods belongs
	 * to interface Sendable
	 */
	public void prepare() throws IOException;

	public boolean send(ChannelIO cio) throws IOException;

	public void release() throws IOException;
}