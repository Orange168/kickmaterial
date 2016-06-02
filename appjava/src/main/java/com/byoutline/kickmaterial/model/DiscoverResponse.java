package com.byoutline.kickmaterial.model;

import com.byoutline.kickmaterial.utils.QueryParamsExtractor;

import java.util.List;
import java.util.Map;

/**
 * Created by Sebastian Kacprzak on 24.03.15.
 */
public class DiscoverResponse {
    public List<Project> projects;
    public DiscoverUrls urls;

    // INote: 5/24/16  the annotation purpose is exclude the findbugs plugin inspection
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings({"NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD"})
    public Map<String, String> getMoreProjectsUrl() {
        // Values are written by retrofit.
        return QueryParamsExtractor.getQueryParams(urls.api.moreProjects);
    }

}
