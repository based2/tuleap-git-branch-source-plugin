package org.jenkinsci.plugins.tuleap_git_branch_source.resteasyclient;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import hudson.util.Secret;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.JaxrsFormProvider;
import org.jenkinsci.plugins.tuleap_git_branch_source.client.api.TuleapProject;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TuleapRestEasyClient {
    private ResteasyClient client;
    private TuleapApiClient proxy;

    public TuleapRestEasyClient(String serverUrl) {
        client = buildRestEasyClient();
        ResteasyWebTarget target = client.target(UriBuilder.fromPath(serverUrl));
        proxy = target.proxyBuilder(TuleapApiClient.class).classloader(TuleapApiClient.class.getClassLoader()).build();
    }

    public boolean isServerUrlValid() throws ProcessingException {
        return this.proxy.getApiExplorer().getStatus() == 200;
    }

    public List<TuleapProject> getProjects(Secret accessKey) {

        return this.proxy.getProjects(accessKey.getPlainText(), "true", 50);
    }

    public void close() {
        if (!this.client.isClosed()) {
            this.client.close();
        }
    }

    private ResteasyClient buildRestEasyClient() {
        ResteasyClientBuilder clientBuilder = new ResteasyClientBuilder();
        clientBuilder.register(new JacksonJsonProvider()).register(new JaxrsFormProvider()).connectionPoolSize(50).connectTimeout(10, TimeUnit.SECONDS);
        return clientBuilder.build();
    }

}
