/* Copyright (c) 2017-2018 MIT 6.031 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package norn;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;


import org.junit.jupiter.api.Test;

/**
 *Tests WebServer.java with partitions below.
 */
public class WebServerTest {
    
    /*
     * Testing strategy: most testing will have to be in the manual tests section, given the visualization.
     * The ExpressionADTTest.java file should test functionality of the ADT Parser.
      *
      * eval request:
      *      expression is valid, not valid
      */
   
    // Manual tests
    
    //1. Run /eval/a@mit.edu,b@mit.edu and check that both visualizations are correct.
    //2. Run /eval/@ and check that invalid expression is shown.
    //3. Connect with two laptops to same server. 
    //      One Computer 1, run /eval/a=x@mit.edu, then go to Computer 2 and go to /eval/a, which should output x@mit.edu.
    //      Then go to Computer 2, run /eval/a=z@mit.edu, then go to Computer 1 and go to /eval/a, which should output x@mit.edu
    

    @Test
    public void testAssertionsEnabled() {
        assertThrows(AssertionError.class, () -> { assert false; },
                "make sure assertions are enabled with VM argument '-ea'");
    }
    
    // This tests for show expression invalid input
    @Test
    public void testEvalValid() throws IOException {
        final WebServer server = new WebServer(0);
        server.start();
        
        final URL valid = new URL("http://localhost:" + server.port() + "/eval/a@mit.edu");
        
        // in this test, we will just assert correctness of the server's output
        final InputStream input = valid.openStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
        
        reader.readLine();
        assert true;
        server.stop();
    }
    
    // This tests for show expression invalid input
    @Test
    public void testEvalInvalid() throws IOException {
        final WebServer server = new WebServer(0);
        server.start();
        
        final URL valid = new URL("http://localhost:" + server.port() + "/eval/@");
        
        // in this test, we will just assert correctness of the server's output
        final InputStream input = valid.openStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
        
        reader.readLine();
        assertEquals(null, reader.readLine(), "end of stream");
        
        server.stop();
    }


    //This tests for input expression invalid response
    @Test
    public void testLookInValid() throws IOException, URISyntaxException {
        final WebServer server = new WebServer(0);
        server.start();
        
        final URL invalid = new URL("http://localhost:" + server.port() + "/expression");
        
        // in this test, we will just assert correctness of the response code
        // unfortunately, an unsafe cast is required here to go from general
        //   URLConnection to the HTTP-specific HttpURLConnection that will
        //   always be returned when we connect to a "http://" URL
        final HttpURLConnection connection = (HttpURLConnection) invalid.openConnection();
        assertEquals(404, connection.getResponseCode(), "response code");
        server.stop();
    }

}
