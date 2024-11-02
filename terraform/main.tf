provider "aws" {
  region      = var.aws_region
  profile     = var.aws_profile
  max_retries = 5 # Increase retries in case of temporary AWS service issues
}

# Use data source to fetch the AWS account ID dynamically
data "aws_caller_identity" "current" {}

module "dynamodb" {
  source = "./modules/dynamodb"

  aws_region  = var.aws_region
  environment = var.environment
  tags        = merge(var.tags, { "environment" = var.environment })

  cards_table_name     = "cards-${var.environment}"
  cards_billing_mode   = var.cards_billing_mode
  cards_read_capacity  = var.cards_read_capacity
  cards_write_capacity = var.cards_write_capacity

  cards_by_bank_account_id_gsi_name           = "cards-by-bank-account-id-gsi-${var.environment}"
  cards_by_bank_account_id_gsi_read_capacity  = var.cards_by_bank_account_id_read_capacity
  cards_by_bank_account_id_gsi_write_capacity = var.cards_by_bank_account_id_write_capacity
}

module "sqs" {
  source = "./modules/sqs"

  aws_region  = var.aws_region
  environment = var.environment
  tags        = merge(var.tags, { "environment" = var.environment })

  card_application_queue_name = "card-application-${var.environment}"
  card_application_dlq_name   = "card-application-dlq-${var.environment}"
}

module "add_cards_lambda" {
  source = "./modules/lambda/add-cards"

  aws_region             = var.aws_region
  current_aws_account_id = data.aws_caller_identity.current.account_id
  environment            = var.environment
  tags                   = merge(var.tags, { "environment" = var.environment })

  lambda_function_name         = "add-cards-lambda-${var.environment}"
  lambda_memory_size           = var.add_cards_lambda_memory_size
  lambda_timeout               = var.add_cards_lambda_timeout
  lambda_runtime               = var.add_cards_lambda_runtime
  lambda_handler               = var.add_cards_lambda_handler
  lambda_environment_variables = var.add_cards_lambda_environment_variables

  dynamodb_cards_table_arn  = module.dynamodb.cards_table_arn
  dynamodb_cards_table_name = module.dynamodb.cards_table_name

  sqs_card_application_queue_arn  = module.sqs.card_application_queue_arn
  sqs_card_application_queue_name = module.sqs.card_application_queue_name
}

module "management_cards_lambda" {
  source = "./modules/lambda/management-cards"

  aws_region             = var.aws_region
  current_aws_account_id = data.aws_caller_identity.current.account_id
  environment            = var.environment
  tags                   = merge(var.tags, { "environment" = var.environment })

  lambda_function_name         = "management-cards-lambda-${var.environment}"
  lambda_memory_size           = var.management_cards_lambda_memory_size
  lambda_timeout               = var.management_cards_lambda_timeout
  lambda_runtime               = var.management_cards_lambda_runtime
  lambda_handler               = var.management_cards_lambda_handler
  lambda_environment_variables = var.add_cards_lambda_environment_variables

  dynamodb_cards_table_arn  = module.dynamodb.cards_table_arn
  dynamodb_cards_table_name = module.dynamodb.cards_table_name

  sqs_card_application_queue_arn  = module.sqs.card_application_queue_arn
  sqs_card_application_queue_name = module.sqs.card_application_queue_name
}

module "apigateway" {
  source = "./modules/apigateway"

  aws_region  = var.aws_region
  environment = var.environment
  tags        = merge(var.tags, { "environment" = var.environment })

  management_cards_lambda_function_arn  = module.management_cards_lambda.lambda_function_arn
  management_cards_lambda_function_name = module.management_cards_lambda.lambda_function_name
  management_cards_lambda_invoke_uri    = module.management_cards_lambda.lambda_invoke_uri
}