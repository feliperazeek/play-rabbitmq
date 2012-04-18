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
package play.modules.rabbitmq.consumer;

import play.Logger;
import play.Play;
import play.jobs.Job;
import play.modules.rabbitmq.RabbitMQPlugin;
import play.modules.rabbitmq.util.ExceptionUtil;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

// TODO: Auto-generated Javadoc
/**
 * The Class RabbitMQConsumerJob.
 * 
 * @param <T>
 *            the generic type
 */
public abstract class RabbitMQPubSubConsumer<T> extends RabbitMQConsumer<T> {

		/**
	 * Creates the channel.
	 * 
	 * @param plugin
	 *            the plugin
	 * @return the channel
	 * @throws Exception
	 *             the exception
	 */
	protected Channel createChannel(RabbitMQPlugin plugin) throws Exception {
		// Create a subscribers channel when binds a queue to an exchange by the routing key
		Channel channel = plugin.createSubscribersChannel(this.exchangeName(),this.queue(), this.routingKey());
		return channel;
	}

	
	
	/**
	 * Gets the exchange name.
	 * 
	 * @return the exchange name
	 */
	protected abstract String exchangeName();
	

}