package com.mygdx.hadal.utils;

import com.badlogic.gdx.Gdx;
import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.GatewayDiscover;
import org.bitlet.weupnp.PortMappingEntry;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.InetAddress;

public class UPNPUtil {

    //this is the player's external ip that other clients will connect to
    public static String myIP = "";
    /**
     * This attempts to enable upnp on the client's router
     * @param protocol: tcp or udp
     * @param descr: Not used for anything rn except logging
     * @param port: what port to map to
     */
    public static void upnp(String protocol, String descr, int port) {

        //We do these on a separate thread to avoid initial loading times
        new Thread(() -> {
            try {
                GatewayDiscover discover = new GatewayDiscover();
                discover.discover();
                GatewayDevice d = discover.getValidGateway();

                //Attempt to find router and acquire its information
                if (null != d) {
                    InetAddress localAddress = d.getLocalAddress();
                    myIP = d.getExternalIPAddress();
                    PortMappingEntry portMapping = new PortMappingEntry();

                    //delete existing mappings before attempting to create a new one
                    d.deletePortMapping(port, protocol);
                    if (!d.getSpecificPortMappingEntry(port, protocol, portMapping)) {
                        if (!d.addPortMapping(port, port, localAddress.getHostAddress(), protocol, descr)) {
                            Gdx.app.log("UPNP", "FAILED TO MAP PORT");
                        } else {
                            Gdx.app.log("UPNP", "SUCCESSFULLY MAPPED");
                        }
                    } else {
                        Gdx.app.log("UPNP", "ALREADY MAPPED");
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException parserConfigurationException) {
                Gdx.app.log("UPNP", "ERROR WHEN MAPPING UPNP PORT");
            }
        }).start();
    }
}
