output "lambda_bucket_name" {
  value = aws_s3_bucket.cards_process_service_bucket.bucket
}

output "query_service_ecr_url" {
  value = aws_ecr_repository.query_service_repo.repository_url
}

output "management_service_ecr_url" {
  value = aws_ecr_repository.management_service_repo.repository_url
}