package com.mygdx.hadal.utils;

import com.badlogic.gdx.Gdx;
import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.GatewayDiscover;
import org.bitlet.weupnp.PortMappingEntry;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The UPNP Util establishes a upnp connection upon opening the game to circumvent port-forwarding
 * This uses the weUPNP library.
 */
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

                        //normally we would just run addPortMapping(), but we copy the code here to expose neqValue to log error
                        Map<String, String> args = new LinkedHashMap<>();
                        args.put("NewRemoteHost", "");    // wildcard, any remote host matches
                        args.put("NewExternalPort", Integer.toString(port));
                        args.put("NewProtocol", protocol);
                        args.put("NewInternalPort", Integer.toString(port));
                        args.put("NewInternalClient", localAddress.getHostAddress());
                        args.put("NewEnabled", Integer.toString(1));
                        args.put("NewPortMappingDescription", descr);
                        args.put("NewLeaseDuration", Integer.toString(0));

                        Map<String, String> nameValue = GatewayDevice.simpleUPnPcommand(d.getControlURL(),
                                d.getServiceType(), "AddPortMapping", args);

                        if (null == nameValue.get("errorDescription")) {
                            Gdx.app.log("UPNP", "SUCCESSFULLY MAPPED");
                        } else {
                            Gdx.app.log("UPNP", "FAILED TO MAP PORT: " + nameValue.get("errorDescription"));
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
