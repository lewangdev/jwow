package org.jwow.contentImpl;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;

import org.jwow.IContent;
import org.jwow.SimpleHTTPServer;
import org.jwow.channel.ChannelIO;
import org.jwow.utils.Logger;

public class FileContent implements IContent {
	private static File ROOT = new File("/home/le/Downloads/linux/xx/compiled/");
	private File file;
	public FileContent(URI uri) {
		file = new File(ROOT, uri.getPath().replace('/', File.separatorChar));
		if (file.exists() && file.isDirectory()) {
			try {
				uri = new URI(uri.toString() + "/index.html");
				file = new File(ROOT, uri.getPath().replace('/',
						File.separatorChar));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		Logger.info(uri.toString() + (file.exists() ? " ok" : " missed"));
	}

	private String type = null;


	// Set the http content type
	public String type() {
		if (type != null)
			return type;
		String nm = file.getName();
		type = SimpleHTTPServer.getType(nm);
		return type;
	}

	// Represents another channel to manipulate the file.
	private FileChannel fileChannel = null;
	private long length = -1;
	private long position = -1;

	public long length() {
		return length;
	}

	/**
	 * You have to call this before call the other methods belongs
	 * to interface Content
	 */
	public void prepare() throws IOException {
		if (fileChannel == null)
			fileChannel = new RandomAccessFile(file, "r").getChannel();
		length = fileChannel.size();
		position = 0;
	}

	public boolean send(ChannelIO channelIO) throws IOException {
		if (fileChannel == null)
			throw new IllegalStateException();
		if (position < 0)
			throw new IllegalStateException();

		if (position >= length) {
			return false;
		}

		position += channelIO.transferTo(fileChannel, position, length
				- position);
		return (position < length);
	}

	public void release() throws IOException {
		if (fileChannel != null) {
			fileChannel.close();
			fileChannel = null;
		}
	}
}