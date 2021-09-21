package app.server;

import ogame.utils.log.AppLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Connector {
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String GET_URL = "http://localhost/osb-v2/hash-get.php";
    private static final String POST_URL = "http://localhost/osb-v2/hash.php";
    private static final String POST_PARAMS = "jpass=start&jhash=$2y$10$pJ.YggKTHsziI2sSxdJafu4sMENvMOtk3n6uinZ4ZDJkEOa1FmN0K";

    public static boolean validatePassword(String pass, String hash) {
        String params = "jpass="+pass+"&jhash="+hash;
        try{
            URL obj = new URL(POST_URL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);

            // For POST only - START
            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            os.write(params.getBytes());
            os.flush();
            os.close();
            // For POST only - END

            int responseCode = con.getResponseCode();
            AppLog.print(Connector.class.getName(),0,"POST Response Code :: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) { //success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // print result
                return response.toString().equals("1");
            } else {
                AppLog.print(Connector.class.getName(),0,"POST request not worked");
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
