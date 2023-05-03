package jp.rouh.util;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * WEB上のドキュメントを読み込むリーダクラス。
 * @author Rouh
 * @version 1.0
 */
public class DocumentReader{
    private static final Logger LOG = LoggerFactory.getLogger(DocumentReader.class);
    private final String cacheDirectory;

    /**
     * 指定されたディレクトリにローカルキャッシュを作成するドキュメントリーダを生成します。
     * @param cacheDirectory キャッシュディレクトリ
     */
    public DocumentReader(String cacheDirectory){
        this.cacheDirectory = cacheDirectory;
    }

    /**
     * キャッシュを使用しないドキュメントリーダを生成します。
     */
    public DocumentReader(){
        this.cacheDirectory = null;
    }

    /**
     * 指定のURLのドキュメントを文字列形式で取得します。
     * @param url URL
     * @return ドキュメント
     */
    public String getDocument(String url){
        try {
            var fileName = "";
            var file = (File)null;
            if (cacheDirectory!=null) {
                fileName = new String(Base64.getEncoder().encode(url.getBytes(UTF_8)), UTF_8);
                file = new File(cacheDirectory + "/" + fileName);
                if (file.exists()) {
                    String content;
                    try (var br = Files.newBufferedReader(file.toPath(), UTF_8)) {
                        content = br.lines().collect(Collectors.joining("\n"));
                    }
                    return content;
                }
            }
            LOG.debug("connect: "+url);
            var content = Jsoup.connect(url).get().toString();
            LOG.debug(" -> done");
            if (cacheDirectory!=null) {
                try (var bw = Files.newBufferedWriter(file.toPath(), UTF_8)) {
                    bw.write(content);
                }
                LOG.debug(" -> saved: " + fileName);
            }
            return content;
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 指定のURLでダウンロード可能なGZIP圧縮済みドキュメントを解凍して文字列形式で取得します。
     * @param url URL
     * @return ドキュメント
     */
    public String getGZippedDocument(String url){
        try{
            var fileName = "";
            var file = (File)null;
            if (cacheDirectory!=null) {
                fileName = new String(Base64.getEncoder().encode(url.getBytes(UTF_8)), UTF_8);
                file = new File(cacheDirectory + "/" + fileName);
                if (file.exists()) {
                    String content;
                    try (var br = Files.newBufferedReader(file.toPath(), UTF_8)) {
                        content = br.lines().collect(Collectors.joining("\n"));
                    }
                    return content;
                }
            }
            LOG.debug("connect: "+url);
            String content;
            try(var gis = new GZIPInputStream(new URL(url).openStream());
                var br = new BufferedReader(new InputStreamReader(gis, StandardCharsets.UTF_8))) {
                content = br.lines().collect(Collectors.joining("\n"));
            }
            LOG.debug(" -> done");
            if (cacheDirectory!=null) {
                try (var bw = Files.newBufferedWriter(file.toPath(), UTF_8)) {
                    bw.write(content);
                }
                LOG.debug(" -> saved: " + fileName);
            }
            return content;
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
