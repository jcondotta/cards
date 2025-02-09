output "cards_process_service_lambda_files_bucket_name" {
  description = "The name of the S3 bucket that stores deployment artifacts for the Cards Process Service Lambda."
  value       = aws_s3_bucket.cards_process_service_lambda_files_bucket.bucket
}