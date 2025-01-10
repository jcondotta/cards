resource "aws_dynamodb_table" "cards" {
  name           = var.cards_table_name
  billing_mode   = var.cards_billing_mode
  read_capacity  = var.cards_read_capacity
  write_capacity = var.cards_write_capacity

  hash_key = "cardId"

  attribute {
    name = "cardId"
    type = "S"
  }

  attribute {
    name = "bankAccountId"
    type = "S"
  }

  global_secondary_index {
    name            = var.cards_by_bank_account_id_gsi_name
    hash_key        = "bankAccountId"
    projection_type = "ALL"
    read_capacity   = var.cards_by_bank_account_id_gsi_read_capacity
    write_capacity  = var.cards_by_bank_account_id_gsi_write_capacity
  }

  tags = var.tags
}