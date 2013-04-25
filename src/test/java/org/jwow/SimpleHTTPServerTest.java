package org.jwow;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class SimpleHTTPServerTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public SimpleHTTPServerTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( SimpleHTTPServerTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testSimpleHTTPServer()
    {
        assertTrue( true );
    }
}
