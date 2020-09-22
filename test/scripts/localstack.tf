provider "aws" {
  region                      = "us-east-1"
  access_key                  = ""
  secret_key                  = ""
  skip_credentials_validation = true
  skip_metadata_api_check     = true
  skip_requesting_account_id  = true

  endpoints {
    s3 = "http://localhost:4566"
  }
}

resource "aws_s3_bucket" "bucket" {
  bucket = "local-s3-bucket-2"
#  region = "us-east-1"
  acl    = "public-read"
}
