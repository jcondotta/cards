output "cards_table_name" {
  description = "The name of the DynamoDB table cards"
  value       = aws_dynamodb_table.cards.name
}

output "cards_table_arn" {
  description = "The ARN of the DynamoDB table cards"
  value       = aws_dynamodb_table.cards.arn
}

output "cards_by_bank_account_id_gsi_name" {
  description = "The name of the Global Secondary Index for querying cards by bankAccountId"
  value       = [for gsi in aws_dynamodb_table.cards.global_secondary_index : gsi.name if gsi.name == var.cards_by_bank_account_id_gsi_name][0]
}

output "cards_by_bank_account_id_gsi_arn" {
  description = "The ARN of the Global Secondary Index for querying cards by bankAccountId"
  value       = "${aws_dynamodb_table.cards.arn}/index/${var.cards_by_bank_account_id_gsi_name}"
}
