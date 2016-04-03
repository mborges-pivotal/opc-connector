package io.pivotal.tola;

import com.gopivotal.tola.opc.ConnectionConfiguration;
import com.gopivotal.tola.opc.OpcDaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.messaging.handler.annotation.SendTo;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.integration.scheduling.PollerMetadata.DEFAULT_POLLER;

@EnableBinding(Source.class)
@EnableConfigurationProperties(OpcSourceProperties.class)
public class OpcSource {

    @Autowired
    OpcSourceProperties _properties;

    private OpcDaClient opc;
    private DataCallbackImpl dcb;

    private List<OpcItemData> events = new ArrayList<>(100);

    // Either an usage_event or dynamic_basket depending on the ratio
    @InboundChannelAdapter( value = Source.OUTPUT, poller = @Poller( DEFAULT_POLLER ) )
    public OpcItemData publishOpcData() {
        if (events.size() <= 0) {
            dcb.drainQueue(events);
        }

        if (events.size() <= 0) {
            return null;
        }

        OpcItemData itemData = events.remove(0);
        return itemData;
    }

    @Override
    public String toString() {
        return "OpcDaSource [clsId=" + _properties.getClsId() + ", progId=" + _properties.getProgId() + ", host="
                + _properties.getHost() + ", domain=" + _properties.getDomain() + ", user=" + _properties.getUser()
                + ", password=" + _properties.getPassword() + ", tags=" + Arrays.toString(_properties.getTags()) + "]";
    }

    @PostConstruct
    public void initialize() throws Exception {

        // Connect
        opc = new OpcDaClient();
        ConnectionConfiguration connConfig = new ConnectionConfiguration();
        connConfig.setProgId(_properties.getProgId());
        connConfig.setClsId(_properties.getClsId());
        connConfig.setDomain(_properties.getDomain());
        connConfig.setHost(_properties.getHost());
        connConfig.setUser(_properties.getUser());
        connConfig.setPassword(_properties.getPassword());
        opc.setConnConfig(connConfig);
        dcb = new DataCallbackImpl("OPC_XD");
        opc.dcb = dcb;

        System.out.println("Setting properties - " + connConfig);

        opc.connect();

        // Add Tags
        opc.tags = _properties.getTags();
        for (String tag : opc.tags) {
            opc.addItem(tag);
        }

        // Listen
        opc.start();
    }

    @PreDestroy
    public void disconnect() {
        // Disconnect
        System.out.println("Disconnecting....");
        opc.disconnect();
        System.out.println("Disconnected");
    }
}
