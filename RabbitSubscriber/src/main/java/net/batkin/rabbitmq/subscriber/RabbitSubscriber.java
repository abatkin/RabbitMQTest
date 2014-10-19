package net.batkin.rabbitmq.subscriber;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import net.batkin.rabbitmq.util.Stopwatch;

public class RabbitSubscriber {

	public static long TIMEOUT = 2000;

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.err.println("Usage: RabbitSubscriber queue");
			System.exit(-1);
		}

		String queueName = args[0];

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(queueName, false, false, false, null);

		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume(queueName, false, consumer);

		try {
			int batchNum = 1;
			int batchLen = 0;
			long bytesInBatch = 0;
			Stopwatch stopwatch = null;
			while (true) {
				QueueingConsumer.Delivery delivery = consumer.nextDelivery(TIMEOUT);
				if (delivery == null) {
					if (batchLen > 0) {
						if (stopwatch != null) {
							stopwatch.adjust(TIMEOUT);
							stopwatch.click("Batch [" + batchNum + "], Items [" + batchLen + "] Bytes [" + bytesInBatch + "]");
						}
					}
					stopwatch = null;
					batchNum++;
					batchLen = 0;
					bytesInBatch = 0;
					continue;
				}
				if (stopwatch == null) {
					stopwatch = new Stopwatch();
				}
				batchLen++;
				int numBytes = delivery.getBody().length;
				bytesInBatch += numBytes;
				channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
			}
		} finally {
			try {
				channel.close();
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
