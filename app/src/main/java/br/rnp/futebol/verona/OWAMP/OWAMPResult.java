package br.rnp.futebol.verona.OWAMP;

/**
 * Created by Matias Schimuneck on 13/01/15 for Coliseu RNP.
 * Used here by Joao Vitor de Camargo on 03/05/2016.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class OWAMPResult {

    private List<String> lines;

    private String address = "";
    private long id = 0;

    public String getOriginalAddress() {
        return originalAddress;
    }

    public void setOriginalAddress(String originalAddress) {
        this.originalAddress = originalAddress;
    }

    private String originalAddress = "";
    private int transmitted = 0;
    private int ttl = 0;
    private long time = 0;
    private int received = 0;
    private int payload = 0;
    private boolean donePinging = false;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getTransmitted() {
        return transmitted;
    }

    public void setTransmitted(int transmitted) {
        this.transmitted = transmitted;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getReceived() {
        return received;
    }

    public void setReceived(int received) {
        this.received = received;
    }

    public int getPayload() {
        return payload;
    }

    public void setPayload(int payload) {
        this.payload = payload;
    }

    public float getRtt_min() {
        return rtt_min;
    }

    public void setRtt_min(float rtt_min) {
        this.rtt_min = rtt_min;
    }

    public float getRtt_avg() {
        return rtt_avg;
    }

    public void setRtt_avg(float rtt_avg) {
        this.rtt_avg = rtt_avg;
    }

    public float getRtt_max() {
        return rtt_max;
    }

    public void setRtt_max(float rtt_max) {
        this.rtt_max = rtt_max;
    }

    public float getRtt_mdev() {
        return rtt_mdev;
    }

    public void setRtt_mdev(float rtt_mdev) {
        this.rtt_mdev = rtt_mdev;
    }

    public void setListRequest(List<OWAMPRequest> listRequest) {
        this.listRequest = listRequest;
    }

    public boolean isDonePinging() {
        return donePinging;
    }

    public void setDonePinging(boolean donePinging) {
        this.donePinging = donePinging;
    }

    public List<OWAMPRequest> getListRequest() {
        return listRequest;
    }

    private List<OWAMPRequest> listRequest;
    private float rtt_min;
    private float rtt_avg;
    private float rtt_max;
    private float rtt_mdev;

    public String address() {
        return address;
    }

    public int transmitted() {
        return transmitted;
    }

    public int ttl() {
        return ttl;
    }

    public long time() {
        return time;
    }

    public int received() {
        return received;
    }

    public int payload() {
        return payload;
    }

    public float rtt_min() {
        return rtt_min;
    }

    public float rtt_avg() {
        return rtt_avg;
    }

    public float rtt_max() {
        return rtt_max;
    }

    public float rtt_mdev() {
        return rtt_mdev;
    }

    public OWAMPResult(List<String> pingOutput) {

        this.lines = pingOutput;
        transmitted = matchTransmitted(pingOutput);
        received = matchReceived(pingOutput);
        time = matchTime(pingOutput);

        rtt_min = matchRttMin(pingOutput);
        rtt_avg = matchRttAvg(pingOutput);
        rtt_max = matchRttMax(pingOutput);
        rtt_mdev = matchRttMdev(pingOutput);

        ttl = matchTTL(pingOutput);

        address = matchIP(pingOutput);

        payload = parsePayload(pingOutput);

        listRequest = matchRequests();

    }

    public OWAMPResult() {
    }

    public List<String> getLines() {
        return lines;
    }


    @Override
    public String toString() {
        return "PingResult [address=" + address + ", transmitted="
                + transmitted + ", ttl=" + ttl + ", time=" + time
                + ", received=" + received + ", payload=" + payload
                + ", rtt_min=" + rtt_min + ", rtt_avg=" + rtt_avg
                + ", rtt_max=" + rtt_max + ", rtt_mdev=" + rtt_mdev + "]";
    }


    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    private String[] pack;

    private void generatePackageArray(List<String> lines) {
        if (pack == null) {
            String packages = lines.get(lines.size() - 2);
            pack = packages.split(",");
        }
    }


    public int matchTransmitted(List<String> lines) {
        generatePackageArray(lines);
        return Integer.parseInt(pack[0].replaceAll("\\D+", ""));

    }


    public int matchReceived(List<String> lines) {
        generatePackageArray(lines);
        return Integer.parseInt(pack[1].replaceAll("\\D+", ""));

    }


    public int matchTime(List<String> lines) {
        generatePackageArray(lines);
        return Integer.parseInt(pack[3].replaceAll("\\D+", ""));

    }

    private void generateRttArray(List<String> lines) {
        if (rtt == null) {
            // rtt
            String rtts = lines.get(lines.size() - 1);
            String[] rtt_equals = rtts.split("=");
            rtt = rtt_equals[1].split("/");
        }
    }

    private String[] rtt;


    public float matchRttMin(List<String> lines) {
        generateRttArray(lines);
        return Float.parseFloat(rtt[0]);

    }


    public float matchRttAvg(List<String> lines) {
        generateRttArray(lines);
        return Float.parseFloat(rtt[1]);
    }


    public float matchRttMax(List<String> lines) {
        generateRttArray(lines);
        return Float.parseFloat(rtt[2]);
    }


    public float matchRttMdev(List<String> lines) {
        generateRttArray(lines);
        return Float.parseFloat(rtt[3].replaceAll("\\D+", ""));
    }

//    @SuppressWarnings("ignored")
    public String matchIP(List<String> lines) {
        String str = lines.toString();
        String pattern = "\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        m.find();

        return m.toMatchResult().group(0);
    }

    public int matchTTL(List<String> lines) {
        String str = lines.toString();
        Pattern pattern = Pattern.compile("ttl=([0-9\\.]+)"); // match
        // ttl=decimal

        Matcher matcher = pattern.matcher(str.toString());

        matcher.find();
        MatchResult result = matcher.toMatchResult();

        return Integer.parseInt(result.group(1).replaceAll("ttl=", ""));
    }

    protected int parsePayload(List<String> lines) {
        // TODO Auto-generated method stub

        return Integer.parseInt(lines.get(1).split("bytes")[0].trim());
    }

    public List<OWAMPRequest> matchRequests() {
        try {
            List<OWAMPRequest> requests = new ArrayList<OWAMPRequest>();

            for (String line : getLines()) {
                if (isPingRequest(line)) {
                    OWAMPRequest request = createPingRequest(line);
                    requests.add(request);
                }
            }
            return requests;
        } catch (Exception e) {
            return null;
        }


    }

    private OWAMPRequest createPingRequest(String line) {
        String[] split = line.split(" ");
        OWAMPRequest.PingRequestBuilder builder = OWAMPRequest.builder();

        int bytes = Integer.parseInt(split[0]);
        String from = split[3];
        String fromIP;
        int reqnr;
        int ttl;
        float time;
        if (split[4].contains("(") && split[4].contains(")")) {
            fromIP = split[4].replace("(", "").replace(")", "")
                    .replace(":", "");
            reqnr = Integer.parseInt(split[5].split("=")[1]);
            ttl = Integer.parseInt(split[6].split("=")[1]);
            time = Float.parseFloat(split[7].split("=")[1]);
        } else {
            fromIP = from;
            reqnr = Integer.parseInt(split[4].split("=")[1]);
            ttl = Integer.parseInt(split[5].split("=")[1]);
            time = Float.parseFloat(split[6].split("=")[1]);
        }
        builder = builder.bytes(bytes).from(from).fromIP(fromIP).reqNr(reqnr)
                .ttl(ttl).time(time);

        return builder.build();

    }

    private boolean isPingRequest(String line) {
        return line.contains("bytes from");
    }

//    public JSONObject toJSon() {
//        JSONObject json = new JSONObject();
//        try {
//            json.put("server_ip", MJsonUtil.prepareString(this.address()));
//            json.put("transmitted", MJsonUtil.prepareString(this.transmitted()));
//            json.put("ttl", MJsonUtil.prepareString(this.ttl()));
//            json.put("time", MJsonUtil.prepareString(this.time()));
//            json.put("received", MJsonUtil.prepareString(this.received()));
//            json.put("payload", MJsonUtil.prepareString(this.payload()));
//            json.put("rtt_min", MJsonUtil.prepareString(this.rtt_min()));
//            json.put("rtt_avg", MJsonUtil.prepareString(this.rtt_max()));
//            json.put("rtt_max", MJsonUtil.prepareString(this.rtt_avg()));
//            json.put("list_requests", toJSonOWAMPRequest(this.matchRequests()));
//            return json;
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    private JSONArray toJSonOWAMPRequest(List<OWAMPRequest> requests) {
//        JSONArray array = new JSONArray();
//        if (requests != null) {
//            for (OWAMPRequest request : requests) {
//                JSONObject json = new JSONObject();
//                try {
//                    json.put("bytes", MJsonUtil.prepareString(request.bytes()));
//                    json.put("from", MJsonUtil.prepareString(request.from()));
//                    json.put("from_ip", MJsonUtil.prepareString(request.fromIP()));
//                    json.put("icmp_seq", MJsonUtil.prepareString(request.reqNr()));
//                    json.put("ttl", MJsonUtil.prepareString(request.ttl()));
//                    json.put("time", MJsonUtil.prepareString(request.time()));
//                    array.put(json);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return array;
//    }


}