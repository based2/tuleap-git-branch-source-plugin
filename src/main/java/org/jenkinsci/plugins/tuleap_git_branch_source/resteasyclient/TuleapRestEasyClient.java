package org.jenkinsci.plugins.tuleap_git_branch_source.resteasyclient;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import hudson.util.Secret;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.JaxrsFormProvider;
import org.jenkinsci.plugins.tuleap_git_branch_source.client.api.*;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.ArrayList;
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
        int limit = 50;
        int offset = 0;
        int totalCountPage = 0;
        int page = 0;
        List<TuleapProject> projectList = new ArrayList<TuleapProject>();

        do {
            offset = page * limit;
            Response response = this.proxy.getProjects(accessKey.getPlainText(), "true", limit, offset);
            if (offset == 0) {
                int numberOfProject = Integer.parseInt(response.getHeaderString("x-pagination-size"));
                totalCountPage = numberOfProject / limit + ((numberOfProject % limit == 0) ? 0 : 1);
            }

            JSONArray jsonProjects = response.readEntity(JSONArray.class);

            for (int i = 0; i < jsonProjects.size(); i++) {
                JSONObject jsonProject = jsonProjects.getJSONObject(i);
                TuleapProject project = new TuleapProject(Integer.parseInt(jsonProject.getString("id")), jsonProject.getString("uri"), jsonProject.getString("label"), jsonProject.getString("shortname"));
                projectList.add(project);
            }
            page++;
        } while (page < totalCountPage);

        return projectList;
    }

    public TuleapProject getProjectById(Secret accessKey, int projectId) {
        return this.proxy.getProjectById(accessKey.getPlainText(), projectId);
    }

    public List<TuleapBranches> getBranches(Secret accessKey, int repoId) {
        int limit = 50;
        int offset = 0;
        int totalCountPage = 0;
        int page = 0;

        List<TuleapBranches> allBranches = new ArrayList<TuleapBranches>();
        do {
            offset = page * limit;
            Response response = this.proxy.getBranches(accessKey.getPlainText(), repoId, limit, offset);
            if (offset == 0) {
                int numberOfProject = Integer.parseInt(response.getHeaderString("x-pagination-size"));
                totalCountPage = numberOfProject / limit + ((numberOfProject % limit == 0) ? 0 : 1);
            }

            JSONArray jsonBranches = response.readEntity(JSONArray.class);

            for (int i = 0; i < jsonBranches.size(); i++) {
                JSONObject jsonBranch = jsonBranches.getJSONObject(i);
                String commitId = jsonBranch.getJSONObject("commit").getString("id");
                TuleapBranches branch = new TuleapBranches(jsonBranch.getString("name"), new TuleapCommit(commitId));
                allBranches.add(branch);
            }
            page++;
        } while (page < totalCountPage);
        return allBranches;

    }

    public List<TuleapGitRepository> getRepositoriesOfAProject(Secret accessKey, int projectId) {
        int limit = 50;
        int offset = 0;
        int totalCountPage = 0;
        int page = 0;

        List<TuleapGitRepository> gitRepositories = new ArrayList<TuleapGitRepository>();


        do {
            offset = page * limit;
            Response response = this.proxy.getRepositoriesOfAProject(accessKey.getPlainText(), projectId);
            if (offset == 0) {
                Logger.getLogger("eee").info("-------------------------------------------- OMG "+ projectId + response.getHeaderString("x-pagination-size") + response.getStatus());
                int numberOfProject = Integer.parseInt(response.getHeaderString("x-pagination-size"));
                totalCountPage = numberOfProject / limit + ((numberOfProject % limit == 0) ? 0 : 1);
            }

            JSONObject jsonRepositoriesField = response.readEntity(JSONObject.class);
            JSONArray jsonRepositoriesArray = jsonRepositoriesField.getJSONArray("repositories");

            for (int i = 0; i < jsonRepositoriesArray.size(); i++) {
                JSONObject jsonRepository = jsonRepositoriesArray.getJSONObject(i);
                TuleapGitRepository tuleapGitRepository = new TuleapGitRepository(Integer.parseInt(jsonRepository.getString("id")), jsonRepository.getString("uri"), jsonRepository.getString("name"), jsonRepository.getString("path"), jsonRepository.getString("description"));
                gitRepositories.add(tuleapGitRepository);
            }
            page++;
        } while (page < totalCountPage);
        return gitRepositories;
    }

    public boolean hasCurrentBranchJenkinsfile(Secret accessKey, int gitRepoId, String file, String branchName) {
        Response response = this.proxy.getFiles(accessKey.getPlainText(), gitRepoId, file, branchName);
        Logger.getLogger("looger").info("INFO ------------------------------------------------- " + response.getStatus() + " ------ " + branchName + " " + file);
        return response.getStatus() == 200;
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
