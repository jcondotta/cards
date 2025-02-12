resource "aws_dynamodb_table" "cards" {
  name           = var.cards_table_name
  billing_mode   = "PROVISIONED"
  read_capacity  = 2
  write_capacity = 2

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
    read_capacity   = 2
    write_capacity  = 2
  }
}