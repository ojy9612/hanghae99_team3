package com.hanghae99_team3.model.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99_team3.elasticsearch.Indices;
import com.hanghae99_team3.elasticsearch.SearchRequestDto;
import com.hanghae99_team3.elasticsearch.SearchUtil;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ResourceService2 {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(ResourceService2.class);

    private final RestHighLevelClient client;

    @Autowired
    public ResourceService2(RestHighLevelClient client) {
        this.client = client;
    }

    public List<Resource> search(final SearchRequestDto dto) {
        final SearchRequest request = SearchUtil.buildSearchRequest(
                Indices.RESOURCE_INDEX,
                dto
        );
        if (request == null){
            LOG.error("Failded to build search request");
            return Collections.emptyList();
        }
        try {
            final SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            final SearchHit[] searchHits = response.getHits().getHits();
            final List<Resource> resources = new ArrayList<>(searchHits.length);
            for (SearchHit hit : searchHits) {
                resources.add(
                        MAPPER.readValue(hit.getSourceAsString(), Resource.class)
                );
            }

            return resources;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }



    public Boolean index(final Resource resource) {
        try {
            final String resourceAsString = MAPPER.writeValueAsString(resource);

            final IndexRequest request = new IndexRequest(Indices.RESOURCE_INDEX);
            request.id(resource.getResourceName());
            request.source(resourceAsString, XContentType.JSON);

            final IndexResponse response = client.index(request, RequestOptions.DEFAULT);

            return response != null && response.status().equals(RestStatus.OK);

        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    public Resource getByResourceName(final String resourceName) {
        try {
            final GetResponse documentFields = client.get(
                    new GetRequest(Indices.RESOURCE_INDEX, resourceName),
                    RequestOptions.DEFAULT
            );
            if (documentFields == null || documentFields.isSourceEmpty()) {
                return null;
            }

            return MAPPER.readValue(documentFields.getSourceAsString(), Resource.class);
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }


    }
}