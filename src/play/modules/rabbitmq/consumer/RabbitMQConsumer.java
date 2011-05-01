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
import play.modules.rabbitmq.stats.StatisticsEvent;
import play.modules.rabbitmq.stats.StatisticsStream;
import play.modules.rabbitmq.util.ExceptionUtil;
import play.modules.rabbitmq.util.JSONMapper;

import com.rabbitmq.client.Channel;
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
				// Create Channel
				if (channel == null) {
					channel = this.createChannel(plugin);
				}

				// Create Consumer
				if (consumer == null) {
					consumer = this.createConsumer(channel, plugin);
				}

				// Get Task
				QueueingConsumer.Delivery task = null;
				task = consumer.nextDelivery();

				// Date Night
				if ((task != null) && (task.getBody() != null)) {
					try {
						// Start Timer
						long start = System.nanoTime();

						// Go have some fun with her
						T message = this.toObject(task.getBody());
						new RabbitMQMessageConsumerJob(this, message).now();

						// Now tell Daddy everything is cool
						channel.basicAck(task.getEnvelope().getDeliveryTag(), false);

						// Execution Time
						long executionTime = System.nanoTime() - start;
						Logger.info("Message %s has been consumed from queue %s (execution time: %s ms)", message, this.queue(), executionTime);

						// Update Stats
						StatisticsStream.add(new StatisticsEvent(this.queue(), StatisticsEvent.Type.CONSUMER, StatisticsEvent.Status.SUCCESS));

					} catch (Throwable t) {
						// Log Debug
						Logger.error("Error trying to acknowledge message delivery - Error: %s", ExceptionUtil.getStackTrace(t));

						// Update Stats
						StatisticsStream.add(new StatisticsEvent(this.queue(), StatisticsEvent.Type.CONSUMER, StatisticsEvent.Status.ERROR));
					}

				}
			} catch (Throwable t) {
				channel = null;
				consumer = null;
				Logger.error("Error creating consumer channel to RabbitMQ, retrying in a few seconds. Exception: %s", ExceptionUtil.getStackTrace(t));
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					Logger.error(ExceptionUtil.getStackTrace(t));
				}
			}
		}
	}

	/**
	 * The Class RabbitMQMessageConsumerJob.
	 * 
	 * @param <T>
	 *            the generic type
	 */
	protected static class RabbitMQMessageConsumerJob<T> extends Job<T> {

		/** The message. */
		private T message;

		/** The consumer. */
		private RabbitMQConsumer consumer;

		/**
		 * Instantiates a new rabbit mq message consumer job.
		 * 
		 * @param consumer
		 *            the consumer
		 * @param message
		 *            the message
		 */
		public RabbitMQMessageConsumerJob(RabbitMQConsumer consumer, T message) {
			this.consumer = consumer;
			this.message = message;
		}

		/**
		 * Consumer Message
		 * 
		 * @see play.jobs.Job#doJob()
		 */
		@Override
		public void doJob() {
			this.consumer.consume(this.message);
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
	 * @param plugin
	 *            the plugin
	 * @return the channel
	 * @throws Exception
	 *             the exception
	 */
	protected Channel createChannel(RabbitMQPlugin plugin) throws Exception {
		// Get Plugin
		Channel channel = plugin.createChannel(this.queue());
		return channel;
	}

	/**
	 * Creates the channel.
	 * 
	 * @param channel
	 *            the channel
	 * @param plugin
	 *            the plugin
	 * @return the channel
	 * @throws Exception
	 *             the exception
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
	 * @param bytes
	 *            the bytes
	 * @return the object
	 * @throws Exception
	 *             the exception
	 */
	protected T toObject(byte[] bytes) throws Exception {
		return (T) JSONMapper.getObject(this.getMessageType(), bytes);
	}

}