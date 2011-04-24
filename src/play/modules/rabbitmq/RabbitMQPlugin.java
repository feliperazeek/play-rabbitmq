/** 
 * Copyright 2011 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author Felipe Oliveira (http://mashup.fm)
 * 
 */
package play.modules.rabbitmq;

import java.io.IOException;

import play.Logger;
import play.Play;
import play.PlayPlugin;
import play.modules.rabbitmq.util.ExceptionUtil;
import play.mvc.Router;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


// TODO: Auto-generated Javadoc
/**
 * The Class RabbitMQPlugin.
 */
public class RabbitMQPlugin extends PlayPlugin {

	/** The Constant factory. */
	public static final ConnectionFactory factory = new ConnectionFactory();

	/**
	 * On application start.
	 */
	@Override
	public void onApplicationStart() {
		// Connection Factory
		factory.setHost(getHost());
		factory.setPort(getPort());
		factory.setUsername(getUserName());
		factory.setPassword(getPassword());
		factory.setVirtualHost(getVhost());
	}

	/**
	 * Gets the task channel.
	 * 
	 * @return the task channel
	 */
	public Channel createChannel() {
		Channel taskChannel = null;

		int attempts = 0;
		while (true) {
			attempts++;
			Logger.info("Attempting to connect to queue: attempt " + attempts);
			try {
				Connection connection = this.getConnection();
				taskChannel = connection.createChannel();
				break;

			} catch (IOException e) {
				Logger
						.error(
								"Error creating RabbitMQ channel, retrying in 5 secs - Exception: %s",
								ExceptionUtil.getStackTrace(e));
				try {
					Thread.sleep(1000 * 5);
				} catch (InterruptedException ex) {
				}
			}
		}
		return taskChannel;
	}

	/**
	 * Gets the host.
	 * 
	 * @return the host
	 */
	public static String getHost() {
		String s = Play.configuration.getProperty("rabbitmq.host");
		if (s == null) {
			return "localhost";
		}
		return s;
	}

	/**
	 * Gets the port.
	 * 
	 * @return the port
	 */
	public static int getPort() {
		String s = Play.configuration.getProperty("rabbitmq.port");
		if (s == null) {
			return 5672;
		}
		return Integer.parseInt(s);
	}

	/**
	 * Gets the user name.
	 * 
	 * @return the user name
	 */
	public static String getUserName() {
		String s = Play.configuration.getProperty("rabbitmq.username");
		if (s == null) {
			return "guest";
		}
		return s;
	}

	/**
	 * Gets the password.
	 * 
	 * @return the password
	 */
	public static String getPassword() {
		String s = Play.configuration.getProperty("rabbitmq.password");
		if (s == null) {
			return "guest";
		}
		return s;
	}

	/**
	 * Gets the vhost.
	 * 
	 * @return the vhost
	 */
	public static String getVhost() {
		String s = Play.configuration.getProperty("rabbitmq.vhost");
		if (s == null) {
			return "/";
		}
		return s;
	}

	/**
	 * Gets the connection.
	 * 
	 * @return the connection
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public Connection getConnection() throws IOException {
		return factory.newConnection();
	}
}