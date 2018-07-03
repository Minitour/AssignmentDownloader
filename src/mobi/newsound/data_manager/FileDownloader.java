package mobi.newsound.data_manager;

import javafx.application.Platform;
import mobi.newsound.network.APIManager;
import mobi.newsound.network.Constants;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Antonio Zaitoun on 02/07/2018.
 */
public class FileDownloader {



    public static void download(String address, String localFileName) throws IOException {
        Request request = new Request.Builder()
                .url(address + "?token=" + Constants.ws_token + "&offline=1")
                .addHeader("host","mw5.haifa.ac.il")
                .addHeader("content-type","application/x-www-form-urlencoded;charset=UTF-8")
                .addHeader("origin","file://")
                .addHeader("cookie","MoodleSession="+ Constants.moodle_session)
                .addHeader("accept","application/json, text/plain, */*")
                .addHeader("user-agent","Mozilla/5.0 (iPhone; CPU iPhone OS 11_2_2 like Mac OS X) AppleWebKit/604.4.7 (KHTML, like Gecko) Mobile/15C202 MoodleMobile")
                .addHeader("accept-language","en-us")
                .addHeader("cache-control","no-cache").build();


        Response response = APIManager.getInstance().client.newCall(request).execute();
        ResponseBody body = response.body();
        long contentLength = body.contentLength();
        BufferedSource source = body.source();

        BufferedSink sink = Okio.buffer(Okio.sink(new File(localFileName)));
        Buffer sinkBuffer = sink.buffer();

        long totalBytesRead = 0;
        int bufferSize = 8 * 1024;
        for (long bytesRead; (bytesRead = source.read(sinkBuffer, bufferSize)) != -1; ) {
            sink.emit();
            totalBytesRead += bytesRead;
            //int progress = (int) ((totalBytesRead * 100) / contentLength);
            //publishProgress(progress);
        }
        sink.flush();
        sink.close();
        source.close();
    }



    public static void download(String url, String fileName,DownloadCallback callback) {
        Thread t1 = new Thread(()-> {
            try {
                try (InputStream in = URI.create(url).toURL().openStream()) {
                    Files.copy(in, Paths.get(fileName));
                }
                Platform.runLater(()-> callback.didFinishDownloading(fileName,null));
            } catch (IOException e) {
                Platform.runLater(()-> callback.didFinishDownloading(null,e));
            }
        });
        t1.start();
    }



    @FunctionalInterface
    interface DownloadCallback {
        void didFinishDownloading(String fileName,IOException e);
    }
}
