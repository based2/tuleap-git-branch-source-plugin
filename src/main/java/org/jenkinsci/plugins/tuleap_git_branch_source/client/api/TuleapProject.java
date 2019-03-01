package org.jenkinsci.plugins.tuleap_git_branch_source.client.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Translate a ProjectRepresentation
 *
 * @see <a href= https://tuleap.net/api/explorer/#!/projects/retrieve>https://tuleap.net/api/explorer/#!/projects/retrieve</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TuleapProject {

    private int id;
    private String uri;
    private String label;
    private String shortname;

    public TuleapProject() {
    }

    public TuleapProject(int id, String uri, String label, String shortname) {
        this.id = id;
        this.uri = uri;
        this.label = label;
        this.shortname = shortname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }
}
