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
import java.io.Serializable;

import play.Logger;
import play.Play;
import play.jobs.Job;
import play.modules.rabbitmq.RabbitMQPlugin;
import play.modules.rabbitmq.stats.StatsService;
import play.modules.rabbitmq.util.ExceptionUtil;
import play.modules.rabbitmq.util.JSONMapper;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.AMQP.Queue;

// TODO: Auto-generated Javadoc
/**
 * The Class RabbitMQConsumerJob.
 * 
 * @param <T>
 *            the generic type
 */
public abstract class RabbitMQConsumer<T> extends Job<T> {

	/** The queue. */
	Queue queue = null;

	/** The channel. */
	Channel channel = null;

	/** The connection. */
	Connection connection = null;

	/** The consumer. */
	QueueingConsumer consumer = null;

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
		// Tell her what's up
		this.channel = this.createChannel();

		// The endless life of a true playa!
		while (true) {
			// Show some interest
			QueueingConsumer.Delivery task = null;

			// Build the relationship
			try {
				// Ask her out
				task = this.consumer.nextDelivery();

			} catch (Throwable t) {
				// Bitch!
				Logger.error(ExceptionUtil.getStackTrace(t));
				this.channel = this.createChannel();
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
					this.channel.basicAck(task.getEnvelope().getDeliveryTag(),
							false);
					
					// Execution Time
					long executionTime = System.nanoTime() - start;
					Logger.info("Message %s has been published to queue %s (execution time: %s ms)", message, this.queue(), executionTime);

					// Update Stats
					boolean success = true;
					StatsService.producerUpdate(this.queue(), executionTime, success, 0);

				} catch (Throwable t) {
					// Log Debug
					Logger
							.error(
									"Error trying to acknowledge message delivery - Error: %s",
									ExceptionUtil.getStackTrace(t));
					
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
	 * @return the channel
	 */
	private Channel createChannel() {
		// Counter that keeps track of number of retries
		int attempts = 0;
		
		// Get Plugin
		RabbitMQPlugin plugin = Play.plugin(RabbitMQPlugin.class);
		
		// Log Debug
		Logger.info("Initializing connections to RabbitMQ instance (%s:%s)",
				RabbitMQPlugin.getHost(), RabbitMQPlugin.getPort());
		
		// Create Channel
		this.channel = plugin.createChannel();

		// Start Daemon
		while (true) {
			// Add to the number of retries
			attempts++;
			
			// Log Debug
			Logger.debug("Retry " + attempts);
			
			// Get Next Delivery Message
			try {
				// RabbitMQMessageListener listener =
				// this.getClass().getSuperclass().getAnnotation(RabbitMQMessageListener.class);
				// if (listener == null) {
				// throw new
				// RuntimeException("Please define annotation @RabbitMQMessageListener.");
				// }
				
				// Define Queue Params
				this.consumer = new QueueingConsumer(this.channel);
				this.channel.exchangeDeclare(this.queue(), "direct", true);
				this.channel.queueDeclare(this.queue(), true, false, false, null);
				this.channel.queueBind(this.queue(), this.queue(), this.queue());
				this.channel.basicConsume(this.queue(), false, this.consumer);

				// Log Debug
				Logger.info("RabbitMQ Task Channel Available: " + this.channel);

				// Return Channel
				return this.channel;

			} catch (Throwable t) {
				// Log Debug
				Logger.error("Error establishing a connection to RabbitMQ, will keep retrying - Exception: %s", ExceptionUtil.getStackTrace(t));
				
				// Sleep a little while before retrying
				try {
					Thread.sleep(1000 * 10);
				} catch (InterruptedException ex) {
				}
			}
		}
	}

	/**
	 * To object.
	 * 
	 * @param bytes
	 *            the bytes
	 * @return the object
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 */
	public T toObject(byte[] bytes) throws Exception {
		return (T)JSONMapper.getObject(this.getMessageType(), bytes);
	}

}