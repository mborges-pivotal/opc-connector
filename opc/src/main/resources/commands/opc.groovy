package commands

import org.crsh.cli.Usage
import org.crsh.cli.Command
import org.crsh.command.InvocationContext
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.BeanFactory;

import com.gopivotal.tola.console.OpcFactoryBean;
import com.gopivotal.tola.opc.ConnectionConfigurationImpl;

@Usage("OPC commands")
class opc {

	def OpcFactoryBean getOpcFactoryBean(InvocationContext context) {
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		OpcFactoryBean opcFactory = beanFactory.getBean(com.gopivotal.tola.console.OpcFactoryBean.class)
		return opcFactory
	}
		
	/**
	 * CONNECT
	 * 
	 * @param context
	 * @param name
	 * @return
	 */
	@Usage("Connect to OPC Server")
	@Command
	def connect(InvocationContext context, @Usage("The name of the Connection to create") @Argument String name) {
		println "Connection to OPC server..."
		
		Environment env = context.attributes["spring.environment"]
    	String[] args = [env.getProperty("opc.host"), env.getProperty("opc.domain"), env.getProperty("opc.user"), env.getProperty("opc.password"), "", env.getProperty("opc.server")]
		ConnectionConfigurationImpl connConfig = new ConnectionConfigurationImpl(args);

		println "Using parms: ${args}"
		
		if (name == null) {
			name = "test"
		}
		
		getOpcFactoryBean(context).createConnection(name, connConfig)
		
		return "Connection ${name} created"
	}
	
	/**
	 * DISCONNECT
	 * 
	 * @param context
	 * @param name
	 * @return
	 */
	@Usage("Disconnect from OPC Server")
	@Command
	def disconnect(InvocationContext context, @Usage("The name of the Connection") @Required @Argument String name) {
		
		println "Disconnecting to OPC server..."	
		getOpcFactoryBean(context).destroyConnection(name)
		
		return "Connection ${name} destroyed"
	}
	
	/**
	 * LIST CONNECTIONS
	 * 
	 * @param context
	 * @return
	 */
	@Usage("List OPC Server Connections")
	@Command
	def list(InvocationContext context) {
		println "Listing OPC server connections..."		
		return getOpcFactoryBean(context).listConnections()
	}

	/**
	 * DUMP TAGS
	 * @param context
	 * @param name
	 * @param branch
	 * @return
	 */
	@Usage("list OPC Server tags")
	@Command
	def tags(InvocationContext context, @Usage("The name of the Connection") @Required @Argument String name, @Usage("initial tag branch") @Argument String branch) {

		getOpcFactoryBean(context).dumpRootTree(name)
		
		return "See log for OPC tags"
	}
	
	@Usage("Add tag to listen")
	@Command
	def atag(InvocationContext context, @Usage("The name of the Connection") @Required @Argument String name, @Required @Usage("tag name") @Argument String tag) {

		getOpcFactoryBean(context).addTag(name, tag)
		
		return "tag ${tag} added to connection ${name}"
	}

	@Usage("Remove tag to listen")
	@Command
	def rtag(InvocationContext context, @Usage("The name of the Connection") @Required @Argument String name, @Required @Usage("tag name") @Argument String tag) {

		getOpcFactoryBean(context).removeTag(name, tag)
		
		return "tag ${tag} removec to connection ${name}"
	}
	
	@Usage("Listen tag updates from OPC Server")
	@Command
	def listen(InvocationContext context, @Usage("The name of the Connection") @Required @Argument String name) {
		
		getOpcFactoryBean(context).listen(name)
		
		return "Connection ${name} is listening"
	}

	@Usage("Quiesce OPC Server")
	@Command
	def quiesce(InvocationContext context, @Usage("The name of the Connection") @Required @Argument String name) {
		
		getOpcFactoryBean(context).quiesce(name)
		
		return "Connection ${name} is quiesce"
	}
}