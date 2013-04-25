package org.jwow;
import java.io.*;
import java.nio.channels.*;

public interface IHandler {
    public void handle(SelectionKey key) throws IOException;
}