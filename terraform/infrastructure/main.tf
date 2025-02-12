module "networking" {
  source = "./modules/networking"

  aws_region  = var.aws_region
  environment = var.environment
}

module "dynamodb" {
  source = "./modules/data/dynamodb"

  cards_table_name                  = "cards-${var.environment}"
  cards_by_bank_account_id_gsi_name = "cards-by-bank-account-id-gsi-${var.environment}"
}

module "sns" {
  source = "./modules/messaging/sns"

  sqs_card_application_dlq_name = module.sqs.card_application_dead_letter_queue_name
}

module "sqs" {
  source = "./modules/messaging/sqs"

  card_application_queue_name = "card-application-${var.environment}"
  card_application_dlq_name   = "card-application-dlq-${var.environment}"

  sns_topic_card_application_dlq_notification_arn = module.sns.card_application_dlq_notifications_arn
}

module "caching" {
  source = "./modules/caching"

  environment = var.environment

  vpc_id             = module.networking.vpc_id
  cache_cluster_name = "cards-cache-${var.environment}"
  subnet_ids         = module.networking.private_subnet_ids
  security_group_ids = [
    module.cards_process_service_lambda.process_cards_lambda_security_group_id
  ]
}

module "cards_process_service_lambda" {
  source = "./modules/compute/lambda/process-service"

  aws_region             = var.aws_region
  environment            = var.environment
  current_aws_account_id = data.aws_caller_identity.current.account_id

  function_name = "cards-process-service-${var.environment}"

  dynamodb_cards_table_arn                       = module.dynamodb.cards_table_arn
  sqs_card_application_queue_arn                 = module.sqs.card_application_queue_arn
  cards_process_service_lambda_files_bucket_name = data.terraform_remote_state.bootstrap.outputs.lambda_bucket_name

  vpc_id     = module.networking.vpc_id
  subnet_ids = module.networking.private_subnet_ids

  environment_variables = {
    "AWS_DYNAMODB_CARDS_TABLE_NAME"       = module.dynamodb.cards_table_name,
    "AWS_SQS_CARD_APPLICATION_QUEUE_NAME" = module.sqs.card_application_queue_name,
    "REDIS_HOST"                          = module.caching.redis_host,
    "REDIS_PORT"                          = module.caching.redis_port,
  }
}

module "ecs_cluster" {
  source = "./modules/compute/ecs/cluster"

  ecs_cluster_name = "cards-ecs-cluster-${var.environment}"
}

module "ecs_management_service" {
  source = "./modules/compute/ecs/services/management-service"

  aws_region  = var.aws_region
  environment = var.environment

  vpc_id             = module.networking.vpc_id
  service_subnet_ids = module.networking.private_subnet_ids

  ecs_cluster_id  = module.ecs_cluster.ecs_cluster_id
  container_name  = "cards-management-service-container"
  container_image = data.terraform_remote_state.bootstrap.outputs.management_service_ecr_url
  container_port  = 8072

  lb_security_group_id = module.load_balancer.security_group_id
  lb_target_group_arn  = module.lb_target_group_management_service.target_group_arn

  dynamodb_cards_table_arn       = module.dynamodb.cards_table_arn
  sqs_card_application_queue_arn = module.sqs.card_application_queue_arn

  environment_variables = {
    "AWS_DEFAULT_REGION"                  = var.aws_region
    "AWS_DYNAMODB_CARDS_TABLE_NAME"       = module.dynamodb.cards_table_name,
    "AWS_SQS_CARD_APPLICATION_QUEUE_NAME" = module.sqs.card_application_queue_name,
    "REDIS_HOST"                          = module.caching.redis_host,
    "REDIS_PORT"                          = module.caching.redis_port,
  }
}

module "ecs_query_service" {
  source = "./modules/compute/ecs/services/query-service"

  aws_region  = var.aws_region
  environment = var.environment

  vpc_id             = module.networking.vpc_id
  service_subnet_ids = module.networking.private_subnet_ids

  ecs_cluster_id  = module.ecs_cluster.ecs_cluster_id
  container_name  = "cards-query-service-container"
  container_image = data.terraform_remote_state.bootstrap.outputs.query_service_ecr_url
  container_port  = 8073

  lb_security_group_id = module.load_balancer.security_group_id
  lb_target_group_arn  = module.lb_target_group_query_service.target_group_arn

  dynamodb_cards_table_arn       = module.dynamodb.cards_table_arn
  dynamodb_cards_by_bank_account_id_gsi_arn = module.dynamodb.cards_by_bank_account_id_gsi_arn

  environment_variables = {
    "AWS_DEFAULT_REGION"                  = var.aws_region
    "AWS_DYNAMODB_CARDS_TABLE_NAME"       = module.dynamodb.cards_table_name,
    "AWS_DYNAMODB_CARDS_BY_BANK_ACCOUNT_ID_GSI_NAME" = module.dynamodb.cards_by_bank_account_id_gsi_name,
    "REDIS_HOST"                          = module.caching.redis_host,
    "REDIS_PORT"                          = module.caching.redis_port,
  }
}

module "load_balancer" {
  source = "./modules/load-balancer"

  environment        = var.environment
  load_balancer_name = "cards-load-balancer-${var.environment}"

  vpc_id     = module.networking.vpc_id
  subnet_ids = module.networking.public_subnet_ids
}

module "lb_target_group_management_service" {
  source = "./modules/load-balancer/target-groups/management-service"

  vpc_id = module.networking.vpc_id

  listener_http_arn    = module.load_balancer.listener_http_arn
  lb_target_group_port = module.ecs_management_service.ecs_management_service_port
}

module "lb_target_group_query_service" {
  source = "./modules/load-balancer/target-groups/query-service"

  vpc_id = module.networking.vpc_id

  listener_http_arn    = module.load_balancer.listener_http_arn
  lb_target_group_port = module.ecs_query_service.ecs_query_service_port
}