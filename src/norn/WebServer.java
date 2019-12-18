/*
 * Copyright (c) 2018 MIT 6.031 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course
 * staff.
 */
package norn;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import edu.mit.eecs.parserlib.internal.ParseException;
import norn.web.ExceptionsFilter;
import norn.web.LogFilter;

/**
 * HTTP web server for norn mailing list.
 * 
 * Specification as found in:
 * http://web.mit.edu/6.031/www/fa19/projects/norn/spec/
 */
public class WebServer {

    private final HttpServer server;
    private static final int VALID_RESPONSE = 200;
    private static final int DISTANCE_BETWEEN_LABELS = 80;
    private final ConcurrentMap<String, Expression> allLists;
    private static List<String> visual = new ArrayList<String>(); //this keeps track of each line of the visualization

    // AF(server) a webserver for the Norn mailing list that is launched on server
    // 'server'

    // RI: True

    // Safety from rep exposure: Server is private and final.

    // Thread safety argument:
    // Server is final and used only once in the setup of the server
    // Map uses thread safe datatype (ConcurrentMap) and is immutable

    /**
     * Make a new web server using that listens for connections on port.
     * 
     * @param port server port number
     * @throws IOException if an error occurs starting the server
     */ 
    public WebServer(int port) throws IOException {
        this.allLists = new ConcurrentHashMap<String, Expression>();
        this.server = HttpServer.create(new InetSocketAddress(port), 0);

        // handle concurrent requests with multiple threads
        server.setExecutor(Executors.newCachedThreadPool());

        List<Filter> logging = List.of(new ExceptionsFilter(), new LogFilter());

        HttpContext show = server.createContext("/eval", exchange -> {
            new Thread(() -> {
                try {
                    handleVisualization(exchange);
                } catch (IOException | ParseException e) {
                    throw new RuntimeException("Could not make a new thread for look");
                }
            }).start();
        });
        show.getFilters().addAll(logging);
    }

    /**
     * @return the port on which this server is listening for connections
     */
    public int port() {
        return server.getAddress().getPort();
    }

    /**
     * Start this server in a new background thread.
     */
    public void start() {
        System.err.println("Server will listen on " + server.getAddress());
        server.start();
    }

    /**
     * Stop this server. Once stopped, this server cannot be restarted.
     */
    public void stop() {
        System.err.println("Server will stop");
        server.stop(0);
    }

    /**
     * Gets the map of lists of the webserver
     * @return a map of String to Expression for the webserver
     */
    public Map<String, Expression> getMap() {
        return this.allLists;
    }

    /**
     * Handle a request for /eval/ by using the URL arguments
     * to process the expression. Expects input in the form:
     * "/eval/<exp>" where <exp> is an expression string.
     * 
     * 
     * ALSO DOES VISUALIZATION. This is done with a tree structure where each node
     * has three children describing how it was formed (left exp, operation, right exp), or 1
     * child with all emails if it was only a list definition. Also has a second visuualization 
     * that explains the calculation performed for an expression
     * 
     * @param exchange HTTP request/response, modified by this method to send a
     *                 response to the client and close the exchange
     * @throws ParseException 
     */
    private void handleVisualization(HttpExchange exchange) throws IOException, ParseException {
        final int numDashesNewSection = 100;
        final int lengthTLD = 6;
        String extra = exchange.getRequestURI().toString().substring(lengthTLD);

        OutputStream body = exchange.getResponseBody();
        PrintWriter out = new PrintWriter(new OutputStreamWriter(body, UTF_8), true);

        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
        exchange.sendResponseHeaders(VALID_RESPONSE, 0);
        
        Boolean stop = false;

        try {
            final Expression result = Expression.parse(extra);

                final Set<String> evaluation = result.evaluate(this.allLists);

                out.print("<H2> Resulting expression parsed is: " + result + "  </H2> <H3> Which equals " + evaluation + "</H3>");
        }
        
        catch (Exception e) {
            stop = true; 
        }
  
        if (stop) {
            out.print("<H2> Warning: Expression has an error in it! </H2>");


        } else {
            out.println("<br><br>" + "-".repeat(numDashesNewSection) + "<br>");
            
            out.println("<H3> Recursive calculation & explanation </H3>");
            
            out.println("<br>" + "-".repeat(numDashesNewSection) + "<br>");
            
            visual = new ArrayList<>();
            
            try {
                WebServer.evaluateV2(Expression.parse(extra), this.allLists, 0);
            } catch (Exception e) {

            }
            
            for (String v : visual) {
                out.println(v + "<br><br>");
                
            }
            
            out.println("<br><br>" + "-".repeat(numDashesNewSection) + "<br>");
            
            out.println("<H3> Tree visualization of expression </H3>");
            
            out.println("<br>" + "-".repeat(numDashesNewSection) + "<br>");
            visual = new ArrayList<>();
            
            visual.add(
                    "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"500\" height=\"500\">");
            visual.add("<text x=\"10\" y=\"20\">expr</text>");
            
            
        }
        
        final int xOffset = 10;
        final int yOffset = 20;
        try {
            WebServer.evaluate(Expression.parse(extra), this.allLists, xOffset, yOffset, true);
        } catch (IllegalArgumentException e) {

        }
        
        visual.add("</svg>");
        
        for (String v : visual) {
            out.println(v);
        }

        exchange.close();
    } 

    /**
     * Evaluates an expression and returns the emails that form part of an expression, generating a visualisation with a tree structure at the same time.
     * 
     * @param expr the expression to evaluate
     * @param map a map mapping list name to an expression of what it contains
     * @param xRoot the x coordinate on the page of the parent of this current expression
     * @param yRoot the y coordinate on the page of the parent of this current expression
     * @param drawLine a flag. true -> print the next line segment, false -> print the previous linee segment
     * @return a set of emails that correspond to this expression
     */
    private static SortedSet<String> evaluate(Expression expr, Map<String, Expression> map, int xRoot, int yRoot,
            Boolean drawLine) {
        switch (expr.getClass().getSimpleName()) {
        case "Email": {
            if (drawLine) {
                String line1 = "  <line x1=\"" + xRoot + "\" y1=\"" + yRoot + "\" x2=\"" + xRoot + "\" y2=\""
                        + (yRoot + DISTANCE_BETWEEN_LABELS) + "\" stroke=\"blue\"/>";
                visual.add(line1);
            } else {
                String line1 = "  <line x1=\"" + (xRoot - DISTANCE_BETWEEN_LABELS) + "\" y1=\"" + yRoot + "\" x2=\"" + xRoot + "\" y2=\""
                        + (yRoot + DISTANCE_BETWEEN_LABELS) + "\" stroke=\"blue\"/>";
                visual.add(line1);
            }
            String entry = "<text x=\"" + xRoot + "\" y=\"" + (yRoot + DISTANCE_BETWEEN_LABELS) + "\">" + expr.toString() + "</text>";
            visual.add(entry);
            return new TreeSet<String>(Set.of(expr.toString()));
        }
        case "MailingList": {
            MailingList m = (MailingList) expr;
            String name = m.getName();

            if (drawLine) {
                String line1 = "  <line x1=\"" + xRoot + "\" y1=\"" + yRoot + "\" x2=\"" + xRoot + "\" y2=\""
                        + (yRoot + DISTANCE_BETWEEN_LABELS) + "\" stroke=\"blue\"/>";
                visual.add(line1);
            }

            if (map.containsKey(name)) {
                String entry = "<text x=\"" + xRoot + "\" y=\"" + (yRoot + DISTANCE_BETWEEN_LABELS) + "\">" + name + "</text>";
                visual.add(entry);
                return evaluate(map.get(name), map, xRoot, yRoot + DISTANCE_BETWEEN_LABELS, true);
            } else {
                map.put(name, EmptyEmailSet.getEmpty());
                String entry = "<text x=\"" + xRoot + "\" y=\"" + (yRoot + DISTANCE_BETWEEN_LABELS) + "\">" + "empty" + "</text>";
                visual.add(entry);
                return new TreeSet<String>(Set.of());
            }
        }
        case "Union": {
            Union e = (Union) expr;
            Expression left = e.getLeft();
            Expression right = e.getRight();

            if (drawLine) {
                String line1 = "  <line x1=\"" + xRoot + "\" y1=\"" + yRoot + "\" x2=\"" + xRoot + "\" y2=\""
                        + (yRoot + DISTANCE_BETWEEN_LABELS) + "\" stroke=\"blue\"/>";
                visual.add(line1);
            } else {
                String line1 = "  <line x1=\"" + (xRoot - DISTANCE_BETWEEN_LABELS) + "\" y1=\"" + yRoot + "\" x2=\"" + xRoot + "\" y2=\""
                        + (yRoot + DISTANCE_BETWEEN_LABELS) + "\" stroke=\"blue\"/>";
                visual.add(line1);
            }

            String entry = "<text x=\"" + xRoot + "\" y=\"" + (yRoot + DISTANCE_BETWEEN_LABELS) + "\">" + "union" + "</text>";
            visual.add(entry);

            Set<String> emailsLeft = new HashSet<String>(evaluate(left, map, xRoot, yRoot + DISTANCE_BETWEEN_LABELS, true));
            Set<String> emailsRight = new HashSet<String>(evaluate(right, map, xRoot + DISTANCE_BETWEEN_LABELS, yRoot + DISTANCE_BETWEEN_LABELS, false));

            emailsLeft.addAll(emailsRight);
            return Collections.unmodifiableSortedSet(new TreeSet<String>(emailsLeft));
        }
        case "Difference": {
            Difference e = (Difference) expr;
            Expression left = e.getLeft();
            Expression right = e.getRight();

            if (drawLine) {
                String line1 = "  <line x1=\"" + xRoot + "\" y1=\"" + yRoot + "\" x2=\"" + xRoot + "\" y2=\""
                        + (yRoot + DISTANCE_BETWEEN_LABELS) + "\" stroke=\"blue\"/>";
                visual.add(line1);
            } else {
                String line1 = "  <line x1=\"" + (xRoot - DISTANCE_BETWEEN_LABELS) + "\" y1=\"" + yRoot + "\" x2=\"" + xRoot + "\" y2=\""
                        + (yRoot + DISTANCE_BETWEEN_LABELS) + "\" stroke=\"blue\"/>";
                visual.add(line1);
            }

            String entry = "<text x=\"" + xRoot + "\" y=\"" + (yRoot + DISTANCE_BETWEEN_LABELS) + "\">" + "diff" + "</text>";
            visual.add(entry);

            Set<String> emailsLeft = new HashSet<String>(evaluate(left, map, xRoot, yRoot + DISTANCE_BETWEEN_LABELS, true));
            Set<String> emailsRight = new HashSet<String>(evaluate(right, map, xRoot + DISTANCE_BETWEEN_LABELS, yRoot + DISTANCE_BETWEEN_LABELS, false));
            SortedSet<String> ret = new TreeSet<String>();

            for (String email : emailsLeft) {
                if (!emailsRight.contains(email)) {
                    ret.add(email);
                }
            }

            return Collections.unmodifiableSortedSet(ret);
        }
        case "Intersection": {
            Intersection e = (Intersection) expr;
            Expression left = e.getLeft();
            Expression right = e.getRight();

            if (drawLine) {
                String line1 = "  <line x1=\"" + xRoot + "\" y1=\"" + yRoot + "\" x2=\"" + xRoot + "\" y2=\""
                        + (yRoot + DISTANCE_BETWEEN_LABELS) + "\" stroke=\"blue\"/>";
                visual.add(line1);
            } else {
                String line1 = "  <line x1=\"" + (xRoot - DISTANCE_BETWEEN_LABELS) + "\" y1=\"" + yRoot + "\" x2=\"" + xRoot + "\" y2=\""
                        + (yRoot + DISTANCE_BETWEEN_LABELS) + "\" stroke=\"blue\"/>";
                visual.add(line1);
            }

            String entry = "<text x=\"" + xRoot + "\" y=\"" + (yRoot + DISTANCE_BETWEEN_LABELS) + "\">" + "and" + "</text>";
            visual.add(entry);

            Set<String> emailsLeft = new HashSet<String>(evaluate(left, map, xRoot, yRoot + DISTANCE_BETWEEN_LABELS, true));
            Set<String> emailsRight = new HashSet<String>(evaluate(right, map, xRoot + DISTANCE_BETWEEN_LABELS, yRoot + DISTANCE_BETWEEN_LABELS, false));
            SortedSet<String> ret = new TreeSet<String>();

            for (String email : emailsLeft) {
                if (emailsRight.contains(email)) {
                    ret.add(email);
                }
            }

            return Collections.unmodifiableSortedSet(ret);
        }
        case "ListDefinition": {
            ListDefinition e = (ListDefinition) expr;
            String name = e.getName();

            // Expression a = cycle(name, e.getExpr(), map);
            Expression a = e.getExpr();

            if (drawLine) {
                String line1 = "  <line x1=\"" + xRoot + "\" y1=\"" + yRoot + "\" x2=\"" + xRoot + "\" y2=\""
                        + (yRoot + DISTANCE_BETWEEN_LABELS) + "\" stroke=\"blue\"/>";
                visual.add(line1);
            } else {
                String line1 = "  <line x1=\"" + (xRoot - DISTANCE_BETWEEN_LABELS) + "\" y1=\"" + yRoot + "\" x2=\"" + xRoot + "\" y2=\""
                        + (yRoot + DISTANCE_BETWEEN_LABELS) + "\" stroke=\"blue\"/>";
                visual.add(line1);
            }
            String entry = "<text x=\"" + xRoot + "\" y=\"" + (yRoot + DISTANCE_BETWEEN_LABELS) + "\">" + " = " + "</text>";
            visual.add(entry);

            map.put(name, a);
            e = new ListDefinition(name, a);

            SortedSet<String> s = evaluate(e.getExpr(), map, xRoot, yRoot + DISTANCE_BETWEEN_LABELS, true);
            return s;
        }
        case "Sequence": {
            Sequence e = (Sequence) expr;
            evaluate(e.getLeft(), map, xRoot, yRoot, true);

            return evaluate(e.getRight(), map, xRoot, yRoot+DISTANCE_BETWEEN_LABELS, true);
        }
        case "Parallel": {
            Parallel e = (Parallel) expr;

            // if valid pipe
            Expression left = e.getLeft();
            Expression right = e.getRight();
            
            if (drawLine) {
                String line1 = "  <line x1=\"" + xRoot + "\" y1=\"" + yRoot + "\" x2=\"" + xRoot + "\" y2=\""
                        + (yRoot + DISTANCE_BETWEEN_LABELS) + "\" stroke=\"blue\"/>";
                visual.add(line1);
            } else {
                String line1 = "  <line x1=\"" + (xRoot - DISTANCE_BETWEEN_LABELS) + "\" y1=\"" + yRoot + "\" x2=\"" + xRoot + "\" y2=\""
                        + (yRoot + DISTANCE_BETWEEN_LABELS) + "\" stroke=\"blue\"/>";
                visual.add(line1);
            }
            
            String entry = "<text x=\"" + xRoot + "\" y=\"" + (yRoot + DISTANCE_BETWEEN_LABELS) + "\">" + " | " + "</text>";
            visual.add(entry);

            Thread t1 = new Thread(() -> {
                evaluate(left, map, xRoot, yRoot, true);
            });

            Thread t2 = new Thread(() -> {
                evaluate(right, map, xRoot, yRoot, false);
            });

            t1.start();
            t2.start();

            try {
                t1.join();
                t2.join();
            } catch (InterruptedException e1) {
             }

            return new TreeSet<String>();
        }
        case "EmptyEmailSet": {
            if (drawLine) {
                String line1 = "  <line x1=\"" + xRoot + "\" y1=\"" + yRoot + "\" x2=\"" + xRoot + "\" y2=\""
                        + (yRoot + DISTANCE_BETWEEN_LABELS) + "\" stroke=\"blue\"/>";
                visual.add(line1);
            } else {
                String line1 = "  <line x1=\"" + (xRoot - DISTANCE_BETWEEN_LABELS) + "\" y1=\"" + yRoot + "\" x2=\"" + xRoot + "\" y2=\""
                        + (yRoot + DISTANCE_BETWEEN_LABELS) + "\" stroke=\"blue\"/>";
                visual.add(line1);
            }
            String entry = "<text x=\"" + xRoot + "\" y=\"" + (yRoot + DISTANCE_BETWEEN_LABELS) + "\">" + " empty " + "</text>";
            visual.add(entry);

            return new TreeSet<String>(Set.of());
        }
        default:
            System.out.println(expr.getClass().getSimpleName());
            throw new AssertionError("should never get here while evaluating");

        }
    }

    /**
     * Evaluates an expression and returns the emails that form part of an expression, 
     * generating a visualisation with an indented recursive structure at the same time.
     * 
     * @param expr the expression to evaluate
     * @param map a map mapping list name to an expression of what it contains
     * @param depth keeps track of how deep down the recursion trace we are
     * @return a set of emails that correspond to this expression
     */
    private static SortedSet<String> evaluateV2(Expression expr, Map<String, Expression> map, int depth) {
        switch (expr.getClass().getSimpleName()) {
        case "Email": {
            String line1 = "-".repeat(depth * 2) + "email: " + expr.toString();
            visual.add(line1);

            return new TreeSet<String>(Set.of(expr.toString()));
        }
        case "MailingList": {
            MailingList m = (MailingList) expr;
            String name = m.getName();

            if (map.containsKey(name)) {
                String line1 = "-".repeat(depth * 2) + name + ": " + expr.toString();
                visual.add(line1);
                return evaluateV2(map.get(name), map, depth * 2 + 1);
            } else {
                map.put(name, EmptyEmailSet.getEmpty());
                String line1 = "-".repeat(depth * 2) + name + ": empty";
                visual.add(line1);
                return new TreeSet<String>(Set.of());
            }
        }
        case "Union": {
            Union e = (Union) expr;
            Expression left = e.getLeft();
            Expression right = e.getRight();

            String line1 = "-".repeat(depth * 2) + "union: " + e.getLeft().toString() + " , " + e.getRight().toString() + " := " + e.toString();
            visual.add(line1);

            Set<String> emailsLeft = new HashSet<String>(evaluateV2(left, map, depth + 1));
            Set<String> emailsRight = new HashSet<String>(evaluateV2(right, map, depth + 1));

            emailsLeft.addAll(emailsRight);
            return Collections.unmodifiableSortedSet(new TreeSet<String>(emailsLeft));
        }
        case "Difference": {
            Difference e = (Difference) expr;
            Expression left = e.getLeft();
            Expression right = e.getRight();

            String line1 = "-".repeat(depth * 2) + "difference: " + e.getLeft().toString() + " ! "
                    + e.getRight().toString() + " := " + e.toString();
            visual.add(line1);

            Set<String> emailsLeft = new HashSet<String>(evaluateV2(left, map, depth + 1));
            Set<String> emailsRight = new HashSet<String>(evaluateV2(right, map, depth + 1));
            SortedSet<String> ret = new TreeSet<String>();

            for (String email : emailsLeft) {
                if (!emailsRight.contains(email)) {
                    ret.add(email);
                }
            }

            return Collections.unmodifiableSortedSet(ret);
        }
        case "Intersection": {
            Intersection e = (Intersection) expr;
            Expression left = e.getLeft();
            Expression right = e.getRight();

            String line1 = "-".repeat(depth * 2) + "intersection: " + e.getLeft().toString() + " * "
                    + e.getRight().toString() + " := " +  e.toString();
            
            visual.add(line1);

            Set<String> emailsLeft = new HashSet<String>(evaluateV2(left, map, depth + 1));
            Set<String> emailsRight = new HashSet<String>(evaluateV2(right, map, depth + 1));
            SortedSet<String> ret = new TreeSet<String>();

            for (String email : emailsLeft) {
                if (emailsRight.contains(email)) {
                    ret.add(email);
                }
            }

            return Collections.unmodifiableSortedSet(ret);
        }
        case "ListDefinition": {
            ListDefinition e = (ListDefinition) expr;
            String name = e.getName();

            // Expression a = cycle(name, e.getExpr(), map);
            Expression a = e.getExpr();

            String line1 = "-".repeat(depth * 2) + name + " = " + a;
            visual.add(line1);

            map.put(name, a);
            e = new ListDefinition(name, a);

            SortedSet<String> s = evaluateV2(e.getExpr(), map, depth + 1);
            return s;
        }
        case "Sequence": {
            Sequence e = (Sequence) expr;
            evaluateV2(e.getLeft(), map, depth + 1);

            return evaluateV2(e.getRight(), map, depth + 1);
        }
        case "Parallel": {
            Parallel e = (Parallel) expr;

            // if valid pipe
            Expression left = e.getLeft();
            Expression right = e.getRight();

            String line1 = "-".repeat(depth * 2) + "parallel: " + e.getLeft().toString() + " | "
                    + e.getRight().toString();
            visual.add(line1);

            Thread t1 = new Thread(() -> {
                evaluateV2(left, map, depth + 1);
            });

            Thread t2 = new Thread(() -> {
                evaluateV2(right, map, depth + 1);
            });

            t1.start();
            t2.start();

            try {
                t1.join();
                t2.join();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

            return new TreeSet<String>();
        }
        case "EmptyEmailSet": {

            String line1 = "-".repeat(depth * 2) + "empty email ";
            visual.add(line1);

            return new TreeSet<String>(Set.of());
        }
        default:
            System.out.println(expr.getClass().getSimpleName());
            throw new AssertionError("should never get here while evaluating");

        }
    }

}
