package com.mygdx.hadal.server;

import org.ice4j.Transport;
import org.ice4j.TransportAddress;
import org.ice4j.ice.*;
import org.ice4j.ice.harvest.StunCandidateHarvester;
import org.ice4j.ice.harvest.TurnCandidateHarvester;
import org.ice4j.security.LongTermCredential;

import java.io.IOException;

public class RelayManager {

    private static final String stunServer = "stun.l.google.com"; // Public STUN server
    private static final String turnServer = "52.14.245.238"; // Your TURN server
    private static final String turnUsername = "Telemachus"; // Username for TURN server
    private static final String turnPassword = "SunlessSea"; // Password for TURN server

//    private static final String turnServer = "us-turn3.xirsys.com"; // Your TURN server
//    private static final String turnUsername = "nDmFyUhkA8ZMYMQmrbfRHmjXoe7Pe7iMYoM9SDKJKHgTWyK6mXqHiNgxStwzit0IAAAAAGcJsApEb25wb21tZWxv"; // Username for TURN server
//    private static final String turnPassword = "cbcc2e0e-8825-11ef-b0ae-0242ac140004"; // Password for TURN server

    private static Agent agent;

    public static void getRelayDetails() throws IOException {
        agent = new Agent();

        LongTermCredential credential = new LongTermCredential(turnUsername, turnPassword);

        TransportAddress stunServerUDPAddress = new TransportAddress(stunServer, 19302, Transport.UDP);
        TransportAddress turnServerUDPAddress = new TransportAddress(turnServer, 3478, Transport.UDP);
        TransportAddress turnServerTCPAddress = new TransportAddress(turnServer, 3478, Transport.TCP);

        // Add STUN server
        agent.addCandidateHarvester(new StunCandidateHarvester(stunServerUDPAddress));
        agent.addCandidateHarvester(new TurnCandidateHarvester(turnServerUDPAddress, credential));
        agent.addCandidateHarvester(new TurnCandidateHarvester(turnServerTCPAddress, credential));

        IceMediaStream stream = IceMediaStream.build(agent, "test");

        Component component = agent.createComponent(stream, KeepAliveStrategy.SELECTED_AND_TCP, true);

        // Gather candidates (this may take some time based on network conditions)
        agent.startConnectivityEstablishment();

        // Periodically check for gathered candidates
        new Thread(() -> {
            while (true) {
                // Sleep for a while (e.g., 2 seconds) before checking again
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                for (LocalCandidate candidate : component.getLocalCandidates()) {
                    System.out.println("Local Candidate: " + candidate);
                }

                // Check ICE state
                if (agent.getState() == IceProcessingState.COMPLETED) {
                    System.out.println("ICE connectivity established!");

                    break; // Exit the loop after finding candidates
                } else {
                    System.out.println("ICE State: " + agent.getState() + " " + stream.getCheckList().getState());
                }
            }
        }).start();
    }
}
