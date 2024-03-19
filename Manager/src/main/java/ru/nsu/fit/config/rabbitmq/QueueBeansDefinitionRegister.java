package ru.nsu.fit.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import ru.nsu.fit.config.rabbitmq.properties.QueueInfo;
import ru.nsu.fit.config.rabbitmq.properties.RabbitMQProperties;

public class QueueBeansDefinitionRegister implements BeanDefinitionRegistryPostProcessor {

    private final RabbitMQProperties properties;

    public QueueBeansDefinitionRegister(RabbitMQProperties properties) {
        this.properties = properties;
    }


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        registerQueue(properties.getQueues().manager());
        properties.getQueues()
                .workers()
                .forEach(this::registerQueue);
    }

    private void registerQueue(QueueInfo queue) {
        GenericBeanDefinition queueBeanDefinition = new GenericBeanDefinition();
        queueBeanDefinition.setBeanClass(Queue.class);
        queueBeanDefinition.setInstanceSupplier(() -> new Queue(queue.name()));

        GenericBeanDefinition exchangeBeanDefinition = new GenericBeanDefinition();
        exchangeBeanDefinition.setBeanClass(DirectExchange.class);
        exchangeBeanDefinition.setInstanceSupplier(() -> new DirectExchange(queue.exchange(), true, false));

        GenericBeanDefinition bindingBeanDefinition = new GenericBeanDefinition();
        bindingBeanDefinition.setBeanClass(Binding.class);
        bindingBeanDefinition.setInstanceSupplier(() ->
                BindingBuilder.bind((Queue) queueBeanDefinition.getInstanceSupplier().get())
                        .to((DirectExchange) exchangeBeanDefinition.getInstanceSupplier().get())
                        .with(queue.routingKey())
        );
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        BeanDefinitionRegistryPostProcessor.super.postProcessBeanFactory(beanFactory);
    }
}
