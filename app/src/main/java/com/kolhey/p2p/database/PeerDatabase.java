package com.kolhey.p2p.database;

import java.util.Map;

public interface PeerDatabase {
    boolean isTrusted(String fingerprint);
    void trustPeer(String peerName, String fingerprint);
    void revokePeer(String fingerprint);
    
    /**
     * Returns a map of all trusted peers.
     * @return Map where key=fingerprint, value=peer name
     */
    Map<String, String> getAllTrustedPeers();
}
