resource "aws_ecr_repository" "management_service_repo" {
  name = "cards/management-service"
  image_scanning_configuration {
    scan_on_push = true
  }
}

resource "aws_ecr_repository" "query_service_repo" {
  name = "cards/query-service"
  image_scanning_configuration {
    scan_on_push = true
  }
}

resource "aws_s3_bucket" "cards_process_service_bucket" {
  bucket = "cards-process-service-${var.environment}"

  force_destroy = true
}

resource "aws_s3_bucket_versioning" "cards_process_service_bucket_versioning" {
  bucket = aws_s3_bucket.cards_process_service_bucket.id

  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_lifecycle_configuration" "cards_process_service_bucket_lifecycle" {
  bucket = aws_s3_bucket.cards_process_service_bucket.id

  rule {
    id     = "delete-old-files"
    status = "Enabled"

    expiration {
      days = 5
    }
  }
}