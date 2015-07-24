package commands

import org.crsh.cli.Usage
import org.crsh.cli.Command
import org.crsh.command.InvocationContext
import org.springframework.core.env.Environment
import org.springframework.beans.factory.BeanFactory

import com.gopivotal.tola.opc.boot.OpcFactoryBean;
import com.gopivotal.tola.opc.ConnectionConfiguration;

@Usage("OPC commands")
class opc {

	def OpcFactoryBean getOpcFactoryBean(InvocationContext context) {
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		OpcFactoryBean opcFactory = beanFactory.getBean(com.gopivotal.tola.opc.boot.OpcFactoryBean.class)
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
	def connect(InvocationContext context, @Usage("The name of the Connection to create") @Required @Argument String name,
			@Usage("The name server to use") @Required @Argument String server,
			@Usage("password to use") @Argument String password) {

		created = getOpcFactoryBean(context).createConnection(name, server, password)

		return "Connection ${name} " + (created ? "created" : "not created")
	}

	/**
	 * SERVER
	 *
	 * @param context
	 * @param name
	 * @return
	 */
	@Usage("create OPC Server")
	@Command
	def server(InvocationContext context, @Usage("The name of the server") @Required @Argument String name,
			@Usage("list of parms - name=value,name=value") @Required @Argument String parms,
			@Usage("server to use as base") @Argument String server) {

		created = getOpcFactoryBean(context).createServer(name, parms, server)

		return "Server ${name} " + (created ? "created" : "not created")
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

		getOpcFactoryBean(context).destroyConnection(name)

		return "Connection ${name} destroyed"
	}

	/**
	 * LIST CONNECTIONS
	 * 
	 * @param context
	 * @return
	 */
	@Usage("List OPC Server definitions and Connections")
	@Command
	def list(InvocationContext context) {
		return getOpcFactoryBean(context).list()
	}

	/**
	 * DUMP TAGS
	 * 
	 * @param context
	 * @param name
	 * @param branch
	 * @return
	 */
	@Usage("list OPC Server tags")
	@Command
	def tags(InvocationContext context, @Usage("The name of the Connection") @Required @Argument String name, @Usage("initial tag branch") @Argument String branch) {

		return getOpcFactoryBean(context).dumpRootTree(name)
	}

	/**
	 * ADD TAG
	 * 
	 * @param context
	 * @param name
	 * @param tag
	 * @return
	 */
	@Usage("Add tag to listen")
	@Command
	def atag(InvocationContext context, @Usage("The name of the Connection") @Required @Argument String name, @Required @Usage("tag name") @Argument String tag) {

		getOpcFactoryBean(context).addTag(name, tag)

		return "tag ${tag} added to connection ${name}"
	}

	/**
	 * REMOVE TAG
	 * 
	 * @param context
	 * @param name
	 * @param tag
	 * @return
	 */
	@Usage("Remove tag to listen")
	@Command
	def rtag(InvocationContext context, @Usage("The name of the Connection") @Required @Argument String name, @Required @Usage("tag name") @Argument String tag) {

		getOpcFactoryBean(context).removeTag(name, tag)

		return "tag ${tag} removec to connection ${name}"
	}

	/**
	 * LISTEN
	 * 
	 * @param context
	 * @param name
	 * @return
	 */
	@Usage("Listen tag updates from OPC Server")
	@Command
	def listen(InvocationContext context, @Usage("The name of the Connection") @Required @Argument String name) {

		getOpcFactoryBean(context).listen(name)

		return "Connection ${name} is listening"
	}

	/**
	 * QUIESCE
	 * 
	 * @param context
	 * @param name
	 * @return
	 */
	@Usage("Quiesce OPC Server")
	@Command
	def quiesce(InvocationContext context, @Usage("The name of the Connection") @Required @Argument String name) {

		getOpcFactoryBean(context).quiesce(name)

		return "Connection ${name} is quiesce"
	}
}