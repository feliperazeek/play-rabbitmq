package controllers;

import play.modules.rabbitmq.stats.Stats;
import play.modules.rabbitmq.stats.StatsService;
import play.mvc.Controller;

// TODO: Auto-generated Javadoc
/**
 * The Class RabbitMQStats.
 */
public class RabbitMQStats extends Controller {
	
	/**
	 * Index.
	 */
	public static void index() {
		render();
	}
	
	/**
	 * Queue stats.
	 *
	 * @param queueName the queue name
	 */
	public static void queueStats(String queueName) {
		Stats stats = StatsService.queueStats(queueName);
		long producerSuccess = 0l;
		long producerFailed = 0l;
		long consumerSuccess = 0l;
		long consumerFailed = 0l;
		if ( stats != null ) {
			producerSuccess = stats.getProducerSuccessCount();
			producerFailed = stats.getProducerFailedCount();
			consumerSuccess = stats.getConsumerSuccessCount();
			consumerFailed = stats.getConsumerFailedCount();
		}
		render(queueName, producerSuccess, producerFailed, consumerSuccess, consumerFailed);
	}
	
	/**
	 * Queue consumer failed.
	 *
	 * @param queueName the queue name
	 */
	public static void queueConsumerFailed(String queueName) {
		Stats stats = StatsService.queueStats(queueName);
		long consumerFailed = 0l;
		if ( stats != null ) {
			consumerFailed = stats.getConsumerFailedCount();
		}
		render(consumerFailed);
	}
	
	/**
	 * Queue producer failed.
	 *
	 * @param queueName the queue name
	 */
	public static void queueProducerFailed(String queueName) {
		Stats stats = StatsService.queueStats(queueName);
		long producerFailed = 0l;
		if ( stats != null ) {
			producerFailed = stats.getProducerFailedCount();
		}
		render(producerFailed);
	}
	
	/**
	 * Queue consumer success.
	 *
	 * @param queueName the queue name
	 */
	public static void queueConsumerSuccess(String queueName) {
		Stats stats = StatsService.queueStats(queueName);
		long consumerSuccess = 0l;
		if ( stats != null ) {
			consumerSuccess = stats.getConsumerSuccessCount();
		}
		render(consumerSuccess);
	}
	
	/**
	 * Queue producer success.
	 *
	 * @param queueName the queue name
	 */
	public static void queueProducerSuccess(String queueName) {
		Stats stats = StatsService.queueStats(queueName);
		long producerSuccess = 0l;
		if ( stats != null ) {
			producerSuccess = stats.getProducerSuccessCount();
		}
		render(producerSuccess);
	}

}
