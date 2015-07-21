package com.gopivotal.tola.opc;

import com.gopivotal.tola.opc.type.Quality;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class QualityTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public QualityTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( QualityTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
		Quality a1 = new Quality((short)192);
		Quality a2 = new Quality((short)28);
		Quality a3 = new Quality((short)72);
		Quality a4 = new Quality((short)159);
		
		System.out.println(a1);
		System.out.println(a2);
		System.out.println(a3);
		System.out.println(a4);
  	
        assertTrue( true );
    }
}
