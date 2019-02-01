package org.jenkinsci.plugins.tuleap_git_branch_source.resteasyclient;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.JaxrsFormProvider;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.UriBuilder;

public class TuleapRestEasyClient {
    private ResteasyClient client;
    private TuleapApiClient proxy;

    public TuleapRestEasyClient(String serverUrl) {
        client = buildRestEasyClient();
        ResteasyWebTarget target = client.target(UriBuilder.fromPath(serverUrl));
        proxy = target.proxyBuilder(TuleapApiClient.class).classloader(TuleapApiClient.class.getClassLoader()).build();
    }

    private ResteasyClient buildRestEasyClient() {
        ResteasyClientBuilder clientBuilder = new ResteasyClientBuilder();
        clientBuilder.register(new JacksonJsonProvider()).register(new JaxrsFormProvider()).connectionPoolSize(50);
        return clientBuilder.build();
    }

    public boolean isServerUrlValid() throws ProcessingException {
        return this.proxy.getApiExplorer().getStatus() == 200;
    }

    public void close() {
        if (!this.client.isClosed()) {
            this.client.close();
        }
    }
}
