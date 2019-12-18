/* Copyright (c) 2018 MIT 6.031 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package norn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import edu.mit.eecs.parserlib.internal.ParseException;

/**
 * Start the Norn mailing list system console interface and web server.
 * <p>You are free to change this class.
 */
public class Main {
    
    /**
     * Read expression and command inputs from the console and output results,
     * and start a web server to handle requests from remote clients.
     * An empty console input terminates the program.
     * @param args unused
     * @throws IOException if there is an error reading the input
     * @throws ParseException 
     * @throws IllegalArgumentException 
     */
    public static void main (String[] args) throws IOException, IllegalArgumentException, ParseException {
        
        final WebServer web = new WebServer(8080);
        web.start();
        
        final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        
        while (true) {
            System.out.print("> ");
            final String input = in.readLine();

            if (input.isEmpty()) {
                web.stop();
                return; // exits the program
            }
            
            String[] arr = input.split(" ");
            
            if (arr[0].equals("/save")) {
                Map<String, Expression> map = web.getMap();
                String fileName = "./files/"+arr[1];
                String str = "";

                File file = new File(fileName);
                file.createNewFile();
                
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                
                for (Map.Entry<String, Expression> entry : map.entrySet()) {
                    str += entry.getKey() + " = " + entry.getValue().toString()+"\n";
                }
                
                
                writer.write(str);
                 
                writer.close();
                
                
            } else if (arr[0].equals("/load")){
                String fileName = "./files/"+arr[1];
                BufferedReader file = new BufferedReader(new FileReader(fileName));
                
                String text = file.readLine();
                while (text!=null) {
                    final Expression expression = Expression.parse(text);
                    expression.evaluate(web.getMap());
                    text = file.readLine();
                }
                
                
                file.close();
                
                
            }
            else {
                final Expression expression = Expression.parse(input);
                SortedSet<String> emails = expression.evaluate(web.getMap());
                System.out.println(formatOutput(emails));
            }
            
            
        }
    }
    
    /**
     * Creates a new instance of Norn mailing list system and parses, evaluates the expression given
     * 
     * @param expression the expression to parse
     * @return a set of string email names that the expression corresponds to
     */
    public static SortedSet<String> parseFactory (Expression expression) {
        Map<String, Expression> map = new HashMap<String, Expression>();
        SortedSet<String> a = expression.evaluate(map);
        return a;
        
    }
    
    /**
     * Formats a set of string email names to a viewable string format (eg. name1, name2, name3 ...)
     * 
     * @param emails the set of string email names
     * @return formatted string of emails 
     */
    public static String formatOutput (SortedSet<String> emails) {
        String out = "";
        boolean first = true;
        for(String email : emails) {
            if(first) { 
                out = email;
                first = false;
            }
            else {
                out = out + ", " + email;
            }
        }
            
        return out;
    }
}
