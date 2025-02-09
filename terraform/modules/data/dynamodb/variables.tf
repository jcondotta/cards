variable "cards_table_name" {
  type        = string
  description = "The name of the DynamoDB table for cards"
}

variable "cards_by_bank_account_id_gsi_name" {
  description = "The name of the Global Secondary Index (GSI) for querying cards by bank account ID"
  type        = string
}

