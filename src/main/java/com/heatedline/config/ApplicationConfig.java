package com.heatedline.config;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.content.fs.config.EnableFilesystemStores;
import org.springframework.content.solr.EnableFullTextSolrIndexing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFullTextSolrIndexing
@EnableFilesystemStores
public class ApplicationConfig {

	@Bean
	public SolrClient solrClient() {
		return new HttpSolrClient.Builder("http://localhost:8983/solr/FileContent").build();
	}

}