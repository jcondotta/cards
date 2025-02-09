resource "aws_s3_bucket" "cards_process_service_lambda_files_bucket" {
  bucket = var.cards_process_service_lambda_files_bucket_name
}

resource "aws_s3_bucket_versioning" "cards_process_service_lambda_bucket_versioning" {
  bucket = aws_s3_bucket.cards_process_service_lambda_files_bucket.id

  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_lifecycle_configuration" "cards_process_service_lambda_bucket_lifecycle" {
  bucket = aws_s3_bucket.cards_process_service_lambda_files_bucket.id

  rule {
    id = "delete-old-lambda-files"
    status = "Enabled"

    expiration {
      days = 5
    }
  }
}