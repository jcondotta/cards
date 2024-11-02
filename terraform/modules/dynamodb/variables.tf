variable "aws_region" {
  description = "The AWS region where resources will be deployed."
  type        = string
}

variable "environment" {
  description = "The environment to deploy to (e.g., dev, staging, prod)"
  type        = string
}

variable "cards_table_name" {
  type        = string
  description = "The name of the DynamoDB table for cards"
}

variable "cards_billing_mode" {
  type        = string
  description = "The billing mode for the DynamoDB table (PROVISIONED or PAY_PER_REQUEST)"
}

variable "cards_read_capacity" {
  type        = number
  description = "The read capacity units for the DynamoDB table"
}

variable "cards_write_capacity" {
  type        = number
  description = "The write capacity units for the DynamoDB table"
}

variable "cards_by_bank_account_id_gsi_name" {
  description = "The name of the Global Secondary Index (GSI) for querying cards by bank account ID"
  type        = string
}

variable "cards_by_bank_account_id_gsi_read_capacity" {
  type        = number
  description = "The read capacity units for the Global Secondary Index (GSI) on the cards table."
}

variable "cards_by_bank_account_id_gsi_write_capacity" {
  type        = number
  description = "The write capacity units for the Global Secondary Index (GSI) on the cards table."
}

variable "tags" {
  description = "Tags applied to all resources for organization and cost tracking across environments and projects."
  type        = map(string)
}

