/*******************************************************************************
 *  Copyright 2016, 2017 Capital One Services, LLC and Bitwise, Inc.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *******************************************************************************/
package hydrograph.server.service;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.client.ClientProtocolException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;

public class HydrographServiceClient {

    String JOB_ID = "CloneDebug";
    String COMPONENT_ID = "input1";
    String SOCKET_ID = "out0";
    //TODO : Add values while testing the class, remove it once testing is done.
    String BASE_PATH = "";
    String USER_ID = "";
    String PASSWORD = "";
    String FILE_SIZE_TO_READ = "";
    String HOST_NAME = "";
    String PORT = "";

    public static void main(String[] args) {
        HydrographServiceClient client = new HydrographServiceClient();
        try {
            System.out.println("+++ Start: " + new Timestamp((new Date()).getTime()));
            // client.calltoReadService();
            // client.calltoFilterService();
          //  client.calltoReadMetastore();
             client.chcekConnectionStatus();
            // client.calltoDeleteLocalDebugService();

            System.out.println("done:");
            System.out.println("+++ End: " + new Timestamp((new Date()).getTime()));
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void chcekConnectionStatus() throws IOException {

        HttpClient httpClient = new HttpClient();
        //TODO : add connection details while testing only,remove it once done
        String teradatajson = "{\"username\":\"\",\"password\":\"\",\"hostname\":\"\",\"database\":\"\",\"dbtype\":\"\",\"port\":\"\"}";
        PostMethod postMethod = new PostMethod("http://" + HOST_NAME + ":" + PORT + "/getConnectionStatus");

        //postMethod.addParameter("request_parameters", redshiftjson);
        postMethod.addParameter("request_parameters", teradatajson);

        int response = httpClient.executeMethod(postMethod);
        InputStream inputStream = postMethod.getResponseBodyAsStream();

        byte[] buffer = new byte[1024 * 1024 * 5];
        String path = null;
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            path = new String(buffer);
        }
        System.out.println("Response of service: " + path);
        System.out.println("==================");
    }

    public void calltoReadMetastore() throws IOException {

        HttpClient httpClient = new HttpClient();
      //TODO : add connection details while testing only,remove it once done
        String teradatajson = "{\"table\":\"testting2\",\"username\":\"\",\"password\":\"\",\"hostname\":\"\",\"database\":\"\",\"dbtype\":\"\",\"port\":\"\"}";

        PostMethod postMethod = new PostMethod("http://" + HOST_NAME + ":" + PORT + "/readFromMetastore");

        //postMethod.addParameter("request_parameters", redshiftjson);
        postMethod.addParameter("request_parameters", teradatajson);

        int response = httpClient.executeMethod(postMethod);
        InputStream inputStream = postMethod.getResponseBodyAsStream();

        byte[] buffer = new byte[1024 * 1024 * 5];
        String path = null;
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            path = new String(buffer);
        }
        System.out.println("Response of service: " + path);
        System.out.println("==================");
    }

    public void calltoFilterService() throws IOException {

        HttpClient httpClient = new HttpClient();

        // String json =
        // "{\"condition\":\"abc\",\"schema\":[{\"fieldName\":\"f1\",\"dateFormat\":\"\",\"dataType\":\"1\",\"scale\":\"scale\",\"dataTypeValue\":\"java.lang.String\",\"scaleType\":1,\"scaleTypeValue\":\"scaleTypeValue\",\"precision\":\"precision\",\"description\":\"description\"},{\"fieldName\":\"f2\",\"dateFormat\":\"\",\"dataType\":1,\"scale\":\"scale\",\"dataTypeValue\":\"java.util.Date\",\"scaleType\":1,\"scaleTypeValue\":\"scaleTypeValue\",\"precision\":\"precision\",\"description\":\"description\"},{\"fieldName\":\"f3\",\"dateFormat\":\"\",\"dataType\":1,\"scale\":\"scale\",\"dataTypeValue\":\"java.util.Date\",\"scaleType\":1,\"scaleTypeValue\":\"scaleTypeValue\",\"precision\":\"precision\",\"description\":\"description\"},{\"fieldName\":\"f4\",\"dateFormat\":\"\",\"dataType\":1,\"scale\":\"scale\",\"dataTypeValue\":\"java.math.BigDecimal\",\"scaleType\":1,\"scaleTypeValue\":\"scaleTypeValue\",\"precision\":\"precision\",\"description\":\"description\"}],\"fileSize\":1,\"jobDetails\":{\"host\":\"127.0.0.1\",\"port\":\"8005\",\"username\":\"hduser\",\"password\":\"Bitwise2012\",\"basepath\":\"C:/Users/santlalg/git/Hydrograph/hydrograph.engine/hydrograph.engine.command-line\",\"uniqueJobID\":\"debug_job\",\"componentID\":\"input\",\"componentSocketID\":\"out0\",\"isRemote\":false}}";
        String json = "{\"condition\":\"(f1 LIKE 'should')\",\"schema\":[{\"fieldName\":\"f1\",\"dateFormat\":\"\",\"dataType\":\"1\",\"scale\":\"scale\",\"dataTypeValue\":\"java.lang.String\",\"scaleType\":1,\"scaleTypeValue\":\"scaleTypeValue\",\"precision\":\"precision\",\"description\":\"description\"},{\"fieldName\":\"f2\",\"dateFormat\":\"\",\"dataType\":1,\"scale\":\"scale\",\"dataTypeValue\":\"java.util.Date\",\"scaleType\":1,\"scaleTypeValue\":\"scaleTypeValue\",\"precision\":\"precision\",\"description\":\"description\"},{\"fieldName\":\"f3\",\"dateFormat\":\"\",\"dataType\":1,\"scale\":\"scale\",\"dataTypeValue\":\"java.lang.Float\",\"scaleType\":1,\"scaleTypeValue\":\"scaleTypeValue\",\"precision\":\"precision\",\"description\":\"description\"},{\"fieldName\":\"f4\",\"dateFormat\":\"\",\"dataType\":1,\"scale\":\"scale\",\"dataTypeValue\":\"java.lang.Double\",\"scaleType\":1,\"scaleTypeValue\":\"scaleTypeValue\",\"precision\":\"precision\",\"description\":\"description\"}],\"fileSize\":"
                + FILE_SIZE_TO_READ + ",\"jobDetails\":{\"host\":\"" + HOST_NAME + "\",\"port\":\"" + PORT
                + "\",\"username\":\"" + USER_ID + "\",\"password\":\"" + PASSWORD + "\",\"basepath\":\"" + BASE_PATH
                + "\",\"uniqueJobID\":\"" + JOB_ID + "\",\"componentID\":\"" + COMPONENT_ID
                + "\",\"componentSocketID\":\"" + SOCKET_ID + "\",\"isRemote\":false}}";

        PostMethod postMethod = new PostMethod("http://" + HOST_NAME + ":" + PORT + "/filter");

        postMethod.addParameter("request_parameters", json);

        int response = httpClient.executeMethod(postMethod);
        InputStream inputStream = postMethod.getResponseBodyAsStream();

        byte[] buffer = new byte[1024 * 1024 * 5];
        String path = null;
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            path = new String(buffer);
        }
        System.out.println("response of service: " + path);
    }

    public void calltoReadService() throws IOException {

        HttpClient httpClient = new HttpClient();

        PostMethod postMethod = new PostMethod("http://" + HOST_NAME + ":" + PORT + "/read");
        postMethod.addParameter("jobId", JOB_ID);
        postMethod.addParameter("componentId", COMPONENT_ID);
        postMethod.addParameter("socketId", SOCKET_ID);
        postMethod.addParameter("basePath", BASE_PATH);
        postMethod.addParameter("userId", USER_ID);
        postMethod.addParameter("password", PASSWORD);
        postMethod.addParameter("file_size", FILE_SIZE_TO_READ);
        postMethod.addParameter("host_name", HOST_NAME);

        InputStream inputStream = postMethod.getResponseBodyAsStream();

        byte[] buffer = new byte[1024 * 1024 * 5];
        String path = null;
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            path = new String(buffer);
        }
        System.out.println("response of service: " + path);
    }

    public void calltoDeleteLocalDebugService() throws IOException {

        HttpClient httpClient = new HttpClient();

        PostMethod postMethod = new PostMethod("http://" + HOST_NAME + ":" + PORT + "/deleteLocalDebugFile");
        postMethod.addParameter("jobId", JOB_ID);
        postMethod.addParameter("componentId", COMPONENT_ID);
        postMethod.addParameter("socketId", SOCKET_ID);

        java.util.Date date = new java.util.Date();
        System.out.println("+++ Start: " + new Timestamp(date.getTime()));

        int response = httpClient.executeMethod(postMethod);
        System.out.println("response: " + response);
    }
}
