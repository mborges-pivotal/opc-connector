package com.gopivotal.tola.opc.boot;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.boot.*;
import org.springframework.stereotype.*;

/**
 * Console - really just a commandline wait thingy.
 * @author mborges
 *
 */
@Component
public class Console implements CommandLineRunner {
	
	Logger logger = LoggerFactory
			.getLogger("com.gopivotal.tola.opc.boot.CommandLineRunner");

	@Autowired
	private Environment env;

    public void run(String... args) {
    	
    	logger.debug(env.toString());
    	
    	Map<String, Object> map = new HashMap<String, Object>();
        for(Iterator<?> it = ((AbstractEnvironment) env).getPropertySources().iterator(); it.hasNext(); ) {
            PropertySource<?> propertySource = (PropertySource<?>) it.next();
            if (propertySource instanceof MapPropertySource) {
            	if (propertySource.getName().startsWith("applicationConfig")) {
            		map.putAll(((MapPropertySource) propertySource).getSource());
            	}
            }
        }
               
    	for(String name: map.keySet()) {
    		logger.debug("{} -> {}", name, map.get(name));
    	}    	
    	
    }

}