// package com.prompter.service;
//
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
//
// import lombok.extern.slf4j.Slf4j;
// import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
// import software.amazon.awssdk.auth.credentials.AwsCredentials;
// import software.amazon.awssdk.regions.Region;
// import software.amazon.awssdk.services.kendra.KendraClient;
// import software.amazon.awssdk.services.kendra.model.QueryRequest;
// import software.amazon.awssdk.services.kendra.model.QueryResponse;
// import software.amazon.awssdk.services.kendra.model.QueryResultItem;
//
// @Service
// @Slf4j
// public class SearchKendraIndex {
//
//
// 	@Value("${aws.credentials.accessKey}")
// 	private String accessKey;
// 	@Value("${aws.credentials.secretKey}")
// 	private String secretKey;
//
// 	private final AwsCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
//
//
// 	private final KendraClient kendra = KendraClient
// 		.builder()
// 		.region(Region.AP_NORTHEAST_1) // change it with your region
// 		.credentialsProvider(() -> awsCredentials)
// 		.build();
//
// 	// private final String query = "your-search-term";
// 	// private final String indexId = "your-index-id";
//
// 	public void search() {
// 		String query = "your-search-term";
//
// 		String indexId = "your-index-id";
// 		QueryRequest queryRequest = QueryRequest
// 			.builder()
// 			.queryText(query)
// 			.indexId(indexId)
// 			.build();
//
// 		QueryResponse queryResponse = kendra.query(queryRequest);
//
// 		log.info("search results for query : {}", query);
// 		for (QueryResultItem item : queryResponse.resultItems()) {
// 			log.info("----------------------");
// 			log.info("type : {}", item.type());
// 			switch (item.type()) {
// 				case QUESTION_ANSWER, ANSWER -> {
// 					String answerText = item.documentExcerpt().text();
// 					log.info(answerText);
// 				}
// 				case DOCUMENT -> {
// 					String documentTitle = item.documentTitle().text();
// 					log.info("title: {}", documentTitle);
//
// 					String documentExcerpt = item.documentExcerpt().text();
// 					log.info("excerpt: {}", documentExcerpt);
// 				}
// 				default -> log.info("unknown query result type: {}", item.type());
// 			}
// 			log.info("-----------------------\n");
// 		}
// 	}
//
// }