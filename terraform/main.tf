module "networking" {
  source = "./modules/networking"

  aws_region = var.aws_region
}

module "load_balancer" {
  source = "./modules//load-balancer"

  vpc_id            = module.networking.vpc_id
  public_subnet_ids = module.networking.public_subnet_ids
  ec2_instance_ids  = module.ec2.instance_ids
}

module "ec2" {
  source = "./modules/compute/ec2"

  vpc_id            = module.networking.vpc_id
  public_subnet_ids = module.networking.public_subnet_ids

  allowed_security_group_ids = [
    module.load_balancer.security_group_id
  ]
}

module "dynamodb" {
  source = "./modules/data/dynamodb"

  vpc_id = module.networking.vpc_id
  allowed_security_group_ids = [
    module.ec2.ec2_security_group_id
  ]

  cards_table_name                  = "cards-${var.environment}"
  cards_by_bank_account_id_gsi_name = "cards-by-bank-account-id-gsi-${var.environment}"
}

module "sqs" {
  source = "./modules/messaging/sqs"

  card_application_queue_name = "card-application-${var.environment}"
  card_application_dlq_name   = "card-application-dlq-${var.environment}"
}

module "cards_add_service_lambda" {
  source = "./modules/compute/lambda/cards-add-service"

  aws_region             = var.aws_region
  current_aws_account_id = data.aws_caller_identity.current.account_id

  function_name = "cards-add-service-${var.environment}"
  memory_size   = 1024
  timeout       = 15
  runtime       = "java21"
  handler       = "com.jcondotta.cards.add.web.events.CardApplicationEventHandler"
  s3_bucket     = "jcondotta-lambda-files"
  s3_key        = "cards-add-service-0.1.jar"

  dynamodb_cards_table_arn                   = module.dynamodb.cards_table_arn
  sqs_card_application_queue_arn             = module.sqs.card_application_queue_arn
  sqs_card_application_dead_letter_queue_arn = module.sqs.card_application_dead_letter_queue_arn

  environment_variables = {
    "AWS_DYNAMODB_CARDS_TABLE_NAME"                  = module.dynamodb.cards_table_name,
    "AWS_SQS_CARD_APPLICATION_QUEUE_NAME"            = module.sqs.card_application_queue_name,
    "AWS_DYNAMODB_CARDS_BY_BANK_ACCOUNT_ID_GSI_NAME" = module.dynamodb.cards_by_bank_account_id_gsi_name
  }
}