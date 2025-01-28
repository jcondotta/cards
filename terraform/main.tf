module "networking" {
  source = "./modules/networking"

  aws_region = var.aws_region
}

# module "load_balancer" {
#   source = "./modules/load-balancer"
#
#   vpc_id            = module.networking.vpc_id
#   public_subnet_ids = module.networking.public_subnet_ids
#   ec2_instance_ids  = module.ec2.instance_ids
# }

# module "ec2" {
#   source = "./modules/compute/ec2"
#
#   aws_region             = var.aws_region
#   environment            = var.environment
#   current_aws_account_id = data.aws_caller_identity.current.account_id
#
#   vpc_id                   = module.networking.vpc_id
#   subnet_ids               = module.networking.private_subnet_ids
#   dynamodb_cards_table_arn = module.dynamodb.cards_table_arn
#
#   allowed_security_group_ids = [
#     module.load_balancer.security_group_id
#   ]
# }

module "ssm_parameters" {
  source = "./modules/ssm-parameters"

  dynamodb_cards_table_ssm_param_value = "cards-${var.environment}"
  dynamodb_cards_gsi_ssm_param_value   = "cards-by-bank-account-id-gsi-${var.environment}"

  sqs_cards_queue_ssm_param_value      = "card-application-${var.environment}"
  sqs_cards_dlq_ssm_param_value        = "card-application-dlq-${var.environment}"
}


module "dynamodb" {
  source = "./modules/data/dynamodb"

  cards_table_name                  = module.ssm_parameters.dynamodb_cards_table_ssm_param_value
  cards_by_bank_account_id_gsi_name = module.ssm_parameters.dynamodb_cards_gsi_ssm_param_value
}

module "sqs" {
  source = "./modules/messaging/sqs"

  card_application_queue_name = module.ssm_parameters.sqs_cards_queue_ssm_param_value
  card_application_dlq_name   = module.ssm_parameters.sqs_cards_dlq_ssm_param_value
}

module "ecs_cards_management_service" {
  source = "./modules/compute/ecs/cards-management-service"

  aws_region  = var.aws_region
  environment = var.environment
  current_aws_account_id = data.aws_caller_identity.current.account_id

  vpc_id             = module.networking.vpc_id
  service_subnet_ids = module.networking.public_subnet_ids

  dynamodb_cards_table_arn       = module.dynamodb.cards_table_arn
  sqs_card_application_queue_arn = module.sqs.card_application_queue_arn
}

module "cards_process_service_lambda" {
  source = "./modules/compute/lambda/cards-process-service"

  aws_region             = var.aws_region
  current_aws_account_id = data.aws_caller_identity.current.account_id

  function_name = "cards-process-service-${var.environment}"
  memory_size   = 1024
  timeout       = 15
  runtime       = "java21"
  handler       = "com.jcondotta.cards.process.web.events.CardApplicationEventHandler"
  s3_bucket     = "jcondotta-lambda-files"
  s3_key        = "cards-process-service-0.1.jar"

  dynamodb_cards_table_arn       = module.dynamodb.cards_table_arn
  sqs_card_application_queue_arn = module.sqs.card_application_queue_arn

  environment_variables = {
    "AWS_DYNAMODB_CARDS_TABLE_NAME"       = module.dynamodb.cards_table_name,
    "AWS_SQS_CARD_APPLICATION_QUEUE_NAME" = module.sqs.card_application_queue_name,
  }
}