package com.heatedline.config;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.content.fs.config.EnableFilesystemStores;
import org.springframework.content.solr.EnableFullTextSolrIndexing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

/*@Configuration
@EnableSolrRepositories(basePackages = "com.heatedline.repository")
@EnableFullTextSolrIndexing
@EnableFilesystemStores
@ComponentScan*/
public class SolrConfig {

	/*@Bean
	public SolrClient solrClient() {
		return new HttpSolrClient.Builder("http://localhost:8983/solr/").build();
	}

	@Bean
	public SolrTemplate solrTemplate(SolrClient client) throws Exception {
		return new SolrTemplate(client);
	}*/

}
