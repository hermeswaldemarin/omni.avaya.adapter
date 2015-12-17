package br.com.omniplusoft.gateway.avayaimpl.domain;

import br.com.omniplusoft.gateway.domain.ctiplatform.CTIStatusResponse;
import com.avaya.jtapi.tsapi.LucentCallInfo;
import com.avaya.jtapi.tsapi.LucentV6Agent;
import com.avaya.jtapi.tsapi.UserToUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.telephony.*;
import javax.telephony.callcontrol.CallControlConnectionEvent;
import javax.telephony.callcontrol.CallControlTerminalConnectionEvent;
import javax.telephony.callcontrol.CallControlTerminalConnectionListener;

@Service
@Lazy
public class AvayaCallControlTerminalConnectionListener implements
        CallControlTerminalConnectionListener {

    private final AvayaService avayaService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public boolean onHold = false;

    @Autowired
    public AvayaCallControlTerminalConnectionListener(AvayaService avayaService){
        this.avayaService = avayaService;
    }

    public void terminalConnectionBridged(CallControlTerminalConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("BRIDGED"));
        logger.trace("terminalConnectionBridged");
    }

    public void terminalConnectionDropped(CallControlTerminalConnectionEvent e) {
        if(!onHold){
            avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("DROPPED"));
        }
        logger.trace("terminalConnectionDropped");
    }

    public void terminalConnectionHeld(CallControlTerminalConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("HOLD"));
        onHold = true;
        logger.trace("terminalConnectionHeld");
    }

    public void terminalConnectionInUse(CallControlTerminalConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("IN_USE"));
        logger.trace("terminalConnectionInUse");
    }

    public void terminalConnectionRinging(CallControlTerminalConnectionEvent e) {

        logger.trace("terminalConnectionRinging");

        Address callingAddress = e.getCallingAddress();
        Call call = e.getCall();
        UserToUserInfo avayaUUI = null;
        String uui = new String();

        if(avayaService.getConsultCall() == null){

            logger.trace("HAS ACTIVE CALL");


            if(avayaService.getActiveCall() == null){

                avayaService.setActiveCall(call);

                if (callingAddress != null) {
                    logger.trace( "Terminal [{}] receiving call from [{}]", avayaService.getActiveTerminal().getName(), callingAddress.getName());
                } else {
                    logger.trace( "Terminal [{}] receiving call from [unknown]", avayaService.getActiveTerminal().getName());
                }

                if (call instanceof LucentCallInfo) {
                    if (e.getID() != CallControlConnectionEvent.CALLCTL_CONNECTION_NETWORK_REACHED) {
                        avayaUUI = ((LucentCallInfo) call).getUserToUserInfo();
                        if (avayaUUI != null) {
                            logger.trace("terminalConnectionRinging : avayaUUI " + avayaUUI.getString());

                            uui = avayaUUI.getString().split("\0")[0];
                        }
                    }
                }

                logger.trace("terminalConnectionRinging : 1 ");

                if (uui != null) {
                    logger.info("UUI: " + uui);
                }

                logger.trace("terminalConnectionRinging : 2 ");

                String origin ;
                if (e.getCallingAddress()!= null) {
                    origin = e.getCallingAddress().getName();
                } else{
                    origin = "unknown";
                }

                try
                {

                    logger.trace("terminalConnectionRinging : 5 ");

                    uui = uui.replace('\r', ' ');
                    uui = uui.replace('\n', ' ');

                    logger.trace("terminalConnectionRinging : 6 ");

                    logger.trace("terminalConnectionRinging : 11 ");

                    avayaService.sendUUI(uui, origin);

                    logger.trace("terminalConnectionRinging : 13 ");

                }
                catch (Exception e2)
                {
                    logger.error("terminalConnectionRinging : Error : ", e2);
                }
            }

        }
    }


    public void terminalConnectionTalking(CallControlTerminalConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("SPEAKING"));
        onHold = false;
        logger.trace("terminalConnectionTalking");
    }


    public void terminalConnectionUnknown(CallControlTerminalConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("UNKNOWN"));
        logger.trace("terminalConnectionUnknown");
    }


    public void connectionAlerting(CallControlConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("ALERTING"));
        logger.trace("connectionAlerting");
    }


    public void connectionDialing(CallControlConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("DIALING"));
        logger.trace("connectionDialing");
    }


    public void connectionDisconnected(CallControlConnectionEvent e) {
        if(!onHold){
            avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("DISCONNECTED"));
        }
        logger.trace("connectionDisconnected");
    }

    public void connectionEstablished(CallControlConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("CALL_RECEIVED"));

        Call call = e.getCall();

        if(this.avayaService.getConsultCall() == null){
            if(this.avayaService.getActiveCall() == null){

                this.avayaService.setActiveCall(call);
            }

        }

        logger.trace("connectionEstablished");
    }


    public void connectionFailed(CallControlConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("FAILED"));
        logger.trace("connectionFailed");
    }


    public void connectionInitiated(CallControlConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("INITIATED"));
        logger.trace("connectionInitiated");
    }


    public void connectionNetworkAlerting(CallControlConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("NETWORK_ALERTING"));
        logger.trace("connectionNetworkAlerting");
    }


    public void connectionNetworkReached(CallControlConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("REACHED"));
        logger.trace("connectionNetworkReached");
    }


    public void connectionOffered(CallControlConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("OFFERED"));
        logger.trace("connectionOffered");
    }


    public void connectionQueued(CallControlConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("QUEUED"));
        logger.trace("connectionQueued");
    }


    public void connectionUnknown(CallControlConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("UNKNOWN"));
        logger.trace("connectionUnknown");
    }


    public void callActive(CallEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("ACTIVE"));
        logger.trace("callActive");
    }


    public void callEventTransmissionEnded(CallEvent e) {

        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("CALL_ENDED"));

        Runnable task2 = () -> {
            try {
                int stateAgent = ((LucentV6Agent)avayaService.getAgentLogged()).getState();
                avayaService.setConsultCall(null);
                avayaService.setActiveCall(null);

                String status = null;

                switch (stateAgent) {
                    case 0:
                        status = "UNKNOWN";
                        break;
                    case 1:
                        status = "LOGGED";
                        break;
                    case 2:
                        status = "LOGGED OUT";
                        break;
                    case 3:
                        status = "NOT READY";
                        break;
                    case 4:
                        status = "READY";
                        break;
                    case 5:
                        status = "ACW";
                        break;
                    case 6:
                        status = "WORKING_READY";
                        break;
                    default:
                        break;
                }
                if(status != null)
                    avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse(status));
            }catch (Exception ex) {
                logger.error("callEventTransmissionEnded - > ERROR ", ex);

            }
        };

        new Thread(task2).start();

    }


    public void callInvalid(CallEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("INVALID"));
        logger.trace("callInvalid");
    }


    public void multiCallMetaMergeEnded(MetaEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("MERGE_ENDED"));
        logger.trace("multiCallMetaMergeEnded");
    }


    public void multiCallMetaMergeStarted(MetaEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("MERGE_STARTED"));
        logger.trace("multiCallMetaMergeStarted");
    }


    public void multiCallMetaTransferEnded(MetaEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("MERGE_TRANSFER_ENDED"));
        logger.trace("multiCallMetaTransferEnded");
    }


    public void multiCallMetaTransferStarted(MetaEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("M_TRANSF_STARTED"));
        logger.trace("multiCallMetaTransferStarted");
    }


    public void singleCallMetaProgressEnded(MetaEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("M_PROGRESS_ENDED"));
        logger.trace("singleCallMetaProgressEnded");
    }


    public void singleCallMetaProgressStarted(MetaEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("M_PROGRESS_STARTED"));
        logger.trace("singleCallMetaProgressStarted");
    }


    public void singleCallMetaSnapshotEnded(MetaEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("M_SNAP_ENDED"));
        logger.trace("singleCallMetaSnapshotEnded");
    }


    public void singleCallMetaSnapshotStarted(MetaEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("M_SNAP_STARTED"));
        logger.trace("singleCallMetaSnapshotStarted");
    }


    public void connectionAlerting(ConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("ALERTING"));
        logger.trace("connectionAlerting");
    }


    public void connectionConnected(ConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("CONNECTED"));
        logger.trace("connectionConnected");
    }


    public void connectionCreated(ConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("CREATED"));
        logger.trace("connectionCreated");
    }


    public void connectionDisconnected(ConnectionEvent e) {
        if(!onHold){
            avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("DISCONNECTED"));
        }
        logger.trace("connectionDisconnected");
    }


    public void connectionFailed(ConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("FAILED"));
        logger.trace("connectionFailed");
    }


    public void connectionInProgress(ConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("IN_PROGRESS"));
        logger.trace("connectionInProgress");
    }


    public void connectionUnknown(ConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("UNKNOWN"));
        logger.trace("connectionUnknown");
    }


    public void terminalConnectionActive(TerminalConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("ACTIVE"));
        logger.trace("terminalConnectionActive");

    }


    public void terminalConnectionCreated(TerminalConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("CREATED"));
        logger.trace("terminalConnectionCreated");

    }


    public void terminalConnectionDropped(TerminalConnectionEvent e) {
        if(!onHold){
            avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("DROPPED"));
        }
        logger.trace("terminalConnectionDropped");
    }


    public void terminalConnectionPassive(TerminalConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("PASSIVE"));
        logger.trace("terminalConnectionPassive");
    }


    public void terminalConnectionRinging(TerminalConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("RINGING"));
        logger.trace("terminalConnectionRinging");
    }


    public void terminalConnectionUnknown(TerminalConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("UNKNOWN"));
        logger.trace("terminalConnectionUnknown");
    }

}