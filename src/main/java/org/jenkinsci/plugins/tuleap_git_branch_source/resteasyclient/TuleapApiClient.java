package org.jenkinsci.plugins.tuleap_git_branch_source.resteasyclient;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.UnknownHostException;

@Path("/api")
public interface TuleapApiClient {

    @GET
    @Path("/explorer/swagger.json")
    public Response getApiExplorer() throws ProcessingException;

}
