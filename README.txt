Play! Framework RabbitMQ Module
by Felipe Oliveira (http://mashup.fm)



1) Installation

play install rabbitmq



2) Configuration

# RabbitMQ
rabbitmq.host=localhost
rabbitmq.port=5672
rabbitmq.userName=guest
rabbitmq.password=guest
rabbitmq.vhost=/



3) Publishing a Message

public static void publish(String q) {
	RabbitMQPublisher.publish("myQueue", q);
    render(q);
}



4) Creating a Message Consumer

@OnApplicationStart(async=true)
public class RabbitMQSampleConsumer extends RabbitMQConsumer {

	/**
	 * Sample Consumer
	 * 
	 * @see play.modules.rabbitmq.consumer.RabbitMQConsumer#consume(java.lang.Object)
	 */
	@Override
	protected void consume(Object message) {
		System.out.println("******************************");
		System.out.println("* Message Consumed: " + message);
		System.out.println("******************************");
	}

	/**
	 * Name of the Queue that this consumer will be listening to
	 * 
	 * @see play.modules.rabbitmq.consumer.RabbitMQConsumer#queue()
	 */
	@Override
	protected String queue() {
		return "myQueue";
	}
}

