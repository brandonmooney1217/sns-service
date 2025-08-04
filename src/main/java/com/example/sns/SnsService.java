package com.example.sns;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;


/**
 * Service class for managing AWS SNS operations including topic creation and email subscriptions.
 * This service provides methods to create SNS topics and subscribe email addresses to those topics.
 */
@Service
public class SnsService {

    private final SnsClient snsClient;
    
    @Value("${aws.sns.topic.arn}")
    private String topicArn;

    /**
     * Constructs a new SnsService with the specified SNS client.
     * 
     * @param snsClient the AWS SNS client to use for SNS operations
     */
    public SnsService(final SnsClient snsClient) {
        this.snsClient = snsClient;
    }

    /**
     * Creates a new SNS topic with the specified name.
     * If a topic with the same name already exists, returns the existing topic's ARN.
     * 
     * @param topicName the name of the topic to create
     * @return the Amazon Resource Name (ARN) of the created or existing topic
     * @throws RuntimeException if the topic creation fails
     */
    public String createTopic(String topicName) {
        CreateTopicRequest request = CreateTopicRequest.builder()
            .name(topicName)
            .build();
        return snsClient.createTopic(request).topicArn();
    }

    /**
     * Subscribes an email address to the configured SNS topic.
     * The email address will receive a confirmation email that must be confirmed
     * before messages are delivered.
     * 
     * @param email the email address to subscribe to the topic
     * @throws RuntimeException if the subscription fails
     */
    public void subEmail(final String email) {
        final SubscribeRequest request = SubscribeRequest
            .builder()
            .endpoint(email)
            .protocol("email")
            .topicArn(this.topicArn)
            .build();
        
        snsClient.subscribe(request);
    }

    /**
     * Publishes a message to the configured SNS topic.
     * All confirmed subscribers will receive the message via their chosen protocol (email, SMS, etc.).
     * 
     * @param message the message content to publish to all subscribers
     * @param subject the subject line for the message (used for email notifications)
     * @return the unique message ID assigned by SNS for tracking purposes
     * @throws RuntimeException if the message publishing fails
     */
    public String publishMessage(final String message, final String subject) {
        PublishRequest request = PublishRequest.builder()
            .message(message)
            .subject(subject)
            .topicArn(this.topicArn)
            .build();

        final PublishResponse response = snsClient.publish(request);
        return response.messageId();
    }

}