package com.mygdx.hadal.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.TerminateInstancesRequest;

public class ServerLauncher {
    private static final Logger logger = LoggerFactory.getLogger(ServerLauncher.class);

    public static void main(String[] args) {
        logger.info("Headless Server Launcher Initialized");

        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();

        config.updatesPerSecond = 30;

        new HeadlessApplication(new HadalGameHeadless(), config);

        // Mock the OpenGL context to prevent errors
        Gdx.gl = new MockGL2();
        Gdx.gl20 = Gdx.gl;

        // Add a shutdown hook to terminate the instance when the server exits
        String instanceID = System.getenv("INSTANCE_ID");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> terminateEC2Instance(instanceID)));
    }

    private static void terminateEC2Instance(String instanceID) {
        logger.info("Terminating EC2 instance with ID {}", instanceID);

        if (instanceID != null && !instanceID.isEmpty()) {
            try (Ec2Client ec2 = Ec2Client.create()) {
                TerminateInstancesRequest terminateRequest = TerminateInstancesRequest.builder()
                        .instanceIds(instanceID)
                        .build();
                ec2.terminateInstances(terminateRequest);
            } catch (Exception e) {
                logger.info("Failed to terminate instance: {}", e.getMessage());
            }
        } else {
            logger.info("Failed to terminate instance: No instanceID was found");
        }
    }
}