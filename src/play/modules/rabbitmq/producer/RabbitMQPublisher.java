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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import play.Logger;
import play.Play;
import play.jobs.Job;
import play.modules.rabbitmq.RabbitMQPlugin;
import play.modules.rabbitmq.util.ExceptionUtil;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

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
	public static void publish(String queueName, String message) {
		new RabbitMQPublisherJob(queueName, message).now();
	}

	/**
	 * The Class RabbitMQPublisherJob.
	 */
	public static class RabbitMQPublisherJob extends Job {

		/** The message. */
		private String message;

		/** The queue name. */
		private String queueName;

		/**
		 * Instantiates a new rabbit mq publisher.
		 * 
		 * @param message
		 *            the message
		 */
		public RabbitMQPublisherJob(String queueName, String message) {
			this.queueName = queueName;
			this.message = message;
		}

		/**
		 * Deliver Message
		 * 
		 * @see play.jobs.Job#doJob()
		 */
		@Override
		public void doJob() {
			try {
				// Get Producer Information
				RabbitMQProducer producer = this.getClass().getAnnotation(RabbitMQProducer.class);
				if ((producer == null) && (this.queueName == null)) {
					throw new RuntimeException("Please define annotation @RabbitMQProducer.");
				}

				// Create Channel
				RabbitMQPlugin plugin = Play.plugin(RabbitMQPlugin.class);
				Channel channel = plugin.createChannel();
				if (channel == null) {
					throw new RuntimeException("Error creating a communication channel with RabbitMQ. Please verify the health of your RabbitMQ node and check your configuration.");
				}

				// Publish Message
				channel.basicPublish("", this.queueName, null, this.getBytes(this.message));

			} catch (Throwable t) {
				Logger.error(ExceptionUtil.getStackTrace(t));
			}
		}

		/**
		 * Gets the bytes.
		 * 
		 * @param message
		 *            the message
		 * @return the bytes
		 * @throws IOException
		 *             Signals that an I/O exception has occurred.
		 */
		public byte[] getBytes(String message) throws IOException {
			// Serialize to a byte array
			// ByteArrayOutputStream bos = new ByteArrayOutputStream();
			// ObjectOutput out = new ObjectOutputStream(bos);
			// out.writeObject(message);
			// out.close();

			// Get the bytes of the serialized object
			// byte[] bytes = bos.toByteArray();

			// Return Bytes
			byte[] bytes = message.getBytes();
			return bytes;
		}

	}
}
