package com.storage.s3demo.repository;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * Repository for request Amazon S3 buckets
 * 
 * @author messi
 *
 */
@RequiredArgsConstructor
@Repository
public class S3ObjectRepository {

	@Value("${localaws.region}")
	private String region;

	@Value("${localaws.s3-bucket.endpoint}")
	private String endpoint;

	/**
	 * Creates an AWS client for S3
	 * 
	 * @return AmazonS3 client instance
	 */
	@Bean
	private AmazonS3 awsS3Client() {
		return AmazonS3ClientBuilder.standard()
				.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
				.withClientConfiguration(new ClientConfiguration().withRequestTimeout(50000)).disableChunkedEncoding()
				.withPathStyleAccessEnabled(true).build();
	}

	/**
	 * Allows to save data into a specified S3 bucket
	 * 
	 * @param bucketName
	 * @param objectKey
	 * @param objectContent
	 * @param objectContentType
	 */
	@SneakyThrows
	public void save(final String bucketName, final String objectKey, final byte[] objectContent,
			final String objectContentType) {
		try (final InputStream byteArrayInputStream = new ByteArrayInputStream(objectContent)) {
			final ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentLength(objectContent.length);
			objectMetadata.setContentType(objectContentType);
			awsS3Client().putObject(new PutObjectRequest(bucketName, objectKey, byteArrayInputStream, objectMetadata));
		}
	}

	/**
	 * Retrieve all files of the specified S3 bucket
	 * 
	 * @param bucketName
	 * @return List of files names
	 */
	@SneakyThrows
	public List<String> getFilesNames(final String bucketName) {
		ListObjectsV2Result result = awsS3Client().listObjectsV2(bucketName);
		List<S3ObjectSummary> objects = result.getObjectSummaries();
		return objects.stream().map(S3ObjectSummary::getKey).collect(Collectors.toList());
	}
}
