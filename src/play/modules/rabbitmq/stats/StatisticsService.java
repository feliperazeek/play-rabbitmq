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
package play.modules.rabbitmq.stats;

/**
 * The Class StatsService.
 */
public class StatisticsService {

	/**
	 * Gets the.
	 * 
	 * @param queue
	 *            the queue
	 * @param type
	 *            the type
	 * @param status
	 *            the status
	 * @return the long
	 */
	public long get(String queue, StatisticsEvent.Type type, StatisticsEvent.Status status) {
		StatisticsEvent activity = new StatisticsEvent(queue, type, status);
		Ticker ticker = StatisticsAction.Tick.map.get(activity);
		if (ticker == null) {
			return 0;
		}
		return ticker.current();
	}

	/**
	 * Queue consumer failed.
	 * 
	 * @param queueName
	 *            the queue name
	 */
	public long queueConsumerFailed(String queueName) {
		long consumerFailed = this.get(queueName, StatisticsEvent.Type.CONSUMER, StatisticsEvent.Status.ERROR);
		return consumerFailed;
	}

	/**
	 * Queue producer failed.
	 * 
	 * @param queueName
	 *            the queue name
	 */
	public long queueProducerFailed(String queueName) {
		long producerFailed = this.get(queueName, StatisticsEvent.Type.PRODUCER, StatisticsEvent.Status.ERROR);
		return producerFailed;
	}

	/**
	 * Queue consumer success.
	 * 
	 * @param queueName
	 *            the queue name
	 */
	public long queueConsumerSuccess(String queueName) {
		long consumerSuccess = this.get(queueName, StatisticsEvent.Type.CONSUMER, StatisticsEvent.Status.SUCCESS);
		return consumerSuccess;
	}

	/**
	 * Queue producer success.
	 * 
	 * @param queueName
	 *            the queue name
	 */
	public long queueProducerSuccess(String queueName) {
		long producerSuccess = this.get(queueName, StatisticsEvent.Type.PRODUCER, StatisticsEvent.Status.SUCCESS);
		return producerSuccess;
	}
}
