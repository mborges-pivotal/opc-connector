package com.gopivotal.tola.opc.boot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.*;
import org.springframework.stereotype.*;

/**
 * Console - really just a commandline wait thingy.
 * @author mborges
 *
 */
@Component
public class Console implements CommandLineRunner {

	@Value("${opc.host}")
	private String opcHost;
	
	private boolean running = false;

    public void run(String... args) {
    	System.out.println("Console running....");
    	System.out.println("opc.host=" + opcHost);
        while(running) {
        	System.out.print("opc> ");
        	String line = System.console().readLine();
        	if (line.toUpperCase().equals("STOP")) {
        		running = false;
        	}
        }
    }

}