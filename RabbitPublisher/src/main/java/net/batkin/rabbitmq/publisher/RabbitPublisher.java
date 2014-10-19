package net.batkin.rabbitmq.publisher;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import net.batkin.rabbitmq.util.Stopwatch;

public class RabbitPublisher {
	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.err.println("Usage: RabbitPublisher exchange queue numMessages");
			System.exit(-1);
		}

		String exchange = args[0];
		if (exchange == null) {
			exchange = "";
		}
		String queueName = args[1];
		String numMessagesString = args[2];
		int numMessages = Integer.valueOf(numMessagesString);

		System.err.println("Using Exchange [" + exchange + "], Queue [" + queueName + "], Num Messages [" + numMessages + "]");

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(queueName, false, false, false, null);

		Stopwatch stopwatch = new Stopwatch();
		for (int i = 0; i < numMessages; i++) {
			String message = "Message Number [" + i + "]";
			channel.basicPublish(exchange, queueName, null, message.getBytes("UTF-8"));
//			System.err.println("Published " + message);
		}
		stopwatch.click("done publishing");

		channel.close();
		stopwatch.click("closed channel");
		connection.close();
		stopwatch.click("closed connection");
	}

}
