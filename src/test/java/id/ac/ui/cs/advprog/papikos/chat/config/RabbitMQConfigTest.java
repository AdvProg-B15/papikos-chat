package id.ac.ui.cs.advprog.papikos.chat.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.junit.jupiter.api.Assertions.*;

class RabbitMQConfigTest {

    private AnnotationConfigApplicationContext context;

    @Configuration
    static class TestConfig {
        @Bean
        public ConnectionFactory connectionFactory() {
            return Mockito.mock(ConnectionFactory.class);
        }
    }

    @BeforeEach
    void setUp() {
        context = new AnnotationConfigApplicationContext(RabbitMQConfig.class, TestConfig.class);
    }

    @AfterEach
    void tearDown() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    void testTopicExchangeBeanCreation() {
        TopicExchange exchange = context.getBean(TopicExchange.class);
        assertNotNull(exchange, "TopicExchange bean should not be null.");
        assertEquals(RabbitMQConfig.TOPIC_EXCHANGE_NAME, exchange.getName(), "TopicExchange name should match the configured name.");
        assertTrue(exchange.isDurable(), "TopicExchange should be durable by default."); // Default TopicExchange adalah durable
        assertFalse(exchange.isAutoDelete(), "TopicExchange should not be auto-delete by default."); // Default TopicExchange bukan auto-delete
    }

    @Test
    void testJsonMessageConverterBeanCreation() {
        MessageConverter converter = context.getBean("jsonMessageConverter", MessageConverter.class);
        assertNotNull(converter, "MessageConverter bean should not be null.");
        assertInstanceOf(Jackson2JsonMessageConverter.class, converter, "Bean should be an instance of Jackson2JsonMessageConverter.");
    }

    @Test
    void testRabbitTemplateBeanCreation() {
        RabbitTemplate rabbitTemplate = context.getBean(RabbitTemplate.class);
        assertNotNull(rabbitTemplate, "RabbitTemplate bean should not be null.");
        assertNotNull(rabbitTemplate.getConnectionFactory(), "RabbitTemplate should have a ConnectionFactory set.");
        assertTrue(rabbitTemplate.getMessageConverter() instanceof Jackson2JsonMessageConverter, "RabbitTemplate should use Jackson2JsonMessageConverter.");
    }

    @Test
    void testKosQueueBeanCreation() {
        Queue queue = context.getBean("kosQueue", Queue.class); // Dapatkan berdasarkan nama bean jika ada beberapa Queue
        assertNotNull(queue, "Queue bean (kosQueue) should not be null.");
        assertEquals(RabbitMQConfig.CHAT_QUEUE_NAME, queue.getName(), "Queue name should match the configured name.");
        assertTrue(queue.isDurable(), "Queue should be durable as configured.");
        assertFalse(queue.isExclusive(), "Queue should not be exclusive as configured.");
        assertFalse(queue.isAutoDelete(), "Queue should not be auto-delete as configured.");
    }

    @Test
    void testKosBindingBeanCreation() {
        Binding binding = context.getBean("kosBinding", Binding.class); // Dapatkan berdasarkan nama bean
        assertNotNull(binding, "Binding bean (kosBinding) should not be null.");
        assertEquals(RabbitMQConfig.CHAT_QUEUE_NAME, binding.getDestination(), "Binding destination (queue name) should match.");
        assertEquals(RabbitMQConfig.TOPIC_EXCHANGE_NAME, binding.getExchange(), "Binding exchange name should match.");
        assertEquals(RabbitMQConfig.ROUTING_KEY_RENTAL_CREATED, binding.getRoutingKey(), "Binding routing key should match.");
        assertEquals(Binding.DestinationType.QUEUE, binding.getDestinationType(), "Binding destination type should be QUEUE.");
    }

    @Test
    void testAllBeansAreSingletonsByDefault() {
        TopicExchange exchange1 = context.getBean(TopicExchange.class);
        TopicExchange exchange2 = context.getBean(TopicExchange.class);
        assertSame(exchange1, exchange2, "TopicExchange bean should be a singleton.");

        MessageConverter converter1 = context.getBean("jsonMessageConverter", MessageConverter.class);
        MessageConverter converter2 = context.getBean("jsonMessageConverter", MessageConverter.class);
        assertSame(converter1, converter2, "MessageConverter bean should be a singleton.");

        RabbitTemplate template1 = context.getBean(RabbitTemplate.class);
        RabbitTemplate template2 = context.getBean(RabbitTemplate.class);
        assertSame(template1, template2, "RabbitTemplate bean should be a singleton.");

        Queue queue1 = context.getBean("kosQueue", Queue.class);
        Queue queue2 = context.getBean("kosQueue", Queue.class);
        assertSame(queue1, queue2, "Queue bean (kosQueue) should be a singleton.");

        Binding binding1 = context.getBean("kosBinding", Binding.class);
        Binding binding2 = context.getBean("kosBinding", Binding.class);
        assertSame(binding1, binding2, "Binding bean (kosBinding) should be a singleton.");
    }
}