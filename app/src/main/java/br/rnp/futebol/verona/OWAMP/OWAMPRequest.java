package br.rnp.futebol.verona.OWAMP;

/**
 * Created by Matias Schimuneck on 13/01/15 for Coliseu RNP.
 * Used here by Joao Vitor de Camargo on 03/05/2016.
 */

public class OWAMPRequest {


    @Override
    public String toString() {
        return "PingRequest [bytes=" + bytes + ", from=" + from + ", fromIP="
                + fromIP + ", reqnr=" + reqnr + ", ttl=" + ttl + ", time="
                + time + "]";
    }

    private long bytes;
    private String from;
    private String fromIP;
    private int reqnr;
    private int ttl;
    private float time;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private long id;

    public long getBytes() {
        return bytes;
    }

    public void setBytes(long bytes) {
        this.bytes = bytes;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFromIP() {
        return fromIP;
    }

    public void setFromIP(String fromIP) {
        this.fromIP = fromIP;
    }

    public int getReqnr() {
        return reqnr;
    }

    public void setReqnr(int reqnr) {
        this.reqnr = reqnr;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public long bytes() {
        return bytes;
    }

    public String from() {
        return from;
    }

    public String fromIP() {
        return fromIP;
    }

    public int reqNr() {
        return reqnr;
    }

    public int ttl() {
        return ttl;
    }

    public float time() {
        return time;
    }

    public OWAMPRequest() {
    }

    public OWAMPRequest(long id, long bytes, String from, String fromIP, int reqnr,
                        int ttl, float time) {
        super();
        this.id = id;
        this.bytes = bytes;
        this.from = from;
        this.fromIP = fromIP;
        this.reqnr = reqnr;
        this.ttl = ttl;
        this.time = time;
    }


    public OWAMPRequest(long bytes, String from, String fromIP, int reqnr,
                        int ttl, float time) {
        super();
        this.bytes = bytes;
        this.from = from;
        this.fromIP = fromIP;
        this.reqnr = reqnr;
        this.ttl = ttl;
        this.time = time;
    }

    public static PingRequestBuilder builder() {
        return new PingRequestBuilder();
    }

    protected static class PingRequestBuilder {
        private long bytes;
        private String from;
        private String fromIP;
        private int reqnr;
        private int ttl;
        private float time;

        public PingRequestBuilder() {

        }

        public PingRequestBuilder bytes(long bytes) {
            this.bytes = bytes;
            return this;
        }

        public PingRequestBuilder from(String from) {
            this.from = from;
            return this;
        }

        public PingRequestBuilder fromIP(String fromIP) {
            this.fromIP = fromIP;
            return this;
        }

        public PingRequestBuilder reqNr(int reqnr) {
            this.reqnr = reqnr;
            return this;
        }

        public PingRequestBuilder ttl(int ttl) {
            this.ttl = ttl;
            return this;
        }

        public PingRequestBuilder time(float time) {
            this.time = time;
            return this;
        }

        public OWAMPRequest build() {
            return new OWAMPRequest(bytes, from, fromIP, reqnr, ttl, time);
        }

    }

}
