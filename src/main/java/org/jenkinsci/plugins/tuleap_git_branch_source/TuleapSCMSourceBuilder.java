package org.jenkinsci.plugins.tuleap_git_branch_source;

import org.jenkinsci.plugins.tuleap_git_branch_source.client.api.TuleapGitRepository;
import org.jenkinsci.plugins.tuleap_git_branch_source.client.api.TuleapProject;

import edu.umd.cs.findbugs.annotations.NonNull;
import jenkins.scm.api.trait.SCMSourceBuilder;

public class TuleapSCMSourceBuilder extends SCMSourceBuilder<TuleapSCMSourceBuilder, TuleapSCMSource> {
    private final String id;
    private final String credentialsId;
    private final TuleapProject project;
    private final TuleapGitRepository repository;
    private final String apiTokenCredentialsId;

    public TuleapSCMSourceBuilder(String id, String credentialsId, TuleapProject project, TuleapGitRepository repository, String apiTokenCredentialsId) {
        super(TuleapSCMSource.class, repository.getPath());
        this.apiTokenCredentialsId = apiTokenCredentialsId;
        this.id = id;
        this.credentialsId = credentialsId;
        this.project = project;
        this.repository = repository;
    }

    public String id() {
        return id;
    }

    public String credentialsId() {
        return credentialsId;
    }

    // projectName is the representation of a repo git in the context of a SCM

    @NonNull
    @Override
    public TuleapSCMSource build() {
        TuleapSCMSource result = new TuleapSCMSource(project, repository);
        result.setId(id());
        result.setTraits(traits());
        result.setCredentialsId(credentialsId());
        result.setTokenApiCredentialsId(apiTokenCredentialsId);
        return result;
    }
}
