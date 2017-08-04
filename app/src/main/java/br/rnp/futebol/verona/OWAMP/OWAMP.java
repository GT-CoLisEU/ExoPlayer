package br.rnp.futebol.verona.OWAMP;

/**
 * Created by matias on 13/01/15.
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class OWAMP {
//ping -c 2 -W iperf-2.0.5 -s 32 coliseu.inf.ufrgs.br

//    public static boolean donePinging = false;

    public enum Backend {
        UNIX {
            @Override
            public OWAMPResult getResult(List<String> output) {
                return new OWAMPResult(output);
            }

        };

        public abstract OWAMPResult getResult(List<String> output);
    }

    public static OWAMPResult ping(OWAMPArguments ping, Backend backend, OWAMPResult result) {
        // result = variable that will have the result of ping
        // Feito dessa forma para que esse result possa ser usado na classe que chama
        // e seu atributo isDonePinging possa ser alterado aqui
        try {
            Process p = Runtime.getRuntime().exec(ping.getCommand());
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String s;
            List<String> lines = new ArrayList<String>();
            while ((s = stdInput.readLine()) != null) {
                lines.add(s);
                if (s.contains("Destination Host Unreachable") || s.contains("0 received, 100% packet loss")) {
//                    Log.i("CHARRT", s);
                    if (result != null) {
//                        Log.i("CHARRT", "result nulou");
                        result.setDonePinging(true);
                    }
                    return null;
                }
            }
            try {
                p.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(!lines.isEmpty()) {
                result = backend.getResult(lines);
                result.setOriginalAddress(ping.getUrl());
                result.setDonePinging(true);
//                Log.i("CHARRT", "result " + result.getAddress() + " retornou");
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static OWAMPResult ping(OWAMPArguments ping, Backend backend) {
        return ping(ping, backend, null);
    }



}
