package com.kolhey.p2p.database;

public interface PeerDatabase {
    boolean isTrusted(String fingerprint);
    void trustPeer(String peerName, String fingerprint);
    void revokePeer(String fingerprint);
}
