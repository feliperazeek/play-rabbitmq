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

import java.io.IOException;

import play.Logger;
import play.Play;
import play.jobs.Job;
import play.modules.rabbitmq.RabbitMQPlugin;
import play.modules.rabbitmq.stats.StatsService;
import play.modules.rabbitmq.util.ExceptionUtil;
import play.modules.rabbitmq.util.JSONMapper;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.QueueingConsumer;

// TODO: Auto-generated Javadoc
/**
 * The Class RabbitMQConsumerJob.
 * 
 * @param <T>
 *            the generic type
 */
public abstract class RabbitMQConsumer<T> extends Job<T> {

	/**
	 * Let our baby go!.
	 * 
	 * @see play.jobs.Job#doJob()
	 */
	@Override
	public void doJob() {
		this.goGetHerSon();
	}

	/**
	 * Gets the queue name.
	 * 
	 * @return the queue name
	 */
	protected abstract String queue();

	/**
	 * Go get her son.
	 */
	private void goGetHerSon() {
		// Get Plugin
		RabbitMQPlugin plugin = Play.plugin(RabbitMQPlugin.class);
		
		// Define Channel
		Channel channel = null;
		QueueingConsumer consumer = null;
		
		// Get Channel
		while (true) {
			try {
				channel = this.createChannel(plugin);
				consumer = this.createConsumer(channel, plugin);
				if ( channel != null && consumer != null ) {
					break;
				}
				
			} catch (Throwable t) {
				Logger.error("Error creating consumer channel to RabbitMQ, retrying in a few seconds. Exception: %s", ExceptionUtil.getStackTrace(t));
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					Logger.error(ExceptionUtil.getStackTrace(t));
				}
			}
		}

		// The endless life of a true playa!
		while (true) {
			// Show some interest
			QueueingConsumer.Delivery task = null;

			// Build the relationship
			try {				
				// Ask her out
				if ( consumer != null ) {
					task = consumer.nextDelivery();
				}

			} catch (Throwable t) {
				// Bitch!
				Logger.error(ExceptionUtil.getStackTrace(t));
				continue;
			}

			// Date Night
			if ((task != null) && (task.getBody() != null)) {
				try {
					// Start Timer
					long start = System.nanoTime();
					
					// Go have some fun with her
					T message = toObject(task.getBody());
					this.consume(message);

					// Now tell Daddy everything is cool
					channel.basicAck(task.getEnvelope().getDeliveryTag(), false);
					
					// Execution Time
					long executionTime = System.nanoTime() - start;
					Logger.info("Message %s has been consumed from queue %s (execution time: %s ms)", message, this.queue(), executionTime);

					// Update Stats
					boolean success = true;
					StatsService.producerUpdate(this.queue(), executionTime, success, 0);

				} catch (Throwable t) {
					// Log Debug
					Logger.error("Error trying to acknowledge message delivery - Error: %s", ExceptionUtil.getStackTrace(t));
					
					// Update Stats
					boolean success = false;
					StatsService.producerUpdate(this.queue(), 0l, success, 0);
				}

			}
		}
	}

	/**
	 * Consume.
	 * 
	 * @param message
	 *            the message
	 */
	protected abstract void consume(T message);
	
	/**
	 * Gets the message type.
	 *
	 * @return the message type
	 */
	protected abstract Class getMessageType();
	
	/**
	 * Creates the channel.
	 *
	 * @param plugin the plugin
	 * @return the channel
	 * @throws Exception the exception
	 */
	protected Channel createChannel(RabbitMQPlugin plugin) throws Exception {
		// Get Plugin
		Channel channel = plugin.createChannel(this.queue());
		return channel;
	}

	/**
	 * Creates the channel.
	 *
	 * @param channel the channel
	 * @param plugin the plugin
	 * @return the channel
	 * @throws Exception the exception
	 */
	protected QueueingConsumer createConsumer(Channel channel, RabbitMQPlugin plugin) throws Exception {
		// Get Plugin
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume(this.queue(), plugin.isAutoAck(), consumer);

		// Log Debug
		Logger.info("RabbitMQ Consumer - Channel: %s, Consumer: %s " + channel, consumer);

		// Return Channel
		return consumer;
	}

	/**
	 * To object.
	 *
	 * @param bytes the bytes
	 * @return the object
	 * @throws Exception the exception
	 */
	protected T toObject(byte[] bytes) throws Exception {
		return (T)JSONMapper.getObject(this.getMessageType(), bytes);
	}

}