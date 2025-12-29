package org.azurecloud.solutions.akiraclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * Rest template config to manage what rest template in context.
 *
 * @author Dmitrii Vinogradov
 */
@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate(@Value("${akira.server.cert-unsecure:false}")
                                     boolean unsecureCerts) throws Exception {
        if (unsecureCerts) {
            disableSecureCertCheck();
        }

        return new RestTemplate();
    }

    private void disableSecureCertCheck() throws Exception {
        HttpsURLConnection.setDefaultHostnameVerifier((_, _) -> true);
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }
}
