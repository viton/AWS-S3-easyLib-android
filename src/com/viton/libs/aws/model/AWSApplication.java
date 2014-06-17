package com.viton.libs.aws.model;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.model.CannedAccessControlList;

public class AWSApplication {

	private BasicAWSCredentials credentials;
	private String bucketName;
	private CannedAccessControlList access;
	
	public AWSApplication(BasicAWSCredentials credentials, String bucketName, CannedAccessControlList access){
		this.credentials = credentials;
		this.bucketName = bucketName;
		this.access = access;
	}
	
	public BasicAWSCredentials getCredentials() {
		return credentials;
	}

	public void setCredentials(BasicAWSCredentials credentials) {
		this.credentials = credentials;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public CannedAccessControlList getAccess() {
		return access;
	}

	public void setAccess(CannedAccessControlList access) {
		this.access = access;
	}
	
}
