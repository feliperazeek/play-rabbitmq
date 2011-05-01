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
package controllers;

import play.modules.rabbitmq.sample.RabbitMQSampleConsumer;
import play.modules.rabbitmq.sample.RabbitMQSampleFirehose;
import play.modules.rabbitmq.stats.StatisticsEvent;
import play.modules.rabbitmq.stats.StatisticsService;
import play.mvc.Controller;

// TODO: Auto-generated Javadoc
/**
 * The Class RabbitMQStats.
 */
public class RabbitMQStats extends Controller {

	/** The service. */
	private static StatisticsService service = new StatisticsService();

	/**
	 * Index.
	 */
	public static void index() {
		render();
	}

	/**
	 * Stream.
	 */
	public static void stream() {
		render();
	}

	/**
	 * Queue stats.
	 * 
	 * @param queueName
	 *            the queue name
	 */
	public static void queueStats(String queueName) {
		long producerSuccess = service.get(queueName, StatisticsEvent.Type.PRODUCER, StatisticsEvent.Status.SUCCESS);
		long producerFailed = service.get(queueName, StatisticsEvent.Type.PRODUCER, StatisticsEvent.Status.ERROR);
		long consumerSuccess = service.get(queueName, StatisticsEvent.Type.CONSUMER, StatisticsEvent.Status.SUCCESS);
		long consumerFailed = service.get(queueName, StatisticsEvent.Type.CONSUMER, StatisticsEvent.Status.ERROR);
		render(queueName, producerSuccess, producerFailed, consumerSuccess, consumerFailed);
	}

	/**
	 * Queue stats details.
	 * 
	 * @param queueName
	 *            the queue name
	 */
	public static void queueStatsDetails(String queueName) {
		long producerSuccess = service.get(queueName, StatisticsEvent.Type.PRODUCER, StatisticsEvent.Status.SUCCESS);
		long producerFailed = service.get(queueName, StatisticsEvent.Type.PRODUCER, StatisticsEvent.Status.ERROR);
		long consumerSuccess = service.get(queueName, StatisticsEvent.Type.CONSUMER, StatisticsEvent.Status.SUCCESS);
		long consumerFailed = service.get(queueName, StatisticsEvent.Type.CONSUMER, StatisticsEvent.Status.ERROR);
		render(queueName, producerSuccess, producerFailed, consumerSuccess, consumerFailed);
	}

	/**
	 * Fire sample firehose.
	 */
	public static void fireSampleQueue() {
		new RabbitMQSampleConsumer().now();
		new RabbitMQSampleFirehose().now();
		render();
	}

}
