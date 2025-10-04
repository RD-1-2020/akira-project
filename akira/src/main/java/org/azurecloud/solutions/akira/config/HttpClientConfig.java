package org.azurecloud.solutions.akira.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/**
 * Configuration for a customized HttpClient that ignores SSL certificate errors.
 * This is used for the HTTP(S) monitoring service to allow checking endpoints with self-signed certificates.
 */
@Configuration
public class HttpClientConfig {

    /**
     * Provides a singleton HttpClient bean that is configured to trust all SSL certificates.
     * @return An HttpClient instance.
     * @throws NoSuchAlgorithmException if a particular cryptographic algorithm is requested but is not available in the environment.
     * @throws KeyManagementException if any of the key management operations fail.
     */
    @Bean
    public HttpClient httpClient() throws NoSuchAlgorithmException, KeyManagementException {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());

        return HttpClient.newBuilder()
                .sslContext(sc)
                .build();
    }
}
