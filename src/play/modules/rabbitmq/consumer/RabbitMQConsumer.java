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

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import play.Logger;
import play.Play;
import play.exceptions.UnexpectedException;
import play.jobs.Job;
import play.modules.rabbitmq.RabbitMQPlugin;
import play.modules.rabbitmq.util.ExceptionUtil;

import java.io.IOException;

// TODO: Auto-generated Javadoc
/**
 * The Class RabbitMQConsumerJob.
 * 
 * @param <T>
 *            the generic type
 */
public abstract class RabbitMQConsumer<T> extends Job<T> {

	/**
	 * This is the default value defined by "rabbitmq.retries" on
	 * application.conf (please override if you need a new value)
	 */
	public static int retries = RabbitMQPlugin.retries();
    DeliveryTag unacknowledgedDeliveryTag;

	/**
	 * Consume.
	 * 
	 * @param message
	 *            the message
	 */
	protected abstract void consume(T message);

	/**
	 * Creates the channel.
	 * 
	 * @param plugin
	 *            the plugin
	 * @return the channel
	 * @throws IOException
	 *             the exception
	 */
	protected Channel createChannel(RabbitMQPlugin plugin) throws IOException {
		// Get Plugin
		Channel channel = plugin.createChannel(this.queue(), this.routingKey());
		return channel;
	}

	/**
	 * Creates the consumer.
	 * 
	 * @param channel
	 *            the channel
	 * @return the channel
	 * @throws IOException
	 *             if calling basicConsume throws
	 */
	protected QueueingConsumer createConsumer(Channel channel) throws IOException {
		// Get Plugin
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume(this.queue(), RabbitMQPlugin.isAutoAck(), consumer);

		// Log Debug
		Logger.trace("RabbitMQ Consumer - Channel: %s, Consumer: %s ", channel, consumer);

		// Return Channel
		return consumer;
	}

	/**
	 * Let our baby go!.
	 * 
	 * @see play.jobs.Job#doJob()
	 */
	@Override
	public void doJob() {
        try {
            this.goGetHerSon();
        } catch (IOException e) {
            throw new UnexpectedException(e);
        }
    }

	/**
	 * Gets the message type.
	 * 
	 * @return the message type
	 */
	protected abstract Class getMessageType();

	/**
	 * Go get her son.
	 */
	private void goGetHerSon() throws IOException {
		// Get Plugin
		RabbitMQPlugin plugin = Play.plugin(RabbitMQPlugin.class);

		// Define Channel
		Channel channel = this.createChannel(plugin);
		QueueingConsumer consumer = this.createConsumer(channel);
		Long deliveryTag = null;

		// Get Channel
		while (true) {
			// Log Debug
			Logger.trace("Entering main loop on consumer: " + this);

			// Are Consumers Running?
			boolean active = RabbitMQPlugin.areConsumersActive();

			// Only do work if consumers are running
			if (active) {
				try {
					// Get Task
					QueueingConsumer.Delivery task = consumer.nextDelivery();

					// Date Night
					if ((task != null) && (task.getBody() != null)) {
						try {
							// Fire job that will pass the message to the
							// consumer,
							// ack the queue and do the retry logic
							deliveryTag = task.getEnvelope().getDeliveryTag();
                            unacknowledgedDeliveryTag = new DeliveryTag(deliveryTag, channel); 
							T message = this.toObject(task.getBody());
							new RabbitMQMessageConsumerJob(channel, deliveryTag, this.queue(), this, message, this.retries()).doJobWithResult();

						} catch (Throwable t) {
							// Handle Exception
							Logger.error(ExceptionUtil.getStackTrace(t));
						}

					}

				} catch (Throwable t) {
					Logger.error("Error creating consumer channel to RabbitMQ, retrying in a few seconds. Exception: %s", ExceptionUtil.getStackTrace(t));
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						Logger.error(ExceptionUtil.getStackTrace(t));
					}

				}
			} else {
				Logger.warn("RabbitMQ consumers are paused and napping for 10 secs...");
				try {
					Thread.sleep(10000);
				} catch (Throwable t) {
				}
			}
		}
	}

    protected void ack() {
        try {
            unacknowledgedDeliveryTag.channel.basicAck(unacknowledgedDeliveryTag.deliveryTag, true);
        } catch (IOException e) {
            throw new UnexpectedException(e);
        }
    }
    
	/**
	 * Gets the queue name.
	 * 
	 * @return the queue name
	 */
	protected abstract String queue();

	/**
	 * Routing key.
	 * 
	 * @return the string
	 */
	protected String routingKey() {
		return this.queue();
	}

	/**
	 * Retries.
	 * 
	 * @return the int
	 */
	protected int retries() {
		return retries;
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
		return (T) RabbitMQPlugin.mapper().getObject(this.getMessageType(), bytes);
	}

    public static class DeliveryTag {
        public long deliveryTag;
        public Channel channel;

        public DeliveryTag(long deliveryTag, Channel channel) {
            this.deliveryTag = deliveryTag;
            this.channel = channel;
        }
    }
}
