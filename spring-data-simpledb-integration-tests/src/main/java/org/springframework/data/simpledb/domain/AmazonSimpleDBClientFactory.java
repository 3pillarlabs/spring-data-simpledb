package org.springframework.data.simpledb.domain;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;

public final class AmazonSimpleDBClientFactory {

    private AmazonSimpleDBClientFactory() { }

	public static final String TEST_AMAZON_ACCESS_KEY = "AKIAIVX775TRPPSZTEMQ";
	public static final String TEST_AMAZON_PRIVATE_KEY = "Nzy6w0iq8JI+DHgdiPPiuqixiMoWQmPhWFgQzOZY";

	public static AmazonSimpleDBClient getTestClient() {
		return new AmazonSimpleDBClient(new AWSCredentials() {

			@Override
			public String getAWSAccessKeyId() {
				return TEST_AMAZON_ACCESS_KEY;
			}

			@Override
			public String getAWSSecretKey() {
				return TEST_AMAZON_PRIVATE_KEY;
			}
		});
	}
}
