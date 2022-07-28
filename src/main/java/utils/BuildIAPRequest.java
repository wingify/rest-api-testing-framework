package utils;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.IdTokenCredentials;
import com.google.auth.oauth2.IdTokenProvider;
import com.google.common.base.Preconditions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generates IAP Bearer Token
 */
public class BuildIAPRequest {
    private static final String IAM_SCOPE = "https://oauth2.googleapis.com/token";

    private static final HttpTransport httpTransport = new NetHttpTransport();

    private BuildIAPRequest() {
    }

    private static IdTokenProvider getIdTokenProvider() throws IOException {
        GoogleCredentials credentials = GoogleCredentials
                .getApplicationDefault()
                .createScoped(Collections.singleton(IAM_SCOPE));

        Preconditions.checkNotNull(credentials, "Expected to load credentials");
        Preconditions.checkState(
                credentials instanceof IdTokenProvider,
                String.format(
                        "Expected credentials that can provide id tokens, got %s instead",
                        credentials.getClass().getName()
                )
        );

        return (IdTokenProvider) credentials;
    }

    /**
     * Clone request and add an IAP Bearer Authorization header with signed JWT token.
     *
     * @param request     Request to add authorization header
     * @param iapClientId OAuth 2.0 client ID for IAP protected resource
     * @return Clone of request with Bearer style authorization header with signed jwt token.
     * @throws IOException exception creating signed JWT
     */
    public static HttpRequest buildIapRequest(
            HttpRequest request,
            String iapClientId
    )
            throws IOException {
        IdTokenProvider idTokenProvider = getIdTokenProvider();
        IdTokenCredentials credentials = IdTokenCredentials
                .newBuilder()
                .setIdTokenProvider(idTokenProvider)
                .setTargetAudience(iapClientId)
                .build();

        HttpRequestInitializer httpRequestInitializer = new HttpCredentialsAdapter(
                credentials
        );

        return httpTransport
                .createRequestFactory(httpRequestInitializer)
                .buildRequest(
                        request.getRequestMethod(),
                        request.getUrl(),
                        request.getContent()
                );
    }

    public static String getIapToken() {
        String bearer = "";
        try {
            String client = TestUtilFunctions.decodeBase64(LocalConfigs.clientId);
            HttpRequest request = httpTransport
                    .createRequestFactory()
                    .buildGetRequest(
                            new GenericUrl(
                                    ""
                            )
                    );
            HttpRequest iapRequest = buildIapRequest(request, client);
            List token = (ArrayList) iapRequest.getHeaders().get("Authorization");
            bearer = token.get(0).toString();
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("Creds File missing on required Path. Please check");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bearer;
    }
}
