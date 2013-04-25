package org.jwow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.net.InetSocketAddress;

import org.jwow.handlerImpl.AcceptHandler;
import org.jwow.utils.Logger;

/**
 * A very simple non-blocking HTTP protocol server.
 * Most of this codes are from JDK NIO example.
 * @author le
 *
 */
public class SimpleHTTPServer implements IHTTPServer{
	private Selector selector = null;
	private ServerSocketChannel serverSocketChannel = null;
	private static int port = 8000;
	private static HashMap<String, String> mimeMap = new HashMap<String, String>();

	public SimpleHTTPServer(int port) {
		try{
			selector = Selector.open();
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().setReuseAddress(true);
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.socket().bind(new InetSocketAddress(port));
			initMimeTypesMap();
			SimpleHTTPServer.port = port;
			Logger.info("%s:%s","Please use your browser to visit http://127.0.0.1", port);
		
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public SimpleHTTPServer() throws IOException {
		this(port);
	}

	@Override
	public void shutdown() {
		try {
			serverSocketChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void start() {
		try {
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT,
					new AcceptHandler());
			for (;;) {
				int n = selector.select();
	
				if (n == 0)
					continue;
				Set<SelectionKey> readyKeys = selector.selectedKeys();
				Iterator<SelectionKey> it = readyKeys.iterator();
				while (it.hasNext()) {
					SelectionKey key = null;
					try {
						key = (SelectionKey) it.next();
						it.remove();
						final IHandler handler = (IHandler) key.attachment();
						handler.handle(key);
					} catch (IOException e) {
						e.printStackTrace();
						try {
							if (key != null) {
								key.cancel();
								key.channel().close();
							}
						} catch (Exception ex) {
							e.printStackTrace();
						}
					}
				}// #while
			}// #while
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//
	//javax.activation.MimetypesFileTypeMap;
	private void initMimeTypesMap() {
		BufferedReader bfr = new BufferedReader(new InputStreamReader(SimpleHTTPServer.class.getResourceAsStream(
				"resources/mime.types")));
		String line = null;
		try {
			while((line = bfr.readLine()) != null) {
				//Logger.info(line);
				line = line.trim();
				if(line.length() > 0) {
					String[] kv = line.split(" ");
					mimeMap.put(kv[0].trim(), kv[1].trim());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getType(String name) {
		//String encode = "; charset=UTF-8";
		//String[] plains = {"html", "xhtml", "htm", "txt", "css", "js"};
		
		if(name.indexOf(".") < 0)
			return "text/plain";
		else {
			int pos = name.lastIndexOf(".");
			String appendix = name.substring(pos + 1);
			String type = mimeMap.get(appendix);
			if(type == null)
				return mimeMap.get("os");
			else 
				return type;
		}
		
	}
	
	public static void main(String args[]) throws Exception {
		final SimpleHTTPServer server = new SimpleHTTPServer();
		server.start();
	}
}