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
package play.modules.rabbitmq.producer;

import java.io.Serializable;

import play.Logger;
import play.Play;
import play.jobs.Job;
import play.modules.rabbitmq.RabbitMQPlugin;
import play.modules.rabbitmq.stats.StatsService;
import play.modules.rabbitmq.util.ExceptionUtil;
import play.modules.rabbitmq.util.JSONMapper;

import com.rabbitmq.client.Channel;

// TODO: Auto-generated Javadoc
/**
 * The Class RabbitMQPublisher.
 */
public abstract class RabbitMQPublisher {

	/**
	 * Publish.
	 * 
	 * @param queueName
	 *            the queue name
	 * @param message
	 *            the message
	 */
	public static void publish(String queueName, Object message) {
		new RabbitMQPublisherJob(queueName, message).now();
	}

	/**
	 * The Class RabbitMQPublisherJob.
	 */
	public static class RabbitMQPublisherJob extends Job {

		/** The message. */
		private Object message;

		/** The queue name. */
		private String queueName;

		/**
		 * Instantiates a new rabbit mq publisher.
		 * 
		 * @param queueName
		 *            the queue name
		 * @param message
		 *            the message
		 */
		public RabbitMQPublisherJob(String queueName, Object message) {
			this.queueName = queueName;
			this.message = message;
		}

		/**
		 * Deliver Message.
		 * 
		 * @see play.jobs.Job#doJob()
		 */
		@Override
		public void doJob() {
			try {
				// Start Timer
				long start = System.nanoTime();
				
				// Get Producer Information
				RabbitMQProducer producer = this.getClass().getAnnotation(
						RabbitMQProducer.class);
				if ((producer == null) && (this.queueName == null)) {
					throw new RuntimeException(
							"Please define annotation @RabbitMQProducer.");
				}

				// Create Channel
				RabbitMQPlugin plugin = Play.plugin(RabbitMQPlugin.class);
				Channel channel = plugin.createChannel();
				if (channel == null) {
					throw new RuntimeException(
							"Error creating a communication channel with RabbitMQ. Please verify the health of your RabbitMQ node and check your configuration.");
				}

				// Publish Message
				channel.basicPublish("", this.queueName, null, JSONMapper.getBytes(this.message));
				
				// Execution Time
				long executionTime = System.nanoTime() - start;
				Logger.info("Message %s has been published to queue %s (execution time: %s ms)", this.message, this.queueName, executionTime);

				// Update Stats
				boolean success = true;
				StatsService.producerUpdate(this.queueName, executionTime, success, 0);

			} catch (Throwable t) {
				// Handle Exception
				Logger.error(ExceptionUtil.getStackTrace(t));

				// Update Stats
				boolean success = false;
				StatsService.producerUpdate(this.queueName, 0l, success, 0);
			}
		}

	}
}
